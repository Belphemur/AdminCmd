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
package be.Balor.Tools.Threads;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class RemovePowerTask implements Runnable {

	private final ACPlayer player;
	private final Type power;
	private final CommandSender sender;
	private final Map<String, String> replace = new HashMap<String, String>();

	/**
	 * @param player
	 * @param power
	 */
	public RemovePowerTask(final ACPlayer player, final Type power,
			final CommandSender sender) {
		super();
		this.player = player;
		this.power = power;
		this.sender = sender;
		replace.put("power", this.power.display());
		if (this.player.isOnline()) {
			replace.put("player", Utils.getPlayerName(this.player.getHandler()));
		} else {
			replace.put("player", this.player.getName());
		}
		replace.put("reason", "Time expired");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		player.removePower(power);
		final Player handler = player.getHandler();
		if (handler != null) {
			Utils.sI18n(handler, "timeOutPower", "power", power.display());
			if (power == Type.FLY) {
				handler.setAllowFlight(false);
				handler.setFlying(false);
			}
		}
		if (!sender.equals(handler)) {
			if (Utils.isPlayer(sender, false) && !((Player) sender).isOnline()) {
				return;
			}
			Utils.sI18n(sender, "timeOutPowerSender", replace);
		}
	}

}
