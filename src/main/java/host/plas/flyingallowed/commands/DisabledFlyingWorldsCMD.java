package host.plas.flyingallowed.commands;

import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.flyingallowed.FlyingAllowed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentSkipListSet;

public class DisabledFlyingWorldsCMD extends SimplifiedCommand {
    public DisabledFlyingWorldsCMD() {
        super("disabled-flying-worlds", FlyingAllowed.getInstance());
    }

    @Override
    public boolean command(CommandContext ctx) {
        if (ctx.isConsole()) {
            ctx.sendMessage("&cOnly players can use this command.");
            return false;
        }

        Player player = ctx.getSender().getPlayer().orElse(null);
        if (player == null) {
            ctx.sendMessage("&cAn error occurred while trying to execute this command.");
            return false;
        }

        if (! ctx.isArgUsable(0)) {
            ctx.sendMessage("&cUsage: /dfw <add|remove|list> [world]");
            return false;
        }

        String action = ctx.getArg(0).getContent().toLowerCase();
        switch (action) {
            case "add":
                if (! ctx.isArgUsable(1)) {
                    ctx.sendMessage("&cUsage: /dfw add <world>");
                    return false;
                }

                String worldToAdd = ctx.getStringArg(1);
                FlyingAllowed.getWorldConfig().addDisabledWorld(worldToAdd);

                ctx.sendMessage("&eWorld &a" + worldToAdd + " &ehas been added to the disabled flying worlds list.");
                return true;
            case "remove":
                if (! ctx.isArgUsable(1)) {
                    ctx.sendMessage("&cUsage: /dfw remove <world>");
                    return false;
                }

                String worldToRemove = ctx.getStringArg(1);
                FlyingAllowed.getWorldConfig().removeDisabledWorld(worldToRemove);
                ctx.sendMessage("&eWorld &a" + worldToRemove + " &ehas been removed from the disabled flying worlds list.");
                return true;
            case "list":
                ConcurrentSkipListSet<String> disabledWorlds = FlyingAllowed.getWorldConfig().getDisabledWorlds();
                if (disabledWorlds.isEmpty()) {
                    ctx.sendMessage("&eThere are no disabled flying worlds.");
                } else {
                    ctx.sendMessage("&eDisabled flying worlds: &a" + String.join("&7, &a", disabledWorlds));
                }
                return true;
            default:
                ctx.sendMessage("&cUsage: /dfw <add|remove|list> [world]");
                return false;
        }
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext ctx) {
        ConcurrentSkipListSet<String> completions = new ConcurrentSkipListSet<>();

        if (ctx.getArgCount() <= 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("list");
        } else if (ctx.getArgCount() == 2) {
            String firstArg = ctx.getArg(0).getContent().toLowerCase();
            if (firstArg.equals("remove")) {
                completions.addAll(FlyingAllowed.getWorldConfig().getDisabledWorlds());
            } else if (firstArg.equals("add")) {
                Bukkit.getWorlds().forEach(world -> completions.add(world.getName()));
            }
        }

        return completions;
    }
}
