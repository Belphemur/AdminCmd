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
package be.Balor.Tools;

import java.util.Comparator;

import org.bukkit.entity.Player;

import be.Balor.Manager.Permissions.PermissionManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerComparator implements Comparator<Player> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Player o1, Player o2) {
		String g1 = PermissionManager.getGroup(o1);
		String g2 = PermissionManager.getGroup(o2);
		if (g1.equals(g2))
			return Utils.getPlayerName(o1).compareTo(Utils.getPlayerName(o2));
		return g1.compareTo(g2);
	}

}
