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
package be.Balor.Tools.Help;

import java.util.List;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
class CmdMatch {
	private final String cmd;
	private final List<HelpEntry> result;

	/**
	 * @param cmd
	 * @param result
	 */
	CmdMatch(final String cmd, final List<HelpEntry> result) {
		super();
		this.cmd = cmd;
		this.result = result;
	}

	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}

	/**
	 * @return the result
	 */
	public List<HelpEntry> getResult() {
		return result;
	}

}
