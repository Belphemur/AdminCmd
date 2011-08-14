package be.Balor.bukkit.AdminCmd;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Listeners.ACBlockListener;
import be.Balor.Listeners.ACEntityListener;
import be.Balor.Listeners.ACPlayerListener;
import be.Balor.Listeners.ACPluginListener;
import be.Balor.Listeners.ACWeatherListener;
import be.Balor.Manager.CommandManager;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.Home.*;
import be.Balor.Manager.Commands.Items.*;
import be.Balor.Manager.Commands.Mob.*;
import be.Balor.Manager.Commands.Player.*;
import be.Balor.Manager.Commands.Server.*;
import be.Balor.Manager.Commands.Spawn.*;
import be.Balor.Manager.Commands.Time.*;
import be.Balor.Manager.Commands.Tp.*;
import be.Balor.Manager.Commands.Weather.*;
import be.Balor.Manager.Commands.Warp.*;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Manager.Terminal.TerminalCommandManager;
import be.Balor.Tools.Utils;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 * 
 * @authors Plague, Balor
 */
public class AdminCmd extends JavaPlugin {
	private static Server server = null;
	private ACHelper worker;

	public static Server getBukkitServer() {
		return server;
	}

	public static final Logger log = Logger.getLogger("Minecraft");

	private void registerPermParents() {
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.item.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.player.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.mob.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.server.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.spawn.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.time.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.tp.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.weather.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.warp.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.invisible.*"));
		PermissionManager.getInstance().addPermParent(new PermParent("admincmd.server.exec.*"));
		PermissionManager.getInstance().setMajorPerm(new PermParent("admincmd.*"));
		PermissionManager.getInstance().addPermChild("admincmd.player.bypass");
		PermissionManager.getInstance().addPermChild("admincmd.item.noblacklist");
		PermissionManager.getInstance().addPermChild("admincmd.player.noreset");
		PermissionManager.getInstance().addPermChild("admincmd.spec.notprequest");
	}

