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

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.RemovePowerTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Fly extends PlayerCommand {

	private enum FlyMode {
		OLD, NEW;
	}

	/**
	 *
	 */
	public Fly() {
		permNode = "admincmd.player.fly";
		cmdName = "bal_fly";
		other = true;
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
		Player player = null;
		final String timeOut = args.getValueFlag('t');
		try {
			player = Utils.getUserParam(sender, args, permNode);
		} catch (final PlayerNotFound e) {
			sender.sendMessage(e.getMessage());
			return;
		}
		if (player != null) {
			if (args.hasFlag('o')) {
				setFly(sender, player, timeOut, Type.FLY_OLD, FlyMode.OLD, args);
			} else {
				setFly(sender, player, timeOut, Type.FLY, FlyMode.NEW, args);
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
		return args != null;
	}

	private void setFly(final CommandSender sender, final Player player,
			final String timeOut, final Type power, final FlyMode c,
			final CommandArgs args) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(player));
		final ACPlayer acp = ACPlayer.getPlayer(player);
		final String powerValueString = args.getValueFlag('p');
		float powerFloat = 0;
		if (powerValueString != null) {
			try {
				powerFloat = Float.parseFloat(powerValueString);
			} catch (final NumberFormatException e) {}
		}
		powerFloat = powerFloat > ConfigEnum.MAX_FLY.getFloat()
				? ConfigEnum.MAX_FLY.getFloat()
				: powerFloat;
		if (acp.hasPower(power)) {
			acp.removePower(power);
			if (c == FlyMode.NEW) {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
			player.setFallDistance(0.0F);
			Utils.sI18n(player, "flyDisabled");
			if (!player.equals(sender)) {
				Utils.sI18n(sender, "flyDisabledTarget", replace);
			}
		} else {
			if (c == FlyMode.NEW) {
				player.setAllowFlight(true);
				player.setFlying(true);
				acp.setPower(power,
						powerFloat == 0
								? ConfigEnum.DFLY.getFloat()
								: powerFloat);
			} else {
				acp.setPower(power,
						powerFloat == 0
								? ConfigEnum.DFLY.getFloat()
								: powerFloat);
			}
			player.setFallDistance(1F);
			Utils.sI18n(player, "flyEnabled");
			if (!player.equals(sender)) {
				Utils.sI18n(sender, "flyEnabledTarget", replace);
			}
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
			ACPluginManager.getScheduler().runTaskLaterAsynchronously(
					ACPluginManager.getCorePlugin(),
					new RemovePowerTask(acp, power, sender),
					Utils.secInTick * ConfigEnum.SCALE_TIMEOUT.getInt()
							* timeOutValue);
		}
	}
}
