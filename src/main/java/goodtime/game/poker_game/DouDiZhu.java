package goodtime.game.poker_game;

import goodtime.game.Casinos;
import goodtime.game.Game;
import goodtime.game.Player;
import goodtime.game.poker_game.poker_game_util.PokerUtil;
import goodtime.game.poker_game.rule.Rule;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DouDiZhu extends PokerGame {

    private int state = NOT_RUNNING;

    public DouDiZhu(Group group) {
        maxPlayer = 3;
        this.group = group;
    }

    @Override
    public String getName() {
        return "斗地主";
    }

    @Override
    public int getState() {
        return state;
    }

    private boolean isCommand(String command) {
        return isSnatchCoomand(command) != -1 || isPassCommand(command) || isReDoubleCommand(command);
    }


    @Override
    public void commandParse(String command, String playerNick) {
        switch (phase) {
            case "":
                initializationPhase(command);
                break;
            case INITIALIZATION_PHASE:
                if (isCommand(command)) {
                    if (isYourTurn(playerNick)) {
                        initializationPhase(command);
                    } else {
                        group.sendMessage("不是你的回合哟");
                    }
                }
                break;
            case GAMING_PHASE:
                if (PokerUtil.outIsPoker(command) || isCommand(command)) {
                    if (isYourTurn(playerNick)) {
                        gamingPhase(command);
                    } else {
                        group.sendMessage("不是你的回合哟");
                    }
                }
                break;
            case END_PHASE:
                endPhase();
                break;
        }
    }

    @Override
    public void start() {

        state = RUNNING;

        PokerUtil.addPokers(pool);
        PokerUtil.deal(pool, players);

        Collections.shuffle(players);

        commandParse("n", "");

    }

    private int playerIndex;

    @Override
    public boolean isYourTurn(String memberNick) {
        return memberNick.equals(players.get(playerIndex).getSender().getNick());
    }


    @Override
    public boolean isJoinCommand(String memberOut) {
        switch (memberOut) {
            case ".上桌":
            case ".fork table":
            case ".sz":
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isLeaveCommand(String memberOut) {
        switch (memberOut) {
            case ".下桌":
            case ".xz":
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isStartCommand(String memberOut) {

        switch (memberOut) {
            case "开始游戏":
            case "start":
            case "ks":
                return true;
        }
        return false;
    }

    @Override
    public int getBasicScore() {
        return 200;
    }

    @Override
    public boolean equals(Game game) {
        return this.getName().equals(game.getName());
    }

    private int isSnatchCoomand(String memberOut) {
        switch (memberOut) {
            case "n":
            case "不抢":
            case "抢你妈":
            case "开始游戏":
                return 0;
            case "y":
            case "抢":
            case "抢他妈的":
                return 1;

        }
        return -1;
    }

    private boolean isPassCommand(String memberOut) {

        String temp = memberOut.toLowerCase();

        switch (temp) {
            case "p":
            case "pass":
            case "passs":
            case "passss":
            case "passsss":
            case "passssss":
            case "passsssss":
            case "passssssss":
            case "passsssssss":
            case "过":
            case "不要":
            case "要不起":
                return true;
        }
        return false;
    }

    private boolean isReDoubleCommand(String memberOut) {
        String temp = memberOut.toLowerCase();
        switch (temp) {
            case "加倍":
            case "减倍":
            case "超级加倍":
            case "超级减倍":
                return true;
        }
        return false;
    }


    private int rejectionTimes;

    @Override
    public void initializationPhase(String memberIn) {

        phase = INITIALIZATION_PHASE;

        manualDoubling(memberIn);

        switch (isSnatchCoomand(memberIn)) {
            case 1:
                Player currentPlayer = players.get(playerIndex);
                Member m = currentPlayer.getSender();

                currentPlayer.setLandlord(true);

                currentPlayer.setBasicScore(currentPlayer.getBasicScore() * 2);

                List<Poker> lPokers = pool.subList(pool.size() - 4, pool.size() - 1);

                currentPlayer.addHandPokers(lPokers);

                group.sendMessage(new At(m.getId()).plus("是地主，底牌是:\n" + PokerUtil.pokersToString(lPokers)));
                m.sendMessage("你是地主，现有牌：\n" + PokerUtil.pokersToString(currentPlayer.getHandPokers()));
                gamingPhase("");
                break;
            case 0:
                subIndex();
                rejectionTimes++;
                if (rejectionTimes == 3) {
                    initializationPhase("y");
                } else {
                    group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("请问您要抢地主吗？ y/n"));
                }
                break;
        }
    }

    private void subIndex() {
        playerIndex++;
        playerIndex = playerIndex == players.size() ? 0 : playerIndex;
    }

    private int passTimer;

    @Override
    public void gamingPhase(String memberOut) {
        phase = GAMING_PHASE;

        Player currentPlayer = players.get(playerIndex);

        if (memberOut.equals("")) {
            group.sendMessage(new At(currentPlayer.getSender().getId()).plus("该你出牌"));
        } else if (outPokers == null) {
            //地主第一次出牌或者其他玩家都pass的情况

            //判断你有这些牌
            if (PokerUtil.youHaveThisPokers(memberOut, currentPlayer.getHandPokers())) {

                outPokers = PokerUtil.charToPokers(memberOut);
                currentRule = new Rule(outPokers);

                //如果你出的牌符合一个规则
                if (currentRule.getRule() != null) {

                    redouble(currentRule.getRule());

                    for (Poker poker : outPokers) {
                        currentPlayer.removeHandPoker(poker);
                    }

                    if (currentPlayer.getHandPokers().size() <= 3 && currentPlayer.getHandPokers().size() > 0) {
                        group.sendMessage(currentPlayer.getSender().getNick() + "只剩" + currentPlayer.getHandPokers().size() + "张牌啦！");
                    }

                    currentPlayer.getSender().sendMessage("现有牌:\n" + PokerUtil.pokersToString(currentPlayer.getHandPokers()));


                    if (isWin(currentPlayer.getHandPokers())) {
                        endPhase();
                    } else {
                        group.sendMessage("玩家：" + currentPlayer.getSender().getNick() + "出了：" +
                                "\n" + PokerUtil.pokersToString(outPokers) +
                                "\n" + "匹配规则：" + currentRule.getRule());

                        subIndex();
                        group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("该你出牌了！"));
                    }
                } else {
                    outPokers = null;
                    group.sendMessage("请按规则出牌，露露哇露露哒！");
                }
            } else {
                group.sendMessage("你无此牌");
            }

        } else if (PokerUtil.youHaveThisPokers(memberOut, currentPlayer.getHandPokers())) {

            passTimer = 0;

            Rule nextRule = new Rule(PokerUtil.charToPokers(memberOut));

            if (nextRule.getRule() != null && currentRule.comparison(nextRule)) {

                outPokers = PokerUtil.charToPokers(memberOut);

                currentRule = nextRule;

                redouble(currentRule.getRule());

                for (Poker poker : outPokers) {
                    currentPlayer.removeHandPoker(poker);
                }

                if (currentPlayer.getHandPokers().size() <= 3 && currentPlayer.getHandPokers().size() > 0) {
                    group.sendMessage(currentPlayer.getSender().getNick() + "只剩" + currentPlayer.getHandPokers().size() + "张牌啦！");
                }

                currentPlayer.getSender().sendMessage("现有牌:\n" + PokerUtil.pokersToString(currentPlayer.getHandPokers()));


                if (isWin(currentPlayer.getHandPokers())) {
                    endPhase();
                } else {

                    group.sendMessage("玩家：" + currentPlayer.getSender().getNick() + "出了：" +
                            "\n" + PokerUtil.pokersToString(outPokers) +
                            "\n" + "匹配规则：" + currentRule.getRule());

                    subIndex();
                    group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("该你出牌了！"));
                }


            } else {
                group.sendMessage("请按规则出牌，露露哇露露哒！");
            }


        } else if (isPassCommand(memberOut)) {
            if (outPokers == null) {
                group.sendMessage("在？为什么不出牌？");
            } else {
                group.sendMessage(new At(currentPlayer.getSender().getId()).plus("过牌！"));
                passTimer++;
                if (passTimer == 2) {
                    passTimer = 0;
                    outPokers = null;
                }
                subIndex();
                group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("现在该你出牌！"));
            }
        } else {
            group.sendMessage("你无此牌");
        }
    }

    private void manualDoubling(String memberIn) {
        switch (memberIn) {
            case "加倍":
                for (Player player : players) {
                    player.setBasicScore(player.getBasicScore() * 2);
                }
                group.sendMessage("加倍成功。");
                break;
            case "超级加倍":
                for (Player player : players) {
                    player.setBasicScore(player.getBasicScore() * 4);
                }
                group.sendMessage("超级加倍成功。");
                break;
            case "减倍":
                for (Player player : players) {
                    player.setBasicScore(player.getBasicScore() / 2);
                }
                group.sendMessage("减倍成功。");
                break;
            case "超级减倍":
                for (Player player : players) {
                    player.setBasicScore(player.getBasicScore() / 4);
                }
                group.sendMessage("超级减倍成功。");
                break;
        }

    }


    private void redouble(String rule) {
        if (rule.equals("炸弹")) {
            for (Player player : players) {
                player.setBasicScore(player.getBasicScore() * 2);
            }
        } else if (rule.equals("王炸")) {
            for (Player player : players) {
                player.setBasicScore(player.getBasicScore() * 4);
            }
        }
    }

    private boolean isWin(ArrayList<Poker> handPokers) {
        return handPokers.isEmpty();
    }

    @Override
    public void endPhase() {
        phase = END_PHASE;

        StringBuilder sb = new StringBuilder("战况：");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int score = Casinos.SCORE_MAP.get(p.getSender().getId());
            if (i == playerIndex) {
                Casinos.SCORE_MAP.put(p.getSender().getId(), score + p.getBasicScore());
                sb.append("\n").append(p.getSender().getNick()).append(" +").append(p.getBasicScore());
            } else {
                Casinos.SCORE_MAP.put(p.getSender().getId(), score - p.getBasicScore());
                sb.append("\n").append(p.getSender().getNick()).append(" -").append(p.getBasicScore());
            }
        }
        Casinos.scoreJson.save();
        group.sendMessage("游戏结束，" + sb.toString().trim());

        state = Game.ENDING;
    }
}
