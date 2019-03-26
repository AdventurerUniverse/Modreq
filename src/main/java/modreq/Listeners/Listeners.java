package modreq.Listeners;

import modreq.Database.Database;
import modreq.Modreq;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.util.UUID;

public class Listeners implements Listener {
	private Modreq plugin;

	public Listeners(Modreq plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {


			Player player = (Player) event.getWhoClicked();
			ItemStack clicked = event.getCurrentItem();
			org.bukkit.inventory.Inventory inventory = event.getInventory();

			if(clicked != null && (clicked.toString().contains("Modreq") == true && clicked.getType() == Material.PAPER)) {
				String name = inventory.getItem(event.getRawSlot()).getItemMeta().getDisplayName();
				event.setCancelled(true);
				ResultSet result = Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE PK_idm=" + name.split("#")[1] + ";");
				while(result.next()) {
					String message = ChatColor.translateAlternateColorCodes('&',
							Modreq.messages.getString("ticket_info")
									.replace("%id%", result.getString("PK_idm"))
									.replace("%from%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(result.getString("uuid"))).getName())
									.replace("%text%", result.getString("text"))
									.replace("%location%", "/tp "
											+ result.getString("world") +
											" " + result.getInt("x") +
											" " + result.getInt("y") +
											" " + result.getInt("z"))

					);
					player.sendMessage(message);

				}
				player.closeInventory();
			} else if(clicked != null && (clicked.toString().contains("page") == true && clicked.getType() == Material.BOOK)) {
				String name = inventory.getItem(event.getRawSlot()).getItemMeta().getDisplayName();
				event.setCancelled(true);
				player.chat("/modreq list " + name.split("#")[1]);
				player.closeInventory();

			}


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		BukkitRunnable r = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Player p = e.getPlayer();
					ResultSet rs = Database.getOwnTicketsNotSend(p.getUniqueId().toString());
					if(rs != null) {
						while(rs.next()) {
							if(rs.getString("status").contains("close")) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
										Modreq.messages.getString("ticket_to_user_close")
												.replace("%id%", rs.getString("PK_idm"))
												.replace("%by%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("claim_uuid"))).getName())
												.replace("%answer%", rs.getString("answer"))
								));
								Database.UpdateSend(rs.getString("PK_idm"));
							} else {
								if(rs.getString("claim_uuid") != null) {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
											Modreq.messages.getString("ticket_to_user_claim")
													.replace("%id%", rs.getString("PK_idm"))
													.replace("%by%", Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("claim_uuid"))).getName())
									));
									Database.UpdateSend(rs.getString("PK_idm"));
								}
							}
						}
					}
				} catch(Exception err) {
					err.printStackTrace();
				}
			}
		};

		r.runTaskAsynchronously(this.plugin);
	}

}
