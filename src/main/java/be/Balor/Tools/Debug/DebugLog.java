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
}
