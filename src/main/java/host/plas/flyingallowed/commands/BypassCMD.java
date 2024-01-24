package host.plas.flyingallowed.commands;

import host.plas.flyingallowed.FlyingAllowed;
import io.streamlined.bukkit.commands.CommandArgument;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.quaint.utils.StringUtils;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class BypassCMD extends SimplifiedCommand {
    public BypassCMD() {
        super("fly-bypass", FlyingAllowed.getInstance());
    }

    @Override
    public boolean command(CommandContext commandContext) {
        CommandSender sender = commandContext.getSender().getCommandSender().orElse(null);
        if (sender == null) return false;

        Player target = null;

        if (! commandContext.isArgUsable(0)) {
            if (! commandContext.isPlayer()) {
                commandContext.getSender().sendMessage("&cYou must be a player to use this command!");
                return false;
            }
            Optional<Player> player = commandContext.getSender().getPlayer();
            if (player.isEmpty()) {
                commandContext.getSender().sendMessage("&cYou must be a player to use this command!");
                return false;
            }
            target = player.get();
        } else {
            String arg = commandContext.getStringArg(0);

            Player player = Bukkit.getPlayer(arg);
            if (player == null) {
                commandContext.getSender().sendMessage("&cThat player is not online!");
                return false;
            }

            target = player;
        }

        if (target == null) {
            commandContext.getSender().sendMessage("&cThat player is not online!");
            return false;
        }

        boolean hasPermission = target.hasPermission(FlyingAllowed.getMainConfig().getSoftBypassingPerm());
        if (! hasPermission) {
            if (sender != target) {
                commandContext.getSender().sendMessage("&cThat player does not have permission to toggle flight bypassing!");
            } else {
                commandContext.getSender().sendMessage("&cYou do not have permission to toggle flight bypassing!");
            }

            return false;
        }

        if (FlyingAllowed.getMainConfig().isBypassing(target)) {
            FlyingAllowed.getMainConfig().removeBypassingPlayer(target);
        } else {
            FlyingAllowed.getMainConfig().addBypassingPlayer(target);
        }
        boolean newVal = FlyingAllowed.getMainConfig().isBypassing(target);

        if (sender != target) {
            commandContext.getSender().sendMessage("&eYou have " + (newVal ? "&aenabled" : "&cdisabled") + " &eflight bypassing for &a" + target.getName() + "&e!");
        } else {
            commandContext.getSender().sendMessage("&eYou have " + (newVal ? "&aenabled" : "&cdisabled") + " &eflight bypassing!");
        }

        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        ConcurrentSkipListSet<String> tabComplete = new ConcurrentSkipListSet<>();

        CommandSender sender = commandContext.getSender().getCommandSender().orElse(null);
        if (sender == null) return tabComplete;

        if (! sender.hasPermission("flyingallowed.command.fly-bypass.others")) return tabComplete;

        if (commandContext.getArgs().size() <= 1) {
            String[] args = commandContext.getArgs().stream().map(CommandArgument::getContent).toArray(String[]::new);

            tabComplete.addAll(StringUtils.getAsCompletion(args, Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)));
        }

        return tabComplete;
    }
}
