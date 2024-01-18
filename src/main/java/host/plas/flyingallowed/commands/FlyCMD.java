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
import java.util.stream.Collectors;

public class FlyCMD extends SimplifiedCommand {
    public FlyCMD() {
        super("fly", FlyingAllowed.getInstance());
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

        boolean current = target.getAllowFlight();
        target.setAllowFlight(! current);

        if (sender.equals(target)) {
            commandContext.getSender().sendMessage("&eYou have " + (current ? "&cdisabled" : "&aenabled") + " &eflying!");
        } else {
            commandContext.getSender().sendMessage("&eYou have " + (current ? "&cdisabled" : "&aenabled") + " &eflying for &a" + target.getName() + "&e!");
        }

        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        ConcurrentSkipListSet<String> tabComplete = new ConcurrentSkipListSet<>();

        CommandSender sender = commandContext.getSender().getCommandSender().orElse(null);
        if (sender == null) return tabComplete;

        if (! sender.hasPermission("flyingallowed.command.fly.others")) return tabComplete;

        if (commandContext.getArgs().size() <= 1) {
            String[] args = commandContext.getArgs().stream().map(CommandArgument::getContent).toList().toArray(new String[0]);

            tabComplete.addAll(StringUtils.getAsCompletion(args, Bukkit.getOnlinePlayers().stream().map(Player::getName).toList().toArray(new String[0])));
        }

        return tabComplete;
    }
}
