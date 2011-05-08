package com.Balor.bukkit.AdminCmd;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import belgium.Balor.Workers.PlayerOnQuitListener;
import belgium.Balor.Workers.PluginListener;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 * 
 * @authors Plague, Balor
 */
public class AdminCmd extends JavaPlugin {
	private static Server server = null;
	private AdminCmdWorker worker;

	public static Server getBukkitServer() {
		return server;
	}

	public static final Logger log = Logger.getLogger("Minecraft");

	private boolean hasPerm(Player p, String perm) {
		return worker.hasPerm(p, perm);
	}

	@Override
	public void onEnable() {
		server = getServer();
		PluginManager pm = getServer().getPluginManager();
		PluginListener pL = new PluginListener();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Enabled. (version" + pdfFile.getVersion()
				+ ")");
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pL, Priority.Monitor, this);
	
		worker = new AdminCmdWorker(getDataFolder().getPath());
		worker.setPluginInstance(this);
		PlayerOnQuitListener pOqL = new PlayerOnQuitListener(worker);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pOqL, Priority.Normal, this);
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Disabled. (version"
				+ pdfFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You have to be a player!");
			return true;
		} else {
			Player player = (Player) sender;

			String cmd = command.getName();

			worker.setPlayer(player);

			// 0 arguments:
			if (cmd.equalsIgnoreCase("bal_timeday"))
				if (hasPerm(player, "admincmd.time.day"))
					return worker.timeDay();

			if (cmd.equalsIgnoreCase("bal_repair"))
				if (hasPerm(player, "admincmd.item.repair"))
					return worker.repair();

			if (cmd.equalsIgnoreCase("bal_repairall"))
				if (hasPerm(player, "admincmd.item.repair"))
					return worker.repairAll();

			if (cmd.equalsIgnoreCase("bal_itemmore"))
				if (hasPerm(player, "admincmd.item.more"))
					return worker.itemMore(args);

			if (cmd.equalsIgnoreCase("bal_playerlist"))
				if (hasPerm(player, "admincmd.player.list"))
					return worker.playerList();

			if (cmd.equalsIgnoreCase("bal_playerloc"))
				if (hasPerm(player, "admincmd.player.loc"))
					return worker.playerLocation(args);

			if (cmd.equalsIgnoreCase("bal_playerheal"))
				if (hasPerm(player, "admincmd.player.heal"))
					return worker.playerSetHealth(args, 20);

			if (cmd.equalsIgnoreCase("bal_playerkill"))
				if (hasPerm(player, "admincmd.player.kill"))
					return worker.playerSetHealth(args, 0);

			if (cmd.equalsIgnoreCase("bal_wclear"))
				if (hasPerm(player, "admincmd.weather.clear"))
					return worker.weather("clear", null);

			if (cmd.equalsIgnoreCase("bal_wstorm"))
				if (hasPerm(player, "admincmd.weather.storm"))
					return worker.weather("storm", args);

			// 1 argument:
			if (args.length < 1)
				return false;
			if (cmd.equalsIgnoreCase("bal_timeset"))
				if (hasPerm(player, "admincmd.time.set"))
					return worker.timeSet(args[0]);

			if (cmd.equalsIgnoreCase("bal_pclear"))
				if (hasPerm(player, "admincmd.player.clear"))
					return worker.clearInventory(args[0]);

			if (cmd.equalsIgnoreCase("bal_item"))
				if (hasPerm(player, "admincmd.item.add"))
					return worker.itemGive(args);

			if (cmd.equalsIgnoreCase("bal_addbl"))
				if (hasPerm(player, "admincmd.item.blacklist"))
					return worker.setBlackListedItem(args[0]);

			if (cmd.equalsIgnoreCase("bal_rmbl"))
				if (hasPerm(player, "admincmd.item.blacklist"))
					return worker.removeBlackListedItem(args[0]);

			if (cmd.equalsIgnoreCase("bal_tpto"))
				if (hasPerm(player, "admincmd.tp.to"))
					return worker.playerTpTo(args[0]);

			if (cmd.equalsIgnoreCase("bal_tphere"))
				if (hasPerm(player, "admincmd.tp.here"))
					return worker.playerTpHere(args[0]);

			if (cmd.equalsIgnoreCase("bal_itemcolor"))
				if (hasPerm(player, "admincmd.item.color"))
					return worker.itemColor(args[0]);

			if (cmd.equalsIgnoreCase("bal_wstrike"))
				if (hasPerm(player, "admincmd.weather.strike"))
					return worker.strikePlayer(args[0]);

			// 2 arguments:
			if (args.length < 2)
				return false;
			if (cmd.equalsIgnoreCase("bal_playermsg"))
				if (hasPerm(player, "admincmd.player.msg"))
					return worker.playerMessage(args);

			if (cmd.equalsIgnoreCase("bal_tp2p"))
				if (hasPerm(player, "admincmd.tp.players"))
					return worker.playerTpPlayer(args);

			// 3 arguments:
			if (args.length < 3)
				return false;
			if (cmd.equalsIgnoreCase("bal_tpthere"))
				if (hasPerm(player, "admincmd.tp.location"))
					return worker.tpTo(args);
			// unknown command, should not really get here
			return false;
		}
	}
}
