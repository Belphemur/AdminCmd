// $Id$
/*
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package be.Balor.Manager.Commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.Balor.Tools.Help.String.Str;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandArgs implements Iterable<String> {
	protected static final String QUOTE_CHARS = "\'\"";
	protected final List<String> args;
	protected final Set<Character> booleanFlags = new HashSet<Character>();
	public final int length;

	public CommandArgs(String args) {
		this(args.split(" "));
	}

	/**
		 * 
		 */
	public CommandArgs(String[] args) {
		char quotedChar;
		for (int i = 0; i < args.length; ++i) {
			if (args[i].length() == 0) {
				args = Str.removeCaseOfArray(args, i);
			} else if (QUOTE_CHARS.indexOf(String.valueOf(args[i].charAt(0))) != -1) {
				StringBuilder build = new StringBuilder();
				quotedChar = args[i].charAt(0);
				int endIndex = i;
				for (; endIndex < args.length; endIndex++) {
					if (args[endIndex].charAt(args[endIndex].length() - 1) == quotedChar) {
						if (endIndex != i)
							build.append(" ");
						build.append(args[endIndex].substring(endIndex == i ? 1 : 0,
								args[endIndex].length() - 1));
						break;
					} else if (endIndex == i) {
						build.append(args[endIndex].substring(1));
					} else {
						build.append(" ").append(args[endIndex]);
					}
				}
				args = Str.removePortionOfArray(args, i, endIndex, build.toString());
			} else if (args[i].charAt(0) == '-' && args[i].matches("^-[a-zA-Z]+$")) {
				for (int k = 1; k < args[i].length(); ++k) {
					booleanFlags.add(args[i].charAt(k));
				}
				args = Str.removeCaseOfArray(args, i);			
				i--;
			}
		}
		this.args = Arrays.asList(args);
		this.length = args.length;
	}

	public String getString(int index) {
		try {
			return args.get(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}

	}

	public int getInt(int index) throws NumberFormatException {
		return Integer.parseInt(getString(index));
	}

	public float getFloat(int index) throws NumberFormatException {
		return Float.parseFloat(getString(index));
	}

	public double getDouble(int index) throws NumberFormatException {
		return Double.parseDouble(getString(index));
	}
	public long getLong(int index) throws NumberFormatException {
		return Long.parseLong(getString(index));
	}
	public boolean hasFlag(char ch) {
		return booleanFlags.contains(ch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Arrays.toString(args.toArray(new String[] {})) + " Flags : "
				+ Arrays.toString(booleanFlags.toArray(new Character[] {}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		return args.iterator();
	}
}
