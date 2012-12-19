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
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class RadiusEgg<T> extends EggType<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8375423945536370931L;
	private final int defaultRadius;
	private final int maxRadius;

	/**
	 * @param defaultRadius
	 * @param maxRadius
	 */
	public RadiusEgg(final int defaultRadius, final int maxRadius) {
		super();
		this.defaultRadius = defaultRadius;
		this.maxRadius = maxRadius;
	}

	protected int getRadius(final Player sender, final CommandArgs args) {
		int radius = defaultRadius;
		final String valFlag = args.getValueFlag('r');
		if (valFlag != null) {
			try {
				radius = Integer.parseInt(valFlag);
			} catch (final NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", valFlag);
				return -1;
			}
		}
		return radius > maxRadius ? maxRadius : radius;
	}
}
