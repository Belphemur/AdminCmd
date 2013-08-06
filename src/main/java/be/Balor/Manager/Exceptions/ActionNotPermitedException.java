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
package be.Balor.Manager.Exceptions;

import org.bukkit.command.CommandSender;

/**
 * @author Antoine
 * 
 */
public class ActionNotPermitedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4261236526664930710L;
	protected final CommandSender sender;

	/**
	 * @param sender
	 */
	public ActionNotPermitedException(final CommandSender sender, final String message) {
		super(message);
		this.sender = sender;
	}

	public void sendMessage() {
		this.sender.sendMessage(this.getMessage());
	}

}