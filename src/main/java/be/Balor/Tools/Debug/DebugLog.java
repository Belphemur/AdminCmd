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
package be.Balor.Tools.Debug;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.Files;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class DebugLog {
	public static final Logger INSTANCE = Logger.getLogger("AdminCmd");
	public static final String BEGIN_PREFIX = "[BEGIN] ";
	public static final String END_PREFIX = "[END] ";
	private static final Stack<String> blockMsg = new Stack<String>();
	private static final Stack<Long> timestamp = new Stack<Long>();
	static {
		INSTANCE.setUseParentHandlers(false);
		INSTANCE.setLevel(Level.ALL);
	}

	public static void setFile(final String path) {
		FileHandler fh;
		try {
			// This block configure the logger with handler and formatter
			final File file = new File(path + File.separator + "debug.log");
			if (file.exists()) {
				file.delete();
			} else {
				Files.createParentDirs(file);
			}

			fh = new FileHandler(file.getPath(), true);
			INSTANCE.addHandler(fh);
			fh.setFormatter(new LogFormatter());

			// the following statement is used to log any messages
			INSTANCE.info("Logger created");

		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopLogging() {
		for (final Handler h : INSTANCE.getHandlers()) {
			h.close();
			INSTANCE.removeHandler(h);
		}
	}

	/**
	 * Begin logging of a block of code
	 * 
	 * @param msg
	 */
	public static void beginInfo(final String msg) {
		timestamp.add(System.currentTimeMillis());
		blockMsg.add(msg);
		INSTANCE.info(getSpaces() + BEGIN_PREFIX + msg);

	}

	/**
	 * @return
	 */
	private static String getSpaces() {
		final StringBuffer space = new StringBuffer();
		for (int i = 0; i < timestamp.size(); i++) {
			space.append("\t");
		}
		return space.toString();
	}

	/**
	 * End logging a block of code
	 */
	public static void endInfo() {
		try {
			INSTANCE.info(getSpaces() + END_PREFIX + blockMsg.pop() + " => "
					+ (System.currentTimeMillis() - timestamp.pop())
					+ " milisec");
		} catch (final Exception e) {
		}

	}
}