	public static void registerCmds() {

		CommandManager.getInstance().registerCommand(Day.class);
		CommandManager.getInstance().registerCommand(Repair.class);
		CommandManager.getInstance().registerCommand(RepairAll.class);
		CommandManager.getInstance().registerCommand(More.class);
		CommandManager.getInstance().registerCommand(PlayerList.class);
		CommandManager.getInstance().registerCommand(PlayerLocation.class);
		CommandManager.getInstance().registerCommand(God.class);
		CommandManager.getInstance().registerCommand(Thor.class);
		CommandManager.getInstance().registerCommand(Kill.class);
		CommandManager.getInstance().registerCommand(Heal.class);
		CommandManager.getInstance().registerCommand(ClearSky.class);
		CommandManager.getInstance().registerCommand(Storm.class);
		CommandManager.getInstance().registerCommand(SetSpawn.class);
		CommandManager.getInstance().registerCommand(Spawn.class);
		CommandManager.getInstance().registerCommand(Memory.class);
		CommandManager.getInstance().registerCommand(SetTime.class);
		CommandManager.getInstance().registerCommand(ClearInventory.class);
		CommandManager.getInstance().registerCommand(Give.class);
		CommandManager.getInstance().registerCommand(AddBlackList.class);
		CommandManager.getInstance().registerCommand(RemoveBlackList.class);
		CommandManager.getInstance().registerCommand(TpHere.class);
		CommandManager.getInstance().registerCommand(TpTo.class);
		CommandManager.getInstance().registerCommand(Coloring.class);
		CommandManager.getInstance().registerCommand(Strike.class);
		CommandManager.getInstance().registerCommand(RemoveAlias.class);
		CommandManager.getInstance().registerCommand(SpawnMob.class);
		CommandManager.getInstance().registerCommand(KickPlayer.class);
		CommandManager.getInstance().registerCommand(PrivateMessage.class);
		CommandManager.getInstance().registerCommand(AddAlias.class);
		CommandManager.getInstance().registerCommand(TpPlayerToPlayer.class);
		CommandManager.getInstance().registerCommand(TpLoc.class);
		CommandManager.getInstance().registerCommand(KickAllPlayers.class);
		CommandManager.getInstance().registerCommand(Vulcan.class);
		CommandManager.getInstance().registerCommand(Drop.class);
		CommandManager.getInstance().registerCommand(Invisible.class);
		CommandManager.getInstance().registerCommand(SpyMsg.class);
		CommandManager.getInstance().registerCommand(Fireball.class);
		CommandManager.getInstance().registerCommand(Home.class);
		CommandManager.getInstance().registerCommand(SetHome.class);
		CommandManager.getInstance().registerCommand(AddWarp.class);
		CommandManager.getInstance().registerCommand(RemoveWarp.class);
		CommandManager.getInstance().registerCommand(TpToWarp.class);
		CommandManager.getInstance().registerCommand(WarpList.class);
		CommandManager.getInstance().registerCommand(Ip.class);
		CommandManager.getInstance().registerCommand(BanPlayer.class);
		CommandManager.getInstance().registerCommand(UnBan.class);
		CommandManager.getInstance().registerCommand(KillMob.class);
		CommandManager.getInstance().registerCommand(Fly.class);
		CommandManager.getInstance().registerCommand(DeleteHome.class);
		CommandManager.getInstance().registerCommand(ListHomes.class);
		CommandManager.getInstance().registerCommand(Freeze.class);
		CommandManager.getInstance().registerCommand(Mute.class);
		CommandManager.getInstance().registerCommand(UnMute.class);
		CommandManager.getInstance().registerCommand(MobLimit.class);
		CommandManager.getInstance().registerCommand(NoPickup.class);
		CommandManager.getInstance().registerCommand(FreezeWeather.class);
		CommandManager.getInstance().registerCommand(MOTD.class);
		CommandManager.getInstance().registerCommand(Execution.class);
		CommandManager.getInstance().registerCommand(News.class);
		CommandManager.getInstance().registerCommand(Rain.class);
		CommandManager.getInstance().registerCommand(Roll.class);
		CommandManager.getInstance().registerCommand(Extinguish.class);
		CommandManager.getInstance().registerCommand(Reload.class);
		CommandManager.getInstance().registerCommand(ReplaceBlock.class);
		CommandManager.getInstance().registerCommand(Undo.class);
		CommandManager.getInstance().registerCommand(ReloadAll.class);
		CommandManager.getInstance().registerCommand(RepeatCmd.class);
		CommandManager.getInstance().registerCommand(Afk.class);
		CommandManager.getInstance().registerCommand(MoreAll.class);
		CommandManager.getInstance().registerCommand(TpToggle.class);
		CommandManager.getInstance().registerCommand(TpAtSee.class);
		CommandManager.getInstance().registerCommand(Uptime.class);
		CommandManager.getInstance().registerCommand(Kit.class);
	}

