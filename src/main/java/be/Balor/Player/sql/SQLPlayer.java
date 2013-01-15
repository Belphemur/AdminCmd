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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import lib.SQL.PatPeter.SQLibrary.Database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Help.String.Str;
import be.Balor.Tools.Threads.PrepStmtExecutorTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

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
	private static PreparedStatement GET_HOMES, GET_INFOS, GET_POWERS,
			GET_KIT_USES, GET_LASTLOC;
	private static int prepStmtTaskID;
	private static final PrepStmtExecutorTask PREP_STMT_TASK = new PrepStmtExecutorTask();
	static {
		initPrepStmt();
	}

	public static void initPrepStmt() {

		GET_HOMES = Database.DATABASE
				.prepare("SELECT `name`,`world`,`x`,`y`,`z`,`yaw`,`pitch` FROM `ac_homes` WHERE `player_id` = ?");
		GET_POWERS = Database.DATABASE
				.prepare("SELECT `key`,`info` FROM `ac_powers` WHERE `player_id` = ?");
		GET_INFOS = Database.DATABASE
				.prepare("SELECT `key`,`info` FROM `ac_informations` WHERE `player_id` = ?");
		GET_KIT_USES = Database.DATABASE
				.prepare("SELECT `kit`,`use` FROM `ac_kit_uses` WHERE `player_id` = ?");
		GET_LASTLOC = Database.DATABASE
				.prepare("SELECT `world`,`x`,`y`,`z`,`yaw`,`pitch` FROM ac_players WHERE id=?");
	}

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
		synchronized (GET_LASTLOC) {
			try {
				GET_LASTLOC.clearParameters();
				GET_LASTLOC.setLong(1, id);
				ResultSet rs;
				synchronized (GET_LASTLOC.getConnection()) {
					rs = GET_LASTLOC.executeQuery();
				}
				if (rs.next()) {
					final String worldName = rs.getString("world");
					if (worldName != null && !worldName.isEmpty()) {
						final World world = Bukkit.getWorld(worldName);
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
			} catch (final SQLException e) {
				ACLogger.severe(
						"Problem with getting last location from the DB", e);
			}
		}
		synchronized (GET_HOMES) {
			try {
				GET_HOMES.clearParameters();
				GET_HOMES.setLong(1, id);
				ResultSet rs;
				synchronized (GET_HOMES.getConnection()) {
					rs = GET_HOMES.executeQuery();
				}
				while (rs.next()) {
					final String worldName = rs.getString("world");
					final World world = Bukkit.getWorld(worldName);
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
				rs.close();
			} catch (final SQLException e) {
				ACLogger.severe("Problem with getting homes from the DB", e);
			}

		}
		synchronized (GET_POWERS) {
			try {
				GET_POWERS.clearParameters();
				GET_POWERS.setLong(1, id);
				ResultSet rs;
				synchronized (GET_POWERS.getConnection()) {
					rs = GET_POWERS.executeQuery();
				}
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
				rs.close();
			} catch (final SQLException e) {
				ACLogger.severe("Problem with getting powers from the DB", e);
			}

		}
		synchronized (GET_INFOS) {
			try {
				GET_INFOS.clearParameters();
				GET_INFOS.setLong(1, id);
				ResultSet rs;
				synchronized (GET_INFOS.getConnection()) {
					rs = GET_INFOS.executeQuery();
				}
				while (rs.next()) {
					synchronized (SQLObjectContainer.yaml) {
						infos.put(rs.getString("key"), SQLObjectContainer.yaml
								.load(rs.getString("info")));
					}
				}
				rs.close();
			} catch (final SQLException e) {
				ACLogger.severe(
						"Problem with getting informations from the DB", e);
			}

		}
		synchronized (GET_KIT_USES) {
			try {
				GET_KIT_USES.clearParameters();
				GET_KIT_USES.setLong(1, id);
				ResultSet rs;
				synchronized (GET_KIT_USES.getConnection()) {
					rs = GET_KIT_USES.executeQuery();
				}
				while (rs.next()) {
					kitUses.put(rs.getString("kit"), rs.getLong("use"));
				}
				rs.close();
			} catch (final SQLException e) {
				ACLogger.severe("Problem with getting kit uses from the DB", e);
			}
		}
	}

	/**
	 * To be sure that all waiting prepStmt will be executed when this is called
	 */
	public static void forceExecuteStmts() {
		PREP_STMT_TASK.run();
	}

	/**
	 * To Schedule the Async task
	 */
	public static void scheduleAsyncSave() {
		if (ACPluginManager.getScheduler().isCurrentlyRunning(prepStmtTaskID)
				|| ACPluginManager.getScheduler().isQueued(prepStmtTaskID)) {
			return;
		}
		final int delay = ConfigEnum.WDELAY.getInt() >= 30 ? ConfigEnum.WDELAY
				.getInt() : 30;
		prepStmtTaskID = ACPluginManager
				.getScheduler()
				.runTaskTimerAsynchronously(
						ACHelper.getInstance().getCoreInstance(),
						PREP_STMT_TASK, 20 * 60, 20 * delay).getTaskId();
		DebugLog.INSTANCE.info("IO Save RepeatingTask created : "
				+ prepStmtTaskID);
	}

	/**
	 * To stop the saving task.
	 */
	public static void stopSavingTask() {
		if (!ACPluginManager.getScheduler().isCurrentlyRunning(prepStmtTaskID)
				&& !ACPluginManager.getScheduler().isQueued(prepStmtTaskID)) {
			return;
		}
		ACPluginManager.getScheduler().cancelTask(prepStmtTaskID);
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
			PREP_STMT_TASK.addPreparedStmt(insertHome);

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
				PREP_STMT_TASK.addPreparedStmt(deleteHome);

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
			PREP_STMT_TASK.addPreparedStmt(insertInfo);
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
				PREP_STMT_TASK.addPreparedStmt(deleteInfo);

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
			PREP_STMT_TASK.addPreparedStmt(updateLastLoc);
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
			PREP_STMT_TASK.addPreparedStmt(insertPower);
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
			PREP_STMT_TASK.addPreparedStmt(insertPower);
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
				PREP_STMT_TASK.addPreparedStmt(deletePower);
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
				PREP_STMT_TASK.addPreparedStmt(deletePower);
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
	public void removeAllSuperPower() {
		boolean found = false;
		for (final Type power : powers.keySet()) {
			if (power.getCategory() != Type.Category.SUPER_POWER) {
				continue;
			}
			powers.remove(power);
			found = true;
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
				PREP_STMT_TASK.addPreparedStmt(deleteSuperPowers);
			} catch (final SQLException e) {
				ACLogger.severe(
						"Problem with deleting super powers from the DB", e);
			}

		}

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
			PREP_STMT_TASK.addPreparedStmt(insertKitUse);
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
