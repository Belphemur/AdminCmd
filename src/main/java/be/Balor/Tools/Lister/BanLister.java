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
import java.util.PriorityQueue;
import java.util.Queue;

import be.Balor.Player.IBan;
import be.Balor.Player.ITempBan;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BanLister extends Lister {
	private final Queue<String> ban = new PriorityQueue<String>();

	/**
	 * 
	 */
	BanLister() {
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Lister.Lister#update()
	 */
	@Override
	public synchronized void update() {
		ban.clear();
		final Collection<IBan> banned = ACHelper.getInstance().getBannedPlayers();
		final HashMap<String, String> replace = new HashMap<String, String>();
		for (final IBan p : banned) {
			replace.clear();
			replace.put("player", p.getPlayer());
			replace.put("player", p.getPlayer());
			replace.put("reason", p.getReason());
			replace.put("date", Utils.replaceDateAndTimeFormat(p.getDate()));
			if (p instanceof ITempBan) {
				replace.put("endban", Utils.replaceDateAndTimeFormat(((ITempBan) p).getEndBan()));
				ban.add(LocaleHelper.TEMP_BANLIST.getLocale(replace));
			} else {
				ban.add(LocaleHelper.BANLIST.getLocale(replace));
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
		return Collections.unmodifiableCollection(ban);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Lister.Lister#getType()
	 */
	@Override
	List getType() {
		return Lister.List.BAN;
	}

}
