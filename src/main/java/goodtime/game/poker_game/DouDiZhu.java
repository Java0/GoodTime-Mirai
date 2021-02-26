package goodtime.game.poker_game;

import goodtime.game.Player;
import goodtime.game.game_util.GameUtil;
import goodtime.game.poker_game.poker_game_util.PokerUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DouDiZhu extends PokerGame {


    public DouDiZhu(Group group) {
        super(group);
    }

    @Override
    public void start() {

        PokerUtil.addPokers(pool);
        PokerUtil.deal(pool, players);

        for (Player player : players) {
            player.setBasicScore(100);
        }

        Collections.shuffle(players);
        initializationPhase("n");

    }

    private int currentPlayerIndex;

    @Override
    public boolean isYourTurn(String memberNick) {
        return memberNick.equals(players.get(currentPlayerIndex).getSender().getNick());
    }

    private int rejectionTimes;

    @Override
    public void initializationPhase(String memberIn) {

        currentPhase = GameUtil.INITIALIZATION_PHASE;

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

        switch (PokerUtil.isSnatch(memberIn)) {
            case 1:
                Player p = players.get(currentPlayerIndex);
                Member m = p.getSender();

                p.setLandlord(true);

                p.setBasicScore(p.getBasicScore() * 2);

                List<Poker> lPokers = pool.subList(pool.size() - 4, pool.size() - 1);

                p.addHandPokers(lPokers);

                group.sendMessage(new At(m.getId()).plus("是地主，底牌是:\n" + PokerUtil.pokersToString(lPokers)));
                m.sendMessage("你是地主，现有牌：\n" + PokerUtil.pokersToString(p.getHandPokers()));
                gamingPhase("");
                break;
            case 0:
                subIndex();
                rejectionTimes++;
                if (rejectionTimes == 3) {
                    initializationPhase("y");
                } else {
                    group.sendMessage(new At(players.get(currentPlayerIndex).getSender().getId()).plus("请问您要抢地主吗？"));
                }
                break;
        }
    }

    private void subIndex() {
        currentPlayerIndex++;
        currentPlayerIndex = currentPlayerIndex == players.size() ? 0 : currentPlayerIndex;
    }

    private int passTimer;

    @Override
    public void gamingPhase(String memberOut) {
        currentPhase = GameUtil.GAMING_PHASE;

        Player currentPlayer = players.get(currentPlayerIndex);

        if (memberOut.equals("")) {
            group.sendMessage(new At(currentPlayer.getSender().getId()).plus("该你出牌"));
        } else if (currentOutPokers == null) {
            //地主第一次出牌或者其他玩家都pass的情况

            //判断你有这些牌
            if (PokerUtil.youHaveThisPokers(memberOut, currentPlayer.getHandPokers())) {

                currentOutPokers = PokerUtil.charToPokers(memberOut);
                currentRule = new Rule(currentOutPokers);

                //如果你出的牌符合一个规则
                if (currentRule.getRule() != null) {

                    reDouble(currentRule.getRule());

                    for (Poker poker : currentOutPokers) {
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
                                "\n" + PokerUtil.pokersToString(currentOutPokers) +
                                "\n" + "匹配规则：" + currentRule.getRule());

                        subIndex();
                        group.sendMessage(new At(players.get(currentPlayerIndex).getSender().getId()).plus("该你出牌了！"));
                    }
                } else {
                    currentOutPokers = null;
                    group.sendMessage("请按规则出牌，露露哇露露哒！");
                }
            } else {
                group.sendMessage("你无此牌");
            }

        } else if (PokerUtil.youHaveThisPokers(memberOut, currentPlayer.getHandPokers())) {

            passTimer = 0;

            Rule nextRule = new Rule(PokerUtil.charToPokers(memberOut));

            if (nextRule.getRule() != null && currentRule.comparison(nextRule)) {

                currentOutPokers = PokerUtil.charToPokers(memberOut);

                currentRule = nextRule;

                reDouble(currentRule.getRule());

                for (Poker poker : currentOutPokers) {
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
                            "\n" + PokerUtil.pokersToString(currentOutPokers) +
                            "\n" + "匹配规则：" + currentRule.getRule());

                    subIndex();
                    group.sendMessage(new At(players.get(currentPlayerIndex).getSender().getId()).plus("该你出牌了！"));
                }


            } else {
                group.sendMessage("请按规则出牌，露露哇露露哒！");
            }


        } else if (PokerUtil.isPassCommand(memberOut)) {
            if (currentOutPokers == null) {
                group.sendMessage("在？为什么不出牌？");
            } else {
                group.sendMessage(new At(currentPlayer.getSender().getId()).plus("过牌！"));
                passTimer++;
                if (passTimer == 2) {
                    passTimer = 0;
                    currentOutPokers = null;
                }
                subIndex();
                group.sendMessage(new At(players.get(currentPlayerIndex).getSender().getId()).plus("现在该你出牌！"));
            }
        } else {
            group.sendMessage("你无此牌");
        }
    }

    private void reDouble(String rule) {
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
        currentPhase = GameUtil.END_PHASE;

        StringBuilder sb = new StringBuilder("战况：");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int score = SCORE_MAP.get(p.getSender().getId());
            if (i == currentPlayerIndex) {
                SCORE_MAP.put(p.getSender().getId(), score + p.getBasicScore());
                sb.append("\n").append(p.getSender().getNick()).append(" +").append(p.getBasicScore());
            } else {
                SCORE_MAP.put(p.getSender().getId(), score - p.getBasicScore());
                sb.append("\n").append(p.getSender().getNick()).append(" -").append(p.getBasicScore());
            }
        }
        scoreJson.save();
        group.sendMessage("游戏结束，" + sb.toString().trim());
        GAME_MAP.remove(group.getId());
    }
}
