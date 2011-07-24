package com.Balor.bukkit.AdminCmd;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Listeners.ACEntityListener;
import be.Balor.Listeners.ACPlayerListener;
import be.Balor.Listeners.ACPluginListener;
import be.Balor.Manager.CommandManager;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.PermParent;
import be.Balor.Manager.PermissionManager;
import be.Balor.Manager.Commands.Items.*;
import be.Balor.Manager.Commands.Mob.*;
import be.Balor.Manager.Commands.Player.*;
import be.Balor.Manager.Commands.Server.*;
import be.Balor.Manager.Commands.Spawn.*;
import be.Balor.Manager.Commands.Time.*;
import be.Balor.Manager.Commands.Tp.*;
import be.Balor.Manager.Commands.Weather.*;
import be.Balor.Manager.Commands.Warp.*;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 * 
 * @authors Plague, Balor
 */
public class AdminCmd extends JavaPlugin {
	private static Server server = null;
	private ACHelper worker;

	public static Server getBukkitServer() {
		return server;
	}

	public static final Logger log = Logger.getLogger("Minecraft");

	private void registerPermParents() {
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.item.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.player.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.mob.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.server.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.spawn.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.time.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.tp.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.weather.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.warp.*"));
		PermissionManager.getInstance().setMajorPerm(new PermParent("admincmd.*"));
	}

	private void registerCmds() {
		CommandManager.getInstance().registerCommand(Day.class);
		CommandManager.getInstance().registerCommand(Repair.class);
		CommandManager.getInstance().registerCommand(RepairAll.class);
		CommandManager.getInstance().registerCommand(More.class);
		CommandManager.getInstance().registerCommand(PlayerList.class);
		CommandManager.getInstance().registerCommand(PlayerLocation.class);
		CommandManager.getInstance().registerCommand(God.class);
		CommandManager.getInstance().registerCommand(Thor.class);
		CommandManager.getInstance().registerCommand(Kill.class);
		CommandManager.getInstance().registerCommand(Heal.class);
		CommandManager.getInstance().registerCommand(ClearSky.class);
		CommandManager.getInstance().registerCommand(Storm.class);
		CommandManager.getInstance().registerCommand(SetSpawn.class);
		CommandManager.getInstance().registerCommand(Spawn.class);
		CommandManager.getInstance().registerCommand(Memory.class);
		CommandManager.getInstance().registerCommand(SetTime.class);
		CommandManager.getInstance().registerCommand(ClearInventory.class);
		CommandManager.getInstance().registerCommand(Give.class);
		CommandManager.getInstance().registerCommand(AddBlackList.class);
		CommandManager.getInstance().registerCommand(RemoveBlackList.class);
		CommandManager.getInstance().registerCommand(TpHere.class);
		CommandManager.getInstance().registerCommand(TpTo.class);
		CommandManager.getInstance().registerCommand(Coloring.class);
		CommandManager.getInstance().registerCommand(Strike.class);
		CommandManager.getInstance().registerCommand(RemoveAlias.class);
		CommandManager.getInstance().registerCommand(SpawnMob.class);
		CommandManager.getInstance().registerCommand(KickPlayer.class);
		CommandManager.getInstance().registerCommand(PrivateMessage.class);
		CommandManager.getInstance().registerCommand(AddAlias.class);
		CommandManager.getInstance().registerCommand(TpPlayerToPlayer.class);
		CommandManager.getInstance().registerCommand(TpLoc.class);
		CommandManager.getInstance().registerCommand(KickAllPlayers.class);
		CommandManager.getInstance().registerCommand(Vulcan.class);
		CommandManager.getInstance().registerCommand(Drop.class);
		CommandManager.getInstance().registerCommand(Invisible.class);
		CommandManager.getInstance().registerCommand(SpyMsg.class);
		CommandManager.getInstance().registerCommand(Fireball.class);
		CommandManager.getInstance().registerCommand(Home.class);
		CommandManager.getInstance().registerCommand(SetHome.class);
		CommandManager.getInstance().registerCommand(AddWarp.class);
		CommandManager.getInstance().registerCommand(RemoveWarp.class);
		CommandManager.getInstance().registerCommand(TpToWarp.class);
		CommandManager.getInstance().registerCommand(WarpList.class);
	}

	public void onEnable() {
		server = getServer();
		PluginManager pm = getServer().getPluginManager();
		ACPluginListener pL = new ACPluginListener();

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Enabled. (version "
				+ pdfFile.getVersion() + ")");
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pL, Priority.Monitor, this);

		worker = ACHelper.getInstance();
		worker.setPluginInstance(this);
		LocaleManager
				.getInstance()
				.addLocale(
						"changeWorld",
						ChatColor.DARK_RED
								+ "All your powers have been deactivated because you teleported to an another world");
		registerPermParents();
		registerCmds();
		PermissionManager.getInstance().addPermChild("admincmd.item.noblacklist");
		PermissionManager.getInstance().registerAllPermParent();
		ACPlayerListener pOqL = new ACPlayerListener(worker);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pOqL, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, pOqL, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pOqL, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, pOqL, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, pOqL, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, new ACEntityListener(worker), Priority.High,
				this);
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		worker = null;
		getServer().getScheduler().cancelTasks(this);
		ACHelper.killInstance();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Disabled. (version "
				+ pdfFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel,
			String[] args) {

		String cmd = command.getName();

		worker.setSender(sender);
		return CommandManager.getInstance().execCmd(cmd.toLowerCase(), sender, args);
	}
}
