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

import be.Balor.Player.ITempBan;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class UnBanTask implements Runnable {

	private final ITempBan toUnBan;
	private final boolean bcast;

	/**
	 * @param toUnBan
	 * @param bcast
	 */
	public UnBanTask(final ITempBan toUnBan, final boolean bcast) {
		super();
		this.toUnBan = toUnBan;
		this.bcast = bcast;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ACHelper.getInstance().unBanPlayer(toUnBan);
		if (!bcast) {
			return;
		}
		final String unbanMsg = Utils.I18n("unban", "player",
				toUnBan.getPlayer());
		if (unbanMsg != null) {
			Utils.broadcastMessage(unbanMsg);
		}
	}

}
