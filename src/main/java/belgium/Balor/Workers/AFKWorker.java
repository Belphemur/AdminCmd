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
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import be.Balor.Tools.Utils;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
final public class AFKWorker {
	private ConcurrentMap<Player, Long> playerTimeStamp;
	private ConcurrentMap<Player, Object> playersAfk = new MapMaker().makeMap();
	private int afkTime = 60000;
	private int kickTime = 180000;
	private AfkChecker afkChecker;
	private KickChecker kickChecker;
	private static AFKWorker instance;

	/**
	 * 
	 */
	private AFKWorker() {
		afkChecker = new AfkChecker();
		kickChecker = new KickChecker();
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
	 * destroy the instance.
	 */
	public static void killInstance() {
		instance = null;
	}

	/**
	 * @return the afkChecker
	 */
	public AfkChecker getAfkChecker() {
		return afkChecker;
	}

	/**
	 * @return the kickChecker
	 */
	public KickChecker getKickChecker() {
		return kickChecker;
	}

	/**
	 * @param afkTime
	 *            the afkTime to set
	 */
	public void setAfkTime(int afkTime) {
		if (afkTime > 0)
			this.afkTime = afkTime * 1000;
	}

	/**
	 * @param kickTime
	 *            the kickTime to set
	 */
	public void setKickTime(int kickTime) {
		if (afkTime > 0)
			this.kickTime = kickTime * 1000 * 60;
	}

	/**
	 * Set the expiration for the values in memory.
	 * 
	 * @param exp
	 */
	public void setExpiration(long exp) {
		playerTimeStamp = new MapMaker().expiration(exp, TimeUnit.MINUTES).makeMap();
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
	public void setAfk(Player p) {
		setAfk(p, null);
	}

	/**
	 * Set the player AFK with the given msg
	 * 
	 * @param p
	 * @param msg
	 */
	public void setAfk(Player p, String msg) {
		if (!InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())) {
			String afkString = Utils.I18n("afk", "player", p.getName());
			if (afkString != null)
				p.getServer().broadcastMessage(afkString);
		}
		if (msg == null || (msg != null && msg.isEmpty()))
			playersAfk.put(p, Long.valueOf(System.currentTimeMillis()));
		else
			playersAfk.put(p, msg);
		p.setSleepingIgnored(true);
	}

	/**
	 * Send the corresponding afk message to the user
	 * 
	 * @param sender
	 * @param buddy
	 */
	public void sendAfkMessage(Player sender, Player buddy) {
		Object obj = playersAfk.get(buddy);
		if (obj != null) {
			Utils.sI18n(sender, "noteAfk", "player", buddy.getName());
			if (obj instanceof String)
				sender.sendMessage((String) obj);
			else if (obj instanceof Long) {
				Long[] time = Utils.getElapsedTime((Long) obj);
				Utils.sI18n(sender, "idleTime", "mins", time[1].toString());
			}

		}
	}

	/**
	 * Set the player Online
	 * 
	 * @param p
	 */
	public void setOnline(Player p) {
		if (!InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())) {
			String online = Utils.I18n("online", "player", p.getName());
			if (online != null)
				p.getServer().broadcastMessage(online);
		}
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

	private class AfkChecker implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			long now = System.currentTimeMillis();
			for (Player p : Utils.getOnlinePlayers())
				if (playerTimeStamp.containsKey(p) && !playersAfk.containsKey(p)
						&& (now - playerTimeStamp.get(p)) >= afkTime)
					setAfk(p);

		}

	}

	private class KickChecker implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			long now = System.currentTimeMillis();
			for (Player p : playersAfk.keySet()) {
				if (now - playerTimeStamp.get(p) >= kickTime) {
					p.kickPlayer(Utils.I18n("afkKick"));
					playersAfk.remove(p);
					playerTimeStamp.remove(p);
				}
			}

		}

	}

}