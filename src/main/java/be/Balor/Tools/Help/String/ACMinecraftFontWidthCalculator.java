/**
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE Version 2, December 2004
 * Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 * 
 * Everyone is permitted to copy and distribute verbatim or modified copies of
 * this license document, and changing it is allowed as long as the name is
 * changed.
 * 
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE TERMS AND CONDITIONS FOR COPYING,
 * DISTRIBUTION AND MODIFICATION
 * 
 * 0. You just DO WHAT THE FUCK YOU WANT TO.
 */
/*
 * Base Class taken from Help
 * https://github.com/tkelly910/Help
 * 
 */
package be.Balor.Tools.Help.String;

import java.util.LinkedList;

public class ACMinecraftFontWidthCalculator {

	public final static int chatwidth = 318; // 325
	public static String charWidthIndexIndex = " !\"#$%&'()*+,-./"
			+ "0123456789:;<=>?" + "@ABCDEFGHIJKLMNO" + "PQRSTUVWXYZ[\\]^_"
			+ "'abcdefghijklmno" + "pqrstuvwxyz{|}~⌂"
			+ "ÇüéâäàåçêëèïîìÄÅ"
			+ "ÉæÆôöòûùÿÖÜø£Ø×ƒ"
			+ "áíóúñÑªº¿®¬½¼¡«»";
	public static int[] charWidths = {4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6,
			2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6,
			6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6,
			6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2,
			6,
			6,
			// not sure what tkelly made these rows for..
			8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9,
			9, 5, 9, 9, 8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7,
			7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1};

	// chat limmitation: repetitions of characters is limmited to 119 per line
	// so: repeating !'s will not fill a line

	public static int getStringWidth(final String s) {
		int i = 0;
		if (s != null) {
			for (final char c : s.replaceAll("\\u00A7.", "").toCharArray()) {
				i += getCharWidth(c);
			}
		}
		return i;
	}

	public static int getCharWidth(final char c) {
		// return getCharWidth(c, 0);
		final int k = charWidthIndexIndex.indexOf(c);
		if (c != '\247' && k >= 0) {
			return charWidths[k];
		}
		return 0;
	}

	public static int getCharWidth(final char c, final int defaultReturn) {
		final int k = charWidthIndexIndex.indexOf(c);
		if (c != '\247' && k >= 0) {
			return charWidths[k];
		}
		return defaultReturn;
	}

	public static String uncoloredStr(final String s) {
		return s != null ? s.replaceAll("\\u00A7.", "") : s;
	}

	/**
	 * pads str on the right with pad (left-align)
	 * 
	 * @param str
	 *            string to format
	 * @param len
	 *            spaces to pad
	 * @param pad
	 *            character to use when padding
	 * @return str with padding appended
	 */
	public static String strPadRight(final String str, int len, final char pad) {
		// for purposes of this function, assuming a normal char to be 6
		len *= 6;
		len -= getStringWidth(str);
		return str + unformattedStrRepeat(pad, len / getCharWidth(pad, 6));
	}

	public static String strPadRightChat(final String str, int abslen,
			final char pad) {
		abslen -= getStringWidth(str);
		return str + unformattedStrRepeat(pad, abslen / getCharWidth(pad, 6));
	}

	public static String strPadRightChat(final String str, int abslen) {
		abslen -= getStringWidth(str);
		return str + unformattedStrRepeat(' ', abslen / getCharWidth(' ', 6));
	}

	public static String strPadRightChat(final String str, final char pad) {
		final int width = chatwidth - getStringWidth(str);
		return str + unformattedStrRepeat(pad, width / getCharWidth(pad, 6));
	}

	public static String strPadRightChat(final String str) {
		final int width = chatwidth - getStringWidth(str);
		return str + unformattedStrRepeat(' ', width / getCharWidth(' ', 6));
	}

	/**
	 * pads str on the left with pad (right-align)
	 * 
	 * @param str
	 *            string to format
	 * @param len
	 *            spaces to pad
	 * @param pad
	 *            character to use when padding
	 * @return str with padding prepended
	 */
	public static String strPadLeft(final String str, int len, final char pad) {
		// for purposes of this function, assuming a normal char to be 6
		len *= 6;
		len -= getStringWidth(str);
		return unformattedStrRepeat(pad, len / getCharWidth(pad, 6)) + str;
	}

