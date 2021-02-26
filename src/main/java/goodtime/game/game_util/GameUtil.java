package goodtime.game.game_util;

public class GameUtil {

    public static final String INITIALIZATION_PHASE = "Initialization";
    public static final String GAMING_PHASE = "Gaming";
    public static final String END_PHASE = "End";

    public static boolean isJoinCommand(String membersIn) {
        switch (membersIn) {
            case ".上桌":
            case ".fork table":
            case ".sz":
            case "斗地主":
                return true;
            default:
                return false;
        }
    }

    public static boolean isLeaveCommand(String membersIn) {
        switch (membersIn) {
            case ".下桌":
            case ".xz":
                return true;
            default:
                return false;
        }
    }

}
