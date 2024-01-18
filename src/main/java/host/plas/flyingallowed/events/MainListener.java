package host.plas.flyingallowed.events;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class MainListener implements Listener {
    public MainListener() {
        Bukkit.getPluginManager().registerEvents(this, FlyingAllowed.getInstance());

        MessageUtils.logInfo("Registered MainListener!");
    }
}
