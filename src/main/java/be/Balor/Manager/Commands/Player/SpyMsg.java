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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.RemovePowerTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SpyMsg extends PlayerCommand {

	/**
	 *
	 */
	public SpyMsg() {
		permNode = "admincmd.player.spymsg";
		cmdName = "bal_spymsg";
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
		final String timeOut = args.getValueFlag('t');
		if (Utils.isPlayer(sender)) {
			final ACPlayer acp = ACPlayer.getPlayer(((Player) sender));
			if (acp.hasPower(Type.SPYMSG)) {
				acp.removePower(Type.SPYMSG);
				ACHelper.getInstance().removeSpy((Player) sender);
				Utils.sI18n(sender, "spymsgDisabled");
			} else {
				acp.setPower(Type.SPYMSG);
				ACHelper.getInstance().addSpy((Player) sender);
				Utils.sI18n(sender, "spymsgEnabled");
				if (timeOut == null) {
					return;
				}
				int timeOutValue;
				try {
					timeOutValue = Integer.parseInt(timeOut);
				} catch (final Exception e) {
					Utils.sI18n(sender, "NaN", "number", timeOut);
					return;
				}
				final CommandSender newSender = sender;
				ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
						ACPluginManager.getCorePlugin(),
						new Runnable() {

							@Override
							public void run() {
								ACHelper.getInstance().removeSpy(
										acp.getHandler());
								new RemovePowerTask(acp, Type.SPYMSG, newSender)
										.run();
							}
						},
						Utils.secInTick * ConfigEnum.SCALE_TIMEOUT.getInt()
								* timeOutValue);
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
		return true;
	}

}
