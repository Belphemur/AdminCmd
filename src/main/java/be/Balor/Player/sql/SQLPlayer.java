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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.SQL.PatPeter.SQLibrary.Database;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.ObjectContainer;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLPlayer extends ACPlayer {
	private final Map<String, Location> homes = new HashMap<String, Location>();
	private final Map<String, Object> infos = new HashMap<String, Object>();
	private final Map<String, Object> powers = new HashMap<String, Object>();
	private final Map<String, Long> kitUses = new HashMap<String, Long>();
	private Location lastLoc;
	private final int id;
	private final static PreparedStatement insertHome, deleteHome, insertInfo,
			deleteInfo, updateLastLoc;
	static {
		insertHome = Database.DATABASE
				.prepare("INSERT OR REPLACE INTO\"ac_homes\" (\"name\",\"player_id\",\"world\",\"x\",\"y\",\"z\",\"yaw\",\"pitch\")"
						+ " VALUES (?,?,?,?,?,?,?,?)");
		deleteHome = Database.DATABASE
				.prepare("DELETE FROM ac_homes WHERE player_id=? AND name=?");
		insertInfo = Database.DATABASE
				.prepare("INSERT OR REPLACE INTO `ac_informations` (`key` ,`player_id` ,`info`) VALUES (?, ?, ?)");
		deleteInfo = Database.DATABASE
				.prepare("DELETE FROM ac_informations WHERE player_id=? AND key=?");
		updateLastLoc = Database.DATABASE
				.prepare("UPDATE `ac_players` SET `world` = ?, `x` = ?, `y` = ?, `z` = ?, `yaw` = ?, `pitch` = ? WHERE `ac_players`.`id` = ?;");

	}

	/**
	 * @param name
	 * @param id
	 */
	public SQLPlayer(final String name, final int id, final Location lastLoc) {
		super(name);
		this.id = id;
		this.lastLoc = lastLoc;
	}
	public SQLPlayer(final Player player, final int id, final Location lastLoc) {
		super(player);
		this.id = id;
		this.lastLoc = lastLoc;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setHome(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void setHome(final String home, final Location loc) {
		homes.put(home, loc);
		synchronized (insertHome) {
			try {
				insertHome.clearParameters();
				insertHome.setString(1, home);
				insertHome.setInt(2, id);
				insertHome.setString(3, loc.getWorld().getName());
				insertHome.setDouble(4, loc.getX());
				insertHome.setDouble(5, loc.getY());
				insertHome.setDouble(6, loc.getZ());
				insertHome.setDouble(7, loc.getYaw());
				insertHome.setDouble(8, loc.getPitch());
				synchronized (insertHome.getConnection()) {
					insertHome.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem with inserting the home in the DB", e);
			}
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeHome(java.lang.String)
	 */
	@Override
	public void removeHome(final String home) {
		homes.remove(home);
		synchronized (deleteHome) {
			try {
				deleteHome.clearParameters();
				deleteHome.setInt(1, id);
				deleteHome.setString(2, home);
				synchronized (deleteHome.getConnection()) {
					deleteHome.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem with deleting the home from the DB", e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getHome(java.lang.String)
	 */
	@Override
	public Location getHome(final String home) {
		return homes.get(home);
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getHomeList()
	 */
	@Override
	public Set<String> getHomeList() {
		return Collections.unmodifiableSet(homes.keySet());
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInformation(final String info, final Object value) {
		infos.put(info, value);
		synchronized (insertInfo) {
			try {
				insertInfo.clearParameters();
				insertInfo.setString(1, info);
				insertInfo.setInt(2, id);
				insertInfo.setString(3, value.toString());
				synchronized (insertInfo.getConnection()) {
					insertInfo.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem with insert info in the DB", e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeInformation(java.lang.String)
	 */
	@Override
	public void removeInformation(final String info) {
		infos.remove(info);
		synchronized (deleteInfo) {
			try {
				deleteInfo.clearParameters();
				deleteInfo.setInt(1, id);
				deleteInfo.setString(2, info);
				synchronized (deleteInfo.getConnection()) {
					deleteInfo.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem with deleting the info from the DB", e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(final String info) {
		return new ObjectContainer(infos.get(info));
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getInformationsList()
	 */
	@Override
	public Set<String> getInformationsList() {
		return Collections.unmodifiableSet(infos.keySet());
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setLastLocation(org.bukkit.Location)
	 */
	@Override
	public void setLastLocation(final Location loc) {
		lastLoc = loc;
		synchronized (updateLastLoc) {
			try {
				updateLastLoc.clearParameters();
				updateLastLoc.setString(1, loc.getWorld().getName());
				updateLastLoc.setDouble(2, loc.getX());
				updateLastLoc.setDouble(3, loc.getY());
				updateLastLoc.setDouble(4, loc.getZ());
				updateLastLoc.setDouble(5, loc.getYaw());
				updateLastLoc.setDouble(6, loc.getPitch());
				updateLastLoc.setInt(7, id);
				synchronized (updateLastLoc.getConnection()) {
					updateLastLoc.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem with updating lastLoc in the DB", e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getLastLocation()
	 */
	@Override
	public Location getLastLocation() {
		return lastLoc;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setPower(be.Balor.Tools.Type,
	 * java.lang.Object)
	 */
	@Override
	public void setPower(final Type power, final Object value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setCustomPower(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setCustomPower(final String power, final Object value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getCustomPower(java.lang.String)
	 */
	@Override
	public ObjectContainer getCustomPower(final String power) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasCustomPower(java.lang.String)
	 */
	@Override
	public boolean hasCustomPower(final String power) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeCustomPower(java.lang.String)
	 */
	@Override
	public void removeCustomPower(final String power) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPower(be.Balor.Tools.Type)
	 */
	@Override
	public ObjectContainer getPower(final Type power) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasPower(be.Balor.Tools.Type)
	 */
	@Override
	public boolean hasPower(final Type power) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removePower(be.Balor.Tools.Type)
	 */
	@Override
	public void removePower(final Type power) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeAllSuperPower()
	 */
	@Override
	public void removeAllSuperPower() {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setLastKitUse(java.lang.String, long)
	 */
	@Override
	public void setLastKitUse(final String kit, final long timestamp) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getLastKitUse(java.lang.String)
	 */
	@Override
	public long getLastKitUse(final String kit) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getKitUseList()
	 */
	@Override
	public Set<String> getKitUseList() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#forceSave()
	 */
	@Override
	public void forceSave() {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPowers()
	 */
	@Override
	public Map<String, String> getPowers() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setPresentation(java.lang.String)
	 */
	@Override
	public void setPresentation(final String presentation) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPresentation()
	 */
	@Override
	public String getPresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
