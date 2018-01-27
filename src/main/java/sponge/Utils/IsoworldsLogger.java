package sponge.Utils;


import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.IsoworldsSponge;

/**
 * Created by Edwin on 26/01/2018.
 */
public class IsoworldsLogger {
    private static final IsoworldsSponge instance = IsoworldsSponge.instance;

    public static void info(String s) {
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[").color(TextColors.WHITE)
                .append(Text.of(Text.builder("IW").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("] ").color(TextColors.WHITE)
                                .append(Text.builder(s).color(TextColors.GREEN).build())))))));
    }

    public static void warning(String s) {
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[").color(TextColors.WHITE)
                .append(Text.of(Text.builder("IW").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("] ").color(TextColors.WHITE)
                                .append(Text.builder(s).color(TextColors.GOLD).build())))))));
    }

    public static void severe(String s) {
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[").color(TextColors.WHITE)
                .append(Text.of(Text.builder("IW").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("] ").color(TextColors.WHITE)
                                .append(Text.builder(s).color(TextColors.RED).build())))))));
    }

    public static void tracking(String s) {
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[").color(TextColors.WHITE)
                .append(Text.of(Text.builder("IW").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("] ").color(TextColors.WHITE)
                                .append(Text.builder(s).color(TextColors.AQUA).build())))))));
    }

    public static void tag() {
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[IW]: ").color(TextColors.GOLD) + " _____          __          __           _      _      "));
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[IW]: ").color(TextColors.GOLD) + "|_   _|         \\ \\        / /          | |    | |     "));
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[IW]: ").color(TextColors.GOLD) + "  | |   ___   ___\\ \\  /\\  / /___   _ __ | |  __| | ___ "));
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[IW]: ").color(TextColors.GOLD) + "  | |  / __| / _ \\\\ \\/  \\/ // _ \\ | '__|| | / _` |/ __|"));
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[IW]: ").color(TextColors.GOLD) + " _| |_ \\__ \\| (_) |\\  /\\  /| (_) || |   | || (_| |\\__ \\"));
        instance.getGame().getServer().getConsole().sendMessage(Text.of(Text.builder("[IW]: ").color(TextColors.GOLD) + "|_____||___/ \\___/  \\/  \\/  \\___/ |_|   |_| \\__,_||___/"));
    }

}