	public static String strPadLeftChat(final String str, int abslen,
			final char pad) {
		abslen -= getStringWidth(str);
		return unformattedStrRepeat(pad, abslen / getCharWidth(pad, 6)).concat(
				str);
	}

	public static String strPadLeftChat(final String str, int abslen) {
		abslen -= getStringWidth(str);
		return unformattedStrRepeat(' ', abslen / getCharWidth(' ', 6)).concat(
				str);
	}

	public static String strPadLeftChat(final String str, final char pad) {
		final int width = chatwidth - getStringWidth(str);
		return unformattedStrRepeat(pad, width / getCharWidth(pad, 6)).concat(
				str);
	}

	public static String strPadLeftChat(final String str) {
		final int width = chatwidth - getStringWidth(str);
		return unformattedStrRepeat(' ', width / getCharWidth(' ', 6)).concat(
				str);
	}

	/**
	 * pads str on the left & right with pad (center-align)
	 * 
	 * @param str
	 *            string to format
	 * @param len
	 *            spaces to pad
	 * @param pad
	 *            character to use when padding
	 * @return str centered with pad
	 */
	public static String strPadCenter(final String str, int len, final char pad) {
		// for purposes of this function, assuming a normal char to be 6
		len *= 6;
		len -= getStringWidth(str);
		final int padwid = getCharWidth(pad, 6);
		final int prepad = (len / padwid) / 2;
		len -= prepad * padwid;
		return unformattedStrRepeat(pad, prepad) + str
				+ unformattedStrRepeat(pad, len / padwid);
	}

	public static String strPadCenterChat(final String str, int abslen,
			final char pad) {
		abslen -= getStringWidth(str);
		final int padwid = getCharWidth(pad, 6);
		final int prepad = (abslen / padwid) / 2;
		abslen -= prepad * padwid;
		return unformattedStrRepeat(pad, prepad) + str
				+ unformattedStrRepeat(pad, abslen / padwid);
	}

	public static String strPadCenterChat(final String str, final char pad) {
		int width = chatwidth - getStringWidth(str);
		final int padwid = getCharWidth(pad, 6);
		final int prepad = (width / padwid) / 2;
		width -= prepad * padwid;
		return unformattedStrRepeat(pad, prepad) + str
				+ unformattedStrRepeat(pad, width / padwid);
	}

	public static int strLen(final String str) {
		if (!str.contains("\u00A7")) {
			return str.length();
		}
		// just searching for \u00A7.
		return str.replaceAll("\\u00A7.", "").length();
	}

	public static String unformattedPadRight(String str, final int len,
			final char pad) {
		for (int i = strLen(str); i < len; ++i) {
			str += pad;
		}
		return str;
	}

	public static String unformattedPadLeft(final String str, final int len,
			final char pad) {
		return unformattedStrRepeat(pad, len - strLen(str)) + str;
	}

	public static String unformattedPadCenter(final String str, int len,
			final char pad) {
		len -= strLen(str);
		final int prepad = len / 2;
		return unformattedStrRepeat(pad, prepad) + str
				+ unformattedStrRepeat(pad, len - prepad);
	}

	public static String unformattedStrRepeat(final char ch, final int len) {
		String str = "";
		for (int i = 0; i < len; ++i) {
			str += ch;
		}
		return str;
	}

	public static String strTrim(final String str, final int length) {
		if (uncoloredStr(str).length() > length) {
			int width = length;
			String ret = "";
			boolean lastCol = false;
			for (final char c : str.toCharArray()) {
				if (c == '\u00A7') {
					ret += c;
					lastCol = true;
				} else {
					if (!lastCol) {
						if (width - 1 >= 0) {
							width -= 1;
							ret += c;
						} else {
							return ret;
						}
					} else {
						ret += c;
						lastCol = false;
					}
				}
			}
		}
		return str;
	}

	public static String strChatTrim(final String str) {
		return strChatTrim(str, chatwidth);
	}

