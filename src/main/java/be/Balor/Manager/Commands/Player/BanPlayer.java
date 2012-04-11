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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.BannedIP;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.TempBannedIP;
import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
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
	public void execute(final CommandSender sender, final CommandArgs args) {
		final Player toBan = sender.getServer().getPlayer(args.getString(0));
		final HashMap<String, String> replace = new HashMap<String, String>();
		String message = "";
		String banPlayerString;
		if (toBan != null) {
			banPlayerString = toBan.getName();
			if (!Utils.checkImmunity(sender, toBan)) {
				Utils.sI18n(sender, "insufficientLvl");
				return;
			}
		} else
			banPlayerString = args.getString(0);
		Integer tmpBan = null;
		if (args.length >= 2) {
			if (args.hasFlag('m'))
				message = LocaleManager.getInstance().get("kickMessages", args.getValueFlag('m'),
						"player", banPlayerString);
			if (message == null || (message != null && message.isEmpty())) {
				message = "";
				for (int i = 1; i < args.length - 1; i++)
					message += args.getString(i) + " ";
			}
			try {
				tmpBan = args.getInt(args.length - 1);
			} catch (final Exception e) {
				if (!args.hasFlag('m'))
					message += args.getString(args.length - 1);
			}
			if (tmpBan != null) {
				message += "(Banned for " + tmpBan + " minutes)";
				final String unban = banPlayerString;
				ACPluginManager.getScheduler().scheduleAsyncDelayedTask(getPlugin(),
						new Runnable() {

							@Override
							public void run() {
								ACHelper.getInstance().unBanPlayer(unban);
								final String unbanMsg = Utils.I18n("unban", "player", unban);
								if (unbanMsg != null)
									Utils.broadcastMessage(unbanMsg);
							}
						}, 20 * 60 * tmpBan);
			}
		} else {
			message = "You have been banned by ";
			if (!Utils.isPlayer(sender, false))
				message += "Server Admin";
			else
				message += Utils.getPlayerName((Player) sender);
		}
		message = message.trim();
		replace.put("player", banPlayerString);
		replace.put("reason", message);
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
		final Matcher ipv4 = Utils.REGEX_IP_V4.matcher(banPlayerString);
		final Matcher inaccurateIp = Utils.REGEX_INACCURATE_IP_V4.matcher(banPlayerString);
		if (tmpBan != null) {
			if (inaccurateIp.find()) {
				if (!ipv4.find()) {
					replace.clear();
					replace.put("ip", banPlayerString);
					LocaleHelper.INACC_IP.sendLocale(sender, replace);
					return;
				}
				ACHelper.getInstance().addBan(
						new TempBannedIP(banPlayerString, message, tmpBan * 60 * 1000));
			} else
				ACHelper.getInstance().addBan(
						new TempBannedPlayer(banPlayerString, message, tmpBan * 60 * 1000));
		} else {
			if (inaccurateIp.find()) {
				if (!ipv4.find()) {
					replace.clear();
					replace.put("ip", banPlayerString);
					LocaleHelper.INACC_IP.sendLocale(sender, replace);
					return;
				}
				ACHelper.getInstance().addBan(new BannedIP(banPlayerString, message));
			} else
				ACHelper.getInstance().addBan(new BannedPlayer(banPlayerString, message));
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
