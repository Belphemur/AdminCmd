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

import java.util.HashMap;

import org.bukkit.entity.Player;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TpRequest {
	private Player from, to;

	/**
	 * 
	 */
	public TpRequest(Player from, Player to) {
		this.from = from;
		this.to = to;
	}

	public void teleport() {
		if (from != null && to != null) {
			from.teleport(to);
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("fromPlayer", from.getName());
			replace.put("toPlayer", to.getName());
			Utils.sI18n(to, "tp", replace);
			Utils.sI18n(from, "tp", replace);
		}
	}
}
