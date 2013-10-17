package be.Balor.Listeners.Features;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;

import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyFieldContract;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

public class ACUnknownCommandListener implements Listener {

	private SimpleCommandMap cmdMap = null;

	public ACUnknownCommandListener() {
		try {
			this.cmdMap = getCommandMap();
		} catch (final Exception e) {
		}
	}

	private SimpleCommandMap getCommandMap() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException /*    */{
		final Server svr = ACPluginManager.getServer();
		final FuzzyFieldContract contract = FuzzyFieldContract.newBuilder().declaringClassDerivedOf(PluginManager.class).typeDerivedOf(CommandMap.class)
				.build();
		return FieldUtils.getAttribute(svr.getPluginManager(), contract);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUnknownCommand(final PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}

		String cmd = event.getMessage();
		if (cmd.charAt(0) == '/') {
			cmd = cmd.replaceFirst("/", "");
		}
		cmd = cmd.split(" ")[0];

		final Player player = event.getPlayer();
		if (!isCmdRegistered(cmd)) {
			LocaleHelper.UNKNOWN_COMMAND.sendLocale(player);
			System.out.println(player.getName() + " issued server command: " + event.getMessage());
			event.setCancelled(true);
		}
	}

	private boolean isCmdRegistered(final String name) {
		if (ConfigEnum.UNKNOWN_COMMAND_BYPASS.getStringList().contains(name)) {
			return true;
		}

		if (this.cmdMap == null) {
			return true;
		}
		return this.cmdMap.getCommand(name) != null;
	}
}
