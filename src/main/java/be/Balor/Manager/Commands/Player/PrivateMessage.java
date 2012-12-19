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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PrivateMessage extends PlayerCommand {

	/**
	 *
	 */
	public PrivateMessage() {
		permNode = "admincmd.player.msg";
		cmdName = "bal_playermsg";
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
		if (Utils.isPlayer(sender, false)
				&& ACPlayer.getPlayer(((Player) sender)).hasPower(Type.MUTED)
				&& ConfigEnum.MUTEDPM.getBoolean()) {
			Utils.sI18n(sender, "muteEnabled");
			return;
		}
		final Player buddy = sender.getServer().getPlayer(args.getString(0));
		if (buddy != null) {
			if (InvisibleWorker.getInstance().hasInvisiblePowers(buddy)
					&& !PermissionManager.hasPerm(sender,
							"admincmd.invisible.cansee", false)) {
				Utils.sI18n(sender, "playerNotFound", "player",
						args.getString(0));
				return;
			}
			String senderPm = "";
			String msg = "";
			String senderName = "Server Admin";
			if (Utils.isPlayer(sender, false)) {
				final Player pSender = (Player) sender;
				senderName = pSender.getName();
				senderPm = Utils.getPlayerName(pSender, buddy);
				ACHelper.getInstance().setReplyPlayer(buddy, pSender);
			} else {
				senderPm = senderName;
			}

			for (int i = 1; i < args.length; ++i) {
				msg += args.getString(i) + " ";
			}
			msg = msg.trim();
			String parsed = Utils.colorParser(msg);
			if (parsed == null) {
				parsed = msg;
			}
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("sender", senderPm);
			replace.put("receiver", Utils.getPlayerName(buddy));
			buddy.sendMessage(Utils.I18n("privateMessageHeader", replace)
					+ parsed);
			if (AFKWorker.getInstance().isAfk(buddy)) {
				AFKWorker.getInstance().sendAfkMessage(sender, buddy);
			} else {
				sender.sendMessage(Utils.I18n("privateMessageHeader", replace)
						+ parsed);
			}
			final String spyMsg = LocaleHelper.SPYMSG_HEADER.getLocale(replace)
					+ parsed;
			for (final Player p : ACHelper.getInstance().getSpyPlayers()) {
				if (p != null && !p.getName().equals(senderName)
						&& !p.getName().equals(buddy.getName())) {
					p.sendMessage(spyMsg);
				}
			}
			if (ConfigEnum.LOG_PM.getBoolean()
					&& !(sender instanceof ConsoleCommandSender)) {
				ACLogger.info(spyMsg);
			}
		} else {
			Utils.sI18n(sender, "playerNotFound", "player", args.getString(0));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 2;
	}

}
