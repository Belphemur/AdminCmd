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

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACBlockListener extends BlockListener {
	@Override
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled())
			return;
		if (!(Boolean) ACHelper.getInstance().getConfValue("ColoredSign"))
			return;
		String parsed = null;
		String line;
		for (int i=0; i < 4; i++)
		{
			line = event.getLine(i);
			if (line != null && !line.isEmpty()) {				
				parsed = Utils.colorParser(line);
				if (parsed != null)
					event.setLine(i, parsed);
			}
		}
	}
		
}
