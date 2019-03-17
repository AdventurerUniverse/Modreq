package modreq.Listeners;

import modreq.Modreq;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.util.UUID;

public class Inventory implements Listener {
    private Modreq plugin;

    public Inventory(Modreq plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {

            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            org.bukkit.inventory.Inventory inventory = event.getInventory();

            if (clicked.toString().contains("Modreq") == true && clicked.getType() == Material.PAPER) {
                String name = inventory.getItem(event.getRawSlot()).getItemMeta().getDisplayName();
                ResultSet result = plugin.statement.executeQuery("SELECT * FROM modreqs WHERE PK_idm=" + name.split("#")[1] + ";");
                while (result.next()) {
                   String message = ChatColor.translateAlternateColorCodes('&',
                            Modreq.messages.getString("ticket_info")
                                    .replace("%id%", result.getString("PK_idm"))
                                    .replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(result.getString("uuid"))).getPlayer().getDisplayName())
                                    .replace("%text%", result.getString("text"))
                                    .replace("%location%", "/tp " +result.getString("location"))
                    );
                    player.sendMessage(message);
                }
                player.closeInventory();
            } else if (clicked.toString().contains("page") == true && clicked.getType() == Material.BOOK) {
                String name = inventory.getItem(event.getRawSlot()).getItemMeta().getDisplayName();
                player.chat("/modreq list " + name.split("#")[1]);
                player.closeInventory();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
