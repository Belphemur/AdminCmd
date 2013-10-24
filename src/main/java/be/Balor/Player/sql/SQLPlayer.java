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
import java.sql.Types;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Help.String.Str;
import be.Balor.World.ACWorld;
import belgium.Balor.SQL.Database;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLPlayer extends ACPlayer {
	private final Map<String, Location> homes = Collections
			.synchronizedMap(new HashMap<String, Location>());
	private final Map<String, Object> infos = Collections
			.synchronizedMap(new HashMap<String, Object>());
	private final Map<Type, Object> powers = Collections
			.synchronizedMap(new EnumMap<Type, Object>(Type.class));
	private final Map<String, Object> customPowers = Collections
			.synchronizedMap(new HashMap<String, Object>());
	private final Map<String, Long> kitUses = Collections
			.synchronizedMap(new HashMap<String, Long>());
	private Location lastLoc;
	private final long id;

	/**
	 * @param name
	 * @param id
	 */
	SQLPlayer(final String name, final long id) {
		super(name);
		this.id = id;
		init();
	}

	SQLPlayer(final Player player, final long id) {
		super(player);
		this.id = id;
		init();
	}

	private void init() {
		try {
			getDBLastLoc();

		} catch (final SQLException e) {
			ACLogger.severe("Problem with getting last location from the DB", e);
		}

		try {
			getDBHomes();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with getting homes from the DB", e);
		}

		try {
			getDBPowers();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with getting powers from the DB", e);
		}

		try {
			getDBInfos();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with getting informations from the DB", e);
		}
		try {
			getDBKitUses();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with getting kit uses from the DB", e);
		}
	}

	/**
	 * @throws SQLException
	 */
	private void getDBKitUses() throws SQLException {

		PreparedStatement getKitUses;

		getKitUses = Database.DATABASE
				.prepare("SELECT `kit`,`use` FROM `ac_kit_uses` WHERE `player_id` = ?");
		try {
			getKitUses.clearParameters();
			getKitUses.setLong(1, id);
			ResultSet rs;
			rs = getKitUses.executeQuery();

			while (rs.next()) {
				kitUses.put(rs.getString("kit"), rs.getLong("use"));
			}

		} finally {
			Database.DATABASE.closePrepStmt(getKitUses);
		}
	}

	/**
	 * @throws SQLException
	 */
	private void getDBInfos() throws SQLException {
		PreparedStatement getInfos;

		getInfos = Database.DATABASE
				.prepare("SELECT `key`,`info` FROM `ac_informations` WHERE `player_id` = ?");
		try {
			getInfos.clearParameters();
			getInfos.setLong(1, id);
			ResultSet rs;
			rs = getInfos.executeQuery();
			while (rs.next()) {
				synchronized (SQLObjectContainer.yaml) {
					infos.put(rs.getString("key"),
							SQLObjectContainer.yaml.load(rs.getString("info")));
				}
			}
		} finally {
			Database.DATABASE.closePrepStmt(getInfos);
		}

	}

	/**
	 * @throws SQLException
	 */
	private void getDBPowers() throws SQLException {
		final PreparedStatement getPowers = Database.DATABASE
				.prepare("SELECT `key`,`info` FROM `ac_powers` WHERE `player_id` = ?");
		try {
			getPowers.clearParameters();
			getPowers.setLong(1, id);
			ResultSet rs;
			rs = getPowers.executeQuery();
			while (rs.next()) {
				final String powerName = rs.getString("key");
				final Type power = Type.matchType(powerName);
				synchronized (SQLObjectContainer.yaml) {
					if (power == null) {
						customPowers.put(powerName, SQLObjectContainer.yaml
								.load(rs.getString("info")));
					} else {
						powers.put(power, SQLObjectContainer.yaml.load(rs
								.getString("info")));
					}
				}
			}
		} finally {
			Database.DATABASE.closePrepStmt(getPowers);
		}

	}

	/**
	 * @throws SQLException
	 */
	private void getDBHomes() throws SQLException {
		final PreparedStatement getHomes = Database.DATABASE
				.prepare("SELECT `name`,`world`,`x`,`y`,`z`,`yaw`,`pitch` FROM `ac_homes` WHERE `player_id` = ?");
		try {
			getHomes.clearParameters();
			getHomes.setLong(1, id);
			ResultSet rs;
			rs = getHomes.executeQuery();
			while (rs.next()) {
				final String worldName = rs.getString("world");
				final World world = loadWorld(worldName);
				if (world != null) {
					homes.put(
							rs.getString("name"),
							new Location(world, rs.getDouble("x"), rs
									.getDouble("y"), rs.getDouble("z"), rs
									.getFloat("yaw"), rs.getFloat("pitch")));
				} else {
					ACLogger.warning("The World " + worldName
							+ " is not loaded");
				}
			}
		} finally {
			Database.DATABASE.closePrepStmt(getHomes);
		}
	}

	/**
	 * @throws SQLException
	 * 
	 */
	private void getDBLastLoc() throws SQLException {
		final PreparedStatement getLastLoc = Database.DATABASE
				.prepare("SELECT `world`,`x`,`y`,`z`,`yaw`,`pitch` FROM ac_players WHERE id=?");
		try {
			getLastLoc.clearParameters();
			getLastLoc.setLong(1, id);
			ResultSet rs;
			rs = getLastLoc.executeQuery();

			if (rs.next()) {
				final String worldName = rs.getString("world");
				if (worldName != null && !worldName.isEmpty()) {
					final World world = loadWorld(worldName);
					if (world != null) {
						lastLoc = new Location(world, rs.getDouble("x"),
								rs.getDouble("y"), rs.getDouble("z"),
								rs.getFloat("yaw"), rs.getFloat("pitch"));
					} else {
						ACLogger.warning("The World " + worldName
								+ " is not loaded");
					}
				}
			}

		} finally {
			Database.DATABASE.closePrepStmt(getLastLoc);
		}
	}

	/**
	 * @param worldName
	 * @return
	 */
	private World loadWorld(final String worldName) {
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			try {
				world = ACWorld.getWorld(worldName).getHandle();
			} catch (final WorldNotLoaded e) {
			}
		}
		return world;
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

		try {
			final PreparedStatement insertHome = Database.DATABASE
					.prepare("REPLACE INTO `ac_homes` (`name`, `player_id`, `world`, `x`, `y`, `z`, `yaw`, `pitch`)"
							+ " VALUES (?,?,?,?,?,?,?,?)");
			insertHome.setString(1, home);
			insertHome.setLong(2, id);
			insertHome.setString(3, loc.getWorld().getName());
			insertHome.setDouble(4, loc.getX());
			insertHome.setDouble(5, loc.getY());
			insertHome.setDouble(6, loc.getZ());
			insertHome.setFloat(7, loc.getYaw());
			insertHome.setFloat(8, loc.getPitch());
			insertHome.executeUpdate();

		} catch (final SQLException e) {
			ACLogger.severe("Problem with inserting the home in the DB", e);
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeHome(java.lang.String)
	 */
	@Override
	public void removeHome(final String home) {
		if (homes.remove(home) != null) {
			final PreparedStatement deleteHome = Database.DATABASE
					.prepare("delete FROM `ac_homes` WHERE `player_id`=? AND `name`=?");

			try {
				deleteHome.setLong(1, id);
				deleteHome.setString(2, home);
				deleteHome.executeUpdate();

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
		Location loc = homes.get(home);
		if (loc == null) {
			final String homeName = Str.matchString(getHomeList(), name);
			if (homeName == null) {
				return null;
			}
			loc = homes.get(homeName);
		}
		return loc;
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
		if (value == null) {
			return;
		}
		infos.put(info, value);
		final PreparedStatement insertInfo = Database.DATABASE
				.prepare("REPLACE INTO `ac_informations` (`key` ,`player_id` ,`info`) VALUES (?, ?, ?)");
		try {
			insertInfo.setString(1, info);
			insertInfo.setLong(2, id);
			insertInfo.setString(3, value.toString());
			insertInfo.executeUpdate();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with insert info in the DB", e);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeInformation(java.lang.String)
	 */
	@Override
	public void removeInformation(final String info) {
		if (infos.remove(info) != null) {
			final PreparedStatement deleteInfo = Database.DATABASE
					.prepare("delete FROM `ac_informations` WHERE `player_id`=? AND `key`=?");
			try {
				deleteInfo.setLong(1, id);
				deleteInfo.setString(2, info);
				deleteInfo.executeUpdate();

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
		return new SQLObjectContainer(infos.get(info));
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
		final PreparedStatement updateLastLoc = Database.DATABASE
				.prepare("UPDATE `ac_players` SET `world` = ?, `x` = ?, `y` = ?, `z` = ?, `yaw` = ?, `pitch` = ? WHERE `ac_players`.`id` = ?;");
		try {
			updateLastLoc.clearParameters();
			if (loc != null) {
				updateLastLoc.setString(1, loc.getWorld().getName());
				updateLastLoc.setDouble(2, loc.getX());
				updateLastLoc.setDouble(3, loc.getY());
				updateLastLoc.setDouble(4, loc.getZ());
				updateLastLoc.setFloat(5, loc.getYaw());
				updateLastLoc.setFloat(6, loc.getPitch());
			} else {
				updateLastLoc.setNull(1, Types.VARCHAR);
				updateLastLoc.setNull(2, Types.DOUBLE);
				updateLastLoc.setNull(3, Types.DOUBLE);
				updateLastLoc.setNull(4, Types.DOUBLE);
				updateLastLoc.setNull(5, Types.FLOAT);
				updateLastLoc.setNull(6, Types.FLOAT);
			}
			updateLastLoc.setLong(7, id);
			updateLastLoc.executeUpdate();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with updating lastLoc in the DB", e);
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
		powers.put(power, value);
		final PreparedStatement insertPower = Database.DATABASE
				.prepare("REPLACE INTO `ac_powers` (`key`, `player_id`, `info`, `category`) VALUES (?, ?, ?, ?);");
		try {

			insertPower.setString(1, power.name());
			insertPower.setLong(2, id);
			if (power == Type.EGG) {
				synchronized (SQLObjectContainer.yaml) {
					insertPower.setString(3,
							SQLObjectContainer.yaml.dump(value));
				}
			} else {
				insertPower.setString(3, value.toString());
			}
			insertPower.setString(4, power.getCategory().name());
			insertPower.executeUpdate();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with inserting power in the DB", e);
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setCustomPower(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setCustomPower(final String power, final Object value) {
		customPowers.put(power, value);
		final PreparedStatement insertPower = Database.DATABASE
				.prepare("REPLACE INTO `ac_powers` (`key`, `player_id`, `info`, `category`) VALUES (?, ?, ?, ?);");
		try {
			insertPower.clearParameters();
			insertPower.setString(1, power);
			insertPower.setLong(2, id);
			insertPower.setString(3, value.toString());
			insertPower.setString(4, Type.Category.OTHER.name());
			insertPower.executeUpdate();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with inserting power in the DB", e);
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getCustomPower(java.lang.String)
	 */
	@Override
	public ObjectContainer getCustomPower(final String power) {
		return new ObjectContainer(customPowers.get(power));
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasCustomPower(java.lang.String)
	 */
	@Override
	public boolean hasCustomPower(final String power) {
		return customPowers.containsKey(power);
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeCustomPower(java.lang.String)
	 */
	@Override
	public void removeCustomPower(final String power) {
		if (customPowers.remove(power) != null) {
			final PreparedStatement deletePower = Database.DATABASE
					.prepare("delete FROM `ac_powers` WHERE `player_id`=? AND `key`=?");
			try {
				deletePower.clearParameters();
				deletePower.setLong(1, id);
				deletePower.setString(2, power);
				deletePower.executeUpdate();
			} catch (final SQLException e) {
				ACLogger.severe("Problem with deleting customPower in the DB",
						e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPower(be.Balor.Tools.Type)
	 */
	@Override
	public ObjectContainer getPower(final Type power) {
		return new SQLObjectContainer(powers.get(power));
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasPower(be.Balor.Tools.Type)
	 */
	@Override
	public boolean hasPower(final Type power) {
		return powers.containsKey(power);
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removePower(be.Balor.Tools.Type)
	 */
	@Override
	public void removePower(final Type power) {
		if (powers.remove(power) != null) {
			final PreparedStatement deletePower = Database.DATABASE
					.prepare("delete FROM `ac_powers` WHERE `player_id`=? AND `key`=?");
			try {
				deletePower.clearParameters();
				deletePower.setLong(1, id);
				deletePower.setString(2, power.name());
				deletePower.executeUpdate();
			} catch (final SQLException e) {
				ACLogger.severe("Problem with deleting power from the DB", e);
			}

		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeAllSuperPower()
	 */
	@Override
	public Set<Type> removeAllSuperPower() {
		boolean found = false;
		final Set<Type> powersRemoved = new HashSet<Type>();
		for (final Type power : powers.keySet()) {
			if (power.getCategory() != Type.Category.SUPER_POWER) {
				continue;
			}
			powers.remove(power);
			found = true;
			powersRemoved.add(power);
			if (power != Type.FLY) {
				continue;
			}
			if (handler == null) {
				continue;
			}
			handler.setFlying(false);
			handler.setAllowFlight(false);
		}
		if (found) {
			final PreparedStatement deleteSuperPowers = Database.DATABASE
					.prepare("delete FROM `ac_powers` WHERE `player_id`=? AND `category`='"
							+ Type.Category.SUPER_POWER.name() + "'");
			try {
				deleteSuperPowers.clearParameters();
				deleteSuperPowers.setLong(1, id);
				deleteSuperPowers.executeUpdate();
			} catch (final SQLException e) {
				ACLogger.severe(
						"Problem with deleting super powers from the DB", e);
			}

		}
		return powersRemoved;

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setLastKitUse(java.lang.String, long)
	 */
	@Override
	public void setLastKitUse(final String kit, final long timestamp) {
		kitUses.put(kit, timestamp);
		final PreparedStatement insertKitUse = Database.DATABASE
				.prepare("REPLACE INTO `ac_kit_uses` (`kit`, `player_id`, `use`) VALUES (?, ?, ?);");
		try {
			insertKitUse.clearParameters();

			insertKitUse.setString(1, kit);
			insertKitUse.setLong(2, id);
			insertKitUse.setLong(3, timestamp);
			insertKitUse.executeUpdate();
		} catch (final SQLException e) {
			ACLogger.severe("Problem with inserting kit_use in the DB", e);
		}

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getLastKitUse(java.lang.String)
	 */
	@Override
	public long getLastKitUse(final String kit) {
		final Long use = kitUses.get(kit);
		if (use == null) {
			return 0L;
		}
		return use.longValue();
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getKitUseList()
	 */
	@Override
	public Set<String> getKitUseList() {
		return Collections.unmodifiableSet(kitUses.keySet());
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#forceSave()
	 */
	@Override
	protected void forceSave() {
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPowers()
	 */
	@Override
	public Map<String, String> getPowersString() {
		final Map<String, String> result = new TreeMap<String, String>();
		for (final Entry<Type, Object> entry : powers.entrySet()) {
			result.put(entry.getKey().name(), entry.getValue().toString());
		}
		for (final Entry<String, Object> entry : customPowers.entrySet()) {
			result.put(entry.getKey(), entry.getValue().toString());
		}
		return result;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setPresentation(java.lang.String)
	 */
	@Override
	public void setPresentation(final String presentation) {
		setInformation("presentation", presentation);

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPresentation()
	 */
	@Override
	public String getPresentation() {
		final ObjectContainer pres = getInformation("presentation");
		if (pres.isNull()) {
			return "";
		}
		return pres.getString();

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPowers()
	 */
	@Override
	public Map<Type, Object> getPowers() {
		return Collections.unmodifiableMap(powers);
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getCustomPowers()
	 */
	@Override
	public Map<String, Object> getCustomPowers() {
		return Collections.unmodifiableMap(customPowers);
	}

}
