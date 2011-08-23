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

import info.somethingodd.bukkit.OddItem.OddItem;

import net.D3GN.MiracleM4n.mChat.mChat;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Manager.Permissions.Plugins.BukkitPermissions;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;

import com.nijikokun.bukkit.Permissions.Permissions;

import de.diddiz.LogBlock.LogBlock;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPluginListener extends ServerListener {

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("PermissionsEx"))
			PermissionManager.setPEX(PermissionsEx.getPermissionManager());

		if (!PermissionManager.isPermissionsExSet()) {
			Plugin Permissions = AdminCmd.getBukkitServer().getPluginManager()
					.getPlugin("PermissionsEx");
			if (Permissions != null) {
				if (Permissions.isEnabled())
					PermissionManager.setPEX(PermissionsEx.getPermissionManager());
			}
		}
		if (!PermissionManager.isYetiPermissionsSet()) {
			Plugin Permissions = AdminCmd.getBukkitServer().getPluginManager()
					.getPlugin("Permissions");
			if (Permissions != null) {
				if (Permissions.isEnabled())
					PermissionManager.setYetiPermissions(((Permissions) Permissions).getHandler());
			}
		}
		if (Utils.oddItem == null) {
			Plugin items = AdminCmd.getBukkitServer().getPluginManager().getPlugin("OddItem");
			if (items != null && items.isEnabled()) {
				Utils.oddItem = (OddItem) items;
				System.out.print("[AdminCmd] Successfully linked with OddItem");
			}
		}
		if (!BukkitPermissions.isApiSet()) {
			Plugin mChatPlugin = AdminCmd.getBukkitServer().getPluginManager().getPlugin("mChat");
			if (mChatPlugin != null && mChatPlugin.isEnabled()) {
				BukkitPermissions.setmChatapi(mChat.API);
				System.out.print("[AdminCmd] Successfully linked with mChat");
			}
		}
		if (Utils.logBlock == null) {
			Plugin plugin = AdminCmd.getBukkitServer().getPluginManager().getPlugin("LogBlock");
			if (plugin != null && plugin.isEnabled()) {
				Utils.logBlock = ((LogBlock) plugin).getConsumer();
				System.out.print("[AdminCmd] Successfully linked with LogBlock");
			}
		}
		if (ACHelper.getInstance().getConfBoolean("help.getHelpForAllPlugins")) {
			for (Plugin plugin : event.getPlugin().getServer().getPluginManager().getPlugins())
					HelpLister.getInstance().addPlugin(plugin);
		}
	}
}