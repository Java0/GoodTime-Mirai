package goodtime.game.poker_game;


import goodtime.game.Game;
import goodtime.game.Player;
import goodtime.game.poker_game.rule.Rule;
import net.mamoe.mirai.contact.Group;

import java.util.ArrayList;

public abstract class PokerGame implements Game {

    public static final String INITIALIZATION_PHASE = "Initialization";
    public static final String GAMING_PHASE = "Gaming";
    public static final String END_PHASE = "End";

    //玩家
    ArrayList<Player> players = new ArrayList<>();

    //牌库
    ArrayList<Poker> pool = new ArrayList<>();

    //运行游戏的群组
    Group group;

    //游戏当前阶段
    protected String phase = "";

    //当前回合出的牌
    ArrayList<Poker> outPokers;

    //当前规则
    Rule currentRule;

    //最大可游玩玩家数
    int maxPlayer;

    @Override
    public boolean addPlayer(Player player) {
        if ((!players.contains(player)) && players.size() < maxPlayer) {
            return players.add(player);
        } else {
            return false;
        }
    }

    @Override
    public boolean removePlayer(Player player) {
        return players.remove(player);
    }

    @Override
    public ArrayList<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean isFull() {
        return getPlayers().size() == maxPlayer;
    }

    public abstract void initializationPhase(String memberIn);

    public abstract void gamingPhase(String memberIn);

    public abstract void endPhase();




}
