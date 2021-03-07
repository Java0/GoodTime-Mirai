package goodtime.game;


import goodtime.game.score.ScoreJson;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;

import java.util.HashMap;
import java.util.Objects;

public class Casinos {


    //每个群对应的游戏
    public static final HashMap<Long, Game> GAME_MAP = new HashMap<>();

    //积分所在json
    public static ScoreJson scoreJson = ScoreJson.getInstance();

    //玩家积分信息
    public static final HashMap<Long, Integer> SCORE_MAP = scoreJson.scoreMap;

    public static void run(GroupMessageEvent event) {

        //群内发言文本
        String membersOut = event.getMessage().contentToString();

        //当前发言群对象
        Group group = event.getSubject();

        //当前发言的群员对象
        Member sender = event.getSender();

        //如果从json里读取不到这个玩家，就把这个玩家丢进去，给一个默认积分
        if (SCORE_MAP.putIfAbsent(sender.getId(), 5000) == null) {
            scoreJson.save();
        }

        if (membersOut.equals("#game")) {
            group.sendMessage("斗地主：[.sz] [.fork table] [.上桌]\n" +
                    "五子棋（暂未实现）：[gobang]\n" +
                    "[]内的文字代表指令");
        }

        Game currentGame = GAME_MAP.get(group.getId());

        //如果是一个游戏指令
        if (Game.getGame(membersOut, group) != null) {
            //如果这个群聊没有已开启的游戏,则添加进map，分配指令对应的的游戏对象
            if (currentGame == null) {
                currentGame = Game.getGame(membersOut, group);
                GAME_MAP.put(group.getId(), currentGame);
            } else if (!Objects.requireNonNull(Game.getGame(membersOut, group)).equals(currentGame)) {
                group.sendMessage("当前群群聊已运行游戏：" + currentGame.getName() + "，若要运行其他游戏请联系管理员结束当前游戏");
            }
        }

        if (currentGame != null) {
            switch (currentGame.getState()) {
                case Game.NOT_RUNNING:
                    if (currentGame.isJoinCommand(membersOut)) {
                        if (currentGame.addPlayer(new Player(sender))) {
                            group.sendMessage(new At(sender.getId()).plus("[" + currentGame.getName() + "] 加入成功，当前玩家：\n" + Game.getPlayerList(currentGame.getPlayers())));
                        } else {
                            group.sendMessage("已加入或已达最大游戏人数，当前玩家：\n" + Game.getPlayerList(currentGame.getPlayers()));
                        }
                    }

                    if (currentGame.isLeaveCommand(membersOut) && !GAME_MAP.isEmpty()) {
                        if (currentGame.removePlayer(new Player(sender))) {
                            group.sendMessage(new At(sender.getId()).plus("退出成功，当前玩家：\n" + Game.getPlayerList(currentGame.getPlayers())));
                        } else {
                            group.sendMessage("你丫就没上桌！当前玩家：\n" + Game.getPlayerList(currentGame.getPlayers()));
                        }
                    }

                    if (currentGame.isStartCommand(membersOut)) {
                        if (currentGame.isFull()) {
                            currentGame.start();
                        } else {
                            group.sendMessage("玩家不够，当前玩家：" + Game.getPlayerList(currentGame.getPlayers()));
                        }
                    }
                    break;

                case Game.RUNNING:
                    currentGame.commandParse(membersOut, sender.getNick());
                    break;
                case Game.ENDING:
                    GAME_MAP.remove(group.getId());
            }
        }


    }


}
