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

import be.Balor.Manager.Commands.CoreCommand;

/**
 * @author Antoine
 * 
 */
public class CommandException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6098533302948315536L;
	private final CoreCommand command;

	/**
	 * @param message
	 * @param command
	 */
	public CommandException(final String message, final CoreCommand command) {
		super(message);
		this.command = command;
	}

	/**
	 * @return the command
	 */
	public CoreCommand getCommand() {
		return command;
	}

}
