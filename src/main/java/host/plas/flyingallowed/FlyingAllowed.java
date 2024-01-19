package host.plas.flyingallowed;

import host.plas.flyingallowed.commands.FlyCMD;
import host.plas.flyingallowed.commands.SetFlyCMD;
import host.plas.flyingallowed.config.MainConfig;
import host.plas.flyingallowed.data.FlightWorlds;
import io.streamlined.bukkit.PluginBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentSkipListMap;

@Getter @Setter
public final class FlyingAllowed extends PluginBase {
    @Getter @Setter
    private static FlyingAllowed instance;
    @Getter @Setter
    private static MainConfig mainConfig;

    @Getter @Setter
    private FlightWorlds flightWorlds;

    @Getter @Setter
    private FlyCMD flyCMD;
    @Getter @Setter
    private SetFlyCMD setFlyCMD;

    public FlyingAllowed() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setInstance(this);

        setMainConfig(new MainConfig());

        setFlightWorlds(new FlightWorlds());

        setFlyCMD(new FlyCMD());
        getFlyCMD().register();
        setSetFlyCMD(new SetFlyCMD());
        getSetFlyCMD().register();
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
        getFlightWorlds().unregister();
    }

    /**
     * Get a map of online players.
     * Sorted by player name.
     * @return A map of online players sorted by player name.
     */
    public ConcurrentSkipListMap<String, Player> getOnlinePlayers() {
        ConcurrentSkipListMap<String, Player> onlinePlayers = new ConcurrentSkipListMap<>();

        for (Player player : getServer().getOnlinePlayers()) {
            onlinePlayers.put(player.getName(), player);
        }

        return onlinePlayers;
    }
}
