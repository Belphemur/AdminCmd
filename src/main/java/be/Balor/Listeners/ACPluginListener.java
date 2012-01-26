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
import info.somethingodd.bukkit.OddItem.OddItemBase;

import org.bukkit.event.EventHandler;
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
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import com.herocraftonline.dev.heroes.Heroes;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

import de.diddiz.LogBlock.LogBlock;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPluginListener implements Listener {

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		ACPluginManager.unRegisterACPlugin(event.getPlugin());
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if (!PermissionManager.isPermissionsExSet()) {
			final Plugin Permissions = ACPluginManager.getServer().getPluginManager()
					.getPlugin("PermissionsEx");
			if (Permissions != null) {
				if (Permissions.isEnabled())
					PermissionManager.setPEX(PermissionsEx.getPermissionManager());
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
				PermissionManager.setbPermissions(
						de.bananaco.permissions.Permissions.getWorldPermissionsManager(),
						de.bananaco.permissions.Permissions.getInfoReader());
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
		if (ACHelper.getInstance().getConfBoolean("help.getHelpForAllPlugins")) {
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