package common;

public class Manager {
    public static MainInterface instance;

    public static MainInterface getInstance() {
        return instance;
    }
}
