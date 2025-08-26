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

    public static void remove(Player player) {
        cache.remove(player.getUniqueId().toString());
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
        if (! contains(player)) return false;

        return get(player).orElse(FlightAbility.UNABLE_TO_FLY) == ability;
    }
}
