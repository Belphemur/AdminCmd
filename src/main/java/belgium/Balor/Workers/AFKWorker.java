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

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Listeners.Events.ACGoAFKEvent;
import be.Balor.Listeners.Events.ACGoAFKEvent.Reason;
import be.Balor.Listeners.Events.ACReturnedAFKEvent;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
final public class AFKWorker {
	private int afkTime = 60000;
	private int kickTime = 180000;
	private final ConcurrentMap<Player, Long> playerTimeStamp = new MapMaker()
			.weakKeys().makeMap();
	private final ConcurrentMap<Player, Object> playersAfk = new MapMaker()
			.weakKeys().makeMap();
	private final AfkChecker afkChecker;
	private final KickChecker kickChecker;
	private static AFKWorker instance = new AFKWorker();

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
		return instance;
	}

	public static AFKWorker createInstance() {
		if (instance == null) {
			instance = new AFKWorker();
		}
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
	public void setAfkTime(final int afkTime) {
		if (afkTime > 0) {
			this.afkTime = afkTime * 1000;
		}
	}

	/**
	 * @param kickTime
	 *            the kickTime to set
	 */
	public void setKickTime(final int kickTime) {
		if (afkTime > 0) {
			this.kickTime = kickTime * 1000 * 60;
		}

	}

	/**
	 * Get the number of afk players
	 * 
	 * @return
	 */
	public int nbAfk() {
		return playersAfk.size();
	}

	/**
	 * update a player timeStamp (last time the player moved)
	 * 
	 * @param player
	 * @param timestamp
	 */
	public void updateTimeStamp(final Player player) {
		playerTimeStamp.put(player, System.currentTimeMillis());
	}

	/**
	 * Remove the player from the check
	 * 
	 * @param player
	 */
	public void removePlayer(final Player player) {
		playersAfk.remove(player);
		playerTimeStamp.remove(player);
	}

	/**
	 * Set player afk with the wanted reason
	 * 
	 * @param p
	 * @param reason
	 */
	public void setAfk(final Player p, final ACGoAFKEvent.Reason reason) {
		setAfk(p, null, reason);
	}

	/**
	 * Set the player AFK with the given msg
	 * 
	 * @param p
	 * @param message
	 */
	public void setAfk(final Player p, final String message,
			final ACGoAFKEvent.Reason reason) {
		final ACGoAFKEvent event = new ACGoAFKEvent(p, reason, message);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		final String msg = event.getMessage();
		if (!InvisibleWorker.getInstance().hasInvisiblePowers(p)
				&& !ACPlayer.getPlayer(p).hasPower(Type.FAKEQUIT)) {
			String afkString = LocaleManager.I18n("afk", "player",
					Users.getPlayerName(p, null));
			if (afkString != null) {
				afkString += (msg != null ? " : " + ChatColor.GOLD + msg : "");
			}
			Users.broadcastMessage(afkString);

		}
		if (msg == null || (msg != null && msg.isEmpty())) {
			playersAfk.put(p, Long.valueOf(System.currentTimeMillis()));
		} else {
			playersAfk.put(p, msg);
		}
		p.setSleepingIgnored(true);
	}

	/**
	 * Send the corresponding afk message to the user
	 * 
	 * @param sender
	 * @param buddy
	 */
	public void sendAfkMessage(final CommandSender sender, final Player buddy) {
		if (InvisibleWorker.getInstance().hasInvisiblePowers(buddy)
				|| ACPlayer.getPlayer(buddy.getName()).hasPower(Type.FAKEQUIT)) {
			return;
		}
		final Object obj = playersAfk.get(buddy);
		if (obj != null) {
			LocaleManager.sI18n(sender, "noteAfk", "player",
					Users.getPlayerName(buddy, sender));
			if (obj instanceof String) {
				sender.sendMessage((String) obj);
			} else if (obj instanceof Long) {
				final Long[] time = Utils.getElapsedTime((Long) obj);
				LocaleManager.sI18n(sender, "idleTime", "mins", time[2].toString());
			}

		}
	}

	/**
	 * Set the player Online
	 * 
	 * @param p
	 */
	public void setOnline(final Player p) {
		if (!InvisibleWorker.getInstance().hasInvisiblePowers(p)
				&& !ACPlayer.getPlayer(p.getName()).hasPower(Type.FAKEQUIT)) {
			final String online = LocaleManager.I18n("online", "player",
					Users.getPlayerName(p, null));
			if (online != null) {
				Users.broadcastMessage(online);
			}
		}
		p.setSleepingIgnored(false);
		playersAfk.remove(p);
		Bukkit.getPluginManager().callEvent(new ACReturnedAFKEvent(p));
	}

	/**
	 * 
	 * @param p
	 * @return if the player is afk
	 */
	public boolean isAfk(final Player p) {
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
			final long now = System.currentTimeMillis();
			for (final Player p : Users.getOnlinePlayers()) {
				final Long timeStamp = playerTimeStamp.get(p);
				if (timeStamp != null && !playersAfk.containsKey(p)
						&& (now - timeStamp) >= afkTime) {
					setAfk(p, Reason.AUTO);
				}
			}

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
			final long now = System.currentTimeMillis();
			for (final Player p : playersAfk.keySet()) {
				final Long timeStamp = playerTimeStamp.get(p);
				if (timeStamp != null
						&& (now - timeStamp >= kickTime)
						&& !PermissionManager.hasPerm(p,
								"admincmd.player.noafkkick")) {
					ACPluginManager.scheduleSyncTask(new Runnable() {

						@Override
						public void run() {
							final HashMap<String, String> replace = new HashMap<String, String>();
							replace.put("player", Users.getPlayerName(p));
							p.kickPlayer(LocaleManager.I18n("afkKick"));
							final String msg = LocaleHelper.AFK_KICK_BCAST
									.getLocale(replace);
							Users.broadcastMessage(msg);
							ACLogger.info(msg);
							playersAfk.remove(p);
							playerTimeStamp.remove(p);

						}
					});
				}
			}

		}

	}

}