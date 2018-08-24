package common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IsoChat {
    static Map<UUID, Boolean> isoChatActivated = new HashMap<>();

    public static void toggle(UUID uuid) {
        isoChatActivated.put(uuid, !isActivated(uuid));
    }

    public static Boolean isActivated(UUID uuid) {
        return isoChatActivated.getOrDefault(uuid, false);
    }
}
