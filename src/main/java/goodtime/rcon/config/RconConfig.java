package goodtime.rcon.config;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RconConfig {

    private static final String CONFIG_PATH = "config/Rcon-Config.json";

    private static final Gson GSON = new Gson();

    public String hostname = "";
    public int port;
    public String password = "";

    private static RconConfig INSTANCE;

    public static Path getPath() {
        return Path.of(CONFIG_PATH);
    }

    public static RconConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;

    }

    private RconConfig() {
    }

    public static RconConfig load() {

        Path config_Path = getPath();

        if (Files.exists(config_Path)) {
            try {
                return GSON.fromJson(Files.readString(config_Path), RconConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            RconConfig rconConfig = new RconConfig();
            rconConfig.save();
            return rconConfig;
        }

        return null;

    }

    public void save() {
        try {
            Files.writeString(getPath(), GSON.toJson(this), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
