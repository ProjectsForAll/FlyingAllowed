package host.plas.flyingallowed.config;

import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;
import host.plas.flyingallowed.FlyingAllowed;

public class MessageConfig extends SimpleConfiguration {
    public MessageConfig() {
        super("messages.yml", FlyingAllowed.getInstance(), true);
    }

    @Override
    public void init() {
        getToggleOnWorldMessage();
        getToggleOffWorldMessage();

        getToggleOnClaimMessage();
        getToggleOffClaimMessage();

        getToggleOnRegionMessage();
        getToggleOffRegionMessage();

        getPlaceholderEnabled();
        getPlaceholderDisabled();

        getCommandFlySelfSelfMessage();
        getCommandFlyOtherSelfMessage();

        getCommandSetFlySelfSelfMessage();
        getCommandSetFlyOtherSelfMessage();
    }

    public String getToggleOnWorldMessage() {
        reloadResource();

        return getOrSetDefault("toggle.on.world.message", "&eToggling &bflight &aon &eas you &ahave &epermission to &dfly &ein this &bworld&8!");
    }

    public String getToggleOffWorldMessage() {
        reloadResource();

        return getOrSetDefault("toggle.off.world.message", "&eToggling &bflight &coff &eas you &cdo not have &epermission to &dfly &ein this &bworld&8!");
    }

    public String getToggleOnClaimMessage() {
        reloadResource();

        return getOrSetDefault("toggle.on.claim.message", "&eToggling &bflight &aon &eas you are &aable to &dfly &ein this &bclaim&8!");
    }

    public String getToggleOnRegionMessage() {
        reloadResource();

        return getOrSetDefault("toggle.on.region.message", "&eToggling &bflight &aon &eas you are &aable to &dfly &ein this &bregion&8!");
    }

    public String getToggleOffClaimMessage() {
        reloadResource();

        return getOrSetDefault("toggle.off.claim.message", "&eToggling &bflight &coff &eas you are &cunable to &dfly &ein this &bclaim&8!");
    }

    public String getToggleOffRegionMessage() {
        reloadResource();

        return getOrSetDefault("toggle.off.region.message", "&eToggling &bflight &coff &eas you are &cunable to &dfly &ein this &bregion&8!");
    }

    public String getPlaceholderEnabled() {
        reloadResource();

        return getOrSetDefault("placeholders.enabled", "&aenabled");
    }

    public String getPlaceholderDisabled() {
        reloadResource();

        return getOrSetDefault("placeholders.disabled", "&cdisabled");
    }

    public String getCommandFlySelfSelfMessage() {
        reloadResource();

        return getOrSetDefault("commands.fly.self.self-message", "&eYou have &r%status% &bflight&8!");
    }

    public String getCommandFlyOtherSelfMessage() {
        reloadResource();

        return getOrSetDefault("commands.fly.other.self-message", "&eYou have &r%status% &bflight &efor &c%player_name%&8!");
    }

    public String getCommandSetFlySelfSelfMessage() {
        reloadResource();

        return getOrSetDefault("commands.set-fly.self.self-message", "&eYou have set your &bflight &eto &r%status%&8!");
    }

    public String getCommandSetFlyOtherSelfMessage() {
        reloadResource();

        return getOrSetDefault("commands.set-fly.other.self-message", "&eYou have set &c%player_name%&&7'&es &bflight &eto &r%status%&8!");
    }
}
