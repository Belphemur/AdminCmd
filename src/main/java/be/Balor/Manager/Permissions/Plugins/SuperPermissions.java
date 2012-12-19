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

import in.mDev.MiracleM4n.mChatSuite.mChatSuite;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import com.miraclem4n.mchat.api.Reader;
import com.miraclem4n.mchat.types.InfoType;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public abstract class SuperPermissions implements IPermissionPlugin {
	private static boolean mChat = false;

	/**
	 *
	 */
	public SuperPermissions() {
	}

	/**
	 * @param mChatSuite
	 *            the mChatAPI to set
	 */
	public static void setmChatapi(final mChatSuite mChatSuite) {
		if (!SuperPermissions.mChat && mChatSuite != null) {
			SuperPermissions.mChat = true;
		}
	}

	/**
	 * @return the mChatAPI
	 */
	public static boolean isApiSet() {
		return mChat;
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
		if (player.hasPermission(perm)) {
			return true;
		} else {
			if (errorMsg) {
				Utils.sI18n(player, "errorNotPerm", "p", perm);
			}

			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#hasPerm(org.bukkit.command
	 * .CommandSender, org.bukkit.permissions.Permission, boolean)
	 */
	@Override
	public boolean hasPerm(final CommandSender player, final Permission perm,
			final boolean errorMsg) {
		if (!(player instanceof Player)) {
			return true;
		}
		if (player.hasPermission(perm)) {
			return true;
		} else {
			if (errorMsg) {
				Utils.sI18n(player, "errorNotPerm", "p", perm.getName());
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#isInGroup(org.java.lang
	 * .String, org.bukkit.entity.Player)
	 */
	@Override
	public boolean isInGroup(final String group, final Player player)
			throws NoPermissionsPlugin {
		throw new NoPermissionsPlugin(
				"To use this functionality you need a Permission Plugin");
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
		throw new NoPermissionsPlugin(
				"To use this functionality you need a Permission Plugin");
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
		String result = null;
		if (mChat) {
			result = Reader.getInfo(p.getName(), InfoType.USER, p.getWorld()
					.getName(), "admincmd." + limit);
		}
		if (result == null || (result != null && result.isEmpty())) {
			final Pattern regex = Pattern.compile("admincmd\\."
					+ limit.toLowerCase() + "\\.[0-9]+");
			Set<PermissionAttachmentInfo> permissions = null;
			if (ACHelper.isMainThread()) {
				permissions = p.getEffectivePermissions();
			} else {
				final Callable<Set<PermissionAttachmentInfo>> perms = new Callable<Set<PermissionAttachmentInfo>>() {

					@Override
					public Set<PermissionAttachmentInfo> call()
							throws Exception {
						return p.getEffectivePermissions();
					}
				};
				final Future<Set<PermissionAttachmentInfo>> permTask = ACPluginManager
						.getScheduler().callSyncMethod(
								ACPluginManager.getCorePlugin(), perms);
				try {
					permissions = permTask.get();
					DebugLog.INSTANCE.info("Perms got for " + p.getName());
				} catch (final InterruptedException e) {
					DebugLog.INSTANCE
							.info("Problem while gettings ASYNC perm of "
									+ p.getName());
				} catch (final ExecutionException e) {
					DebugLog.INSTANCE
							.info("Problem while gettings ASYNC perm of "
									+ p.getName());
				}
			}
			return permissionCheck(permissions, regex);

		} else {
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPrefix(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public String getPrefix(final Player player) {
		if (mChat) {
			return Reader.getPrefix(player.getName(), InfoType.USER, player
					.getWorld().getName());
		} else {
			return "";
		}
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
		if (mChat) {
			return Reader.getSuffix(player.getName(), InfoType.USER, player
					.getWorld().getName());
		} else {
			return "";
		}
	}

	private String permissionCheck(final Set<PermissionAttachmentInfo> perms,
			final Pattern regex) {
		if (perms == null) {
			return null;
		}
		int max = Integer.MIN_VALUE;
		for (final PermissionAttachmentInfo info : perms) {
			final Matcher regexMatcher = regex.matcher(info.getPermission()
					.toLowerCase());
			if (!regexMatcher.find()) {
				continue;
			}
			final int current = Integer.parseInt(info.getPermission().split(
					"\\.")[2]);
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
}
