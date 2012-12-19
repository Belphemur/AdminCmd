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
package be.Balor.Tools.Egg;

import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class SimpleRadiusEgg extends RadiusEgg<Integer> {

	/**
	 * @param defaultRadius
	 * @param maxRadius
	 */
	public SimpleRadiusEgg(final int defaultRadius, final int maxRadius) {
		super(defaultRadius, maxRadius);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7867915319728073263L;

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
		value = radius;
	}

}
