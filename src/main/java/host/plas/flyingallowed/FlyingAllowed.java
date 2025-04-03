package host.plas.flyingallowed;

import host.plas.bou.BetterPlugin;
import host.plas.flyingallowed.commands.BypassCMD;
import host.plas.flyingallowed.commands.FlyCMD;
import host.plas.flyingallowed.commands.SetFlyCMD;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.wg.WGHeld;
import host.plas.flyingallowed.compat.plugins.wg.WGHolder;
import host.plas.flyingallowed.config.MainConfig;
import host.plas.flyingallowed.config.MessageConfig;
import host.plas.flyingallowed.data.FlightWorlds;
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
    private FlightWorlds flightWorlds;

    @Getter @Setter
    private FlyCMD flyCMD;
    @Getter @Setter
    private SetFlyCMD setFlyCMD;
    @Getter @Setter
    private BypassCMD bypassCMD;

    public FlyingAllowed() {
        super();
    }

    @Override
    public void onBaseLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            getLogger().info("WorldGuard found, registering flag...");
            WGHolder.registerFlightFlag();
        }
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setInstance(this);

        setMainConfig(new MainConfig());

        setFlightWorlds(new FlightWorlds());

        setFlyCMD(new FlyCMD());
        setSetFlyCMD(new SetFlyCMD());
        setBypassCMD(new BypassCMD());

        CompatManager.init();
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
        getFlightWorlds().unregister();
    }
}
