package host.plas.flyingallowed.data;

import gg.drak.thebase.objects.SingleSet;
import host.plas.bou.commands.Sender;
import host.plas.bou.scheduling.TaskManager;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.*;
import host.plas.flyingallowed.config.WorldConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter @Setter
public class PlayerMoveData {
    @NonNull
    private PlayerMoveEvent event;

    @NonNull
    private Location from;
    @NonNull
    private Location to;
    @NonNull
    private World fromWorld;
    @NonNull
    private World toWorld;

    @NonNull
    private Player player;

    public PlayerMoveData(@NonNull PlayerMoveEvent event, @NonNull Location from, @NonNull Location to,
                          @NonNull World fromWorld, @NonNull World toWorld, @NonNull Player player) {
        this.event = event;
        this.from = from;
        this.to = to;
        this.fromWorld = fromWorld;
        this.toWorld = toWorld;
        this.player = player;
    }

    public boolean isSameWorld() {
        return fromWorld.equals(toWorld);
    }

    public boolean isDifferentWorld() {
        return ! isSameWorld();
    }

    public Location getTopLocation() {
        Location topLocation = to.clone();

        int iterations = 0;
        while (isTopable(topLocation)) {
            topLocation = topLocation.subtract(0, 1, 0);

            iterations ++;
        }

        if (iterations > 0) {
            while (! isTopable(topLocation)) {
                topLocation = topLocation.add(0, 1, 0);
            }
        } else {
            topLocation = topLocation.add(0, 1, 0);
        }

        return topLocation;
    }

    public static boolean isTopable(Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        return location.getBlock().isEmpty() & location.getBlockY() < world.getMaxHeight() && location.getBlockY() > world.getMinHeight();
    }

    public boolean checkBypassPermissionOn() {
        return FlyingAllowed.getMainConfig().getAutoToggleOnEnabled() && ! player.hasPermission(FlyingAllowed.getMainConfig().getAutoToggleOnBypassPerm());
    }

    public boolean checkBypassPermissionOff() {
        return FlyingAllowed.getMainConfig().getAutoToggleOffEnabled() && ! player.hasPermission(FlyingAllowed.getMainConfig().getAutoToggleOffBypassPerm());
    }

    public boolean isSoftBypassing() {
        return player.hasPermission(FlyingAllowed.getMainConfig().getSoftBypassingPerm()) && FlyingAllowed.getMainConfig().isBypassing(player);
    }

    public boolean checkSoftBypassPermissionOn() {
        return FlyingAllowed.getMainConfig().getAutoToggleOnEnabled() && ! isSoftBypassing();
    }

    public boolean checkSoftBypassPermissionOff() {
        return FlyingAllowed.getMainConfig().getAutoToggleOffEnabled() && ! isSoftBypassing();
    }

    public void checkPermission() {
        SingleSet<Boolean, Boolean> ss = isWorldDisabled();
        boolean worldAllow = ss.getKey();
        boolean fullDisable = ss.getValue();

        if (! worldAllow) {
            if (! fullDisable) {
                return;
            } else {
                teleportTopLocation();
                getPlayer().setFlying(false);
                getPlayer().setAllowFlight(false);
                return;
            }
        }

        Optional<FlightAbility> cached = MoveDataCache.get(player);
        if (cached.isPresent()) return;

        GameMode gameMode = player.getGameMode();
        if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) return;