	public static String strChatTrim(final String str, final int absLen) {
		int width = getStringWidth(str);
		if (width > absLen) {
			width = absLen;
			String ret = "";
			boolean lastCol = false;
			for (final char c : str.toCharArray()) {
				if (c == '\u00A7') {
					ret += c;
					lastCol = true;
				} else {
					if (!lastCol) {
						final int w = getCharWidth(c);
						if (width - w >= 0) {
							width -= w;
							ret += c;
						} else {
							return ret;
						}
					} else {
						ret += c;
						lastCol = false;
					}
				}
			}
		}
		return str;
	}

	public static String strChatWordWrap(final String str) {
		return strChatWordWrap(str, 0, ' ');
	}

	public static String strChatWordWrap(final String str, final int tab) {
		return strChatWordWrap(str, tab, ' ');
	}

	public static String strChatWordWrap(String str, final int tab,
			final char tabChar) {
		String ret = "";
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= chatwidth) {
				return (ret.length() > 0 ? ret + "\n"
						+ unformattedStrRepeat(tabChar, tab) : "").concat(str);
			}
			final String line1 = strChatTrim(str);
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			}
			// ret += strPadRightChat((ret.length() > 0 ?
			// unformattedStrRepeat(tabChar, tab) : "") + str.substring(0,
			// lastPos));
			ret += (ret.length() > 0 ? "\n"
					+ unformattedStrRepeat(tabChar, tab) : "")
					+ str.substring(0, lastPos);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	public static String strChatWordWrapRight(final String str, final int tab) {
		return strChatWordWrapRight(str, tab, ' ');
	}

	/**
	 * right-aligns paragraphs
	 * 
	 * @param str
	 * @param tab
	 * @param tabChar
	 * @return
	 */
	public static String strChatWordWrapRight(String str, final int tab,
			final char tabChar) {
		String ret = "";
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= chatwidth) {
				return (ret.length() > 0 ? ret + "\n" : "")
						.concat(strPadLeftChat(str, tabChar));
			}
			final String line1 = strChatTrim(str);
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			}
			// ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" : "")
					+ strPadLeftChat(str.substring(0, lastPos), tabChar);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	/**
	 * will left-align the start of the string until sepChar, then right-align
	 * the remaining paragraph
	 * 
	 * @param str
	 * @param tab
	 * @param tabChar
	 * @param sepChar
	 * @return
	 */
	public static String strChatWordWrapRight(String str, final int tab,
			final char tabChar, final char sepChar) {
		String ret = "";
		String line1 = strChatTrim(str);
		// first run the first left & right align
		if (line1.contains("" + sepChar)) {
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			final int sepPos = line1.indexOf(sepChar) + 1;
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			} else if (sepPos > lastPos) {
				lastPos = sepPos;
			}
			ret += str.substring(0, sepPos);
			ret += strPadLeftChat(str.substring(sepPos, lastPos), chatwidth
					- getStringWidth(ret));
			str = str.substring(lastPos + 1);
		}
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= chatwidth) {
				return (ret.length() > 0 ? ret + "\n" : "")
						.concat(strPadLeftChat(str, tabChar));
			}
			line1 = strChatTrim(str);
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			}
			// ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" + lastStrColor(ret) : "")
					+ strPadLeftChat(str.substring(0, lastPos), tabChar);
			str = str.substring(lastPos + 1);
		}
		System.out.println(str + " changed to " + ret + "("
				+ getStringWidth(ret) + " pixels long)");
		return ret;
	}

	public static String strWordWrap(final String str, final int width) {
		return strWordWrap(str, width, 0, ' ');
	}

	public static String strWordWrap(final String str, final int width,
			final int tab) {
		return strWordWrap(str, width, tab, ' ');
	}

	public static String strWordWrap(String str, final int width,
			final int tab, final char tabChar) {
		String ret = "";
		while (str.length() > 0) {
			// find last char of first line
			if (strLen(str) <= width) {
				return (ret.length() > 0 ? ret + "\n"
						+ unformattedStrRepeat(tabChar, tab) : "").concat(str);
			}
			final String line1 = strTrim(str, width);
			int lastPos = line1.length()
					- (ret.length() > 0 && line1.length() > tab + 1
							? tab + 1
							: 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length()
						- (ret.length() > 0 && line1.length() > tab + 1
								? tab + 1
								: 1);
			}
			// ret += strPadRightChat((ret.length() > 0 ?
			// unformattedStrRepeat(tabChar, tab) : "") + str.substring(0,
			// lastPos));
			ret += (ret.length() > 0 ? "\n"
					+ unformattedStrRepeat(tabChar, tab) : "")
					+ str.substring(0, lastPos);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	public static String strWordWrapRight(final String str, final int width,
			final int tab) {
		return strWordWrapRight(str, width, tab, ' ');
	}

	/**
	 * right-aligns paragraphs
	 * 
	 * @param str
	 * @param width
	 * @param tab
	 * @param tabChar
	 * @return
	 */
	public static String strWordWrapRight(String str, final int width,
			final int tab, final char tabChar) {
		String ret = "";
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= width) {
				return (ret.length() > 0 ? ret + "\n" : "")
						.concat(unformattedPadLeft(str, width, tabChar));
			}
			final String line1 = strTrim(str, width);
			int lastPos = line1.length()
					- (ret.length() > 0 && line1.length() > tab + 1
							? tab + 1
							: 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos <= 0) {
				lastPos = line1.length()
						- (ret.length() > 0 && line1.length() > tab + 1
								? tab + 1
								: 1);
			}
			// ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" : "")
					+ unformattedPadLeft(str.substring(0, lastPos), width,
							tabChar);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	/**
	 * will left-align the start of the string until sepChar, then right-align
	 * the remaining paragraph
	 * 
	 * @param str
	 * @param width
	 * @param tab
	 * @param tabChar
	 * @param sepChar
	 * @return
	 */
	public static String strWordWrapRight(String str, final int width,
			final int tab, final char tabChar, final char sepChar) {
		String ret = "";
		String line1 = strTrim(str, width);
		// first run the first left & right align
		if (line1.contains("" + sepChar)) {
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			final int sepPos = line1.indexOf(sepChar) + 1;
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length()
						- (ret.length() > 0 && line1.length() > tab + 1
								? tab + 1
								: 1);
			} else if (sepPos > lastPos) {
				lastPos = sepPos;
			}
			ret += str.substring(0, sepPos);
			ret += strPadLeftChat(str.substring(sepPos, lastPos), width
					- strLen(ret));
			str = str.substring(lastPos + 1);
		}
		while (str.length() > 0) {
			// find last char of first line
			if (strLen(str) <= width) {
				return (ret.length() > 0 ? ret + "\n" : "")
						.concat(unformattedPadLeft(str, width, tabChar));
			}
			line1 = strChatTrim(str);
			int lastPos = line1.length()
					- (ret.length() > 0 && line1.length() > tab + 1
							? tab + 1
							: 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length()
						- (ret.length() > 0 && line1.length() > tab + 1
								? tab + 1
								: 1);
			}
			// ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" + lastStrColor(ret) : "")
					+ unformattedPadLeft(str.substring(0, lastPos), width,
							tabChar);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	public static String lastStrColor(final String str) {
		final int i = str.lastIndexOf('\u00A7');
		if (i >= 0 && i + 1 < str.length()) {
			return str.substring(i, i + 2);
		}
		return "\u00A7F";// white
	}

	private static boolean containsAlignTag(final String str, final String tag) {
		final int pos = str.indexOf("<" + tag);
		if (pos >= 0) {
			return str.length() > pos + ("<" + tag).length()
					&& (str.charAt(pos + ("<" + tag).length()) == '>' || str
							.charAt(pos + ("<" + tag).length() + 1) == '>');
		}
		return false;
	}

	/**
	 * UNTESTED: DON'T USE YET
	 */
	public static String alignTags(String input,
			final boolean minecraftChatFormat) {
		for (final String fm : new String[]{"l", "r", "c"}) {
			while (containsAlignTag(input, fm)) {
				char repl = ' ';
				if (input.matches("^.*<" + fm + ".>.*$")) {
					repl = input.substring(input.indexOf("<" + fm) + 2).charAt(
							0);
					input = input.replaceFirst("<" + fm + ".>", "<" + fm + ">");
				}

				if (fm.equals("l")) {
					if (minecraftChatFormat) {
						input = strPadRight(
								input.substring(0,
										input.indexOf("<" + fm + ">")),
								input.indexOf("<" + fm + ">"), repl)
								+ input.substring(input.indexOf("<" + fm + ">") + 3);
					} else {
						input = Str.padRight(
								input.substring(0,
										input.indexOf("<" + fm + ">")),
								input.indexOf("<" + fm + ">"), repl)
								+ input.substring(input.indexOf("<" + fm + ">") + 3);
					}
				} else if (fm.equals("c")) {
					if (minecraftChatFormat) {
						input = strPadCenter(
								input.substring(0,
										input.indexOf("<" + fm + ">")),
								input.indexOf("<" + fm + ">"), repl)
								+ input.substring(input.indexOf("<" + fm + ">") + 3);
					} else {
						input = Str.padCenter(
								input.substring(0,
										input.indexOf("<" + fm + ">")),
								input.indexOf("<" + fm + ">"), repl)
								+ input.substring(input.indexOf("<" + fm + ">") + 3);
					}
				} else {
					if (minecraftChatFormat) {
						input = strPadLeft(
								input.substring(0,
										input.indexOf("<" + fm + ">")),
								input.indexOf("<" + fm + ">"), repl)
								+ input.substring(input.indexOf("<" + fm + ">") + 3);
					} else {
						input = Str.padLeft(
								input.substring(0,
										input.indexOf("<" + fm + ">")),
								input.indexOf("<" + fm + ">"), repl)
								+ input.substring(input.indexOf("<" + fm + ">") + 3);
					}
				}
			}
		}
		return input;
	}

	public static LinkedList<String> alignTags(LinkedList<String> input,
			final boolean minecraftChatFormat) {
		for (final String fm : new String[]{"l", "r", "c"}) {
			while (containsAlignTag(input.get(1), fm)) {
				char repl = ' ';
				if (input.get(1).matches("^.*<" + fm + ".>.*$")) {// ||
																	// input.get(1).matches("^.*<r.>.*$"))
																	// {
					repl = input.get(1)
							.substring(input.get(1).indexOf("<" + fm) + 2)
							.charAt(0); // ,
										// input.get(1).indexOf(">")
					for (int i = 0; i < input.size(); ++i) {
						input.set(
								i,
								input.get(i).replaceFirst("<" + fm + ".>",
										"<" + fm + ">"));
					}
				}

				int maxPos = 0;
				for (int i = 1; i < input.size(); ++i) {
					if (input.get(i).indexOf("<" + fm + ">") > maxPos) {
						maxPos = input.get(i).indexOf("<" + fm + ">");
					}
				}

				final LinkedList<String> newinput = new LinkedList<String>();
				for (int i = 0; i < input.size(); ++i) {
					final String line = input.get(i);
					if (line.indexOf("<" + fm + ">") != -1) {
						if (fm.equals("l")) {
							if (minecraftChatFormat) {
								newinput.add(strPadRight(
										line.substring(0,
												line.indexOf("<" + fm + ">")),
										maxPos, repl)
										+ line.substring(line.indexOf("<" + fm
												+ ">") + 3));
							} else {
								newinput.add(Str.padRight(
										line.substring(0,
												line.indexOf("<" + fm + ">")),
										maxPos, repl)
										+ line.substring(line.indexOf("<" + fm
												+ ">") + 3));
							}
						} else if (fm.equals("c")) {
							if (minecraftChatFormat) {
								newinput.add(strPadCenter(
										line.substring(0,
												line.indexOf("<" + fm + ">")),
										maxPos, repl)
										+ line.substring(line.indexOf("<" + fm
												+ ">") + 3));
							} else {
								newinput.add(Str.padCenter(
										line.substring(0,
												line.indexOf("<" + fm + ">")),
										maxPos, repl)
										+ line.substring(line.indexOf("<" + fm
												+ ">") + 3));
							}
						} else {
							if (minecraftChatFormat) {
								newinput.add(strPadLeft(
										line.substring(0,
												line.indexOf("<" + fm + ">")),
										maxPos, repl)
										+ line.substring(line.indexOf("<" + fm
												+ ">") + 3));
							} else {
								newinput.add(Str.padLeft(
										line.substring(0,
												line.indexOf("<" + fm + ">")),
										maxPos, repl)
										+ line.substring(line.indexOf("<" + fm
												+ ">") + 3));
							}
						}
					} else {
						newinput.add(line);
					}
				}
				input = newinput;
			}
		}
		return input;
	}
}