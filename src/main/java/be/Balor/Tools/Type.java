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

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum Type {
	FLY(Category.PLAYER), 
	VULCAN(Category.PLAYER), 
	GOD(Category.PLAYER), 
	THOR(Category.PLAYER), 
	BANNED(Category.SANCTION), 
	FIREBALL(Category.PLAYER), 
	SPYMSG(Category.OTHER), 
	FREEZED(Category.SANCTION), 
	MUTED(Category.SANCTION), 
	MOB_LIMIT(Category.WORLD), 
	NO_PICKUP(Category.PLAYER), 
	WEATHER_FREEZED(Category.WORLD), 
	REPEAT_CMD(Category.OTHER), 
	TIME_FREEZED(Category.WORLD);
	@Override
	public String toString() {
		String s = super.toString();
		return s.toLowerCase();
	}

	private final Category category;

	private Type(Category category) {
		this.category = category;
	}

	/**
	 * Gets the Category assigned to this event
	 * 
	 * @return Category of this Event.Type
	 */
	public Category getCategory() {
		return category;
	}

	public enum Category {
		PLAYER, WORLD, OTHER, SANCTION;
	}
	public enum Weather {
		STORM, RAIN, CLEAR, FREEZE;
	}
	public enum Tp {
		TP_HERE, TP_TO, TP_PLAYERS;
	}
}
