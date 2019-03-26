package modreq.Messages;

import github.scarsz.discordsrv.dependencies.jda.core.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import modreq.Database.Database;
import modreq.Modreq;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.ResultSet;
import java.util.UUID;


public class Discord {
    public static void send(String what, String id) {
        String text = null;
        try {
            TextChannel channel = DiscordUtil.getTextChannelById(Modreq.config.getString("discord_channel_id"));
            ResultSet rs = Database.getTicketId(id);

            if(rs.next()) {
                channel.sendMessage(ChatColor.stripColor(
                        Modreq.messages.getString(what)
                                .replace("%id%", id)
                                .replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid"))).getName())
                                .replace("%text%", rs.getString("text"))
                )).queue();
            }
        } catch(
                Exception e) {
            e.printStackTrace();
        }
    }

    public static void Claimsend(String what, String id, String uuid) {
        String text = null;
        try {
            TextChannel channel = DiscordUtil.getTextChannelById(Modreq.config.getString("discord_channel_id"));
            ResultSet rs = Database.getTicketId(id);

            if(rs.next()) {
                channel.sendMessage(ChatColor.stripColor(
                        Modreq.messages.getString(what)
                                .replace("%id%", id)
                                .replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid)).getPlayer().getDisplayName())
                )).queue();
            }
        } catch(
                Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSend(String what, String id, String answer) {
        String text = null;
        try {
            TextChannel channel = DiscordUtil.getTextChannelById(Modreq.config.getString("discord_channel_id"));
            ResultSet rs = Database.getTicketId(id);

            if(rs.next()) {
                channel.sendMessage(ChatColor.stripColor(
                        Modreq.messages.getString(what)
                                .replace("%id%", id)
                                .replace("%by%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("claim_uuid"))).getPlayer().getDisplayName())
                                .replace("%answer%", answer)
                )).queue();
            }
        } catch(
                Exception e) {
            e.printStackTrace();
        }
    }

    public static void unClaimSend(String what, String id, String uuid) {
        String text = null;
        try {
            TextChannel channel = DiscordUtil.getTextChannelById(Modreq.config.getString("discord_channel_id"));
            ResultSet rs = Database.getTicketId(id);

            if(rs.next()) {
                channel.sendMessage(ChatColor.stripColor(
                        Modreq.messages.getString(what)
                                .replace("%id%", id)
                                .replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid)).getPlayer().getDisplayName())
                )).queue();
            }
        } catch(
                Exception e) {
            e.printStackTrace();
        }
    }
}