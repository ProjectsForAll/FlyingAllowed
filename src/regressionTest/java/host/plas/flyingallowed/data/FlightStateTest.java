package host.plas.flyingallowed.data;

import gg.drak.thebase.objects.SingleSet;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

public final class FlightStateTest {
    public static void main(String[] args) {
        FakePlayer allowed = new FakePlayer(true, false);
        expect(false, FlightState.enable(allowed.player), "already allowed to fly");
        expect(0, allowed.enableCalls, "no redundant setter for allowed flight");
        checkClaimHandler(allowed);

        FakePlayer flying = new FakePlayer(false, true);
        expect(false, FlightState.enable(flying.player), "already actively flying");
        expect(0, flying.enableCalls, "no redundant setter for active flight");
        checkClaimHandler(flying);

        FakePlayer disabled = new FakePlayer(false, false);
        expect(true, FlightState.enable(disabled.player), "enable disabled flight");
        for (int i = 0; i < 100; i++) {
            expect(false, FlightState.enable(disabled.player), "repeated movement lookup");
        }
        expect(1, disabled.enableCalls, "only one flight toggle");
        disabled.allowFlight = false;
        expect(true, FlightState.enable(disabled.player), "enable again after flight turns off");
        expect(2, disabled.enableCalls, "second real flight toggle");
        checkLookupRefreshes();
        System.out.println("Flight state regression checks passed.");
    }

    private static void checkLookupRefreshes() {
        MoveDataCache.flushAll();
        FakePlayer player = new FakePlayer(false, false);
        World world = fakeWorld();
        Location location = new Location(world, 0, 64, 0);
        PlayerMoveEvent event = new PlayerMoveEvent(player.player, location, location);
        LookupData data = new LookupData(event, location, world, player.player);
        data.checkPermission();
        expect(1, player.enableCalls, "first allowed lookup enables flight");

        for (int i = 0; i < 100; i++) {
            // Reproduce an allow-flight state being reset between movement lookups.
            player.allowFlight = false;
            MoveDataCache.flush();
            data.checkPermission();
            data.checkPermission();
        }
        expect(1, player.enableCalls, "cache refresh never reapplies the same allowed state");
        expect(1, data.handledCalls, "no repeated toggle handler or message path");
        expect(101, data.lookupCalls, "unchanged result still repopulates the short cache");

        data.ability = FlightAbility.NO_CLAIM;
        MoveDataCache.flush();
        data.checkPermission();
        data.ability = FlightAbility.ABLE_TO_FLY;
        MoveDataCache.flush();
        data.checkPermission();
        expect(2, player.enableCalls, "leaving and reentering enables flight again");

        MoveDataCache.remove(player.player);
        player.allowFlight = false;
        data.checkPermission();
        expect(3, player.enableCalls, "command or reconnect invalidation allows a fresh toggle");

        data.ability = FlightAbility.UNABLE_TO_FLY;
        MoveDataCache.flush();
        data.checkPermission();
        int before = data.handledCalls;
        player.allowFlight = true;
        MoveDataCache.flush();
        data.checkPermission();
        expect(before + 1, data.handledCalls, "unchanged denial still enforces flight restriction");
        expect(false, player.allowFlight, "denied flight stays disabled");
        MoveDataCache.flushAll();
    }

    private static World fakeWorld() {
        return (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[]{World.class},
                (proxy, method, args) -> { throw new AssertionError("Unexpected world lookup"); });
    }

    private static final class LookupData extends PlayerMoveData {
        private FlightAbility ability = FlightAbility.ABLE_TO_FLY;
        private int lookupCalls;
        private int handledCalls;

        private LookupData(PlayerMoveEvent event, Location location, World world, Player player) {
            super(event, location, location, world, world, player);
        }

        @Override
        public SingleSet<Boolean, Boolean> isWorldDisabled() {
            return new SingleSet<>(true, false);
        }

        @Override
        protected SingleSet<FlightAbility, FlightExtent> lookupFlightAbility() {
            lookupCalls++;
            return new SingleSet<>(ability, FlightExtent.LANDS);
        }

        @Override
        protected boolean hasAutoToggleOnPermission() {
            return true;
        }

        @Override
        public boolean checkFlyAndIsHandled(FlightAbility ability, FlightExtent extent, List<FlightFlag> flags) {
            handledCalls++;
            if (ability == FlightAbility.ABLE_TO_FLY) {
                FlightState.enable(getPlayer());
            } else {
                getPlayer().setAllowFlight(false);
            }
            return true;
        }
    }

    private static void expect(Object expected, Object actual, String scenario) {
        if (! expected.equals(actual)) {
            throw new AssertionError(scenario + ": expected " + expected + ", got " + actual);
        }
    }

    private static void checkClaimHandler(FakePlayer player) {
        World world = fakeWorld();
        Location location = new Location(world, 0, 64, 0);
        PlayerMoveEvent event = new PlayerMoveEvent(player.player, location, location);
        PlayerMoveData data = new PlayerMoveData(event, location, location, world, world, player.player);
        for (FlightExtent extent : new FlightExtent[]{FlightExtent.LANDS, FlightExtent.WORLDGUARD}) {
            for (int i = 0; i < 100; i++) {
                // Config and messages are deliberately uninitialized: existing flight must return first.
                expect(true, data.checkFlyAndIsHandled(FlightAbility.ABLE_TO_FLY, extent,
                        FlightFlag.TOGGLE_ALLOWED), "already enabled flight is handled");
            }
        }
        expect(0, player.enableCalls, "claim and region checks never toggle existing flight");
    }

    private static final class FakePlayer {
        private boolean allowFlight;
        private final boolean flying;
        private int enableCalls;
        private final UUID id = UUID.randomUUID();
        private final Player player;

        private FakePlayer(boolean allowFlight, boolean flying) {
            this.allowFlight = allowFlight;
            this.flying = flying;
            player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "getUniqueId": return id;
                            case "getGameMode": return GameMode.SURVIVAL;
                            case "getAllowFlight": return this.allowFlight;
                            case "isFlying": return this.flying;
                            case "setAllowFlight":
                                this.allowFlight = (boolean) args[0];
                                if (this.allowFlight) enableCalls++;
                                return null;
                            default: throw new AssertionError("Unexpected player call: " + method.getName());
                        }
                    });
        }
    }
}
