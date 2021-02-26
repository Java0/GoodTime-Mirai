package goodtime.game.poker_game.poker_game_util;


import goodtime.game.Player;
import goodtime.game.poker_game.Poker;

import java.util.*;


public class PokerUtil {

    public static ArrayList<Poker> getPool(ArrayList<Poker> pool, int type) {

        return null;
    }

    public static void addPokers(ArrayList<Poker> defaultPool) {

        for (int i = 0; i < Pokers.simpleSymbols.size() - 2; i++) {
            for (int j = 0; j < 4; j++) {
                defaultPool.add(new Poker(Pokers.simpleSymbols.get(i), i + 1));
            }
        }

        defaultPool.add(new Poker('鬼', 14));
        defaultPool.add(new Poker('王', 15));

    }

    public static void deal(ArrayList<Poker> pool, ArrayList<Player> players) {

        Collections.shuffle(pool);

        for (int i = 0; i < pool.size() - 3; i++) {
            players.get(i % players.size()).addHandPoker(pool.get(i));
        }

        for (Player player : players) {
            player.getSender().sendMessage("现有牌:\n" + pokersToString(player.getHandPokers()));
        }
    }

    public static String pokersToString(List<Poker> pokers) {
        StringBuilder sb = new StringBuilder();

        pokers.sort(Comparator.comparingInt(Poker::getPower));

        for (Poker handPoker : pokers) {
            char pokerMark = handPoker.getMark();
            if (pokerMark == 'I') {
                sb.append("[10] ");
            } else {
                sb.append("[").append(pokerMark).append("] ");
            }

        }
        return sb.toString().trim();

    }


    public static int isSnatch(String memberOut) {
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

    public static boolean isPassCommand(String memberOut) {

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

    public static boolean isReDoubleCommand(String memberout) {
        switch (memberout) {
            case "加倍":
            case "减倍":
            case "超级加倍":
            case "超级减倍":
                return true;
        }
        return false;
    }


    public static ArrayList<Poker> getNoRepeatPokers(ArrayList<Poker> outPokers) {

        ArrayList<Poker> noRepeatPokers = new ArrayList<>(new HashSet<>(outPokers));
        noRepeatPokers.sort(Comparator.comparingInt(Poker::getPower));

        return noRepeatPokers;
    }

    public static HashMap<Poker, Integer> getPokersCountMap(ArrayList<Poker> outPokers) {

        HashMap<Poker, Integer> pokersCountMap = new HashMap<>();

        for (Poker poker : outPokers) {
            int count = 1;
            if (pokersCountMap.get(poker) != null) {
                count = pokersCountMap.get(poker) + 1;
            }
            pokersCountMap.put(poker, count);
        }
        return pokersCountMap;

    }

    public static ArrayList<Poker> charToPokers(String memberOut) {
        ArrayList<Poker> outPokers = new ArrayList<>();

        String temp = memberOut.toUpperCase();

        if (memberOut.contains("10")) {
            temp = memberOut.replaceAll("10", "I").toUpperCase();
        }

        for (char c : temp.toCharArray()) {
            outPokers.add(new Poker(c, Pokers.simpleSymbols.indexOf(c) + 1));
        }


        return outPokers;

    }


    public static boolean outIsPoker(String memberOut) {

        String temp = memberOut.toUpperCase();

        if (memberOut.contains("10")) {
            temp = memberOut.replaceAll("10", "I").toUpperCase();
        }

        for (char c : temp.toCharArray()) {
            if (!Pokers.simpleSymbols.contains(c)) {
                return false;
            }
        }

        return true;

    }

    public static boolean youHaveThisPokers(String memberOut, ArrayList<Poker> handPokers) {

        ArrayList<Poker> temp = (ArrayList<Poker>) handPokers.clone();
        for (Poker poker : charToPokers(memberOut)) {
            if (!temp.remove(poker)) {
                return false;
            }
        }
        return true;
    }

}
