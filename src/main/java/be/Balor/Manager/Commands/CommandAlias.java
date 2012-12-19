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
package be.Balor.Manager.Commands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandAlias {
	private final String commandName;
	private final String alias;
	private final String parameters;
	private CoreCommand cmd;

	/**
	 * @param commandName
	 * @param alias
	 * @param parameters
	 */
	public CommandAlias(final String commandName, final String alias,
			final String parameters) {
		super();
		this.commandName = commandName;
		this.alias = alias;
		this.parameters = parameters;
	}

	/**
	 * @return the commandName
	 */
	public String getCommandName() {
		return commandName;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @return the cmd
	 */
	public CoreCommand getCmd() {
		return cmd;
	}

	/**
	 * @param cmd
	 *            the cmd to set
	 */
	public void setCmd(final CoreCommand cmd) {
		this.cmd = cmd;
	}

	public String[] processArguments(final String[] args) {
		final String[] params = parameters.split("\\s+");
		final String[] result = new String[params.length + args.length];
		int index = 0;
		for (final String param : params) {
			result[index++] = param;
		}
		for (final String arg : args) {
			result[index++] = arg;
		}
		return result;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CommandAlias [commandName=" + commandName + ", parameters="
				+ parameters + "]";
	}

}
