package modreq.Messages;

import modreq.Database.Database;
import modreq.Modreq;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.sql.ResultSet;
import java.util.UUID;

public class MessageAdmin {
    public static void choose(String choose, String id) {
        Discord.send("discord_"+choose, id);
        MessageAdmin.send(choose, id);
    }

    public static void send(String what, String id) {
        String message = null;
        try {
            ResultSet rs = Database.getTicketId(id);
            if (rs.next()) {
                message = ChatColor.translateAlternateColorCodes('&',
                    Modreq.messages.getString(what)
                        .replace("%id%", id)
                        .replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid"))).getPlayer().getDisplayName())
                        .replace("%text%", rs.getString("text"))
                );
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.hasPermission("modreq.claim")) {
                    player.sendMessage(Modreq.prefix + message);
                } else if (player.isOp()) {
                    player.sendMessage(Modreq.prefix + message);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
