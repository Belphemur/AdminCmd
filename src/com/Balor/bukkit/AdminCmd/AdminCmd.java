package com.Balor.bukkit.AdminCmd;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
//Permissions imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 * 
 * @authors Plague, Balor
 */
public class AdminCmd extends JavaPlugin {

	private AdminCmdWorker worker;
	public static final Logger log = Logger.getLogger("Minecraft");
	/**
	 * Permission plugin
	 */
	private static PermissionHandler Permissions = null;

	/**
	 * Checks that Permissions is installed.
	 */
	private void setupPermissions() {

		Plugin perm_plugin = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (Permissions == null)
			if (perm_plugin != null) {
				// Permissions found, enable it now
				this.getServer().getPluginManager().enablePlugin(perm_plugin);
				Permissions = ((Permissions) perm_plugin).getHandler();
				log.info("[" + pdfFile.getName() + "]" + " (version " + pdfFile.getVersion()
						+ ") Enabled with Permissions.");
			} else {
				log.info("[" + pdfFile.getName() + "]" + " (version " + pdfFile.getVersion()
						+ ") Enables without Permissions.");
				log.info("[" + pdfFile.getName() + "]" + " Instead of Permissions, check if the user is OP.");
			}
	}

	/**
	 * Check the permissions
	 * 
	 * @param player
	 * @param perm
	 * @return boolean
	 */
	private boolean hasPerm(Player player, String perm) {
		if (Permissions == null)
			return player.isOp();
		else
			return Permissions.has(player, perm);
	}

	@Override
	public void onEnable() {
		setupPermissions();
		worker = new AdminCmdWorker(getDataFolder().getPath());
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Disabled. (version" + pdfFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
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
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_repair"))
				if (hasPerm(player, "admincmd.item.repair"))
					return worker.repair();
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_repairall"))
				if (hasPerm(player, "admincmd.item.repair"))
					return worker.repairAll();
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_itemmore"))
				if (hasPerm(player, "admincmd.item.more"))
					return worker.itemMore(args);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_playerlist"))
				if (hasPerm(player, "admincmd.player.list"))
					return worker.playerList();
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_playerloc"))
				if (hasPerm(player, "admincmd.player.loc"))
					return worker.playerLocation(args);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_playerheal"))
				if (hasPerm(player, "admincmd.player.heal"))
					return worker.playerSetHealth(args, 20);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_playerkill"))
				if (hasPerm(player, "admincmd.player.kill"))
					return worker.playerSetHealth(args, 0);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			// 1 argument:
			if (args.length < 1)
				return false;
			if (cmd.equalsIgnoreCase("bal_timeset"))
				if (hasPerm(player, "admincmd.time.set"))
					return worker.timeSet(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_pclear"))
				if (hasPerm(player, "admincmd.player.clear"))
					return worker.clearInventory(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_item"))
				if (hasPerm(player, "admincmd.item.add"))
					return worker.itemGive(args);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_addbl"))
				if (hasPerm(player, "admincmd.item.blacklist"))
					return worker.setBlackListedItem(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}
			if (cmd.equalsIgnoreCase("bal_rmbl"))
				if (hasPerm(player, "admincmd.item.blacklist"))
					return worker.removeBlackListedItem(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_tpto"))
				if (hasPerm(player, "admincmd.tp.to"))
					return worker.playerTpTo(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_tphere"))
				if (hasPerm(player, "admincmd.tp.here"))
					return worker.playerTpHere(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_itemcolor"))
				if (hasPerm(player, "admincmd.item.color"))
					return worker.itemColor(args[0]);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			// 2 arguments:
			if (args.length < 2)
				return false;
			if (cmd.equalsIgnoreCase("bal_playermsg"))
				if (hasPerm(player, "admincmd.player.msg"))
					return worker.playerMessage(args);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			if (cmd.equalsIgnoreCase("bal_tp2p"))
				if (hasPerm(player, "admincmd.tp.players"))
					return worker.playerTpPlayer(args);
				else {
					player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
					return true;
				}

			// unknown command, should not really get here
			return false;
		}
	}
}
