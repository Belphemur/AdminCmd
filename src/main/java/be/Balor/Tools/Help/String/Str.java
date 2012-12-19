/**
 * Programmer: Jacob Scott
 * Program Name: Str
 * Description:
 * Date: Mar 31, 2011
 */
package be.Balor.Tools.Help.String;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * @author jacob
 */
public class Str extends OutputStream {

	public String text = "";

	public static String argStr(final String[] s) {
		return argStr(s, " ", 0);
	}

	public static String argStr(final String[] s, final int start) {
		return argStr(s, " ", start);
	}

	public static String argStr(final String[] s, final String sep) {
		return argStr(s, sep, 0);
	}

	public static String argStr(final String[] s, final String sep,
			final int start) {
		String ret = "";
		if (s != null) {
			for (int i = start; i < s.length; ++i) {
				ret += s[i];
				if (i + 1 < s.length) {
					ret += sep;
				}
			}
		}
		return ret;
	}

	public static String argStr(final String[] s, final String sep,
			final int start, final int length) {
		String ret = "";
		if (s != null) {
			for (int i = start, j = 0; i < s.length && j < length; ++i, ++j) {
				ret += s[i];
				if (i + 1 < s.length) {
					ret += sep;
				}
			}
		}
		return ret;
	}

	public static boolean isIn(String input, final String[] check) {
		input = input.trim();
		for (final String c : check) {
			if (input.equalsIgnoreCase(c.trim())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isIn(String input, final String check) {
		final String comms[] = check.split(",");
		input = input.trim();
		for (final String c : comms) {
			if (input.equalsIgnoreCase(c.trim())) {
				return true;
			}
		}
		return false;
	}

	public static boolean startIsIn(final String input, final String check) {
		final String comms[] = check.split(",");
		for (final String c : comms) {
			if (input.length() >= c.length()) {
				if (input.substring(0, c.length()).equalsIgnoreCase(c)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean startIsIn(final String input, final String[] check) {
		for (final String c : check) {
			if (input.length() >= c.length()) {
				if (input.substring(0, c.length()).equalsIgnoreCase(c)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int count(final String str, final String find) {
		int c = 0;
		for (int i = 0; i < str.length() - find.length(); ++i) {
			if (str.substring(i, i + find.length()).equals(find)) {
				++c;
			}
		}
		return c;
	}

	public static int count(final String str, final char find) {
		int c = 0;
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) == find) {
				++c;
			}
		}
		return c;
	}

	public static int countIgnoreCase(final String str, final String find) {
		int c = 0;
		for (int i = 0; i < str.length() - find.length(); ++i) {
			if (str.substring(i, i + find.length()).equalsIgnoreCase(find)) {
				++c;
			}
		}
		return c;
	}

	public static int indexOf(final String array[], final String search) {
		if (array != null && array.length > 0) {
			for (int i = array.length - 1; i >= 0; --i) {
				if (array[i].equals(search)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int indexOfIgnoreCase(final String array[],
			final String search) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i].equalsIgnoreCase(search)) {
				return i;
			}
		}
		return -1;
	}

	public static String getStackStr(final Exception err) {
		if (err == null) {// || err.getCause() == null) {
			return "";
		}
		final Str stackoutstream = new Str();
		final PrintWriter stackstream = new PrintWriter(stackoutstream);
		err.printStackTrace(stackstream);
		stackstream.flush();
		stackstream.close();
		return stackoutstream.text;

	}

	/**
	 * pads str on the right (space-padded) (left-align)
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String padRight(String str, final int len) {
		for (int i = str.length(); i < len; ++i) {
			str += ' ';
		}
		return str;
	}

	/**
	 * pads str on the right with pad (left-align)
	 * 
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 */
	public static String padRight(String str, final int len, final char pad) {
		for (int i = str.length(); i < len; ++i) {
			str += pad;
		}
		return str;
	}

	/**
	 * pads str on the left (space-padded) (right-align)
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String padLeft(final String str, final int len) {
		return repeat(' ', len - str.length()) + str;
	}

	/**
	 * pads str on the left with pad (right-align)
	 * 
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 */
	public static String padLeft(final String str, final int len, final char pad) {
		return repeat(pad, len - str.length()) + str;
	}

	/**
	 * pads str on the left & right (space-padded) (center-align)
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String padCenter(final String str, int len) {
		len -= str.length();
		final int prepad = len / 2;
		return repeat(' ', prepad) + str + repeat(' ', len - prepad);
	}

	/**
	 * pads str on the left & right with pad (center-align)
	 * 
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 */
	public static String padCenter(final String str, int len, final char pad) {
		len -= str.length();
		final int prepad = len / 2;
		return repeat(pad, prepad) + str + repeat(pad, len - prepad);
	}

	public static String repeat(final char ch, final int len) {
		String str = "";
		for (int i = 0; i < len; ++i) {
			str += ch;
		}
		return str;
	}

	/**
	 * Returns a sequence str of the provided str count # of times
	 * 
	 * @param str
	 * @param count
	 * @return
	 */
	public static String repeat(final String str, final int count) {
		String retstr = "";
		for (int i = 0; i < count; ++i) {
			retstr += str;
		}
		return retstr;
	}

	@Override
	public void write(final int b) throws IOException {
		text += (char) b;
	}

	public static String[] removePortionOfArray(final String[] array,
			final int from, final int to, final String replace) {
		final String[] newArray = new String[from + array.length - to
				- (replace == null ? 1 : 0)];
		System.arraycopy(array, 0, newArray, 0, from);
		if (replace != null) {
			newArray[from] = replace;
		}
		System.arraycopy(array, to + (replace == null ? 0 : 1), newArray, from
				+ (replace == null ? 0 : 1), array.length - to - 1);
		return newArray;
	}

	public static String[] removeCaseOfArray(final String[] array,
			final int index) {
		final String[] newArray = new String[array.length - 1];
		for (int i = 0; i < index; i++) {
			newArray[i] = array[i];
		}
		for (int j = index + 1; j < array.length; j++) {
			newArray[j - 1] = array[j];
		}
		return newArray;
	}

	public static String[] concat(final String[] A, final String[] B) {
		final String[] C = new String[A.length + B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);

		return C;
	}

	/**
	 * Search for the given string in the list and return it.
	 * 
	 * @param container
	 * @param search
	 * @return
	 */
	public static String matchString(final Collection<String> container,
			final String search) {
		String found = null;
		if (search == null) {
			return found;
		}
		final String lowerSearch = search.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (final String str : container) {
			if (str.toLowerCase().startsWith(lowerSearch)) {
				final int curDelta = str.length() - lowerSearch.length();
				if (curDelta < delta) {
					found = str;
					delta = curDelta;
				}
				if (curDelta == 0) {
					break;
				}
			}
		}
		return found;

	}

} // end class Str
