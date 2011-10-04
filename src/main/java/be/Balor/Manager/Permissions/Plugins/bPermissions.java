package be.Balor.Manager.Permissions.Plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.D3GN.MiracleM4n.mChat.mChatAPI;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

import de.bananaco.permissions.worlds.WorldPermissionsManager;

import be.Balor.Manager.Permissions.AbstractPermission;
import be.Balor.Tools.Utils;

public class bPermissions extends AbstractPermission {
	protected WorldPermissionsManager worlPermManager;
	private static mChatAPI mChatAPI = null;

	/**
	 *
	 */
	public bPermissions(WorldPermissionsManager plugin) {
		worlPermManager = plugin;
	}

	/**
	 * @param mChatAPI
	 *            the mChatAPI to set
	 */
	public static void setmChatapi(mChatAPI mChatAPI) {
		if (bPermissions.mChatAPI == null && mChatAPI != null)
			bPermissions.mChatAPI = mChatAPI;
	}

	/**
	 * @return the mChatAPI
	 */
	public static boolean isApiSet() {
		return mChatAPI != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#hasPerm(org.bukkit.command
	 * .CommandSender, java.lang.String, boolean)
	 */
	@Override
	public boolean hasPerm(CommandSender player, String perm, boolean errorMsg) {
		if (!(player instanceof Player))
			return true;
		if (player.hasPermission(perm))
			return true;
		else {
			if (errorMsg)
				Utils.sI18n(player, "errorNotPerm", "p", perm);

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
	public boolean hasPerm(CommandSender player, Permission perm, boolean errorMsg) {
		if (!(player instanceof Player))
			return true;
		if (player.hasPermission(perm))
			return true;
		else {
			if (errorMsg)
				Utils.sI18n(player, "errorNotPerm", "p", perm.getName());
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#isInGroup(org.java.lang.String,
	 * org.java.lang.String, org.bukkit.entity.Player)
	 */
	@Override
	public boolean isInGroup(String groupName, String worldName, Player player) {
		List <String> groups = new ArrayList<String>();
		groups = worlPermManager.getPermissionSet(worldName).getGroups(player);
		for (String group : groups)
			if (group.equalsIgnoreCase(groupName))
				return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPermissionLimit(org
	 * .bukkit.entity.Player, java.lang.String)
	 */
	@Override
	public String getPermissionLimit(Player p, String limit) {String result = null;
	if (mChatAPI != null)
		result = mChatAPI.getInfo(p, "admincmd." + limit);
	if (result == null || (result != null && result.isEmpty())) {
		Pattern regex = Pattern.compile("admincmd\\." + limit.toLowerCase() + "\\.[0-9]+");
		for (PermissionAttachmentInfo info : p.getEffectivePermissions()) {
			Matcher regexMatcher = regex.matcher(info.getPermission());
			if (regexMatcher.find())
				return info.getPermission().split("\\.")[2];

		}
	}
	else
		return result;
	return null;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPrefix(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public String getPrefix(Player player) {
		if (mChatAPI != null)
			return mChatAPI.getPrefix(player);
		else
			return "";
	}

}
