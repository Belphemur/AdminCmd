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
package be.Balor.Listeners;

import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
import info.somethingodd.OddItem.OddItemBase;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Manager.Permissions.Plugins.SuperPermissions;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

import com.herocraftonline.dev.heroes.Heroes;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

import de.diddiz.LogBlock.LogBlock;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class ACPluginListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		ACPluginManager.unRegisterACPlugin(event.getPlugin());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		if (!PermissionManager.isPermissionsExSet()) {
			final Plugin Permissions = ACPluginManager.getServer().getPluginManager()
					.getPlugin("PermissionsEx");
			if (Permissions != null) {
				if (Permissions.isEnabled())
					PermissionManager.setPEX(PermissionsEx.getPermissionManager());
			}
		}
		if (!PermissionManager.isGroupManagerSet()) {
			final Plugin GMplugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("GroupManager");
			if (GMplugin != null) {
				if (GMplugin.isEnabled())
					PermissionManager.setGroupManager(GMplugin);
			}
		}
		if (!PermissionManager.isYetiPermissionsSet()) {
			final Plugin Permissions = ACPluginManager.getServer().getPluginManager()
					.getPlugin("Permissions");
			if (Permissions != null) {
				if (Permissions.isEnabled())
					PermissionManager.setYetiPermissions(((Permissions) Permissions).getHandler());
			}
		}
		if (!PermissionManager.isbPermissionsSet()) {
			final Plugin plugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("bPermissions");
			if (plugin != null) {
				String version = plugin.getDescription().getVersion();
				version = version.replace(".", "");
				int ver = Integer.parseInt(version);
				if (ver < 285) {
					ACLogger.info("You are using bPermissions v" + plugin.getDescription().getVersion() + ". This is an outdated version, permission support for bPermission will be disabled.");
					return;
				}
				PermissionManager.setbPermissions();
			}
		}
		if (!PermissionManager.isPermissionsBukkitSet()) {
			final Plugin plugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("PermissionsBukkit");
			if (plugin != null) {
				PermissionManager.setPermissionsBukkit((PermissionsPlugin) plugin);
			}
		}
		if (Utils.oddItem == null) {
			final Plugin items = ACPluginManager.getServer().getPluginManager()
					.getPlugin("OddItem");
			if (items != null && items.isEnabled()) {
				Utils.oddItem = (OddItemBase) items;
				ACLogger.info("Successfully linked with OddItem");
			}
		}
		if (!SuperPermissions.isApiSet()) {
			final Plugin mChatPlugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("mChatSuite");
			if (mChatPlugin != null && mChatPlugin.isEnabled()) {
				SuperPermissions.setmChatapi((mChatSuite) mChatPlugin);
				Utils.mChatApi = ((mChatSuite) mChatPlugin).getInfoReader();
				ACLogger.info("Successfully linked with mChatSuite");
			}
		}
		if (Utils.logBlock == null) {
			final Plugin plugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("LogBlock");
			if (plugin != null && plugin.isEnabled()) {
				Utils.setLogBlock(((LogBlock) plugin).getConsumer());
				ACLogger.info("Successfully linked with LogBlock");
			}
		}
		if (ConfigEnum.H_ALLPLUGIN.getBoolean()) {
			for (final Plugin plugin : event.getPlugin().getServer().getPluginManager()
					.getPlugins())
				HelpLister.getInstance().addPlugin(plugin);
		}
		if (!Utils.signExtention) {
			final Plugin plugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("SignExtensions");
			if (plugin != null)
				Utils.signExtention = true;
		}
		if (Utils.heroes == null) {
			final Plugin plugin = ACPluginManager.getServer().getPluginManager()
					.getPlugin("Heroes");
			if (plugin != null && plugin.isEnabled()) {
				Utils.heroes = (Heroes) plugin;
				ACLogger.info("Successfully linked with Heroes");
			}
		}
	}
}