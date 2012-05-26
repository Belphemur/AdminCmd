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
package be.Balor.Tools.Lister;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MuteLister extends Lister {
	private final Queue<String> mute = new PriorityQueue<String>();

	/**
	 * 
	 */
	MuteLister() {
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Lister.Lister#update()
	 */
	@Override
	public synchronized void update() {
		mute.clear();
		final Set<ACPlayer> players = new HashSet<ACPlayer>();
		players.addAll(ACPlayer.getPlayers(Type.MUTED));
		players.addAll(ACPlayer.getPlayers(Type.MUTED_COMMAND));
		final HashMap<String, String> replace = new HashMap<String, String>();
		for (final ACPlayer p : players) {
			replace.clear();
			if (p.hasPower(Type.MUTED)) {
				replace.put("player", p.getName());
				replace.put("msg", p.getPower(Type.MUTED).getString());
				mute.add(LocaleHelper.MUTELIST.getLocale(replace));
			} else if (p.hasPower(Type.MUTED_COMMAND)) {
				replace.put("player", p.getName());
				replace.put("msg", p.getPower(Type.MUTED_COMMAND).getString());
				mute.add(LocaleHelper.MUTELIST.getLocale(replace));
			} else {
				continue;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Lister.Lister#getList()
	 */
	@Override
	Collection<String> getList() {
		return Collections.unmodifiableCollection(mute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Lister.Lister#getType()
	 */
	@Override
	List getType() {
		return List.MUTE;
	}

}
