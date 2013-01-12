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
package be.Balor.Tools.Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Antoine
 * 
 */
public class AdminCmdVersion {
	private final static Pattern admincmdVersion = Pattern
			.compile("([\\d]*)\\.([\\d]*)\\.([\\d]*)(-SNAPSHOT | )\\(BUILD (.*)\\)");
	private int major, minor, build;
	private boolean dev = false;
	private Date buildDate;
	private final String version;

	/**
	 * 
	 */
	public AdminCmdVersion(final String version) {
		this.version = version;
		try {

			final Matcher regexMatcher = admincmdVersion.matcher(version);
			if (regexMatcher.find()) {
				major = Integer.parseInt(regexMatcher.group(1));
				minor = Integer.parseInt(regexMatcher.group(2));
				build = Integer.parseInt(regexMatcher.group(3));
				if (regexMatcher.group(4).equalsIgnoreCase("-SNAPSHOT ")) {
					dev = true;
				}
				buildDate = new SimpleDateFormat("dd.MM.yyyy '@' HH:mm:ss")
						.parse(regexMatcher.group(5));
			}

		} catch (final PatternSyntaxException ex) {
			ex.printStackTrace();
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check if this version is newer than an other one
	 * 
	 * @param version
	 * @return true if newer.
	 */
	public boolean isNewerThan(final AdminCmdVersion version) {
		if (this.major > version.major) {
			return true;
		}
		if (this.minor > version.minor) {
			return true;
		}
		if (this.build > version.build) {
			return true;
		}
		if (this.buildDate.after(version.buildDate)) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + build;
		result = prime * result
				+ ((buildDate == null) ? 0 : buildDate.hashCode());
		result = prime * result + (dev ? 1231 : 1237);
		result = prime * result + major;
		result = prime * result + minor;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AdminCmdVersion other = (AdminCmdVersion) obj;
		if (build != other.build) {
			return false;
		}
		if (buildDate == null) {
			if (other.buildDate != null) {
				return false;
			}
		} else if (!buildDate.equals(other.buildDate)) {
			return false;
		}
		if (dev != other.dev) {
			return false;
		}
		if (major != other.major) {
			return false;
		}
		if (minor != other.minor) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("AdminCmdVersion [major=%s, minor=%s, build=%s, dev=%s, buildDate=%s]",
						major, minor, build, dev, buildDate);
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

}
