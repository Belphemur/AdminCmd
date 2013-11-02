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
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Tools;

import info.somethingodd.OddItem.OddItemBase;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.NotANumberException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Blocks.IBlockRemanenceFactory;
import be.Balor.Tools.Blocks.LogBlockRemanenceFactory;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import de.JeterLP.MakeYourOwnCommands.utils.CommandUtils;
import de.diddiz.LogBlock.Consumer;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public final class Utils {
	public static OddItemBase oddItem = null;
	public static Consumer logBlock = null;
        public static CommandUtils myoc = null;
	public static boolean mChatPresent = false;
	public static boolean signExtention = false;
	public final static long secondInMillis = 1000;
	public final static long minuteInMillis = secondInMillis * 60;
	public final static long hourInMillis = minuteInMillis * 60;
	public final static long dayInMillis = hourInMillis * 24;
	public final static int secInTick = 20;
	public static final Pattern REGEX_IP_V4 = Pattern
			.compile("\\b(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b");
	public static final Pattern REGEX_INACCURATE_IP_V4 = Pattern
			.compile("\\b([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\b");
	public static final Pattern NUMBERS = Pattern.compile("(\\d*[.|\\.]?\\d+)"
			+ "|(\\d+)");
	public static final Pattern TIMES1 = Pattern
			.compile("month(s?)|day(s?)|hour(s?)|week(s?)|year(s?)");
	public static final Pattern TIMES2 = Pattern.compile("m|h|d|w|y");

	/**
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */
	/**
	 *
	 */
	private Utils() {
	}

	public final static int MAX_BLOCKS = 512;

	@SuppressWarnings("unchecked")
	public static <T> T[] Arrays_copyOfRange(final T[] original,
			final int start, final int end) {
		if (original.length >= start && 0 <= start) {
			if (start <= end) {
				final int length = end - start;
				final int copyLength = Math
						.min(length, original.length - start);
				final T[] copy = (T[]) Array.newInstance(original.getClass()
						.getComponentType(), length);

				System.arraycopy(original, start, copy, 0, copyLength);
				return copy;
			}
			throw new IllegalArgumentException();
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public static double getDistanceSquared(final Player player1,
			final Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName())) {
			return Double.MAX_VALUE;
		}
		final Location loc1 = player1.getLocation();
		final Location loc2 = player2.getLocation();
		return Math.pow((loc1.getX() - loc2.getX()), 2)
				+ Math.pow((loc1.getZ() - loc2.getZ()), 2);
	}

	/**
	 * Get the elapsed time since the start.
	 * 
	 * @param start
	 * @return
	 */
	public static Long[] getElapsedTime(final long start) {
		return transformToElapsedTime(System.currentTimeMillis() - start);
	}

	/**
	 * Get the real time from the server
	 * 
	 * @author Lathanael
	 * @param gmt
	 *            The wanted GMT offset
	 * @return serverTime Represents the time read from the server
	 */
	public static Date getServerRealTime(final String gmt) {
		Date serverTime;
		final TimeZone tz = TimeZone.getTimeZone(gmt);
		final Calendar cal = Calendar.getInstance(tz);
		cal.setTime(new Date());
		serverTime = cal.getTime();
		return serverTime;
	}

	/**
	 * Replace the time and date to the format given in the config with the
	 * corresponding date and time
	 * 
	 * @author Lathanael
	 * @param 
	 * @return timeFormatted
	 */
	public static String replaceDateAndTimeFormat(final Date date) {
		String timeFormatted = "";
		final String format = ConfigEnum.DT_FORMAT.getString();
		final SimpleDateFormat formater = new SimpleDateFormat(format);
		final Date serverTime = date;
		timeFormatted = formater.format(serverTime);
		return timeFormatted;
	}

	public static String replaceDateAndTimeFormat(final ACPlayer player,
			final Type.Whois type) {
		final String format = ConfigEnum.DT_FORMAT.getString();
		final SimpleDateFormat formater = new SimpleDateFormat(format);
		String lastlogin = "";
		lastlogin = formater.format(new Date(player.getInformation(
				type.getVal()).getLong(1)));
		if (lastlogin == formater.format(new Date(1))) {
			return null;
		}
		return lastlogin;
	}

	/**
	 * @param logBlock
	 *            the logBlock to set
	 */
	public static void setLogBlock(final Consumer logBlock) {
		Utils.logBlock = logBlock;
		IBlockRemanenceFactory.FACTORY = new LogBlockRemanenceFactory();
	}

	/**
	 * Transform a given time to an elapsed time.
	 * 
	 * @param time
	 *            in milisec
	 * @return Long[] containing days, hours, mins and sec.
	 */
	public static Long[] transformToElapsedTime(final long time) {
		long diff = time;

		final long elapsedDays = diff / dayInMillis;
		diff = diff % dayInMillis;
		final long elapsedHours = diff / hourInMillis;
		diff = diff % hourInMillis;
		final long elapsedMinutes = diff / minuteInMillis;
		diff = diff % minuteInMillis;
		final long elapsedSeconds = diff / secondInMillis;

		return new Long[] { elapsedDays, elapsedHours, elapsedMinutes,
				elapsedSeconds };
	}

	private static String timeLongToSring(final Long time) {
		return time < 10 ? "0" + time : time.toString();
	}

	/**
	 * Send the played time of a player to a another one.
	 * 
	 * @param playername
	 *            name of the player that the time belong to
	 * @param total
	 *            total time played
	 */
	public static Map<String, String> playedTime(final String playername,
			final long total) {
		final Long[] time = Utils.transformToElapsedTime(total);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("d", time[0].toString());
		replace.put("h", timeLongToSring(time[1]));
		replace.put("m", timeLongToSring(time[2]));
		replace.put("s", timeLongToSring(time[3]));
		replace.put("player", playername);
		return replace;
	}

	/**
	 * Cut in 2 part the given time if it's in the format : <br />
	 * <blockquote> <Xday | Xhour | Xminute | Xweek | Xmonth> </blockquote>
	 * 
	 * @param toParse
	 *            input to be parsed
	 * @return a 2 sized String array with the parsed time if successful, else
	 *         an empty one.
	 */
	public static String[] tempStringParser(final String toParse) {
		final String[] parsed = new String[2];
		final Matcher numberMatcher = NUMBERS.matcher(toParse);
		final Matcher time1Matcher = TIMES1.matcher(toParse);
		final Matcher time2Matcher = TIMES2.matcher(toParse);
		if (numberMatcher.find()) {
			parsed[0] = numberMatcher.group();
		}
		if (time1Matcher.find()) {
			parsed[1] = time1Matcher.group();
		} else if (time2Matcher.find()) {
			parsed[1] = time2Matcher.group();
		}
		return parsed;
	}

	/**
	 * Parse the given string to get the time in an integer it's in the format : <br />
	 * <blockquote> <Xday | Xhour | Xminute | Xweek | Xmonth> </blockquote>
	 * 
	 * @param toParse
	 *            time to parse
	 * @return time parsed, -1 if nothing to be parsed
	 * @throws NotANumberException
	 *             if the String to be parsed doesn't have the right format
	 */
	public static int timeParser(final String toParse)
			throws NotANumberException {
		DebugLog.beginInfo("Parsing time : " + toParse);
		try {
			int tmpBan;
			final String[] tmpTimeParsed = Utils.tempStringParser(toParse);
			if (tmpTimeParsed[0] == null) {
				return -1;
			}
			if (tmpTimeParsed[1] == null) {
				try {
					return Integer.parseInt(tmpTimeParsed[0]);
				} catch (final NumberFormatException e) {
					throw new NotANumberException("Time given : "
							+ tmpTimeParsed[0], e);
				}
			} else {
				DebugLog.addInfo("Parsed ouput : " + tmpTimeParsed[0] + "-"
						+ tmpTimeParsed[1]);
				try {
					tmpBan = Integer.parseInt(tmpTimeParsed[0]);
				} catch (final NumberFormatException e) {
					throw new NotANumberException("Time given : "
							+ tmpTimeParsed[0], e);
				}
				final String timeMulti = tmpTimeParsed[1];
				if (timeMulti.contains("month") || timeMulti.contains("m")) {
					return tmpBan * 43200;
				}
				if (timeMulti.contains("week") || timeMulti.contains("w")) {
					return tmpBan * 10080;
				}
				if (timeMulti.contains("day") || timeMulti.contains("d")) {
					return tmpBan * 1440;
				}
				if (timeMulti.contains("hour") || timeMulti.contains("h")) {
					return tmpBan * 60;
				}
				if (timeMulti.contains("year") || timeMulti.contains("y")) {
					return tmpBan * 525600;
				}
				throw new NotANumberException("Can't parse the time : "
						+ tmpTimeParsed[1]);
			}
		} finally {
			DebugLog.endInfo();
		}
	}
}
