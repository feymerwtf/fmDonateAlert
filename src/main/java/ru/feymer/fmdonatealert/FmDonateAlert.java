package ru.feymer.fmdonatealert;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.feymer.fmdonatealert.commands.FmDonateAlertCommand;
import ru.feymer.fmdonatealert.commands.FmDonateAlertTabCompleter;
import ru.feymer.fmdonatealert.utils.Config;
import ru.feymer.fmdonatealert.utils.Hex;

public final class FmDonateAlert extends JavaPlugin {

    public static FmDonateAlert instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&a» &fПлагин &a" + getPlugin(FmDonateAlert.class).getName() + " &fвключился&f!"));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&a» &fВерсия: &av" + getPlugin(FmDonateAlert.class).getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));

        Config.loadYamlFile(this);
        this.getCommand("fmdonatealert").setExecutor(new FmDonateAlertCommand());
        this.getCommand("fmdonatealert").setTabCompleter(new FmDonateAlertTabCompleter());

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&a» &fПлагин &a" + getPlugin(FmDonateAlert.class).getName() + " &fвключился&f!"));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&a» &fВерсия: &av" + getPlugin(FmDonateAlert.class).getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));
    }

    public static FmDonateAlert getInstance() {
        return instance;
    }
}
