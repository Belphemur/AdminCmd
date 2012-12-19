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
package be.Balor.Manager.Permissions;

import org.bukkit.command.CommandSender;

import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ActionNotPermitedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -49502752416573412L;
	private final CommandSender sender;

	/**
	 * @param message
	 */
	public ActionNotPermitedException(final CommandSender sender,
			final String perm) {
		super(Utils.I18n("errorNotPerm", "p", perm));
		this.sender = sender;
	}

	public void sendMessage() {
		this.sender.sendMessage(this.getMessage());
	}

}
