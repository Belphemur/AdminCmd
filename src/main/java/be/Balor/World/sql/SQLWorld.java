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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import lib.SQL.PatPeter.SQLibrary.Database;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Warp;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLWorld extends ACWorld {
	private Location defaultSpawn;
	private final Map<String, Object> informations = new HashMap<String, Object>();
	private final Map<String, Integer> mobLimits = new HashMap<String, Integer>();
	private final Map<String, Location> gSpawns = new HashMap<String, Location>();
	private final Map<String, Location> warps = new HashMap<String, Location>();
	private final static PreparedStatement DEF_SPAWN, G_SPAWN, INSERT_INFO,
			DELETE_INFO;
	private final long id;
	static {
		DEF_SPAWN = Database.DATABASE
				.prepare("INSERT OR REPLACE INTO 'ac_spawns' ('name','world_id','x','y','z','pitch','yaw') VALUES ('none',?,?,?,?,?,?)");
		G_SPAWN = Database.DATABASE
				.prepare("INSERT OR REPLACE INTO 'ac_spawns' ('name','world_id','x','y','z','pitch','yaw') VALUES (?,?,?,?,?,?,?)");
		INSERT_INFO = Database.DATABASE
				.prepare("INSERT OR REPLACE INTO 'ac_w_infos' ('key','world_id','info') VALUES (?,?,?)");
		DELETE_INFO = Database.DATABASE
				.prepare("DELETE FROM 'ac_w_infos' WHERE key=? AND world_id=?");
	}

	/**
	 * @param world
	 */
	public SQLWorld(final World world, final long id) {
		super(world);
		this.id = id;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setSpawn(org.bukkit.Location)
	 */
	@Override
	public void setSpawn(final Location loc) {
		defaultSpawn = loc;
		synchronized (DEF_SPAWN) {
			try {
				DEF_SPAWN.clearParameters();
				DEF_SPAWN.setLong(1, id);
				DEF_SPAWN.setDouble(2, loc.getX());
				DEF_SPAWN.setDouble(3, loc.getY());
				DEF_SPAWN.setDouble(4, loc.getZ());
				DEF_SPAWN.setFloat(5, loc.getPitch());
				DEF_SPAWN.setFloat(6, loc.getYaw());
				synchronized (DEF_SPAWN.getConnection()) {
					DEF_SPAWN.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while setting the global spawn", e);
			}
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getSpawn()
	 */
	@Override
	public Location getSpawn() {
		if (DEF_SPAWN == null) {
			return handler.getSpawnLocation();
		}
		return defaultSpawn;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getDifficulty()
	 */
	@Override
	public Difficulty getDifficulty() throws WorldNotLoaded {
		final Object obj = informations.get("difficulty");
		if (obj == null) {
			return handler.getDifficulty();
		}
		if (obj instanceof Difficulty) {
			return (Difficulty) obj;
		}
		return handler.getDifficulty();
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setDifficulty(org.bukkit.Difficulty)
	 */
	@Override
	public void setDifficulty(final Difficulty dif) {
		informations.put("difficulty", dif);
		handler.setDifficulty(dif);
		synchronized (INSERT_INFO) {
			try {
				INSERT_INFO.clearParameters();
				INSERT_INFO.setString(1, "difficulty");
				INSERT_INFO.setLong(2, id);
				INSERT_INFO.setInt(3, dif.getValue());
				synchronized (INSERT_INFO.getConnection()) {
					INSERT_INFO.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while setting the difficulty", e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#addWarp(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void addWarp(final String name, final Location loc) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarp(java.lang.String)
	 */
	@Override
	public Warp getWarp(final String name) throws WorldNotLoaded,
			IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarpList()
	 */
	@Override
	public Set<String> getWarpList() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeWarp(java.lang.String)
	 */
	@Override
	public void removeWarp(final String name) {
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
		synchronized (INSERT_INFO) {
			try {
				INSERT_INFO.clearParameters();
				INSERT_INFO.setString(1, info);
				INSERT_INFO.setLong(2, id);
				INSERT_INFO.setString(3, value.toString());
				synchronized (INSERT_INFO.getConnection()) {
					INSERT_INFO.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while setting the information", e);
			}

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
		synchronized (DELETE_INFO) {
			try {
				DELETE_INFO.clearParameters();
				DELETE_INFO.setString(1, info);
				DELETE_INFO.setLong(2, id);
				synchronized (DELETE_INFO.getConnection()) {
					DELETE_INFO.executeUpdate();
				}
			} catch (final SQLException e) {
				ACLogger.severe("Problem while deleting information ", e);
			}
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(final String info) {
		return new ObjectContainer(informations.get(info));
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformations()
	 */
	@Override
	public Map<String, String> getInformationsList() {
		final TreeMap<String, String> result = new TreeMap<String, String>();
		for (final Entry<String, Object> entry : informations.entrySet()) {
			result.put(entry.getKey(), entry.getValue().toString());
		}
		for (final Entry<String, Integer> entry : mobLimits.entrySet()) {
			result.put("Limit on " + entry.getKey(), entry.getValue()
					.toString());
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
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setMobLimit(java.lang.String, int)
	 */
	@Override
	public void setMobLimit(final String mob, final int limit) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeMobLimit(java.lang.String)
	 */
	@Override
	public boolean removeMobLimit(final String mob) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimit(java.lang.String)
	 */
	@Override
	public int getMobLimit(final String mob) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimitList()
	 */
	@Override
	public Set<String> getMobLimitList() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawn(java.lang.String)
	 */
	@Override
	public Location getGroupSpawn(final String group) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setGroupSpawn(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void setGroupSpawn(final String group, final Location spawn) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawns()
	 */
	@Override
	protected Map<String, Location> getGroupSpawns() {
		// TODO Auto-generated method stub
		return null;
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
		for (final Entry<String, Integer> entry : mobLimits.entrySet()) {
			result.put("mobLimit:" + entry.getKey(), entry.getValue());
		}
		return result;
	}

}
