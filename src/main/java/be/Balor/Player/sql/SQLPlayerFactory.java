/*This file is part of AdminCmd.

    AdminCmd is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AdminCmd is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.*/
package be.Balor.Player.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.SQL.PatPeter.SQLibrary.Database;

import org.bukkit.entity.Player;

import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Player.IPlayerFactory;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLPlayerFactory implements IPlayerFactory {
	private final PreparedStatement insertPlayer;
	private final Map<String, Long> players = new HashMap<String, Long>();

	/**
 * 
 */
	public SQLPlayerFactory() {
		insertPlayer = Database.DATABASE
				.prepare("INSERT INTO `ac_players` (`name`) VALUES (?);");
		final ResultSet rs = Database.DATABASE
				.query("SELECT `name`,`id` FROM `ac_players`");
		try {
			while (rs.next()) {
				players.put(rs.getString("name"), rs.getLong("id"));
			}
			rs.close();
		} catch (final SQLException e) {
			ACLogger.severe("Problem when getting players from the DB", e);
		}
		DebugLog.INSTANCE.info("Players found : "
				+ Joiner.on(", ").join(players.keySet()));

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.IPlayerFactory#createPlayer(java.lang.String)
	 */
	@Override
	public ACPlayer createPlayer(final String playername) {
		final Long id = players.get(playername);
		if (id == null) {
			return new EmptyPlayer(playername);
		} else {
			return new SQLPlayer(playername, id);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see
	 * be.Balor.Player.IPlayerFactory#createPlayer(org.bukkit.entity.Player)
	 */
	@Override
	public ACPlayer createPlayer(final Player player) {
		final Long id = players.get(player.getName());
		if (id == null) {
			return new EmptyPlayer(player);
		} else {
			return new SQLPlayer(player, id);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.IPlayerFactory#getExistingPlayers()
	 */
	@Override
	public Set<String> getExistingPlayers() {
		return Collections.unmodifiableSet(players.keySet());
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.IPlayerFactory#addExistingPlayer(java.lang.String)
	 */
	@Override
	public void addExistingPlayer(final String player) {
		if (!players.containsKey(player)) {
			try {
				insertPlayer.clearParameters();
				insertPlayer.setString(1, player);
				synchronized (insertPlayer.getConnection()) {
					insertPlayer.executeUpdate();
				}
				final ResultSet rs = insertPlayer.getGeneratedKeys();
				if (rs.next()) {
					players.put(player, rs.getLong(1));
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem when adding player to the DB", e);
			}
		}

	}

}
