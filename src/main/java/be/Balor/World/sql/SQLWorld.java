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
package be.Balor.World.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;


import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.sql.SQLObjectContainer;
import be.Balor.Tools.Warp;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Help.String.Str;
import be.Balor.World.ACWorld;
import belgium.Balor.SQL.Database;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLWorld extends ACWorld {
	private Location defaultSpawn;
	private final Map<String, Object> informations = Collections.synchronizedMap(new HashMap<String, Object>());
	private final Map<String, Integer> mobLimits = Collections.synchronizedMap(new HashMap<String, Integer>());
	private final Map<String, Location> gSpawns = Collections.synchronizedMap(new HashMap<String, Location>());
	private final Map<String, Warp> warps = Collections.synchronizedMap(new HashMap<String, Warp>());
	private static PreparedStatement GET_INFOS, GET_SPAWNS, GET_WARPS;

	private static final Object infoLock = new Object();
	private static final Object spawnLock = new Object();
	private static final Object warpLock = new Object();
	private final long id;
	static {
		initPrepStmt();
	}

	public static void initPrepStmt() {
		GET_INFOS = Database.DATABASE.prepare("SELECT `key`,`info` FROM `ac_w_infos` WHERE `world_id`=?");
		GET_SPAWNS = Database.DATABASE.prepare("SELECT `name`,`x`,`y`,`z`,`yaw`,`pitch` FROM `ac_spawns` WHERE `world_id`=?");
		GET_WARPS = Database.DATABASE.prepare("SELECT `name`,`x`,`y`,`z`,`yaw`,`pitch` FROM `ac_warps` WHERE `world_id`=?");
	}

	/**
	 * @param world
	 */
	SQLWorld(final World world, final long dbID) {
		super(world);
		this.id = dbID;
		synchronized (infoLock) {
			try {
				getDBInfo();
			} catch (final CommunicationsException e) {
				initPrepStmt();
				try {
					getDBInfo();
				} catch (final SQLException e1) {
					ACLogger.severe("Problem while getting the informations of the world" + this.getName(), e1);
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while getting the informations of the world" + this.getName(), e);
			}
		}
		synchronized (spawnLock) {
			try {
				getDBSpawn();
			} catch (final CommunicationsException e) {
				initPrepStmt();
				try {
					getDBSpawn();
				} catch (final SQLException e1) {
					ACLogger.severe("Problem while getting the spawns of the world" + this.getName(), e1);
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while getting the spawns of the world" + this.getName(), e);
			}

		}
		synchronized (warpLock) {
			try {
				getDBWarps();
			} catch (final CommunicationsException e) {
				initPrepStmt();
				try {
					getDBWarps();
				} catch (final SQLException e1) {
					ACLogger.severe("Problem while getting the warps of the world" + this.getName(), e1);
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while getting the warps of the world" + this.getName(), e);
			}

		}
	}

	/**
	 * @throws SQLException
	 */
	private void getDBWarps() throws SQLException {
		GET_WARPS.clearParameters();
		GET_WARPS.setLong(1, id);
		ResultSet rs;
		synchronized (GET_WARPS.getConnection()) {
			rs = GET_WARPS.executeQuery();
		}
		while (rs.next()) {
			final String name = rs.getString("name");
			final Location loc = getLoc(rs);
			if (loc != null) {
				warps.put(name, new Warp(name, loc));
			}
		}
	}

	/**
	 * @throws SQLException
	 */
	private void getDBSpawn() throws SQLException {
		GET_SPAWNS.clearParameters();
		GET_SPAWNS.setLong(1, id);
		ResultSet rs;
		synchronized (GET_SPAWNS.getConnection()) {
			rs = GET_SPAWNS.executeQuery();
		}
		while (rs.next()) {
			final String name = rs.getString("name");
			if (name.equals("none")) {
				defaultSpawn = getLoc(rs);
			} else {
				final Location loc = getLoc(rs);
				if (loc != null) {
					gSpawns.put(name, loc);
				}
			}
		}
	}

	/**
	 * @throws SQLException
	 */
	private void getDBInfo() throws SQLException {
		GET_INFOS.clearParameters();
		GET_INFOS.setLong(1, id);
		ResultSet rs;
		synchronized (GET_INFOS.getConnection()) {
			rs = GET_INFOS.executeQuery();
		}
		while (rs.next()) {
			final String key = rs.getString("key");
			if (key.startsWith("mobLimit:")) {
				mobLimits.put(key.substring(9), rs.getInt("info"));
			} else {
				synchronized (SQLObjectContainer.yaml) {
					informations.put(key, SQLObjectContainer.yaml.load(rs.getString("info")));
				}
			}
		}
		rs.close();
	}

	private Location getLoc(final ResultSet rs) {
		try {
			return new Location(this.getHandle(), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
		} catch (final SQLException e) {
			ACLogger.severe("Problem while getting the location from SQL of world " + this.getName(), e);
			return null;
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setSpawn(org.bukkit.Location)
	 */
	@Override
	public void setSpawn(final Location loc) {
		defaultSpawn = loc;
		setDBSpawn(loc, "none");

	}

	/**
	 * @param loc
	 * @param name
	 */
	private void setDBSpawn(final Location loc, final String name) {
		final PreparedStatement setSpawn = Database.DATABASE
				.prepare("REPLACE INTO `ac_spawns` (`name`,`world_id`,`x`,`y`,`z`,`pitch`,`yaw`) VALUES (?,?,?,?,?,?,?)");
		try {
			setSpawn.clearParameters();
			setSpawn.setString(1, name);
			setSpawn.setLong(2, id);
			setSpawn.setDouble(3, loc.getX());
			setSpawn.setDouble(4, loc.getY());
			setSpawn.setDouble(5, loc.getZ());
			setSpawn.setFloat(6, loc.getPitch());
			setSpawn.setFloat(7, loc.getYaw());
			synchronized (setSpawn.getConnection()) {
				setSpawn.executeUpdate();
			}
		} catch (final SQLException e) {
			ACLogger.severe("Problem while setting the global spawn", e);
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getSpawn()
	 */
	@Override
	public Location getSpawn() {
		if (defaultSpawn == null) {
			return handler.getSpawnLocation();
		}
		return defaultSpawn;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#addWarp(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void addWarp(final String name, final Location loc) {
		warps.put(name, new Warp(name, loc));
		final PreparedStatement insertWarp = Database.DATABASE
				.prepare("REPLACE INTO `ac_warps` (`name`,`world_id`,`x`,`y`,`z`,`pitch`,`yaw`) VALUES (?,?,?,?,?,?,?)");
		try {
			insertWarp.clearParameters();
			insertWarp.setString(1, name);
			insertWarp.setLong(2, id);
			insertWarp.setDouble(3, loc.getX());
			insertWarp.setDouble(4, loc.getY());
			insertWarp.setDouble(5, loc.getZ());
			insertWarp.setFloat(6, loc.getPitch());
			insertWarp.setFloat(7, loc.getYaw());
			synchronized (insertWarp.getConnection()) {
				insertWarp.executeUpdate();
			}
		} catch (final SQLException e) {
			ACLogger.severe("Problem while setting the Warp", e);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarp(java.lang.String)
	 */
	@Override
	public Warp getWarp(final String name) throws WorldNotLoaded, IllegalArgumentException {
		if (name == null || (name != null && name.isEmpty())) {
			throw new IllegalArgumentException("Name can't be null or Empty");
		}
		Warp warp = warps.get(name);
		if (warp == null) {
			final String warpName = Str.matchString(getWarpList(), name);
			if (warpName == null) {
				return null;
			}
			warp = warps.get(warpName);
		}
		return warp;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarpList()
	 */
	@Override
	public Set<String> getWarpList() {
		return Collections.unmodifiableSet(warps.keySet());
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeWarp(java.lang.String)
	 */
	@Override
	public void removeWarp(final String name) {
		warps.remove(name);
		final PreparedStatement deleteWarp = Database.DATABASE.prepare("DELETE FROM `ac_warps` WHERE `name`=? AND `world_id`=?");
		try {
			deleteWarp.clearParameters();
			deleteWarp.setString(1, name);
			deleteWarp.setLong(2, id);
			synchronized (deleteWarp.getConnection()) {
				deleteWarp.executeUpdate();
			}
		} catch (final SQLException e) {
			ACLogger.severe("Problem while deleting the warp", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#addWarp(java.lang.String,
	 * org.bukkit.Location, java.lang.String)
	 */
	@Override
	public void addPermWarp(final String name, final Location loc, final String perm) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInformation(final String info, final Object value) {
		informations.put(info, value);
		final PreparedStatement insertInfo = Database.DATABASE.prepare("REPLACE INTO `ac_w_infos` (`key`,`world_id`,`info`) VALUES (?,?,?)");
		try {
			insertInfo.clearParameters();
			insertInfo.setString(1, info);
			insertInfo.setLong(2, id);
			insertInfo.setString(3, value.toString());
			synchronized (insertInfo.getConnection()) {
				insertInfo.executeUpdate();
			}
		} catch (final SQLException e) {
			ACLogger.severe("Problem while setting the information", e);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeInformation(java.lang.String)
	 */
	@Override
	public void removeInformation(final String info) {
		informations.remove(info);
		final PreparedStatement deleteInfo = Database.DATABASE.prepare("DELETE FROM `ac_w_infos` WHERE `key`=? AND `world_id`=?");
		try {
			deleteInfo.clearParameters();
			deleteInfo.setString(1, info);
			deleteInfo.setLong(2, id);
			synchronized (deleteInfo.getConnection()) {
				deleteInfo.executeUpdate();
			}
		} catch (final SQLException e) {
			ACLogger.severe("Problem while deleting information ", e);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(final String info) {
		return new SQLObjectContainer(informations.get(info));
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformations()
	 */
	@Override
	public Map<String, String> getInformationsList() {
		final TreeMap<String, String> result = new TreeMap<String, String>();
		synchronized (informations) {
			for (final Entry<String, Object> entry : informations.entrySet()) {
				result.put(entry.getKey(), entry.getValue().toString());
			}
		}
		synchronized (mobLimits) {
			for (final Entry<String, Integer> entry : mobLimits.entrySet()) {
				result.put("Limit on " + entry.getKey(), entry.getValue().toString());
			}
		}
		return result;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#forceSave()
	 */
	@Override
	protected void forceSave() {
		DebugLog.INSTANCE.log(Level.WARNING, "Force Save shouldn't be called for SQLWorld", new Throwable());

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setMobLimit(java.lang.String, int)
	 */
	@Override
	public void setMobLimit(final String mob, final int limit) {
		mobLimits.put(mob, limit);

		final PreparedStatement insertInfo = Database.DATABASE.prepare("REPLACE INTO `ac_w_infos` (`key`,`world_id`,`info`) VALUES (?,?,?)");
		try {
			insertInfo.clearParameters();
			insertInfo.setString(1, "mobLimit:" + mob);
			insertInfo.setLong(2, id);
			insertInfo.setInt(3, limit);
			synchronized (insertInfo.getConnection()) {
				insertInfo.executeUpdate();
			}
		} catch (final SQLException e) {
			ACLogger.severe("Problem while setting the information", e);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeMobLimit(java.lang.String)
	 */
	@Override
	public boolean removeMobLimit(final String mob) {
		if (mobLimits.remove(mob) != null) {
			removeInformation("mobLimit:" + mob);
			return true;
		}
		return false;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimit(java.lang.String)
	 */
	@Override
	public int getMobLimit(final String mob) {
		final Integer limit = mobLimits.get(mob);
		if (limit == null) {
			return -1;
		}
		return limit.intValue();
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimitList()
	 */
	@Override
	public Set<String> getMobLimitList() {
		return Collections.unmodifiableSet(mobLimits.keySet());
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawn(java.lang.String)
	 */
	@Override
	public Location getGroupSpawn(final String group) {
		if (group == null) {
			return getSpawn();
		}
		final Location spawn = gSpawns.get(group);
		if (spawn == null) {
			return getSpawn();
		}
		return spawn;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setGroupSpawn(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void setGroupSpawn(final String group, final Location spawn) {
		gSpawns.put(group, spawn);
		setDBSpawn(spawn, group);

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawns()
	 */
	@Override
	protected Map<String, Location> getGroupSpawns() {
		return Collections.unmodifiableMap(gSpawns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformations()
	 */
	@Override
	protected Map<String, Object> getInformations() {
		final HashMap<String, Object> result = new HashMap<String, Object>();
		result.putAll(informations);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimits()
	 */
	@Override
	protected Map<String, Integer> getMobLimits() {
		return Collections.unmodifiableMap(mobLimits);
	}
}
