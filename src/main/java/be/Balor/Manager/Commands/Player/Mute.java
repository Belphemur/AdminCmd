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
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.CommandUtils.Immunity;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Lister.Lister;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Mute extends PlayerCommand {
	private PermChild cmdMute;

	/**
	 *
	 */
	public Mute() {
		permNode = "admincmd.player.mute";
		cmdName = "bal_mute";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) throws ActionNotPermitedException, PlayerNotFound {
		final Player player = Users.getPlayer(args.getString(0));
		if (args.hasFlag('c') && !PermissionManager.hasPerm(sender, cmdMute.getPermName())) {
			return;
		}
		if (player != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Users.getPlayerName(player));
			final ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (acp instanceof EmptyPlayer) {
				LocaleManager.sI18n(sender, "playerNotFound", replace);
				return;
			}
			if (!Immunity.checkImmunity(sender, player)) {
				LocaleManager.sI18n(sender, "insufficientLvl");
				return;
			}
			String reason;
			if (args.hasFlag('r')) {
				reason = args.getValueFlag('r');
			} else {
				reason = "None";
			}
			replace.put("reason", reason);
			if (args.hasFlag('c')) {
				if (!acp.hasPower(Type.MUTED_COMMAND)) {
					String msg = "Server Admin";
					if (Users.isPlayer(sender, false)) {
						msg = Users.getPlayerName((Player) sender);
					}

					if (!player.equals(sender)) {
						LocaleManager.sI18n(sender, "commandMuteEnabledTarget", replace);
					}
					if (args.length >= 2) {
						Integer tmpMute = null;
						try {
							tmpMute = args.getInt(args.length - 1);
							final String unmute = player.getName();
							final CommandSender senderFinal = sender;
							acp.setPower(Type.MUTED_COMMAND, "Muted(including commands) by " + msg);
							ACPluginManager.getScheduler().runTaskLaterAsynchronously(getPlugin(), new Runnable() {

								@Override
								public void run() {
									ACPlayer.getPlayer(unmute).removePower(Type.MUTED_COMMAND);
									LocaleManager.sI18n(senderFinal, "commandMuteDisabledTarget", "player", unmute);
									final Lister list = Lister.getLister(Lister.List.MUTE, false);
									if (list != null) {
										list.update();
									}
								}
							}, 20 * 60 * tmpMute);

						} catch (final Exception e) {
						}
						if (tmpMute == null) {
							acp.setPower(Type.MUTED_COMMAND, "Permanently muted(including commands) by " + msg);
							replace.put("time", "permanently");
							LocaleManager.sI18n(player, "commandMuteEnabled", "reason", reason);
						} else {
							acp.setPower(Type.MUTED_COMMAND, "Muted(including commands) by " + msg + " for " + tmpMute + " minutes");
							replace.put("minutes", tmpMute.toString());
							replace.put("time", "for" + tmpMute.toString() + " minutes");
							LocaleManager.sI18n(player, "commandTmpMuteEnabled", replace);
						}
					} else {
						acp.setPower(Type.MUTED_COMMAND, "Permanently muted(including commands) by " + msg);
						replace.put("time", "permanently");
						LocaleManager.sI18n(player, "commandMuteEnabled", replace);
					}
					if (args.hasFlag('b')) {
						replace.put("muter", msg);
						Users.broadcastMessage(LocaleHelper.MUTE_BROADCAST.getLocale(replace));
					}
				} else {
					LocaleManager.sI18n(sender, "alreadyCommandMuted");
				}
				return;
			}
			if (!acp.hasPower(Type.MUTED)) {
				String msg = "Server Admin";
				if (Users.isPlayer(sender, false)) {
					msg = Users.getPlayerName((Player) sender);
				}
				if (!player.equals(sender)) {
					LocaleManager.sI18n(sender, "muteEnabledTarget", replace);
				}
				if (args.length >= 2) {
					Integer tmpMute = null;
					try {
						tmpMute = args.getInt(args.length - 1);
						final String unmute = player.getName();
						final CommandSender senderFinal = sender;
						ACPluginManager.getScheduler().runTaskLaterAsynchronously(getPlugin(), new Runnable() {

							@Override
							public void run() {
								ACPlayer.getPlayer(unmute).removePower(Type.MUTED);
								LocaleManager.sI18n(senderFinal, "muteDisabledTarget", "player", unmute);
								final Lister list = Lister.getLister(Lister.List.MUTE, false);
								if (list != null) {
									list.update();
								}
							}
						}, 20 * 60 * tmpMute);

					} catch (final Exception e) {
					}
					if (tmpMute == null) {
						acp.setPower(Type.MUTED, "Permanently muted by " + msg);
						replace.put("time", "permanently");
						LocaleManager.sI18n(player, "muteEnabled", replace);
					} else {
						acp.setPower(Type.MUTED, "Muted by " + msg + " for " + tmpMute + " minutes");
						replace.put("minutes", tmpMute.toString());
						replace.put("time", "for" + tmpMute.toString() + " minutes");
						LocaleManager.sI18n(player, "tmpMuteEnabled", replace);
					}
				} else {
					acp.setPower(Type.MUTED, "Permanently muted by " + msg);
					replace.put("time", "permanently");
					LocaleManager.sI18n(player, "muteEnabled", replace);
				}
				if (args.hasFlag('b')) {
					replace.put("muter", msg);
					Users.broadcastMessage(LocaleHelper.MUTE_BROADCAST.getLocale(replace));
				}
			} else {
				LocaleManager.sI18n(sender, "alreadyMuted");
			}
			final Lister list = Lister.getLister(Lister.List.MUTE, false);
			if (list != null) {
				list.update();
			}
		}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		cmdMute = new PermChild(permNode + ".command");
		final PermParent parent = new PermParent(permNode + ".*");
		parent.addChild(cmdMute);
		permParent.addChild(parent);
		super.registerBukkitPerm();

	}
}
