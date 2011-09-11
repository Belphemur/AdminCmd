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

import java.sql.Date;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TempBannedPlayer extends BannedPlayer {
	private Date endBan;

	/**
	 * 
	 */
	public TempBannedPlayer() {
	}

	/**
	 * @param player
	 * @param reason
	 * @param time
	 */
	public TempBannedPlayer(String player, String reason, long time) {
		super(player, reason);
		endBan = new Date(System.currentTimeMillis() + time);
	}

	/**
	 * @return the endBan
	 */
	public Date getEndBan() {
		return endBan;
	}

	/**
	 * @param endBan
	 *            the endBan to set
	 */
	public void setEndBan(Date endBan) {
		this.endBan = endBan;
	}

}
