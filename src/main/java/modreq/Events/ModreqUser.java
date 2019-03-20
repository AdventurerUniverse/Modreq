package modreq.Events;

import modreq.Database.Database;
import modreq.Messages.MessageAdmin;
import modreq.Modreq;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ModreqUser {
    public static void create(Player player, String[] args) {
        if(Integer.valueOf(Modreq.config.getString("min_words")) <= args.length){
            String problem = "";
            for(int i = 0; i < args.length; i++) {
                problem += args[i] + " ";
            }
            String id =  Database.InsertModreq(player.getUniqueId().toString(),problem, player.getLocation().getWorld().getName(),  String.valueOf(player.getLocation().getBlockX()), String.valueOf(player.getLocation().getBlockY()), String.valueOf(player.getLocation().getBlockZ()));
            MessageAdmin.choose("new_ticket", id);

        }else{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.messages.getString("min_words")));
        }

    }

    public static void list(Player player, String[] args) {
        player.sendMessage("Budoucí funkce :D");
    }
}
