package modreq.Events;

import modreq.Database.Database;
import modreq.Messages.MessageAdmin;
import modreq.Modreq;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ModreqUser {
    public static void create(Player player, String[] args) {
        if(Integer.valueOf(Modreq.config.getString("min_words")) <= args.length){
            String problem = "";
            for(int i = 0; i < args.length; i++) {
                problem += args[i] + " ";
            }
            String id =  Database.InsertModreq(player.getUniqueId().toString(),problem, player.getLocation().getWorld().getName(),  String.valueOf(player.getLocation().getBlockX()), String.valueOf(player.getLocation().getBlockY()), String.valueOf(player.getLocation().getBlockZ()));
            MessageAdmin.choose("new_ticket", id);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +  Modreq.messages.getString("ticket_to_user_created").replace("%id%", id)));
        }else{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +  Modreq.messages.getString("min_words")));
        }

    }

    public static void own(Player player, String[] args) {
        ResultSet rs;
        try {
            rs = Database.getOwnTickets(player.getUniqueId().toString());
            while (rs.next()){
                String message = ChatColor.translateAlternateColorCodes('&',
                        Modreq.messages.getString("ticket_to_user_info")
                                .replace("%id%", rs.getString("PK_idm"))
                                .replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid"))).getName())
                                .replace("%text%", rs.getString("text"))
                                .replace("%location%", "/tp "
                                        + rs.getString("world") +
                                        " " + String.valueOf(rs.getInt("x")) +
                                        " " + String.valueOf(rs.getInt("y")) +
                                        " " + String.valueOf(rs.getInt("z")))
                                .replace("%status%", rs.getString("status"))
                );

                if(rs.getString("claim_uuid") != null) {
                    message =  message.replace("%claimed%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("claim_uuid"))).getName());
                }else{
                    message =  message.replace("%by%", " ");
                }

                if(rs.getString("answer") != null) {
                    message =  message.replace("%answer%", rs.getString("answer"));
                }else{
                    message =  message.replace("%answer%", " ");
                }

                player.sendMessage(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
