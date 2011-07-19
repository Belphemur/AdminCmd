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
package be.Balor.Manager;

import java.util.LinkedHashMap;

import org.bukkit.permissions.Permission;

import com.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class PermParent {
	protected LinkedHashMap<String, Boolean> children;
	protected String permName = "";
	protected String compareName="";
	
	public PermParent(String perm, String compare)
	{
		this.permName=perm;
		this.compareName = compare;
		children = new LinkedHashMap<String, Boolean>();
	}
	/**
	 * Add a Permission Child.
	 * @param name
	 * @param bool
	 */
	public void addChild(String name, boolean bool)
	{
		children.put(name, bool);
	}
	public void addChild(String name)
	{
		children.put(name, true);
	}
	/**
	 * @return the compareName
	 */
	public String getCompareName() {
		return compareName;
	}
	public void registerBukkitPerm()
	{
		AdminCmd.getBukkitServer().getPluginManager().addPermission(new Permission(permName, children));
	}
	
}