        try {
            SingleSet<FlightAbility, FlightExtent> set = CompatManager.getFlyingAllowed(this);

            FlightAbility ability = set.getKey();
            FlightExtent extent = set.getValue();

            if (MoveDataCache.checkEquals(player, ability)) return; // Before setting.
            MoveDataCache.cache(getPlayer(), ability);

            if (ability == FlightAbility.ABLE_TO_FLY || ability == FlightAbility.UNABLE_TO_FLY || ability == FlightAbility.NO_CLAIM) {
                if (player.hasPermission(FlyingAllowed.getMainConfig().getLandsToggleOnPerm())) {
                    if (checkFlyAndIsHandled(ability, extent, FlightFlag.TOGGLE_ALLOWED)) return;
                } else if (checkFlyAndIsHandled(ability, extent)) return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (! player.getAllowFlight() && hasPermission() && checkBypassPermissionOn() && checkSoftBypassPermissionOn()) {
            player.setAllowFlight(true);

            Sender sender = new Sender(player);
            sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOnWorldMessage());
        } else if (player.getAllowFlight() && ! hasPermission() && checkBypassPermissionOff() && checkSoftBypassPermissionOff()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            teleportTopLocation();

            Sender sender = new Sender(player);
            sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOffWorldMessage());
        }
    }

    public SingleSet<Boolean, Boolean> isWorldDisabled() {
        if (getToWorld() == null) return new SingleSet<>(false, false); // probably not possible

        String worldName = getToWorld().getName();

        WorldConfig config = FlyingAllowed.getWorldConfig();
        ConcurrentSkipListSet<String> worlds = config.getDisabledWorlds();
        boolean isWhitelist = config.isWhitelist();
        boolean isFullDisable = config.isFullDisable();

        boolean drop = false;
        if (isWhitelist) { // whitelist = drop if in list
            drop = worlds.contains(worldName);
        } else {
            drop = ! worlds.contains(worldName);
        }

        return new SingleSet<>(drop, isFullDisable);
    }

    public String getWorldPermission() {
        return "flyingallowed.allow." + toWorld.getName();
    }

    public boolean hasPermission() {
        return player.hasPermission(getWorldPermission());
    }

    public boolean checkFlyAndIsHandled(FlightAbility ability, FlightExtent extent, FlightFlag... flags) {
        return checkFlyAndIsHandled(ability, extent, new ArrayList<>(List.of(flags)));
    }

    public boolean checkFlyAndIsHandled(FlightAbility ability, FlightExtent extent, List<FlightFlag> flags) {
        if (ability == FlightAbility.ABLE_TO_FLY) {
            if (flags.contains(FlightFlag.TOGGLE_ALLOWED)) {
                if (! player.getAllowFlight() && checkBypassPermissionOn() && checkSoftBypassPermissionOn()) {
                    player.setAllowFlight(true);

                    Sender sender = new Sender(player);
                    if (extent == FlightExtent.WORLDGUARD) {
                        sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOnRegionMessage());
                    } else {
                        sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOnClaimMessage());
                    }
                }
            }
            return true;
        } else if (ability == FlightAbility.UNABLE_TO_FLY || ability == FlightAbility.NO_CLAIM) {
            if (player.getAllowFlight() && checkBypassPermissionOff() && checkSoftBypassPermissionOff()) {
                player.setFlying(false);
                player.setAllowFlight(false);
                teleportTopLocation();

                Sender sender = new Sender(player);
                if (extent == FlightExtent.WORLDGUARD) {
                    sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOffRegionMessage());
                } else {
                    sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOffClaimMessage());
                }
            }
            return true;
        }

        return false;
    }

    public void teleportTopLocation() {
        if (getIsShouldTeleport()) {
            TaskManager.teleport(player, getTopLocation());

            setYVelocity0();

            setInvulnerableForTicks();
        }
    }

    public boolean getIsShouldTeleport() {
        if (! FlyingAllowed.getMainConfig().getAutoToggleOffTeleportEnabled()) return false;

        if (player.isGliding()) return false;

        if (FlyingAllowed.getMainConfig().getAutoToggleOffTeleportCheckIfOnGround()) {
            if (player.isOnGround()) return false;
        }

        double distance = getDistanceFromTopLocation();
        if (distance <= 1.5) return false;

        return true;
    }

    public double getDistanceFromTopLocation() {
        return getTopLocation().distance(to);
    }

    public void setYVelocity0() {
        if (player.isGliding()) return;

        if (to.getY() < from.getY()) {
            Vector velocity = player.getVelocity().setY(0);
            player.setVelocity(velocity);
        }
    }

    public void setInvulnerableForTicks() {
        if (player.isGliding()) return;

        long invulnerableTicks = FlyingAllowed.getMainConfig().getAutoToggleOffTeleportMakeInvulnerableTicks();
        if (invulnerableTicks > 0) {
            player.setNoDamageTicks((int) invulnerableTicks);
            player.setInvulnerable(true);
            TaskManager.runTaskLater(player, () -> {
                player.setInvulnerable(false);
            }, invulnerableTicks);
        }
    }

    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    public boolean hasFlyInClaimPermission() {
        return hasPermission(FlyingAllowed.getMainConfig().getFlyInClaimsPermission()) ||
                hasPermission("flyingallowed.in.lands"); // For old versions of FlyingAllowed
    }

    public boolean hasFlyInRegionPermission() {
        return hasPermission(FlyingAllowed.getMainConfig().getFlyInRegionsPermission());
    }

    public static String getAllClaimsPermission() {
        return FlyingAllowed.getMainConfig().getFlyInClaimsAllPermission();
    }

    public boolean hasAllClaimsPermission() {
        return hasPermission(getAllClaimsPermission());
    }
}
