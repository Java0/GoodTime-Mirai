package goodtime.rcon;

import goodtime.rcon.config.RconConfig;
import org.glavo.rcon.AuthenticationException;
import org.glavo.rcon.Rcon;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class RconSupport {

    private static Rcon rcon;

    private static final RconConfig config = RconConfig.getInstance();

    public static boolean connectToServer() {

        try {
            rcon = new Rcon(config.hostname, config.port, config.password.getBytes());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String runCommand(@NotNull String command, String sender) {
        try {
            if (command.contains("/list")) {
                return rcon.command("list");
            } else if (command.contains("/say")) {

                try {
                    rcon.connect(config.hostname, config.port, config.password.getBytes());
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }


                StringBuilder text = new StringBuilder("From Group Message: " + sender + '：' + command.replace("/say ", ""));

                rcon.command("say" + ' ' + text.toString());

                return "您向服务器发送了：" + text.toString().replace("From Group Message: " + sender + '：', "");

            } else if (command.contains("/forge tps")) {

                return rcon.command("forge tps");

            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
