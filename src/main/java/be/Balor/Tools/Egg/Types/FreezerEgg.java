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
package be.Balor.Tools.Egg.Types;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Egg.BlockChangeInfo;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FreezerEgg extends BlockEgg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6781269132940660439L;

	/**
	 * 
	 */
	public FreezerEgg() {
		super(ConfigEnum.DEGG_FREEZE_RADIUS.getInt(),
				ConfigEnum.MAXEGG_FREEZE_RADIUS.getInt());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(org.bukkit.entity.Player,
	 * be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	protected void processArguments(final Player sender, final CommandArgs args)
			throws ProcessingArgsException {
		final int radius = getRadius(sender, args);
		if (radius == -1) {
			return;
		}
		value = new BlockChangeInfo(Material.ICE.getId(), radius);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.Types.BlockEgg#blockTimeOut()
	 */
	@Override
	protected int blockTimeOut() {
		return ConfigEnum.EGG_FREEZE_TIMEOUT.getInt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FreezerEgg = " + value.getRadius();
	}

}
