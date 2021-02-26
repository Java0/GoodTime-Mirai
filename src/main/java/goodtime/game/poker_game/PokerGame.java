package goodtime.game.poker_game;


import goodtime.game.Game;
import goodtime.game.Player;
import goodtime.game.poker_game.rule.Rule;
import net.mamoe.mirai.contact.Group;

import java.util.ArrayList;

public abstract class PokerGame extends Game {

    //牌库
    protected ArrayList<Poker> pool = new ArrayList<>();

    //最大可游玩玩家数
    protected int maxPlayerCount;

    //玩家
    protected ArrayList<Player> players = new ArrayList<>();

    //当前运行游戏的群组消息事件
    Group group;

    //游戏当前阶段
    protected String currentPhase = "";

    //当前回合出的牌
    ArrayList<Poker> currentOutPokers;

    //当前规则
    Rule currentRule;


    public boolean hasMaxPlayer() {
        return getPlayers().size() == maxPlayerCount;
    }


    public boolean addPlayer(Player player) {
        if ((!players.contains(player)) && players.size() < maxPlayerCount) {
            return players.add(player);
        } else {
            return false;
        }
    }


    public boolean removePlayer(Player player) {
        return players.remove(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    private void removeAllPlayers() {
        players.clear();
    }


    public abstract void initializationPhase(String memberIn);

    public abstract void gamingPhase(String memberIn);

    public abstract void endPhase();

    public abstract void start();

    public abstract boolean isYourTurn(String memberNick);

    public PokerGame(Group group) {
        super();
        maxPlayerCount = 3;
        this.group = group;
    }

}
