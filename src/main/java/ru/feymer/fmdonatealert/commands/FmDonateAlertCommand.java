package ru.feymer.fmdonatealert.commands;

import com.google.common.base.Joiner;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.feymer.fmdonatealert.FmDonateAlert;
import ru.feymer.fmdonatealert.utils.Config;
import ru.feymer.fmdonatealert.utils.Hex;
import ru.feymer.fmdonatealert.utils.Utils;
import ru.feymer.fmdonatealert.utils.VKUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FmDonateAlertCommand extends TelegramLongPollingBot implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("fmdonatealert.reload")) {
                    Config.reloadConfig(FmDonateAlert.getInstance());
                    Utils.sendMessage(sender, Utils.getString("messages.reload"));
                } else {
                    Utils.sendMessage(sender, Utils.getString("messages.no-permission"));
                }
            } else {
                Utils.sendMessage(sender, Utils.getString("messages.no-args"));
            }
            return true;
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("alert")) {
                if (sender.hasPermission("fmdonatealert.alert")) {
                    Random random = new Random();
                    String player = args[1];
                    String donate = Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length));
                    List<String> texts = new ArrayList<>(Utils.getConfig().getConfigurationSection("settings.texts").getKeys(false));
                    List<String> list = Utils.getStringList("settings.texts." + texts.get(random.nextInt(texts.size())));
                    list.forEach((a) -> {
                        Bukkit.broadcastMessage(Hex.color(a.replace("%player%", player).replace("%donate%", donate)));
                    });
                    if (Utils.getBoolean("settings.enable-title")) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            players.sendTitle(Utils.getString("settings.title"), Hex.color(Utils.getString("settings.sub-title").replace("%player%", player).replace("%donate%", donate)));
                        }
                    }
                    if (Utils.getBoolean("telegram.settings.enable")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String stringList : Utils.getStringList("telegram.messages.notify-donate")) {
                            stringBuilder.append(stringList).append("\n");
                        }
                        String donateTelegram = donate;
                        Pattern pattern = Pattern.compile("(&[a-zA-F0-9]+?)");
                        Matcher matcher = pattern.matcher(donateTelegram);
                        while (matcher.find()) {
                            donateTelegram = donateTelegram.replace(matcher.group(1), "");
                        }
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String dataTime = now.format(formatter);
                        String message = stringBuilder.toString().replace("%player%", player).replace("%donate%", donateTelegram).replace("%date%", dataTime);
                        for (String admin_chat_ids : Utils.getStringList("telegram.settings.admin-chat-ids")) {
                            long chatId = Long.parseLong(admin_chat_ids);
                            if (chatId != 0) {
                                this.sendMessage(chatId, message);
                            }
                        }
                    }
                    if (Utils.getBoolean("vk.settings.enable")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String stringList : Utils.getStringList("vk.messages.notify-donate")) {
                            stringBuilder.append(stringList).append("\n");
                        }
                        String donateVK = donate;
                        Pattern pattern = Pattern.compile("(&[a-zA-F0-9]+?)");
                        Matcher matcher = pattern.matcher(donateVK);
                        while (matcher.find()) {
                            donateVK = donateVK.replace(matcher.group(1), "");
                        }
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String dataTime = now.format(formatter);
                        String message = stringBuilder.toString().replace("%player%", player).replace("%donate%", donateVK).replace("%date%", dataTime);
                        for (String admin_user_ids : Utils.getStringList("vk.settings.admin-user-ids")) {
                            Integer userId = Integer.valueOf(admin_user_ids);
                            if (userId != 0) {
                                try {
                                    VKUtils.sendMessage(userId, message);
                                } catch (ClientException | ApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    Utils.sendMessage(sender, Utils.getString("messages.alert"));
                } else {
                    Utils.sendMessage(sender, Utils.getString("messages.no-permission"));
                }
            } else {
                for (String help : Utils.getStringList("messages.help")) {
                    Utils.sendMessage(sender, help);
                }
            }
            return true;
        } else {
            for (String help : Utils.getStringList("messages.help")) {
                Utils.sendMessage(sender, help);
            }
        }
        return false;
    }

    public void sendMessage(long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException exception) {
            throw new RuntimeException(exception);
        }
    }


    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return Utils.getString("telegram.settings.bot-username");
    }

    @Override
    public String getBotToken() {
        return Utils.getString("telegram.settings.bot-token");
    }
}
