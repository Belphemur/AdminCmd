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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum ConfigEnum {

	MOTD("MessageOfTheDay", true, "if true, display MOTD on join"),
	NEWS("DisplayNewsOnJoin", true, "If true, display the news message on Join"),
	RULES("DisplayRulesOnJoin", true, "If true, display the rules on Join"),
	FJ_RULES("DisplayRulesOnlyOnFirstJoin", false, "If true, display the rules ONLY ON FIRST JOIN\n"
			+ "(override the above parameter DisplayRulesOnJoin)"),
	DVULCAN("DefaultVulcanPower", 4.0F, "Default value for Vulcan power"),
	DFB("DefaultFireBallPower", 1.0F, "Default value for Fireball power"),
	DFLY("DefaultFlyPower", 1.75F, "Default value for Fly power"),
	SUPERPERM("forceOfficialBukkitPerm", false, "Only useful when using bridge with SuperPerm,\n"
			+ "to avoid the use of the bridge."),
	LOCALE("locale", "en_US", "Language of messages displayed in the plugin."),
	NO_MSG("noMessage", false, "Disable every message of the plugin"),
	AUTO_AFK("autoAfk", true, "Activate the autoAfk, meaning the player\n"
			+ "will be set AFK after the given time"),
	AFK_TIMEOUT("afkTimeInSecond", 60, "Time before setting the player AFK"),
	AUTO_KICK("autoKickAfkPlayer", false, "Activate the auto kick AFK people after the given time"),
	KICK_TIMEOUT("afkKickInMinutes", 3, "Time before kicking the AFK player"),
	STATUS("statutCheckInSec", 20, "After how many seconds the plugin will check\n"
			+ "if the player didn't move for the AFK\n"
			+ "Also used to send update about the Invisible status."),
	MHOME("maxHomeByUser", 0, "How many homes a user can have. 0 = Infinite, max = 150"),
	MITEM("maxItemAmount", 0, "How many Items a user can spawn at once. 0 = Infinite, max = 150"),
	FCSPAWN("firstConnectionToSpawnPoint", false, "If true and if it's the first time that the player connect,\n"
			+ "he will be spawn at the spawn point you set"),
	RESET_POWERS("resetPowerWhenTpAnotherWorld", true, "Fly,God,vulcan, etc ... are power.\n"
			+ "If true, these power are lost when tp to another world."),
	RINVISIBLE("invisibleRangeInBlock", 312, "Range check for update the invisible status."),
	FQINVISIBLE("fakeQuitWhenInvisible", true, "Fake quit when you become invisible"),
	NPINVISIBLE("InvisAndNoPickup", true, "Setting this to true, will disable the auto-picking item when invisible"),
	GLIDE("glideWhenFallingInFlyMode", true, "Activate the parachute when falling in fly mode"),
	G_NEWYVEL("gliding.newYvelocity", -0.5D, "New velocity to slower the fall"),
	G_VELCHECK("gliding.YvelocityCheckToGlide", -0.2D, "Velocity needed to open the parachute"),
	G_MULT("gliding.multiplicator", 0.1D, "Gliding multiplicator"),
	COLSIGN("ColoredSign", true, "Activate the color sign, using & to select the color."),
	MUTEDPM("mutedPlayerCantPm", false, "Disallow muted player to send private message"),
	RTPSEE("maxRangeForTpAtSee", 312, "Max Range in block for the tp at see."),
	TPREQUEST("tpRequestActivatedByDefault", false, "Mean, when you want to tp to a player or tp the player,\n"
			+ "he'll receive a request that he can ignore or accept.\n"
			+ "By setting this option to true,\n" + "the tp request will be activated by default."),
	TPR_TIMEOUT("tpRequestTimeOutInMinutes", 1, "How much minute before a tp request become invalid."),
	VERBOSE("verboseLog", false, "Disable some \"debug\" message when launching the plugin\n"
			+ "(change it to true only if you have a good reason to do it.)"),
	LOG_PM("logPrivateMessages", true, "Private message send with command /msg are logged in the server.log"),
	BSRELOAD("broadcastServerReload", true, "Broadcast a message to every player when reloading the server."),
	H_ENTRY("help.entryPerPage", 9, "Number of help entry per page"),
	H_SHORTE("help.shortenEntries", false, "Shorten the help text"),
	H_WRAP("help.useWordWrap", false, "Check the word to do the new line"),
	H_RWRAP("help.wordWrapRight", false, "Wrap by the right"),
	H_ALLPLUGIN("help.getHelpForAllPlugins", true, "If set to true, get the commands of every plugins installed\n"
			+ "Else only from the folder HelpFiles where you set your help files."),
	SB_ITEM("superBreakerItem", 278, "ID of the item used for the SuperBreaker mode"),
	DT_FORMAT("DateAndTime.Format", "dd/MM/yy - HH:mm:ss", "Date/Time format"),
	DT_GMT("DateAndTime.GMToffset", "GMT+00:00", "TimeZone to be displayed, only Use GMT or UTC here"),
	IMMUNITY("useImmunityLvl", false, "if you want to use the immunity level explained in the Read Me."),
	DIMMUNITY("defaultImmunityLvl", 1, "Level assigned by default to an user."),
	DNAME("useDisplayName", true, "Use the DisplayName of the player when using the player name"),
	DEBUG("debug", false, "To activate the debug log (debug.log file)"),
	GSPAWN("globalRespawnSetting", "globalSpawn", "To change the global Respawn point\n"
			+ "Can be : globalSpawn, bed, home, none"),
	CHECKTP("checkTeleportLocation", false, "When using a Spawn/Home command, the plugin will check\n"
			+ "if the player didn't moved"),
	TP_DELAY("teleportDelay", 0, "Delay before teleporting (Spawn/Home) in ticks (20 Ticks = 1 Sec)"),
	LOG_CMD("logAllCmd", false, "To log all command in the console and server.log"),
	JQMSG("useJoinQuitMsg", true, "To activate the change of the Join/Quit message\n"
			+ "(that can be configured in the locale file)"),
	WDELAY("delayBeforeWriteUserFileInSec", 120, "Delay before writing the user file on the disk (in seconds)"),

	GNAMES("groupNames", Collections.list(new Enumeration<String>() {
		private int count = 0;
		private final String[] val = new String[] { "default", "mod", "admin" };

		@Override
		public boolean hasMoreElements() {
			return count < val.length;
		}

		@Override
		public String nextElement() {
			return val[count++];
		}
	}), "Will be used in a later version"),
	SCALE_TIMEOUT("timeOutScale", 60, "When using the parameter -t in power commands (god, fly, etc ...)\n"
			+ "Configure the scale used for the time (here 60 sec).\n"
			+ "It mean if you type /god -t 5 you will be god for 5 minutes"),
	TIME_STOP("timeInSecBeforeStop", 0, "Time before stopping the server when using the AdminCmd command Stop."),
	TP_DIFF_WORLD("tpInDiffWorld", true, "Allow users to teleport to other user that are in a different world.\n"
			+ "If FALSE the player that want to tp to another player that is in an another world will have to have the permission node :\n"
			+ "admincmd.tp.world.WORLDNAME (where WORLDNAME is the name of the world where the space are replaced by underscore (_) )"),
	ARMOR_KIT_OVERRIDE("armorKitOverride", true, "When using an armored kit, if this options is set to true,"
			+ " it will override what the player is wearing to replace it by the armor of the kit."),
	USE_PREFIX("usePSfix", true, "Use the prefix and the suffix when displaying names"),
	DEGG_KILL_RADIUS("egg.killer.defaultRadiusInBlock", 15, "When the Killer Egg is used, this radius will be used if no radius is set in the command."),
	MAXEGG_KILL_RADIUS("egg.killer.maxRadiusInBlock", 30, "When the Killer Egg is used, this radius will be the max radius possible for the command."),
	EGG_BLOCK_TIMEOUT("egg.block.Timeout", 5, "How much time the new blocks stay (in sec) when using the BlockEgg. Put 0 for never change back."),
	DEGG_BLOCK_RADIUS("egg.block.defaultRadiusInBlock", 8, "Default freeze radius when using the Block"),
	MAXEGG_BLOCK_RADIUS("egg.block.maxRadiusInBlock", 12, "Maximum radius for the BlockEgg"),
	EGG_FREEZE_TIMEOUT("egg.freezer.Timeout", 5, "How much time the ice stay (in sec) when using the FreezerEgg. Put 0 for never change back."),
	DEGG_FREEZE_RADIUS("egg.freezer.defaultRadiusInBlock", 8, "Default freeze radius when using the FreezerEgg"),
	MAXEGG_FREEZE_RADIUS("egg.freezer.maxRadiusInBlock", 12, "Maximum radius for the FreezergEgg"),
	DEGG_EX_RADIUS("egg.explosion.defaultRadiusInBlock", 8, "Default freeze radius when using the ExplosionEgg"),
	MAXEGG_EX_RADIUS("egg.explosion.maxRadiusInBlock", 12, "Maximum radius for the ExplosionEgg"),
	EGG_HIGH_TIMEOUT("egg.high.Timeout", 20, "How much time (in sec) the high effect least."),
	DEGG_HIGH_RADIUS("egg.high.defaultRadiusInBlock", 5, "Default radius when using the HighEgg"),
	MAXEGG_HIGH_RADIUS("egg.high.maxRadiusInBlock", 12, "Maximum radius for the HighEgg");
	public final static String PREFIX = "ac_";
	private final String confVal;
	private final Object defaultVal;
	private final String description;
	private static ConfigurationSection config;
	private static String pluginVersion;
	private static String pluginName;

	/**
	 * @param confVal
	 * @param defaultVal
	 * @param description
	 */
	private ConfigEnum(String confVal, Object defaultVal, String description) {
		this.confVal = confVal;
		this.defaultVal = defaultVal;
		this.description = description;
	}

	public String getString() {
		return config.getString(confVal);
	}

	public int getInt() {
		return config.getInt(confVal);
	}

	public double getDouble() {
		return config.getDouble(confVal);
	}

	public boolean getBoolean() {
		return config.getBoolean(confVal);
	}

	public long getLong() {
		return config.getLong(confVal);
	}

	public float getFloat() {
		return Float.parseFloat(config.getString(confVal));
	}

	public List<String> getStringList() {
		return config.getStringList(confVal);
	}

	/**
	 * @return the defaultvalues
	 */
	public static Map<String, Object> getDefaultvalues() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		for (ConfigEnum ce : values())
			values.put(ce.confVal, ce.defaultVal);
		return values;
	}

	public static String getHeader() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Configuration file of ").append(pluginName).append('\n');
		buffer.append("Plugin Version: ").append(pluginVersion).append('\n').append('\n');
		for (ConfigEnum ce : values())
			buffer.append(ce.confVal).append("\t:\t").append(ce.description).append(" (Default : ")
					.append(ce.defaultVal).append(')').append('\n');
		return buffer.toString();
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public static void setConfig(ConfigurationSection config) {
		ConfigEnum.config = config;
	}

	public static void setPluginInfos(PluginDescriptionFile pdf) {
		ConfigEnum.pluginVersion = pdf.getVersion();
		ConfigEnum.pluginName = pdf.getName();
	}
}
