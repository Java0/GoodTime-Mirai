package goodtime.game;

import goodtime.game.poker_game.Poker;
import net.mamoe.mirai.contact.Member;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final Member sender;

    private boolean isLandlord;

    private final ArrayList<Poker> handPokers = new ArrayList<>();

    private int basicScore;

    public int getBasicScore() {
        return basicScore;
    }

    public void setBasicScore(int basicScore) {
        this.basicScore = basicScore;
    }

    public Player(Member sender) {
        this.sender = sender;
    }

    public Member getSender() {
        return sender;
    }

    public void setLandlord(boolean landlord) {
        isLandlord = landlord;
    }

    public ArrayList<Poker> getHandPokers() {
        return handPokers;
    }

    public void addHandPoker(Poker poker) {
        this.handPokers.add(poker);
    }

    public void addHandPokers(List<Poker> pokers) {
        this.handPokers.addAll(pokers);
    }

    public void removeHandPoker(Poker poker) {
        this.handPokers.remove(poker);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return sender.getNick().equals(player.sender.getNick());
    }

    @Override
    public int hashCode() {
        int result = sender.hashCode();
        result = 31 * result + (isLandlord ? 1 : 0);
        result = 31 * result + handPokers.hashCode();
        return result;
    }
}
