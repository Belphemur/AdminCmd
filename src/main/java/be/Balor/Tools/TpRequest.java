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
package be.Balor.Tools;

import java.util.HashMap;

import org.bukkit.entity.Player;

import be.Balor.Player.ACPlayer;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TpRequest {
	private Player from, to;
	private long timeOut;

	/**
	 *
	 */
	public TpRequest(Player from, Player to) {
		this.from = from;
		this.to = to;
		timeOut = System.currentTimeMillis()
				+ (ACHelper.getInstance().getConfInt(
						"tpRequestTimeOutInMinutes") * 60000);
	}

	public void teleport(Player sender) {
		if (System.currentTimeMillis() > timeOut) {
			Utils.sI18n(sender, "tpRequestTimeOut");
			return;
		}
		if (from != null && to != null) {
			final String fromName = Utils.getPlayerName(from);
			final String toName = Utils.getPlayerName(to);
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					ACPlayer.getPlayer(from)
							.setLastLocation(from.getLocation());
					from.teleport(to);
					HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("fromPlayer", fromName);
					replace.put("toPlayer", toName);
					Utils.sI18n(to, "tp", replace);
					Utils.sI18n(from, "tp", replace);

				}
			});

		}
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from == null ? "null" : from.getName();
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to == null ? "null" : to.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return from == null || to == null ? "false" : from.getName() + ":"
				+ to.getName();
	}
}
