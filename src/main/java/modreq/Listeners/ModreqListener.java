package modreq.Listeners;

import modreq.Modreq;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ModreqListener extends JavaPlugin implements Listener {
	Modreq plugin;

	//Bukkit.getPluginManager().registerEvents(this, this);
	public ModreqListener(Modreq plugin) {
		this.plugin = plugin;

	}
}
