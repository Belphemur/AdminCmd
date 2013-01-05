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
package be.Balor.Tools.Threads;

import org.bukkit.entity.Player;

/**
 * @author Antoine
 * 
 */
public class KickTask extends BukkitSyncTask {

	private final Player toKick;
	private final String message;

	/**
	 * @param toKick
	 * @param message
	 */
	public KickTask(final Player toKick, final String message) {
		super();
		this.toKick = toKick;
		this.message = message;
	}

	/**
	 * To kick a player
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		toKick.kickPlayer(message);
	}

}
