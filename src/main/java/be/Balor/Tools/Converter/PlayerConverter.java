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
package be.Balor.Tools.Converter;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import be.Balor.Player.AfterPlayerConvertTask;
import be.Balor.Player.IPlayerFactory;
import be.Balor.Player.PlayerConvertTask;
import be.Balor.Tools.Debug.ACLogger;

/**
 * @author Antoine
 * 
 */
public class PlayerConverter {
	private final Executor pool = Executors.newFixedThreadPool(5);
	private final IPlayerFactory oldFactory, newFactory;

	/**
	 * @param oldFactory
	 * @param newFactory
	 */
	public PlayerConverter(final IPlayerFactory oldFactory,
			final IPlayerFactory newFactory) {
		super();
		this.oldFactory = oldFactory;
		this.newFactory = newFactory;
	}

	public synchronized void convert() {
		final Set<String> existingPlayers = oldFactory.getExistingPlayers();
		ACLogger.info("Begin conversion of " + existingPlayers.size()
				+ " players");
		for (final String name : this.oldFactory.getExistingPlayers()) {
			pool.execute(new PlayerConvertTask(oldFactory, newFactory, name));
		}
		pool.execute(new AfterPlayerConvertTask(newFactory));
	}

}
