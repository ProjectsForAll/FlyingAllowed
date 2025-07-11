package host.plas.flyingallowed.commands;

import gg.drak.thebase.utils.StringUtils;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.bou.commands.CommandArgument;
import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

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

        boolean oldVal = target.getAllowFlight();
        boolean newVal = ! target.getAllowFlight();

        target.setAllowFlight(newVal);

        if (target.isFlying() && ! newVal) {
            target.setFlying(false);
        } else if (! target.isOnGround() && newVal) {
            target.setFlying(true);
        }

        if (sender.equals(target)) {
            commandContext.getSender().sendMessage(
                    FlyingAllowed.getMessageConfig().getCommandFlySelfSelfMessage()
                            .replace("%status%",
                                    newVal ? FlyingAllowed.getMessageConfig().getPlaceholderEnabled() : FlyingAllowed.getMessageConfig().getPlaceholderDisabled())
                            .replace("%old_status%",
                                     oldVal ? FlyingAllowed.getMessageConfig().getPlaceholderEnabled() : FlyingAllowed.getMessageConfig().getPlaceholderDisabled())
                            .replace("%player_name%", target.getName())
                            .replace("%player_uuid%", target.getUniqueId().toString())
                            .replace("%player_display%", target.getDisplayName())
            );
        } else {
            commandContext.getSender().sendMessage(
                    FlyingAllowed.getMessageConfig().getCommandFlyOtherSelfMessage()
                            .replace("%status%",
                                    newVal ? FlyingAllowed.getMessageConfig().getPlaceholderEnabled() : FlyingAllowed.getMessageConfig().getPlaceholderDisabled())
                            .replace("%old_status%",
                                    oldVal ? FlyingAllowed.getMessageConfig().getPlaceholderEnabled() : FlyingAllowed.getMessageConfig().getPlaceholderDisabled())
                            .replace("%player_name%", target.getName())
                            .replace("%player_uuid%", target.getUniqueId().toString())
                            .replace("%player_display%", target.getDisplayName())
            );
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
            String[] args = commandContext.getArgs().stream().map(CommandArgument::getContent).toArray(String[]::new);

            tabComplete.addAll(StringUtils.getAsCompletion(args, Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)));
        }

        return tabComplete;
    }
}
