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
package be.Balor.Player;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.Filters.YmlFilter;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FilePlayerFactory implements IPlayerFactory {
	final String directory;
	private final Set<String> existingPlayers = new HashSet<String>();

	/**
	 * 
	 */
	public FilePlayerFactory(final String directory) {
		this.directory = directory;
		final File[] players = YmlFilter.INSTANCE.listRecursively(new File(
				directory), 1);
		final StringBuffer files = new StringBuffer();
		for (final File player : players) {
			final String name = player.getName();
			existingPlayers.add(name.substring(0, name.lastIndexOf('.')));
			files.append(name + " ");
		}
		DebugLog.INSTANCE
				.info("User's file found : " + files.toString().trim());
	}

	@Override
	public void addExistingPlayer(final String player) {
		existingPlayers.add(player);
	}

	@Override
	public ACPlayer createPlayer(final String playername) {
		if (!existingPlayers.contains(playername)) {
			return new EmptyPlayer(playername);
		} else if (directory != null) {
			return new FilePlayer(directory, playername);
		} else {
			return null;
		}
	}

	@Override
	public ACPlayer createPlayer(final Player player) {
		if (!existingPlayers.contains(player.getName())) {
			return new EmptyPlayer(player);
		} else if (directory != null) {
			return new FilePlayer(directory, player);
		} else {
			return null;
		}
	}

	/**
	 * @return the existingPlayers
	 */
	@Override
	public Set<String> getExistingPlayers() {
		return existingPlayers;
	}

}
