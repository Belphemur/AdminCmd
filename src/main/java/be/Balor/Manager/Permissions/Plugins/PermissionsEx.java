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
package be.Balor.Manager.Permissions.Plugins;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Permissions.Group;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @authors Balor, Lathanael
 * 
 */
public class PermissionsEx extends SuperPermissions {
	private final PermissionManager PEX;

	/**
	 *
	 */
	public PermissionsEx(final PermissionManager PEX) {
		this.PEX = PEX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#hasPerm(org.bukkit.command
	 * .CommandSender, java.lang.String, boolean)
	 */
	@Override
	public boolean hasPerm(final CommandSender player, final String perm,
			final boolean errorMsg) {
		if (!(player instanceof Player)) {
			return true;
		}
		if (PEX.has((Player) player, perm)) {
			return true;
		} else {
			if (errorMsg) {
				Utils.sI18n(player, "errorNotPerm", "p", perm);
			}
			return false;
		}
	}

	@Override
	public boolean hasPerm(final CommandSender player, final Permission perm,
			final boolean errorMsg) {
		return this.hasPerm(player, perm.getName(), errorMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getUsers(org.java.lang
	 * .String)
	 */
	@Override
	public Set<Player> getUsers(final String groupName)
			throws NoPermissionsPlugin {
		PermissionUser[] users = null;
		users = PEX.getUsers(groupName);
		final Set<Player> players = new HashSet<Player>();
		if (users != null) {
			Player player = null;
			for (final PermissionUser user : users) {
				player = ACPlayer.getPlayer(user.getName()).getHandler();
				if (player == null) {
					continue;
				}
				players.add(player);
			}
			return players;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPermissionLimit(org
	 * .bukkit.entity.Player, java.lang.String)
	 */
	@Override
	public String getPermissionLimit(final Player p, final String limit) {
		String permLimit = null;
		final PermissionUser permUser = PEX.getUser(p);
		try {
			permLimit = permUser.getOption("admincmd." + limit);
		} catch (final ConcurrentModificationException e) {
			final Future<String> permTask = ACPluginManager.getScheduler()
					.callSyncMethod(ACPluginManager.getCorePlugin(),
							new Callable<String>() {

								@Override
								public String call() throws Exception {
									return permUser.getOption("admincmd."
											+ limit);
								}
							});
			try {
				permLimit = permTask.get();
			} catch (final InterruptedException e1) {
				permLimit = null;
				DebugLog.INSTANCE.info("Problem while gettings ASYNC perm of "
						+ p.getName());
			} catch (final ExecutionException e1) {
				permLimit = null;
				DebugLog.INSTANCE.info("Problem while gettings ASYNC perm of "
						+ p.getName());
			}
		}

		if (permLimit == null || (permLimit != null && permLimit.isEmpty())) {
			Set<String> permissions = new HashSet<String>();
			final String worldName = p.getWorld().getName();
			try {
				for (final String str : permUser.getPermissions(worldName)) {
					permissions.add(str);
				}
				for (final PermissionGroup group : permUser.getGroups()) {
					for (final String str : group.getPermissions(worldName)) {
						permissions.add(str);
					}
				}
			} catch (final ConcurrentModificationException e) {
				final Future<Set<String>> permTask = ACPluginManager
						.getScheduler().callSyncMethod(
								ACPluginManager.getCorePlugin(),
								new Callable<Set<String>>() {

									@Override
									public Set<String> call() throws Exception {
										final Set<String> permissions = new HashSet<String>();
										for (final String str : permUser
												.getPermissions(worldName)) {
											permissions.add(str);
										}
										for (final PermissionGroup group : permUser
												.getGroups()) {
											for (final String str : group
													.getPermissions(worldName)) {
												permissions.add(str);
											}
										}
										return permissions;
									}
								});
				try {
					permissions = permTask.get();
				} catch (final InterruptedException e1) {
					DebugLog.INSTANCE
							.info("Problem while gettings ASYNC perm (PEX) of "
									+ p.getName());
					return null;
				} catch (final ExecutionException e1) {
					DebugLog.INSTANCE
							.info("Problem while gettings ASYNC perm (PEX) of "
									+ p.getName());
					return null;
				}
			}
			return limitSearcher(limit, permissions);
		}
		return permLimit;
	}

	private String limitSearcher(final String limit, final Set<String> perms) {
		final Pattern regex = Pattern.compile("admincmd\\."
				+ limit.toLowerCase() + "\\.[0-9]+");
		int max = Integer.MIN_VALUE;
		for (final String perm : perms) {
			final Matcher regexMatcher = regex.matcher(perm.toLowerCase());
			if (!regexMatcher.find()) {
				continue;
			}
			final int current = Integer.parseInt(perm.split("\\.")[2]);
			if (current < max) {
				continue;
			}
			max = current;
		}

		if (max != Integer.MIN_VALUE) {
			return String.valueOf(max);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPrefix(org.bukkit.
	 * entity.Player)
	 */
	@Override
	public String getPrefix(final Player player) {
		PermissionUser user = null;
		try {
			user = PEX.getUser(player);
		} catch (final Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE,
					"Problem when trying to get the prefix of the user "
							+ player.getName(), e);
			return "";
		}

		if (user != null) {
			return user.getPrefix() == null ? "" : user.getPrefix();
		}

		String prefix = "";
		for (final PermissionGroup group : PEX.getUser(player).getGroups()) {
			if ((prefix = group.getPrefix()) != null && !prefix.isEmpty()) {
				break;
			}
		}
		return prefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.IPermissionPlugin#getSuffix(org.bukkit.entity
	 * .Player)
	 */
	@Override
	public String getSuffix(final Player player) {
		final PermissionUser user = PEX.getUser(player);
		if (user != null) {
			return user.getSuffix() == null ? "" : user.getSuffix();
		}

		String suffix = "";
		for (final PermissionGroup group : PEX.getUser(player).getGroups()) {
			if ((suffix = group.getSuffix()) != null && !suffix.isEmpty()) {
				break;
			}
		}
		return suffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.SuperPermissions#getGroup(org.bukkit
	 * .entity.Player)
	 */
	@Override
	public Group getGroup(final Player player) {
		int max = Integer.MIN_VALUE;
		PermissionGroup cur = null;
		for (final PermissionGroup group : PEX.getUser(player).getGroups()) {
			final int rank = group.getRank();
			if (rank > max) {
				max = rank;
				cur = group;
			}
		}
		if (cur == null) {
			return new Group();
		}
		return new Group(cur.getName(), cur.getRank());
	}

}
