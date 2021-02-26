package goodtime.game.poker_game.poker_game_util;

import java.util.ArrayList;
import java.util.Arrays;

public class Pokers {

    public static char[] simpleColors = {'♥', '♠', '♦', '♣'};

    public static final ArrayList<Character> simpleSymbols = new ArrayList(Arrays.asList('3', '4', '5', '6', '7', '8', '9', 'I', 'J', 'Q', 'K', 'A', '2', '鬼', '王'));

    public static final int SIMPLE = 0;
    public static final int WITH_COLOR = 1;
    public static final int NO_JOKER = 2;


}
