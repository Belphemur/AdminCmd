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
package belgium.Balor.Workers;

import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;

import com.Balor.files.utils.Utils;
import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
final public class AFKWorker implements Runnable {
	private ConcurrentMap<Player, Long> playerTimeStamp = new MapMaker().concurrencyLevel(10)
			.softValues().makeMap();
	private ConcurrentMap<Player, String> playersAfk = new MapMaker().concurrencyLevel(10)
			.softValues().makeMap();
	private int afkTime = 60000;
	private static AFKWorker instance;

	/**
	 * 
	 */
	private AFKWorker() {

	}

	/**
	 * @return the instance
	 */
	public static AFKWorker getInstance() {
		if (instance == null)
			instance = new AFKWorker();
		return instance;
	}

	/**
	 * @param afkTime
	 *            the afkTime to set
	 */
	public void setAfkTime(int afkTime) {
		if (afkTime > 0)
			this.afkTime = afkTime*1000;
	}

	/**
	 * update a player timeStamp (last time the player moved)
	 * 
	 * @param player
	 * @param timestamp
	 */
	public void updateTimeStamp(Player player) {
		playerTimeStamp.put(player, System.currentTimeMillis());
	}

	/**
	 * Remove the player from the check
	 * 
	 * @param player
	 */
	public void removePlayer(Player player) {
		playerTimeStamp.remove(player);
		playersAfk.remove(player);
	}

	/**
	 * Set the player AFK
	 * 
	 * @param p
	 */
	private void setAfk(Player p) {
		p.getServer().broadcastMessage(Utils.I18n("afk", "player", p.getName()));
		playersAfk.put(p, p.getDisplayName());
		p.setDisplayName(Utils.I18n("afkTitle") + p.getName());
		p.setSleepingIgnored(true);
		playerTimeStamp.remove(p);
	}

	/**
	 * Set the player Online
	 * 
	 * @param p
	 */
	public void setOnline(Player p) {
		p.getServer().broadcastMessage(Utils.I18n("online", "player", p.getName()));
		p.setDisplayName(playersAfk.get(p));
		p.setSleepingIgnored(false);
		playersAfk.remove(p);
	}

	/**
	 * 
	 * @param p
	 * @return if the player is afk
	 */
	public boolean isAfk(Player p) {
		return playersAfk.containsKey(p);
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		long now = System.currentTimeMillis();
		for (Player p : playerTimeStamp.keySet())
			if ((now - playerTimeStamp.get(p)) >= afkTime)
				setAfk(p);
	}

}
