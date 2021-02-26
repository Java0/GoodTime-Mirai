package goodtime.game.poker_game;

public class Poker {

    private char color;

    private char mark;

    private int power;

    public Poker(char mark, int power) {
        this.mark = mark;
        this.power = power;
    }

    public Poker(char mark, char color, int power) {
        this.mark = mark;
        this.power = power;
        this.color = color;
    }


    public char getMark() {
        return mark;
    }


    public int getPower() {
        return power;
    }

    public char getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poker poker = (Poker) o;

        if (mark != poker.mark) return false;
        return power == poker.power;
    }

    @Override
    public int hashCode() {
        int result = mark;
        result = 31 * result + power;
        return result;
    }

    public String toString() {
        return "" + mark;
    }


}
