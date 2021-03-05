package goodtime.game;

import goodtime.game.poker_game.DouDiZhu;
import net.mamoe.mirai.contact.Group;

import java.util.ArrayList;

public interface Game {

    int NOT_RUNING = 0;

    int RUNING = 1;

    String getName();

    boolean addPlayer(Player player);

    boolean removePlayer(Player player);

    ArrayList<Player> getPlayers();

    boolean hasMaxPlayer();

    int getState();

    void start();

    boolean isYourTurn(String memberNick);

    void commandParse(String command, String playerNick);

    boolean isJoinCommand(String memberOut);

    boolean isLeaveCommand(String memberOut);

    boolean isStartCommand(String memberOut);

    boolean equals(Game game);

    static String getPlayerList(ArrayList<Player> players) {

        StringBuilder playList = new StringBuilder(" ");

        for (Player player : players) {
            playList.append(player.getSender().getNick()).append(" | ");
        }

        return playList.toString().trim();
    }

    static Game getGame(String memberOut, Group group) {
        switch (memberOut) {
            case ".上桌":
            case ".fork table":
            case ".sz":
                return new DouDiZhu(group);
            case "gobang":
                return null;
        }
        return null;
    }


}
