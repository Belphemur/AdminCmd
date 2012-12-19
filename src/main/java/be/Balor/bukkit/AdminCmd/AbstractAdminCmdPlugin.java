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

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.Permissions.PermissionLinker;

/**
 * @author Balor (aka Antoine Aflalo)
 */
public abstract class AbstractAdminCmdPlugin extends JavaPlugin {
	protected final PermissionLinker permissionLinker;
	protected final String name;
	private final int hashCode;

	/**
	 * Create the AdminCmd plugin.
	 * 
	 * @param name
	 *            the name used for the plugin.
	 * 
	 */
	public AbstractAdminCmdPlugin(final String name) {
		this.name = name;
		permissionLinker = PermissionLinker.getPermissionLinker(name);
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((permissionLinker == null) ? 0 : permissionLinker.hashCode());
		hashCode = result;
	}

	public AbstractAdminCmdPlugin() {
		this.name = this.getClass().getSimpleName();
		permissionLinker = PermissionLinker.getPermissionLinker(name);
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((permissionLinker == null) ? 0 : permissionLinker.hashCode());
		hashCode = result;
	}

	/**
	 * @return the name
	 */
	public String getAddonName() {
		return name;
	}

	/**
	 * @return the permissionLinker
	 */
	public PermissionLinker getPermissionLinker() {
		return permissionLinker;
	}

	/**
	 * Return the name of the plugin in the plugin.yml
	 * 
	 * @return the bukkit name of the plugin
	 */
	public String getPluginName() {
		return getDescription().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	@Override
	public void onEnable() {
		ACPluginManager.registerACPlugin(this);
		registerPermParents();
		CommandManager.getInstance().registerACPlugin(this);
		registerCmds();
		CommandManager.getInstance().checkAlias(this);
		setDefaultLocale();
	}

	/**
	 * Definitions of the command used by the plugin
	 */
	public abstract void registerCmds();

	/**
	 * Definition of the Permissions used by the plugin
	 */
	protected abstract void registerPermParents();

	/**
	 * Definitions of the locale used by the plugin
	 */
	protected abstract void setDefaultLocale();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractAdminCmdPlugin [permissionLinker=" + permissionLinker
				+ ", name=" + name + ", hashCode=" + hashCode + "]";
	}

}
