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
package be.Balor.Listeners.Events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACSignChangeEvent extends SignChangeEvent {

	/**
	 * @param theBlock
	 * @param thePlayer
	 * @param theLines
	 */
	public ACSignChangeEvent(final Block theBlock, final Player thePlayer,
			final String[] theLines) {
		super(theBlock, thePlayer, theLines);
	}

}
