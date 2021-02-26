package goodtime.game;


import goodtime.game.game_util.GameUtil;
import goodtime.game.poker_game.DouDiZhu;
import goodtime.game.poker_game.PokerGame;
import goodtime.game.poker_game.poker_game_util.PokerUtil;
import goodtime.game.score.ScoreJson;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private static String getPlayerList(ArrayList<Player> players) {

        StringBuilder playList = new StringBuilder(" ");

        for (Player player : players) {
            playList.append(player.getSender().getNick()).append(" | ");
        }

        return playList.toString().trim();
    }

    //每个群对应的游戏
    public static final HashMap<Long, PokerGame> GAME_MAP = new HashMap<>();

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
        Member groupMember = event.getSender();

        //如果Map里没有这个群id，则添加进map，分配一个新的游戏对象
        GAME_MAP.computeIfAbsent(group.getId(), k -> new DouDiZhu(group));

        PokerGame currentGame = GAME_MAP.get(group.getId());

        //如果从json里读取不到这个玩家，就把这个玩家丢进去，给一个默认积分
        SCORE_MAP.putIfAbsent(groupMember.getId(), 5000);

        //存入json
        scoreJson.save();

        switch (currentGame.getCurrentPhase()) {
            case "":
                if (GameUtil.isJoinCommand(membersOut)) {
                    if (currentGame.addPlayer(new Player(groupMember))) {
                        group.sendMessage(new At(groupMember.getId()).plus("加入成功，当前玩家：\n" + getPlayerList(currentGame.getPlayers())));
                    } else {
                        group.sendMessage("已加入或已达最大游戏人数，当前玩家：\n" + getPlayerList(currentGame.getPlayers()));
                    }
                }

                if (GameUtil.isLeaveCommand(membersOut) && !GAME_MAP.isEmpty()) {
                    if (currentGame.removePlayer(new Player(groupMember))) {
                        group.sendMessage(new At(groupMember.getId()).plus("退出成功，当前玩家：\n" + getPlayerList(currentGame.getPlayers())));
                    } else {
                        group.sendMessage("你丫就没上桌！当前玩家：\n" + getPlayerList(currentGame.getPlayers()));
                    }


                }

                if ("start".equals(membersOut)) {
                    if (currentGame.hasMaxPlayer()) {
                        currentGame.start();
                    } else {
                        group.sendMessage("玩家不够，当前玩家：" + getPlayerList(currentGame.getPlayers()));
                    }
                }
                break;
            case GameUtil.INITIALIZATION_PHASE:
                if (PokerUtil.isSnatch(membersOut) != -1 || PokerUtil.isReDoubleCommand(membersOut)) {
                    if (currentGame.isYourTurn(event.getSender().getNick())) {
                        currentGame.initializationPhase(membersOut);
                    } else {
                        group.sendMessage("不是你的回合！");
                    }
                }
                break;
            case GameUtil.GAMING_PHASE:
                if (PokerUtil.outIsPoker(membersOut) || PokerUtil.isPassCommand(membersOut)) {
                    if (currentGame.isYourTurn(event.getSender().getNick())) {
                        currentGame.gamingPhase(membersOut);
                    } else {
                        group.sendMessage("不是你的回合！");
                    }
                }
                break;
            case GameUtil.END_PHASE:
                currentGame.endPhase();
                break;
        }
    }


}
