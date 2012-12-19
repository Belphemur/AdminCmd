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
package be.Balor.Manager.Terminal.Commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.bukkit.command.CommandSender;

import be.Balor.Manager.Terminal.TerminalCommand;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class WindowsTerminalCommand extends TerminalCommand {

	/**
	 * @param commandName
	 * @param execution
	 * @param args
	 * @param workingDir
	 */
	public WindowsTerminalCommand(final String commandName,
			final String execution, final String args, final File workingDir) {
		super(commandName, execution, args, workingDir);
	}

	/**
	 * @param commandName
	 * @param execution
	 * @param args
	 * @param workingDir
	 */
	public WindowsTerminalCommand(final String commandName,
			final String execution, final String args, final String workingDir) {
		super(commandName, execution, args, workingDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Terminal.TerminalCommand#execute(org.bukkit.command.
	 * CommandSender)
	 */
	@Override
	public void execute(final CommandSender sender) {
		try {
			ProcessBuilder pb;
			if (args != null) {
				pb = new ProcessBuilder("cmd.exe", "/C", execution, args);
			} else {
				pb = new ProcessBuilder("cmd.exe", "/C", execution);
			}
			pb.redirectErrorStream(true);
			pb.directory(workingDir);
			final Process p = pb.start();
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				sender.sendMessage(line);
			}
		} catch (final Throwable e) {
			sender.sendMessage("CMD ERROR : " + e.getMessage());
			e.printStackTrace();
		}

	}

}
