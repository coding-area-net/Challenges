package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.CommandHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GodModeCommand implements SenderCommand, Completer, Listener {

    private final List<UUID> godModePlayers = new ArrayList<>();

    @Override
    public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {

        List<Player> targets = new ArrayList<>();

        if (args.length > 0) {
            targets.addAll(CommandHelper.getPlayers(sender, args[0]));

        } else if (sender instanceof Player) {
            targets.add((Player) sender);
        }

        if (targets.isEmpty()) {
            Message.forName("command-no-target").send(sender, Prefix.CHALLENGES);
            return;
        }

        boolean otherPlayers = false;
        for (Player target : targets) {
            if (godModePlayers.contains(target.getUniqueId())) {
                godModePlayers.remove(target.getUniqueId());
                Message.forName("command-god-mode-disabled").send(target, Prefix.CHALLENGES);
            } else {
                godModePlayers.add(target.getUniqueId());
                Message.forName("command-god-mode-enabled").send(target, Prefix.CHALLENGES);
                target.setFoodLevel(20);
            }

            if (target != sender)
                otherPlayers = true;

        }

        if (otherPlayers)
            Message.forName("command-god-mode-toggled-others").send(sender, Prefix.CHALLENGES, targets.size());

    }

    @Nullable
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
        return CommandHelper.getCompletions(sender);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (godModePlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (godModePlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
