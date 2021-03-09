package goodtime.game.gobang;

import goodtime.game.Casinos;
import goodtime.game.Game;
import goodtime.game.Player;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Gobang implements Game {

    //最大玩家数量
    private final int MAX_PLAYER = 2;

    //玩家
    private final ArrayList<Player> players = new ArrayList<>();

    //运行游戏的群组
    Group group;

    //可以落子玩家的索引
    private int playerIndex;

    private final char[] COLORS = {'白', '黑'};


    //游戏当前运行状态
    private int state = Game.NOT_RUNNING;

    //棋盘
    private final char[][] BOARD = new char[15][15];

    public Gobang(Group group) {
        this.group = group;
    }

    @Override
    public String getName() {
        return "五子棋";
    }

    @Override
    public boolean addPlayer(Player player) {
        if ((!players.contains(player)) && players.size() < MAX_PLAYER) {
            return players.add(player);
        }
        return false;
    }

    private InputStream input(String path) {
        return Gobang.class.getClassLoader().getResourceAsStream(path);
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
        return players.size() == MAX_PLAYER;
    }

    @Override
    public int getState() {
        return state;
    }


    @Override
    public void start() {
        state = Game.RUNNING;

        playerIndex = new Random().nextInt(2);

        for (Player player : players) {
            player.setBasicScore(500);
        }

        commandParse("", players.get(playerIndex).getSender().getNick());

    }

    @Override
    public boolean isYourTurn(String memberNick) {
        return memberNick.equals(players.get(playerIndex).getSender().getNick());
    }

    private void subIndex() {
        playerIndex++;
        playerIndex = playerIndex == players.size() ? 0 : playerIndex;
    }

    private int colorIndex;

    private void subColorIndex() {
        colorIndex++;
        colorIndex = colorIndex == COLORS.length ? 0 : colorIndex;
    }

    private boolean isWin(int x, int y) {
        char color = BOARD[x][y];
        return left_Right(color, x, y) || up_Down(color, x, y) || lowRight_UpperLeft(color, x, y) || lowLeft_UpperRight(color, x, y);
    }

    private boolean left_Right(char color, int x, int y) {
        int timer = 1;

        for (int i = x - 1; i >= 0; i--) {
            if (BOARD[i][y] == color) {
                timer++;
            } else {
                break;
            }
        }

        for (int i = x + 1; i < 15; i++) {
            if (BOARD[i][y] == color) {
                timer++;
            } else {
                break;
            }
        }

        return timer == 5;

    }

    private boolean up_Down(char color, int x, int y) {

        int timer = 1;

        for (int j = y - 1; j >= 0; j--) {
            if (BOARD[x][j] == color) {
                timer++;
            } else {
                break;
            }
        }

        for (int j = y + 1; j < 15; j++) {
            if (BOARD[x][j] == color) {
                timer++;
            } else {
                break;
            }
        }

        return timer == 5;
    }


    private boolean lowRight_UpperLeft(char color, int x, int y) {
        int timer = 1;

        for (int i = x + 1, j = y + 1; i >= 0 && j >= 0; i++, j++) {
            if (BOARD[i][j] == color) {
                timer++;
            } else {
                break;
            }
        }

        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
            if (BOARD[i][j] == color) {
                timer++;
            } else {
                break;
            }
        }

        return timer == 5;
    }

    private boolean lowLeft_UpperRight(char color, int x, int y) {
        int timer = 1;

        for (int i = x - 1, j = y + 1; i >= 0 && j >= 0; i--, j++) {
            if (BOARD[i][j] == color) {
                timer++;
            } else {
                break;
            }
        }

        for (int i = x + 1, j = y - 1; i >= 0 && j >= 0; i++, j--) {
            if (BOARD[i][j] == color) {
                timer++;
            } else {
                break;
            }
        }

        return timer == 5;
    }


    @Override
    public void commandParse(String command, String playerNick) {

        if (command.equals("")) {
            Contact.sendImage(group, input("image/board.png"), "png");

            group.sendMessage("游戏开始，请使用yx的格式落子(如h8)");
            group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("你是白棋，该你落子哟~"));
        } else if (isCoordinate(command)) {

            int x = Integer.parseInt(command.toLowerCase().substring(1)) - 1;
            int y = command.toLowerCase().charAt(0) - 'a';

            char currentColor = COLORS[colorIndex];

            if (isYourTurn(playerNick)) {

                if (BOARD[x][y] == '\u0000') {

                    BOARD[x][y] = currentColor;
                    drawImage(currentColor, x + 1, y + 1);

                    if (isWin(x, y)) {
                        endPhase();
                    } else {
                        Contact.sendImage(group, current, "png");
                        subColorIndex();
                        subIndex();

                        group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("你是" + COLORS[colorIndex] + "棋，该你落子哟~"));
                    }

                } else {
                    group.sendMessage("下棋位置已有棋子");
                }

            } else {
                group.sendMessage("不是你的回合哦");
            }
        }
    }

    private void endPhase() {

        Player player = players.get(playerIndex);
        int score = Casinos.SCORE_MAP.get(player.getSender().getId());
        Casinos.SCORE_MAP.put(player.getSender().getId(), score + player.getBasicScore());
        Casinos.scoreJson.save();

        Contact.sendImage(group, current, "png");
        group.sendMessage(new At(players.get(playerIndex).getSender().getId()).plus("你赢了" + player.getBasicScore() + "分"));
        state = Game.ENDING;

    }


    BufferedImage white;
    BufferedImage black;
    BufferedImage board;

    InputStream current;

    {
        try {
            board = ImageIO.read(input("image/board.png"));
            white = ImageIO.read(input("image/white.png"));
            black = ImageIO.read(input("image/black.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawImage(char color, int x, int y) {

        Graphics2D g = board.createGraphics();
        switch (color) {
            case '白':
                g.drawImage(white, (43 + (x * 94) - white.getWidth() / 2) + 1 + (x - 1) * 3, (43 + (y * 94) - white.getHeight() / 2) + 1 + (y - 1) * 3, white.getWidth(), white.getHeight(), null);
                break;
            case '黑':
                g.drawImage(black, (43 + (x * 94) - black.getWidth() / 2) + 1 + +(x - 1) * 3, (43 + (y * 94) - black.getHeight() / 2) + 1 + (y - 1) * 3, black.getWidth(), black.getHeight(), null);
                break;
        }
        g.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(board, "png", bos);
            current = new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean isJoinCommand(String memberOut) {
        switch (memberOut) {
            case "gobang":
            case "wzq":
            case "五子棋":
                return true;
        }
        return false;
    }

    @Override
    public boolean isLeaveCommand(String memberOut) {
        switch (memberOut) {
            case "摸了":
            case "下桌":
                return true;
        }
        return false;
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

    public boolean isCoordinate(String memberOut) {
        char start = memberOut.toLowerCase().charAt(0);
        int next;
        try {
            next = Integer.parseInt(memberOut.toLowerCase().substring(1));
        } catch (NumberFormatException e) {
            return false;
        }
        return memberOut.length() <= 3 && (start >= 'a' && start <= 'o') && (next >= 1 && next <= 15);
    }


    @Override
    public boolean equals(Game game) {
        return this.getName().equals(game.getName());
    }

}
