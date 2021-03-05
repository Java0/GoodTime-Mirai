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


    //牌库
    protected ArrayList<Poker> pool = new ArrayList<>();

    //最大可游玩玩家数
    protected int maxPlayerCount;

    //玩家
    protected ArrayList<Player> players = new ArrayList<>();


    Group group;

    //游戏当前阶段
    protected String currentPhase = "";

    //当前回合出的牌
    ArrayList<Poker> currentOutPokers;

    //当前规则
    Rule currentRule;


    @Override
    public boolean addPlayer(Player player) {
        if ((!players.contains(player)) && players.size() < maxPlayerCount) {
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
    public boolean hasMaxPlayer() {
        return getPlayers().size() == maxPlayerCount;
    }

    public abstract void initializationPhase(String memberIn);

    public abstract void gamingPhase(String memberIn);

    public abstract void endPhase();




}