	private void setDefaultLocale() {
		Utils.addLocale("playerNotFound", ChatColor.RED + "No such player: " + ChatColor.WHITE
				+ "%player");
		Utils.addLocale("kitNotFound", ChatColor.RED + "No such kit: " + ChatColor.WHITE + "%kit");
		Utils.addLocale("pluginNotFound", ChatColor.RED + "No such Plugin: " + ChatColor.WHITE
				+ "%plugin");
		Utils.addLocale("worldNotFound", ChatColor.RED + "No such world: " + ChatColor.WHITE
				+ "%world");
		Utils.addLocale("unknownMat", ChatColor.RED + "Unknown Material : " + ChatColor.WHITE
				+ "%material");
		Utils.addLocale("onlinePlayers", ChatColor.RED + "Online players:");
		Utils.addLocale("serverReload", ChatColor.YELLOW + "Server Reloaded.");
		Utils.addLocale(
				"changedWorld",
				ChatColor.DARK_RED
						+ "All your powers have been deactivated because you teleported to an another world");
		Utils.addLocale("stillInv", ChatColor.RED + "You are still Invisible");
		Utils.addLocale("errorNotPerm", ChatColor.RED
				+ "You don't have the Permissions to do that " + ChatColor.BLUE + "(%p)");
		Utils.addLocale("dropItemOtherPlayer", ChatColor.RED + "[%sender]" + ChatColor.WHITE
				+ " dropped at your feet " + ChatColor.GOLD + "%amount %material");
		Utils.addLocale("dropItemCommandSender", ChatColor.RED + "Dropped " + ChatColor.GOLD
				+ "%amount %material to " + ChatColor.WHITE + "%target");
		Utils.addLocale("dropItemYourself", ChatColor.RED + "Dropped " + ChatColor.GOLD
				+ "%amount %material");
		Utils.addLocale("giveItemOtherPlayer", ChatColor.RED + "[%sender]" + ChatColor.WHITE
				+ " send you " + ChatColor.GOLD + "%amount %material");
		Utils.addLocale("giveItemCommandSender", ChatColor.RED + "Added " + ChatColor.GOLD
				+ "%amount %material to " + ChatColor.WHITE + "%target's inventory");
		Utils.addLocale("giveItemYourself", ChatColor.RED + "Added " + ChatColor.GOLD
				+ "%amount %material" + ChatColor.WHITE + " to your inventory");
		Utils.addLocale("errorHolding", ChatColor.RED + "You have to be holding something!");
		Utils.addLocale("moreTooMuch", "Excedent(s) item(s) (" + ChatColor.BLUE + "%amount"
				+ ChatColor.WHITE + ") have been stored in your inventory");
		Utils.addLocale("repair", "Your item " + ChatColor.RED + "%type" + ChatColor.WHITE
				+ " have been successfully repaired.");
		Utils.addLocale("errorRepair", "You can't repair this item : " + ChatColor.RED + "%type");
		Utils.addLocale("repairAll", "All %player's items have been repaired.");
		Utils.addLocale("repairAllTarget", "All your items have been repaired.");
		Utils.addLocale("errorMob", ChatColor.RED + "No such creature: " + ChatColor.WHITE + "%mob");
		Utils.addLocale("spawnMob", ChatColor.BLUE + "Spawned " + ChatColor.WHITE + "%nb %mob");
		Utils.addLocale("clear", ChatColor.RED + "Your inventory has been cleared");
		Utils.addLocale("clearTarget", ChatColor.RED + "Inventory of " + ChatColor.WHITE
				+ "%player" + ChatColor.RED + " cleared");
		Utils.addLocale("fireballDisabled", ChatColor.DARK_RED + "Fireball mode disabled.");
		Utils.addLocale("fireballDisabledTarget", ChatColor.DARK_RED
				+ "Fireball mode disabled for %player");
		Utils.addLocale("fireballEnabled", ChatColor.DARK_RED + "Fireball mode enabled.");
		Utils.addLocale("fireballEnabledTarget", ChatColor.DARK_RED
				+ "Fireball mode enabled for %player");
		Utils.addLocale("godDisabled", ChatColor.DARK_AQUA + "GOD mode disabled.");
		Utils.addLocale("godDisabledTarget", ChatColor.DARK_AQUA + "GOD mode disabled for %player");
		Utils.addLocale("godEnabled", ChatColor.DARK_AQUA + "GOD mode enabled.");
		Utils.addLocale("godEnabledTarget", ChatColor.DARK_AQUA + "GOD mode enabled for %player");
		Utils.addLocale("thorDisabled", ChatColor.DARK_AQUA + "THOR mode disabled.");
		Utils.addLocale("thorDisabledTarget", ChatColor.DARK_AQUA
				+ "THOR mode disabled for %player");
		Utils.addLocale("thorEnabled", ChatColor.DARK_AQUA + "THOR mode enabled.");
		Utils.addLocale("thorEnabledTarget", ChatColor.DARK_AQUA + "THOR mode enabled for %player");
		Utils.addLocale("vulcanDisabled", ChatColor.DARK_RED + "VULCAN mode disabled.");
		Utils.addLocale("vulcanDisabledTarget", ChatColor.DARK_RED
				+ "VULCAN mode disabled for %player");
		Utils.addLocale("vulcanEnabled", ChatColor.DARK_RED + "VULCAN mode enabled.");
		Utils.addLocale("vulcanEnabledTarget", ChatColor.DARK_RED
				+ "VULCAN mode enabled for %player");
		Utils.addLocale("spymsgDisabled", ChatColor.DARK_AQUA + "SPYMSG mode disabled.");
		Utils.addLocale("spymsgEnabled", ChatColor.DARK_AQUA + "SPYMSG mode enabled.");
		Utils.addLocale("invisibleEnabled", ChatColor.RED + "You are now Invisible");
		Utils.addLocale("invisibleEnabledTarget", ChatColor.DARK_AQUA
				+ "INVISIBLE mode enabled for %player");
		Utils.addLocale("invisibleDisabled", ChatColor.GREEN + "You are now Visible");
		Utils.addLocale("invisibleDisabledTarget", ChatColor.DARK_AQUA
				+ "INVISIBLE mode disabled for %player");
		Utils.addLocale("errorMultiHome", ChatColor.DARK_GREEN + "Home " + ChatColor.RED + "%home"
				+ ChatColor.WHITE + " not set.");
		Utils.addLocale("multiHome", ChatColor.DARK_GREEN + "Teleported" + ChatColor.WHITE
				+ " to your home " + ChatColor.DARK_AQUA + "%home.");
		Utils.addLocale("setMultiHome", ChatColor.DARK_GREEN + "Home " + ChatColor.DARK_AQUA
				+ "%home" + ChatColor.WHITE + " set.");
		Utils.addLocale("rmHome", ChatColor.RED + "Home " + ChatColor.DARK_AQUA + "%home"
				+ ChatColor.WHITE + " removed.");
		Utils.addLocale("homeLimit", ChatColor.RED + "You have reached your "
				+ ChatColor.DARK_GREEN + "home limit");
		Utils.addLocale("errorLocation", ChatColor.RED + "Location has to be formed by numbers");
		Utils.addLocale("addWarp", ChatColor.GREEN + "WarpPoint %name" + ChatColor.WHITE
				+ " added.");
		Utils.addLocale("rmWarp", ChatColor.RED + "WarpPoint %name" + ChatColor.WHITE + " removed.");
		Utils.addLocale("errorWarp", ChatColor.DARK_RED + "WarpPoint %name not found");
		Utils.addLocale("tpWarp", ChatColor.GREEN + "Teleported to " + ChatColor.WHITE + "%name");
		Utils.addLocale("strike", "%player was striked by Thor");
		Utils.addLocale("tp", "Successfully teleported " + ChatColor.BLUE + "%fromPlayer"
				+ ChatColor.WHITE + " to " + ChatColor.GREEN + "%toPlayer");
		Utils.addLocale("addBlacklist", ChatColor.GREEN + "Item (" + ChatColor.WHITE + "%material"
				+ ChatColor.GREEN + ") added to the Black List.");
		Utils.addLocale("rmBlacklist", ChatColor.GREEN + "Item (" + ChatColor.WHITE + "%material"
				+ ChatColor.GREEN + ") removed from the Black List.");
		Utils.addLocale("inBlacklist", ChatColor.DARK_RED + "This item (" + ChatColor.WHITE
				+ "%material" + ChatColor.DARK_RED + ") is black listed.");
		Utils.addLocale("errorSpawn", ChatColor.DARK_GREEN + "spawn" + ChatColor.WHITE
				+ " not set for this world.");
		Utils.addLocale("spawn", ChatColor.DARK_GREEN + "Teleported" + ChatColor.WHITE
				+ " to your spawn.");
		Utils.addLocale("setSpawn", ChatColor.DARK_GREEN + "spawn" + ChatColor.WHITE + " set.");
		Utils.addLocale("sClear", "Sky cleared in world :");
		Utils.addLocale("sStorm", "Storm set for %duration mins in world : ");
		Utils.addLocale("sRain", "Rain set for %duration mins in world : ");
		Utils.addLocale("afk", "%player " + ChatColor.RED + "is AFK");
		Utils.addLocale("online", "%player " + ChatColor.GREEN + "is Online");
		Utils.addLocale("afkTitle", ChatColor.BLUE + "[AFK]" + ChatColor.WHITE);
		Utils.addLocale("ip", ChatColor.YELLOW + "IP adress of " + ChatColor.WHITE
				+ "%player - %ip");
		Utils.addLocale("ban", ChatColor.YELLOW + "%player has been banned.");
		Utils.addLocale("unban", ChatColor.YELLOW + "%player is now unbanned.");
		Utils.addLocale("killMob", ChatColor.RED + "Killing mobs (" + ChatColor.WHITE + "%type"
				+ ChatColor.RED + ") of worlds : " + ChatColor.DARK_PURPLE + "%worlds");
		Utils.addLocale("killedMobs", "%nbKilled" + ChatColor.DARK_RED + " mobs have been killed.");
		Utils.addLocale("flyDisabled", ChatColor.GOLD + "FLY mode disabled.");
		Utils.addLocale("flyDisabledTarget", ChatColor.GOLD + "FLY mode disabled for %player");
		Utils.addLocale("flyEnabled", ChatColor.GOLD + "FLY mode enabled.");
		Utils.addLocale("flyEnabledTarget", ChatColor.GOLD + "FLY mode enabled for %player");
		Utils.addLocale("npDisabled", ChatColor.GOLD + "No Pickup mode disabled.");
		Utils.addLocale("npDisabledTarget", ChatColor.GOLD + "No Pickup mode disabled for %player");
		Utils.addLocale("npEnabled", ChatColor.GOLD + "No Pickup mode enabled.");
		Utils.addLocale("npEnabledTarget", ChatColor.GOLD + "No Pickup mode enabled for %player");
		Utils.addLocale("afkKick", "You have been kick because you were AFK");
		Utils.addLocale("freezeDisabled", ChatColor.DARK_GREEN + "You can now move again.");
		Utils.addLocale("freezeDisabledTarget", ChatColor.DARK_GREEN
				+ "Freeze mode disabled for %player");
		Utils.addLocale("freezeEnabled", ChatColor.DARK_RED
				+ "You can't move until you are defrozen.");
		Utils.addLocale("freezeEnabledTarget", ChatColor.DARK_RED
				+ "Freeze mode enabled for %player");
		Utils.addLocale("muteDisabled", ChatColor.DARK_GREEN + "You can chat again.");
		Utils.addLocale("muteDisabledTarget", ChatColor.DARK_GREEN + "%player is unmuted.");
		Utils.addLocale("muteEnabled", ChatColor.DARK_RED + "You can't chat anymore.");
		Utils.addLocale("tmpMuteEnabled", ChatColor.DARK_RED + "You can't chat anymore for %minutes minutes.");
		Utils.addLocale("muteEnabledTarget", ChatColor.DARK_RED + "%player is muted.");
		Utils.addLocale("alreadyMuted", ChatColor.DARK_AQUA
				+ "This player is already muted. To unmute him it's the unmute command.");
		Utils.addLocale("notMuted", ChatColor.DARK_AQUA + "This player is not muted.");
		Utils.addLocale("NaN", "%number " + ChatColor.DARK_RED + "is not a number.");
		Utils.addLocale("mobLimit", ChatColor.GOLD + "Mob limit (%number) set for world : %world");
		LocaleManager.getInstance().save();
		Utils.addLocale("mobLimitRemoved", ChatColor.GREEN
				+ "Mob limit is removed for world : %world");
		Utils.addLocale("wFrozen", "Weather is frozen in world :");
		Utils.addLocale("wUnFrozen", "Weather can change in world :");
		Utils.addLocale("invTitle", "[INV]");
		Utils.addLocale("MOTD", ChatColor.GOLD + "Welcome " + ChatColor.WHITE + "%player"
				+ ChatColor.GOLD + ", there is currently " + ChatColor.DARK_RED
				+ "%nb players connected /n" + ChatColor.GOLD + "%connected");
		Utils.addLocale("MOTDset", ChatColor.YELLOW + "The new Message Of The Day is : %motd");
		Utils.addLocale("NEWSset", ChatColor.YELLOW + "The News is : %news");
		Utils.addLocale("NEWS", ChatColor.DARK_GREEN + "News : AdminCmd Plugin has been installed");
		Utils.addLocale("roll", ChatColor.DARK_GREEN + "[%player] " + ChatColor.WHITE + "rolled a "
				+ ChatColor.GOLD + "%face dice : " + ChatColor.YELLOW + "%result");
		Utils.addLocale("extinguish", ChatColor.AQUA + "%nb blocks" + ChatColor.DARK_AQUA
				+ " have been extinguished.");
		Utils.addLocale("pluginReloaded", ChatColor.YELLOW + "This plugin has been reloaded : "
				+ ChatColor.WHITE + "%plugin");
		Utils.addLocale("replaced", ChatColor.RED + "%nb blocks of " + ChatColor.DARK_PURPLE
				+ "%mat" + ChatColor.DARK_AQUA + " are now AIR.");
		Utils.addLocale("undo", ChatColor.GREEN + "%nb blocks " + ChatColor.DARK_GREEN
				+ "have been replaced");
		Utils.addLocale("nothingToUndo", ChatColor.DARK_PURPLE + "Nothing to undo.");
		Utils.addLocale("noRepeat", ChatColor.DARK_RED + "No command to repeat.");
		Utils.addLocale("reExec", ChatColor.YELLOW + "Repeating the last command.");
		Utils.addLocale("timeSet", ChatColor.GOLD + "Time set to %type in world : "
				+ ChatColor.WHITE + "%world");
		Utils.addLocale("timePaused", ChatColor.DARK_RED + "Time is paused in " + ChatColor.WHITE
				+ "%world. " + ChatColor.DARK_GREEN + "To unpause : /time unpause .");
		Utils.addLocale("moreAll", ChatColor.AQUA
				+ "All your items are now at their max stack size.");
		Utils.addLocale("tpRequestTo", ChatColor.BLUE + "%player " + ChatColor.GOLD
				+ "want to tp to you. " + ChatColor.GREEN + "/tpt yes " + ChatColor.DARK_GREEN
				+ "to accept.");
		Utils.addLocale("tpRequestSend", ChatColor.DARK_PURPLE + "You send a Tp request to "
				+ ChatColor.WHITE + "%player" + ChatColor.DARK_PURPLE + " for a " + ChatColor.AQUA
				+ "%tp_type");
		Utils.addLocale("tpRequestFrom", ChatColor.BLUE + "%player " + ChatColor.DARK_AQUA
				+ "want to tp you at his/her location. " + ChatColor.GREEN + "/tpt yes "
				+ ChatColor.DARK_GREEN + "to accept.");
		Utils.addLocale("tpRequestOff", ChatColor.DARK_GREEN + "Tp Request system Disabled.");
		Utils.addLocale("tpRequestOn", ChatColor.DARK_RED + "Tp Request system Enabled.");
		Utils.addLocale("tpSeeEnabled", ChatColor.DARK_GREEN + "You Tp at see when you left click.");
		Utils.addLocale("tpSeeDisabled", ChatColor.DARK_RED + "TP AT SEE mode disabled.");
		Utils.addLocale("elapsedTime", "Uptime : " + ChatColor.YELLOW + "%d day(s) %h:%m:%s");
		Utils.addLocale("kitList", ChatColor.GOLD + "Available Kits : " + ChatColor.AQUA + "%list");
		Utils.addLocale("kitOtherPlayer", ChatColor.RED + "[%sender]" + ChatColor.WHITE
				+ " send you the kit : " + ChatColor.GOLD + "%kit");
		Utils.addLocale("kitCommandSender", ChatColor.RED + "Added " + ChatColor.GOLD + "%kit to "
				+ ChatColor.WHITE + "%target's inventory");
		Utils.addLocale("kitYourself", ChatColor.RED + "Added " + ChatColor.GOLD + "%kit"
				+ ChatColor.WHITE + " to your inventory");
		Utils.addLocale("tpRequestTimeOut", ChatColor.RED
				+ "This tp request has timed out and will not be executed.");
		Utils.addLocale("noTpRequest", ChatColor.GREEN
				+ "There is no tp request to execute");
		LocaleManager.getInstance().save();
	}

