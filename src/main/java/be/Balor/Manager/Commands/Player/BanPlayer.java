/************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Manager.Commands.Player;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.NotANumberException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.BannedIP;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.ITempBan;
import be.Balor.Player.TempBannedIP;
import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.UnBanTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BanPlayer extends PlayerCommand {

	/**
	 *
	 */
	public BanPlayer() {
		permNode = "admincmd.player.ban";
		cmdName = "bal_ban";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final Player toBan = Utils.getPlayer(args.getString(0));
		final HashMap<String, String> replace = new HashMap<String, String>();
		String message = "";
		String banPlayerString;
		if (toBan != null) {
			banPlayerString = toBan.getName();
			if (!Utils.checkImmunity(sender, toBan)) {
				Utils.sI18n(sender, "insufficientLvl");
				return;
			}
		} else {
			banPlayerString = args.getString(0);
		}
		Integer tmpBan = null;
		if (args.length >= 2) {
			if (args.hasFlag('m')) {
				message = LocaleManager.getInstance().get("kickMessages",
						args.getValueFlag('m'), "player", banPlayerString);
			}
			try {
				final int tmpIntTime = Utils.timeParser(args
						.getString(args.length - 1));
				if (tmpIntTime != -1) {
					tmpBan = tmpIntTime;
				}
			} catch (final NotANumberException e) {
				Utils.sI18n(sender, "NaN", "number",
						args.getString(args.length - 1));
				return;
			}
			if (message == null || (message != null && message.isEmpty())) {
				message = "";
				if (tmpBan == null) {
					for (int i = 1; i < args.length; i++) {
						message += args.getString(i) + " ";
					}
				} else { 
					for (int i = 1; i < args.length - 1; i++) {
						message += args.getString(i) + " ";
					}
				}
			}
			if (message.isEmpty()) {
				message += " You have been banned ";
				if (!Utils.isPlayer(sender, false)) {
					message += "by Server Admin";
				} else {
					message += "by "
						+ ChatColor.stripColor(Utils
								.getPlayerName((Player) sender));
				}
			}
		} else {
			if (message.isEmpty()) {
				message = "You have been banned ";
				if (!Utils.isPlayer(sender, false)) {
					message += "by Server Admin";
				} else {
					message += "by "
						+ ChatColor.stripColor(Utils
								.getPlayerName((Player) sender));
				}
			}
		}
		message = message.trim();
		replace.put("player", banPlayerString);
		replace.put("reason", message);
		final Matcher ipv4 = Utils.REGEX_IP_V4.matcher(banPlayerString);
		final Matcher inaccurateIp = Utils.REGEX_INACCURATE_IP_V4
				.matcher(banPlayerString);
		if (tmpBan != null) {
			message += " (Banned for " + tmpBan + " minutes)";
			replace.put("reason", message);
			ITempBan ban;
			if (inaccurateIp.find()) {
				if (!ipv4.find()) {
					replace.clear();
					replace.put("ip", banPlayerString);
					LocaleHelper.INACC_IP.sendLocale(sender, replace);
					return;
				}
				ban = new TempBannedIP(banPlayerString, message,
						tmpBan * 60 * 1000);
				ACHelper.getInstance().banPlayer(ban);
			} else {
				ban = new TempBannedPlayer(banPlayerString, message,
						tmpBan * 60 * 1000);
				ACHelper.getInstance().banPlayer(ban);
			}
			ACPluginManager.getScheduler().runTaskLaterAsynchronously(
					getPlugin(), new UnBanTask(ban, true),
					Utils.secInTick * 60 * tmpBan);
		} else {
			if (inaccurateIp.find()) {
				if (!ipv4.find()) {
					replace.clear();
					replace.put("ip", banPlayerString);
					LocaleHelper.INACC_IP.sendLocale(sender, replace);
					return;
				}
				ACHelper.getInstance().banPlayer(
						new BannedIP(banPlayerString, message));
			} else {
				ACHelper.getInstance().banPlayer(
						new BannedPlayer(banPlayerString, message));
			}
		}
		ACPlayer.getPlayer(toBan).setPower(Type.KICKED);
		if (toBan != null) {
			final String finalmsg = message;
			final Player finalToKick = toBan;
			ACPluginManager.scheduleSyncTask(new Runnable() {

				@Override
				public void run() {
					finalToKick.kickPlayer(finalmsg);
				}
			});
		}
		Utils.broadcastMessage(Utils.I18n("ban", replace));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
