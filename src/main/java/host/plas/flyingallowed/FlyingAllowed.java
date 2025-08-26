package host.plas.flyingallowed;

import host.plas.bou.BetterPlugin;
import host.plas.flyingallowed.commands.BypassCMD;
import host.plas.flyingallowed.commands.DisabledFlyingWorldsCMD;
import host.plas.flyingallowed.commands.FlyCMD;
import host.plas.flyingallowed.commands.SetFlyCMD;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.wg.WGHolder;
import host.plas.flyingallowed.config.MainConfig;
import host.plas.flyingallowed.config.MessageConfig;
import host.plas.flyingallowed.config.WorldConfig;
import host.plas.flyingallowed.data.FlightWorlds;
import host.plas.flyingallowed.data.MoveDataCache;
import host.plas.flyingallowed.timers.CacheTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter @Setter
public final class FlyingAllowed extends BetterPlugin {
    @Getter @Setter
    private static FlyingAllowed instance;

    @Getter @Setter
    private static MainConfig mainConfig;
    @Getter @Setter
    private static MessageConfig messageConfig;
    @Getter @Setter
    private static WorldConfig worldConfig;

    @Getter @Setter
    private FlightWorlds flightWorlds;

    @Getter @Setter
    private FlyCMD flyCMD;
    @Getter @Setter
    private SetFlyCMD setFlyCMD;
    @Getter @Setter
    private BypassCMD bypassCMD;
    @Getter @Setter
    private DisabledFlyingWorldsCMD disabledFlyingWorldsCMD;

    public FlyingAllowed() {
        super();
    }

    @Override
    public void onBaseLoad() {
        setInstance(this);

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            logInfo("WorldGuard found, registering region flag...");
            WGHolder.registerFlightFlag();
        }
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setMainConfig(new MainConfig());
        setMessageConfig(new MessageConfig());
        setWorldConfig(new WorldConfig());

        setFlightWorlds(new FlightWorlds());

        setFlyCMD(new FlyCMD());
        setSetFlyCMD(new SetFlyCMD());
        setBypassCMD(new BypassCMD());
        setDisabledFlyingWorldsCMD(new DisabledFlyingWorldsCMD());

        CompatManager.init();

        MoveDataCache.init();
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
        MoveDataCache.onDisable();

        getFlightWorlds().unregister();

        CompatManager.onDisable();
    }
}
