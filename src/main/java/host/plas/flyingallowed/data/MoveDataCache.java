package host.plas.flyingallowed.data;

import host.plas.flyingallowed.timers.CacheTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class MoveDataCache {
    @Getter @Setter
    private static ConcurrentSkipListMap<String, FlightAbility> cache = new ConcurrentSkipListMap<>();
    @Getter @Setter
    private static ConcurrentSkipListMap<String, FlightAbility> cacheLong = new ConcurrentSkipListMap<>();

    @Getter @Setter
    private static CacheTimer timer;

    public static void init() {
        setTimer(new CacheTimer());
    }

    public static void onDisable() {
        if (getTimer() != null) getTimer().cancel();

        setTimer(null);

        flushAll();
    }

    public static void flushAll() {
        flush();

        getCacheLong().clear();
    }

    public static void cache(Player player, FlightAbility ability) {
        if (contains(player)) return;

        cache.put(player.getUniqueId().toString(), ability);
        cacheLong.put(player.getUniqueId().toString(), ability);
    }

    /**
     * Forces a fresh lookup on the player's next move while keeping their last known verdict,
     * so an unchanged {@link FlightAbility#ABLE_TO_FLY} does not re-enable flight the player
     * (or a staff member) just turned off.
     */
    public static void invalidate(Player player) {
        cache.remove(player.getUniqueId().toString());
    }

    /**
     * Forgets the player entirely; the next lookup is treated as an eligibility change and may
     * auto-enable flight again.
     */
    public static void remove(Player player) {
        cache.remove(player.getUniqueId().toString());
        cacheLong.remove(player.getUniqueId().toString());
    }

    public static void flush() {
        cache.clear();
    }

    public static boolean contains(Player player) {
        return cache.containsKey(player.getUniqueId().toString());
    }

    public static Optional<FlightAbility> get(Player player) {
        return Optional.ofNullable(cache.get(player.getUniqueId().toString()));
    }

    public static boolean checkEquals(Player player, FlightAbility ability) {
        return cacheLong.get(player.getUniqueId().toString()) == ability;
    }
}
