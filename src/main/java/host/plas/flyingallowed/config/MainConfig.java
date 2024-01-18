package host.plas.flyingallowed.config;

import host.plas.flyingallowed.FlyingAllowed;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", FlyingAllowed.getInstance(), false);
    }

    @Override
    public void init() {

    }
}
