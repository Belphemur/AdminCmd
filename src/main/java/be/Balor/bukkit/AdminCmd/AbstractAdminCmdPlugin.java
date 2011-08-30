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
package be.Balor.bukkit.AdminCmd;

import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Manager.Permissions.PermissionLinker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class AbstractAdminCmdPlugin extends JavaPlugin {
	protected final PermissionLinker permissionLinker;

	/**
	 * 
	 */
	public AbstractAdminCmdPlugin(String name) {
		permissionLinker = PermissionLinker.getPermissionLinker(name);
	}

	/**
	 * @return the permissionLinker
	 */
	public PermissionLinker getPermissionLinker() {
		return permissionLinker;
	}

	/**
	 * Definition of the Permissions used by the plugin
	 */
	protected abstract void registerPermParents();

	/**
	 * Definitions of the command used by the plugin
	 */
	public abstract void registerCmds();

	/**
	 * Definitions of the locale used by the plugin
	 */
	protected abstract void setDefaultLocale();
}
