package com.Balor.bukkit.AdminCmd;

import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.Commands.Items.*;
import be.Balor.Manager.Commands.Player.*;
import be.Balor.Manager.Commands.Time.*;
import be.Balor.Manager.Commands.Weather.*;
import belgium.Balor.Workers.ACEntityListener;
import belgium.Balor.Workers.ACPlayerListener;
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

	private boolean hasPerm(CommandSender p, String perm) {
		return worker.hasPerm(p, perm);
	}

	private void registerCmds()
	{
		CommandManager.getInstance().registerCommand(Day.class);
		CommandManager.getInstance().registerCommand(Repair.class);
		CommandManager.getInstance().registerCommand(RepairAll.class);
		CommandManager.getInstance().registerCommand(More.class);
		CommandManager.getInstance().registerCommand(PlayerList.class);
		CommandManager.getInstance().registerCommand(PlayerLocation.class);
		CommandManager.getInstance().registerCommand(God.class);
		CommandManager.getInstance().registerCommand(Thor.class);
	}
	@Override
	public void onEnable() {
		server = getServer();
		PluginManager pm = getServer().getPluginManager();
		PluginListener pL = new PluginListener();

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Enabled. (version "
				+ pdfFile.getVersion() + ")");
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pL, Priority.Monitor, this);

		worker = AdminCmdWorker.getInstance();
		worker.setPluginInstance(this);
		registerCmds();
		ACPlayerListener pOqL = new ACPlayerListener(worker);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pOqL, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, new ACEntityListener(worker), Priority.High, this);
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		worker = null;
		log.info("[" + pdfFile.getName() + "]" + " Plugin Disabled. (version"
				+ pdfFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel,
			String[] args) {

		String cmd = command.getName();

		worker.setSender(sender);
		if(!CommandManager.getInstance().execCmd(cmd.toLowerCase(), sender, args))
			return false;
		// 0 arguments:
		
		if (cmd.equalsIgnoreCase("bal_playerheal"))
			if (hasPerm(sender, "admincmd.player.heal"))
				return worker.playerSetHealth(args, 20);

		if (cmd.equalsIgnoreCase("bal_playerkill"))
			if (hasPerm(sender, "admincmd.player.kill"))
				return worker.playerSetHealth(args, 0);

		if (cmd.equalsIgnoreCase("bal_wclear"))
			if (hasPerm(sender, "admincmd.weather.clear"))
				return worker.weather("clear", null);

		if (cmd.equalsIgnoreCase("bal_wstorm"))
			if (hasPerm(sender, "admincmd.weather.storm"))
				return worker.weather("storm", args);
		
		if (cmd.equalsIgnoreCase("bal_setspawn"))
			if (hasPerm(sender, "admincmd.spawn.set"))
				return worker.setSpawn();
		
		if (cmd.equalsIgnoreCase("bal_spawn"))
			if (hasPerm(sender, "admincmd.spawn.tp"))
				return worker.spawn();
		
		if (cmd.equalsIgnoreCase("bal_memory"))
			if (hasPerm(sender, "admincmd.server.memory"))
				return worker.memory();
		// 1 argument:
		if (args.length < 1)
			return false;
		if (cmd.equalsIgnoreCase("bal_timeset"))
			if (hasPerm(sender, "admincmd.time.set"))
				return worker.timeSet(args[0]);

		if (cmd.equalsIgnoreCase("bal_pclear"))
			if (hasPerm(sender, "admincmd.player.clear"))
				return worker.clearInventory(args[0]);

		if (cmd.equalsIgnoreCase("bal_item"))
			if (hasPerm(sender, "admincmd.item.add"))
				return worker.itemGive(args);

		if (cmd.equalsIgnoreCase("bal_addbl"))
			if (hasPerm(sender, "admincmd.item.blacklist"))
				return worker.setBlackListedItem(args[0]);

		if (cmd.equalsIgnoreCase("bal_rmbl"))
			if (hasPerm(sender, "admincmd.item.blacklist"))
				return worker.removeBlackListedItem(args[0]);

		if (cmd.equalsIgnoreCase("bal_tpto"))
			if (hasPerm(sender, "admincmd.tp.to"))
				return worker.playerTpTo(args[0]);

		if (cmd.equalsIgnoreCase("bal_tphere"))
			if (hasPerm(sender, "admincmd.tp.here"))
				return worker.playerTpHere(args[0]);

		if (cmd.equalsIgnoreCase("bal_itemcolor"))
			if (hasPerm(sender, "admincmd.item.color"))
				return worker.itemColor(args[0]);

		if (cmd.equalsIgnoreCase("bal_wstrike"))
			if (hasPerm(sender, "admincmd.weather.strike"))
				return worker.strikePlayer(args[0]);

		if (cmd.equalsIgnoreCase("bal_rmalias"))
			if (hasPerm(sender, "admincmd.item.alias"))
				return worker.rmAlias(args[0]);

		if (cmd.equalsIgnoreCase("bal_mob"))
			if (hasPerm(sender, "admincmd.mob.spawn"))
				return worker.spawnMob(args);
		
		if (cmd.equalsIgnoreCase("bal_kick"))
			if (hasPerm(sender, "admincmd.player.kick"))
				return worker.kickPlayer(args);

		// 2 arguments:
		if (args.length < 2)
			return false;
		if (cmd.equalsIgnoreCase("bal_playermsg"))
			if (hasPerm(sender, "admincmd.player.msg"))
				return worker.playerMessage(args);

		if (cmd.equalsIgnoreCase("bal_tp2p"))
			if (hasPerm(sender, "admincmd.tp.players"))
				return worker.playerTpPlayer(args);
		if (cmd.equalsIgnoreCase("bal_addalias"))
			if (hasPerm(sender, "admincmd.item.alias"))
				return worker.alias(args);

		// 3 arguments:
		if (args.length < 3)
			return false;
		if (cmd.equalsIgnoreCase("bal_tpthere"))
			if (hasPerm(sender, "admincmd.tp.location"))
				return worker.tpTo(args);
		// unknown command, should not really get here
		return false;
	}
}
