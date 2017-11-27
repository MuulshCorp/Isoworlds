package common;

public class Logger {
    private final String type;

    public Logger(String type) {
        this.type = type;
    }

    void log(String message) {
        if (this.type.equals("sponge")) {
            sponge.Utils.IsoworldsUtils.cm(message);
        } else if (this.type.equals("bukkit")) {
            bukkit.Utils.IsoworldsUtils.cm("[IsoWorlds]: " + message);
        }
    }
}
