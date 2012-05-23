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
package be.Balor.bukkit.AdminCmd;

import java.io.IOException;

import be.Balor.Manager.LocaleManager;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Files.FileManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum TextLocale {
	NEWS("NEWS"),
	RULES("Rules"),
	MOTD("MOTD"),
	MOTD_NEW("MOTDNewUser", "motdNewUser");
	private final String locale;
	private final String file;
	private static ExtendedConfiguration version;

	/**
 * 
 */
	private TextLocale(final String locale) {
		this.locale = locale;
		this.file = this.locale.toLowerCase();
	}

	private TextLocale(final String locale, final String file) {
		this.locale = locale;
		this.file = file;
	}

	/**
	 * Save the new content of the locale
	 * 
	 * @param content
	 *            new content
	 */
	public void saveContent(final String content) {
		LocaleManager.getInstance().addLocale(locale, content, true);
		FileManager.getInstance().setTxtFile(file, content);
		version.set(file, System.currentTimeMillis());
		try {
			version.save();
		} catch (final IOException e) {
		}
	}

	/**
	 * @param version
	 *            the version to set
	 */
	static void setVersion(final ExtendedConfiguration version) {
		TextLocale.version = version;
	}

	/**
	 * Get the last modification time of this locale
	 * 
	 * @return milliseconds
	 */
	public int getModifTime() {
		return version.getInt(file, 0);
	}
}
