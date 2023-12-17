package ru.feymer.fmdonatealert.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FmDonateAlertTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "alert");
        } else if (args[0].equalsIgnoreCase("alert")) {
            if (args.length == 2) {
                Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                ArrayList<String> onlinePlayerName = new ArrayList<>();
                for (Player onlinePlayer : onlinePlayers) {
                    String name = onlinePlayer.getName();
                    onlinePlayerName.add(name);
                }
                return onlinePlayerName;
            }
            if (args.length == 3) {
                return Arrays.asList("&e&lБЕРСЕРК", "&4&lДЕМОН", "&b&lВИЗЕР");
            }
        }
        return new ArrayList<>();
    }
}
