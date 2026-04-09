package host.plas.flyingallowed.config;

import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;
import host.plas.flyingallowed.FlyingAllowed;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

public class WorldConfig extends SimpleConfiguration {
    public WorldConfig() {
        super("world-config.yml", FlyingAllowed.getInstance(), true);
    }

    @Override
    public void init() {
        getDisabledWorlds();

        isWhitelist();
    }

    public ConcurrentSkipListSet<String> getDisabledWorlds() {
        reloadResource();

        return new ConcurrentSkipListSet<>(getOrSetDefault("disabled-worlds", new ArrayList<>()));
    }

    public void setDisabledWorlds(ConcurrentSkipListSet<String> worlds) {
        write("disabled-worlds", new ArrayList<>(worlds));
    }

    public void addDisabledWorld(String world) {
        ConcurrentSkipListSet<String> worlds = getDisabledWorlds();
        worlds.add(world);
        setDisabledWorlds(worlds);
    }

    public void removeDisabledWorld(String world) {
        ConcurrentSkipListSet<String> worlds = getDisabledWorlds();
        worlds.remove(world);
        setDisabledWorlds(worlds);
    }

    public boolean isWhitelist() {
        reloadResource();

        return getResource().getOrSetDefault("is-whitelist", false);
    }

    public void setWhitelist(boolean whitelist) {
        write("is-whitelist", whitelist);
    }

    public boolean isFullDisable() {
        reloadResource();

        return getResource().getOrSetDefault("is-full-disable", false);
    }

    public void setFullDisable(boolean fullDisable) {
        write("is-full-disable", fullDisable);
    }
}
