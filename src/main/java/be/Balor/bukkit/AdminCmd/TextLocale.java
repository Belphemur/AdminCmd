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
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type.Whois;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import belgium.Balor.Workers.InvisibleWorker;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum TextLocale {
	NEWS("NEWS"), RULES("Rules"), MOTD("MOTD"), MOTD_NEW(
			"MOTDNewUser",
			"motdNewUser");
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
		final String parsedContent = parseContent(content);
		LocaleManager.getInstance().addLocale(locale, parsedContent, true);
		FileManager.getInstance().setTxtFile(file, parsedContent);
		version.set(file, System.currentTimeMillis());
		try {
			version.save();
		} catch (final IOException e) {
		}
	}

	/**
	 * @param content
	 */
	private String parseContent(final String content) {
		return Utils.colorParser(content.replaceAll("\\n", "\n").replaceAll(
				"//n", "\n"));
	}

	/**
	 * Reload the text file
	 */
	public void reloadContent() {
		final String result = parseContent(FileManager.getInstance()
				.getTextFile(file + ".txt"));
		LocaleManager.getInstance().addLocale(locale, result, true);
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
	public long getModifTime() {
		return version.getLong(file, 0);
	}

	/**
	 * Send the formated text of the locale to the player.
	 * 
	 * @return .
	 */
	public void sendText(final Player p) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		final ACPlayer acPlayer = ACPlayer.getPlayer(p);
		final long total = acPlayer.getCurrentPlayedTime();
		replace.putAll(Utils.playedTime(Utils.getPlayerName(p), total));
		replace.put(
				"nb",
				String.valueOf(p.getServer().getOnlinePlayers().length
						- InvisibleWorker.getInstance().nbInvisibles()));
		final Collection<String> list = Utils.getPlayerList(p);
		String connected = Joiner.on(", ").join(list);
		if (connected.length() >= ACMinecraftFontWidthCalculator.chatwidth) {
			final String tmp = connected.substring(0,
					ACMinecraftFontWidthCalculator.chatwidth);
			final String tmp2 = connected.substring(
					ACMinecraftFontWidthCalculator.chatwidth,
					connected.length());
			connected = tmp + "//n" + tmp2;
		}
		replace.put("connected", connected);
		final String serverTime = Utils.replaceDateAndTimeFormat(Utils
				.getServerRealTime("GMT" + ConfigEnum.DT_GMT.getString()));
		replace.put("time", serverTime);
		final String date = Utils.replaceDateAndTimeFormat(acPlayer,
				Whois.LOGIN);
		if (date == null) {
			replace.put("lastlogin", Utils.I18n("noLoginInformation"));
		} else {
			replace.put("lastlogin", date);
		}
		final String messageToSend = parseContent(Utils.I18n(locale, replace));
		if (messageToSend != null) {
			for (final String toSend : messageToSend.split("\n")) {
				if (toSend.isEmpty()) {
					continue;
				}
				p.sendMessage(toSend);
			}
		}
	}
}
