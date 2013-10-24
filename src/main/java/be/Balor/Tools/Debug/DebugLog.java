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
	private static class DebugInfo {
		private final String blockMsg;
		private final Long timeStamp;

		/**
		 * @param blockMsg
		 * @param timeStamp
		 */
		public DebugInfo(final String blockMsg) {
			this.blockMsg = blockMsg;
			this.timeStamp = System.currentTimeMillis();
		}

		/**
		 * @return the blockMsg
		 */
		public String getBlockMsg() {
			return blockMsg;
		}

		/**
		 * @return the timeStamp
		 */
		public Long getElapsedTime() {
			return System.currentTimeMillis() - timeStamp;
		}

	}

	private static class DebugThreadLocale extends
			ThreadLocal<Stack<DebugInfo>> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		@Override
		protected Stack<DebugInfo> initialValue() {
			return new Stack<DebugInfo>();
		}
	}

	public static final Logger INSTANCE = Logger.getLogger("AdminCmd");
	public static final String BEGIN_PREFIX = "[BEGIN] ";
	public static final String END_PREFIX = "[END] ";
	private static final ThreadLocal<Stack<DebugInfo>> debugInfos = new DebugThreadLocale();
	private static boolean disabled = false;
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
		disabled = true;
	}

	/**
	 * Begin logging of a block of code
	 * 
	 * @param msg
	 */
	public static void beginInfo(final String msg) {
		if (disabled) {
			return;
		}
		debugInfos.get().add(new DebugInfo(msg));
		INSTANCE.info(getSpaces() + BEGIN_PREFIX + msg);

	}

	/**
	 * @return
	 */
	private static String getSpaces(final int add) {
		final StringBuffer space = new StringBuffer();
		for (int i = 0; i < debugInfos.get().size() + add; i++) {
			space.append("\t");
		}
		return space.toString();
	}

	private static String getSpaces() {
		return getSpaces(0);
	}

	/**
	 * Add a message between beginInfo and endInfo
	 * 
	 * @param message
	 *            message
	 * @param level
	 *            level of the log
	 */
	public static void addInfo(final String message, final Level level) {
		if (disabled) {
			return;
		}
		INSTANCE.log(level, getSpaces(1) + message);
	}

	/**
	 * Add a message between beginInfo and endInfo
	 * 
	 * @param message
	 *            message
	 */
	public static void addInfo(final String message) {
		addInfo(message, Level.INFO);
	}

	/**
	 * Log the exception betwen beginInfo and endInfo
	 * 
	 * @param message
	 *            message
	 * @param thrown
	 *            the exception
	 */
	public static void addException(final String message, final Exception thrown) {
		if (disabled) {
			return;
		}
		INSTANCE.log(Level.SEVERE, getSpaces(1) + message, thrown);
	}

	/**
	 * End logging a block of code
	 */
	public static void endInfo() {
		if (disabled) {
			return;
		}
		try {
			final String spaces = getSpaces();
			final DebugInfo info = debugInfos.get().pop();
			INSTANCE.info(spaces + END_PREFIX + info.getBlockMsg() + " => "
					+ info.getElapsedTime() + " milisec");
		} catch (final Exception e) {
		}

	}
}