	public void onEnable() {
		server = getServer();
		PluginManager pm = getServer().getPluginManager();
		ACPluginListener pL = new ACPluginListener();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Enabled. (version "
				+ pdfFile.getVersion() + ")");
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pL, Priority.Monitor, this);

		worker = ACHelper.getInstance();
		registerPermParents();
		worker.setPluginInstance(this);
		setDefaultLocale();
		TerminalCommandManager.getInstance();
		PermissionManager.getInstance().registerAllPermParent();
		worker.loadInfos();
		ACPlayerListener playerListener = new ACPlayerListener();
		ACEntityListener entityListener = new ACEntityListener();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest,
				this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.High, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.High, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, new ACBlockListener(), Priority.Normal, this);
		pm.registerEvent(Event.Type.WEATHER_CHANGE, new ACWeatherListener(), Priority.Normal, this);
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		CommandManager.getInstance().stopAllExecutorThreads();
		worker = null;
		getServer().getScheduler().cancelTasks(this);
		ACHelper.killInstance();
		InvisibleWorker.killInstance();
		AFKWorker.killInstance();
		CommandManager.killInstance();
		System.gc();
		log.info("[" + pdfFile.getName() + "]" + " Plugin Disabled. (version "
				+ pdfFile.getVersion() + ")");
	}
}
