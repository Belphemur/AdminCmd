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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

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
	public void execute(CommandSender sender, CommandArgs args) {
		Player toBan = sender.getServer().getPlayer(args.getString(0));
		HashMap<String, String> replace = new HashMap<String, String>();
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
			} catch (Exception e) {
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
								String unbanMsg = Utils.I18n("unban", "player", unban);
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
		if (tmpBan != null)
			ACHelper.getInstance().addBannedPlayer(
					new TempBannedPlayer(banPlayerString, message, tmpBan * 60 * 1000));
		else
			ACHelper.getInstance().addBannedPlayer(new BannedPlayer(banPlayerString, message));
		Utils.broadcastMessage(Utils.I18n("ban", replace));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}
