package be.Balor.bukkit.AdminCmd;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.mcstats.Metrics;

import be.Balor.Kit.ArmoredKitInstance;
import be.Balor.Kit.KitInstance;
import be.Balor.Listeners.ACBlockListener;
import be.Balor.Listeners.ACChatListener;
import be.Balor.Listeners.ACEntityListener;
import be.Balor.Listeners.ACPlayerListener;
import be.Balor.Listeners.ACPluginListener;
import be.Balor.Listeners.ACWeatherListener;
import be.Balor.Listeners.Commands.ACBanListener;
import be.Balor.Listeners.Commands.ACEggListener;
import be.Balor.Listeners.Commands.ACFireballListener;
import be.Balor.Listeners.Commands.ACFlyListener;
import be.Balor.Listeners.Commands.ACFoodListener;
import be.Balor.Listeners.Commands.ACFrozenPlayerListener;
import be.Balor.Listeners.Commands.ACGodListener;
import be.Balor.Listeners.Commands.ACLockedServerListener;
import be.Balor.Listeners.Commands.ACOpenInvListener;
import be.Balor.Listeners.Commands.ACSuperBreaker;
import be.Balor.Listeners.Commands.ACTeleportBackListener;
import be.Balor.Listeners.Commands.ACThorListener;
import be.Balor.Listeners.Commands.ACTimePausedListener;
import be.Balor.Listeners.Commands.ACTpAtSeeListener;
import be.Balor.Listeners.Commands.ACVulcanListener;
import be.Balor.Listeners.Features.ACColorSignListener;
import be.Balor.Listeners.Features.ACCreatureSpawnListener;
import be.Balor.Listeners.Features.ACDeathListener;
import be.Balor.Listeners.Features.ACFrozenTimeWorldListener;
import be.Balor.Listeners.Features.ACInvisibleListener;
import be.Balor.Listeners.Features.ACIpCheckListener;
import be.Balor.Listeners.Features.ACLogCommandListener;
import be.Balor.Listeners.Features.ACNoDropListener;
import be.Balor.Listeners.Features.ACPowerOffListener;
import be.Balor.Listeners.Features.ACRemoveNoAccessPowers;
import be.Balor.Listeners.Features.ACResetPowerListener;
import be.Balor.Listeners.Features.ACRespawnWorldFeature;
import be.Balor.Listeners.Features.ACSignEditListener;
import be.Balor.Listeners.Features.ACSuperBlacklistListener;
import be.Balor.Listeners.Features.ACUnknownCommandListener;
import be.Balor.Manager.CommandManager;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.Home.DeleteHome;
import be.Balor.Manager.Commands.Home.Home;
import be.Balor.Manager.Commands.Home.ListHomes;
import be.Balor.Manager.Commands.Home.SetHome;
import be.Balor.Manager.Commands.Items.AddAlias;
import be.Balor.Manager.Commands.Items.AddBlackList;
import be.Balor.Manager.Commands.Items.Coloring;
import be.Balor.Manager.Commands.Items.Drop;
import be.Balor.Manager.Commands.Items.DynKit;
import be.Balor.Manager.Commands.Items.Enchant;
import be.Balor.Manager.Commands.Items.GetItemId;
import be.Balor.Manager.Commands.Items.Give;
import be.Balor.Manager.Commands.Items.Kit;
import be.Balor.Manager.Commands.Items.More;
import be.Balor.Manager.Commands.Items.MoreAll;
import be.Balor.Manager.Commands.Items.RemoveAlias;
import be.Balor.Manager.Commands.Items.RemoveBlackList;
import be.Balor.Manager.Commands.Items.Repair;
import be.Balor.Manager.Commands.Items.RepairAll;
import be.Balor.Manager.Commands.Mob.ChangeMobSpawner;
import be.Balor.Manager.Commands.Mob.EggSpawner;
import be.Balor.Manager.Commands.Mob.KillMob;
import be.Balor.Manager.Commands.Mob.MobLimit;
import be.Balor.Manager.Commands.Mob.SpawnMob;
import be.Balor.Manager.Commands.Player.Afk;
import be.Balor.Manager.Commands.Player.AsUser;
import be.Balor.Manager.Commands.Player.BanList;
import be.Balor.Manager.Commands.Player.BanPlayer;
import be.Balor.Manager.Commands.Player.ClearInventory;
import be.Balor.Manager.Commands.Player.Enderchest;
import be.Balor.Manager.Commands.Player.Eternal;
import be.Balor.Manager.Commands.Player.Experience;
import be.Balor.Manager.Commands.Player.FakeQuit;
import be.Balor.Manager.Commands.Player.Feed;
import be.Balor.Manager.Commands.Player.Fireball;
import be.Balor.Manager.Commands.Player.Fly;
import be.Balor.Manager.Commands.Player.Freeze;
import be.Balor.Manager.Commands.Player.GameModeSwitch;
import be.Balor.Manager.Commands.Player.God;
import be.Balor.Manager.Commands.Player.Head;
import be.Balor.Manager.Commands.Player.Heal;
import be.Balor.Manager.Commands.Player.Invisible;
import be.Balor.Manager.Commands.Player.Ip;
import be.Balor.Manager.Commands.Player.KickAllPlayers;
import be.Balor.Manager.Commands.Player.KickPlayer;
import be.Balor.Manager.Commands.Player.Kill;
import be.Balor.Manager.Commands.Player.Mute;
import be.Balor.Manager.Commands.Player.MuteList;
import be.Balor.Manager.Commands.Player.NoDrop;
import be.Balor.Manager.Commands.Player.NoPickup;
import be.Balor.Manager.Commands.Player.OpenInventory;
import be.Balor.Manager.Commands.Player.Played;
import be.Balor.Manager.Commands.Player.PlayerList;
import be.Balor.Manager.Commands.Player.PlayerLocation;
import be.Balor.Manager.Commands.Player.Potion;
import be.Balor.Manager.Commands.Player.Presentation;
import be.Balor.Manager.Commands.Player.PrivateMessage;
import be.Balor.Manager.Commands.Player.Quit;
import be.Balor.Manager.Commands.Player.RemoveStatusEffects;
import be.Balor.Manager.Commands.Player.Reply;
import be.Balor.Manager.Commands.Player.Roll;
import be.Balor.Manager.Commands.Player.Search;
import be.Balor.Manager.Commands.Player.SpyMsg;
import be.Balor.Manager.Commands.Player.SuperBreaker;
import be.Balor.Manager.Commands.Player.UnBan;
import be.Balor.Manager.Commands.Player.UnMute;
import be.Balor.Manager.Commands.Player.UnMuteAll;
import be.Balor.Manager.Commands.Player.Vulcan;
import be.Balor.Manager.Commands.Player.WalkSpeed;
import be.Balor.Manager.Commands.Player.Whois;
import be.Balor.Manager.Commands.Player.Withdraw;
import be.Balor.Manager.Commands.Player.Workbench;
import be.Balor.Manager.Commands.Server.Broadcast;
import be.Balor.Manager.Commands.Server.Execution;
import be.Balor.Manager.Commands.Server.Extinguish;
import be.Balor.Manager.Commands.Server.Help;
import be.Balor.Manager.Commands.Server.ListValues;
import be.Balor.Manager.Commands.Server.LockServer;
import be.Balor.Manager.Commands.Server.MOTD;
import be.Balor.Manager.Commands.Server.Memory;
import be.Balor.Manager.Commands.Server.News;
import be.Balor.Manager.Commands.Server.PluginsList;
import be.Balor.Manager.Commands.Server.Reload;
import be.Balor.Manager.Commands.Server.ReloadAll;
import be.Balor.Manager.Commands.Server.ReloadTxt;
import be.Balor.Manager.Commands.Server.RemoveSuperPowers;
import be.Balor.Manager.Commands.Server.RepeatCmd;
import be.Balor.Manager.Commands.Server.ReplaceBlock;
import be.Balor.Manager.Commands.Server.Rules;
import be.Balor.Manager.Commands.Server.Set;
import be.Balor.Manager.Commands.Server.StopServer;
import be.Balor.Manager.Commands.Server.Undo;
import be.Balor.Manager.Commands.Server.Uptime;
import be.Balor.Manager.Commands.Server.Version;
import be.Balor.Manager.Commands.Server.WorldDifficulty;
import be.Balor.Manager.Commands.Spawn.GroupSpawn;
import be.Balor.Manager.Commands.Spawn.SetGroupSpawn;
import be.Balor.Manager.Commands.Spawn.SetSpawn;
import be.Balor.Manager.Commands.Spawn.Spawn;
import be.Balor.Manager.Commands.Time.Day;
import be.Balor.Manager.Commands.Time.PlayerTime;
import be.Balor.Manager.Commands.Time.SetTime;
import be.Balor.Manager.Commands.Tp.*;
import be.Balor.Manager.Commands.Warp.AddWarp;
import be.Balor.Manager.Commands.Warp.RemoveWarp;
import be.Balor.Manager.Commands.Warp.TpToWarp;
import be.Balor.Manager.Commands.Weather.ClearSky;
import be.Balor.Manager.Commands.Weather.FreezeWeather;
import be.Balor.Manager.Commands.Weather.Rain;
import be.Balor.Manager.Commands.Weather.Storm;
import be.Balor.Manager.Commands.Weather.Strike;
import be.Balor.Manager.Commands.Weather.Thor;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Terminal.TerminalCommandManager;
import be.Balor.OpenInv.InventoryManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Egg.EggTypeClassLoader;
import be.Balor.Tools.Help.HelpLister;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 * 
 * @authors Plague, Balor, Lathanael
 */
public final class AdminCmd extends AbstractAdminCmdPlugin {
	private ACHelper worker;
	private Metrics metrics;

	/**
	 * @return the metrics
	 */
	public Metrics getMetrics() {
		return metrics;
	}

	@Override
	public void onDisable() {
		final PluginDescriptionFile pdfFile = this.getDescription();
		getServer().getScheduler().cancelTasks(this);
		for (final ACPlayer p : PlayerManager.getInstance()
				.getOnlineACPlayers()) {
			PlayerManager.getInstance().setOffline(p);
		}
		ACPluginManager.getInstance().stopChildrenPlugins();
		CommandManager.getInstance().stopAllExecutorThreads();
		worker = null;
		ACHelper.killInstance();
		InvisibleWorker.killInstance();
		AFKWorker.killInstance();
		CommandManager.killInstance();
		HelpLister.killInstance();
		DebugLog.stopLogging();
		System.gc();

		ACLogger.info("Plugin Disabled. (version " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onEnable() {

		registerConfigurationClass();

		DebugLog.setFile(getDataFolder().getPath());

		setMetrics();

		final PluginDescriptionFile pdfFile = this.getDescription();
		DebugLog.INSTANCE.info("Plugin Version : " + pdfFile.getVersion());
		final PluginManager pm = getServer().getPluginManager();
		ACLogger.info("Plugin Enabled. (version " + pdfFile.getVersion() + ")");
		pm.registerEvents(new ACPluginListener(), this);
		worker = ACHelper.getInstance();
		worker.setCoreInstance(this);

		worker.loadInfos();
		super.onEnable();
		TerminalCommandManager.getInstance().setPerm(this);
		pm.registerEvents(new ACBlockListener(), this);
		pm.registerEvents(new ACEntityListener(), this);
		pm.registerEvents(new ACPlayerListener(), this);
		pm.registerEvents(new ACWeatherListener(), this);
		checkModulableFeatures(pm);
		System.gc();
	}

	/**
	 * 
	 */
	private void registerConfigurationClass() {
		ExtendedConfiguration.setClassLoader(this.getClassLoader());
		ConfigurationSerialization.registerClass(KitInstance.class);
		ConfigurationSerialization.registerClass(ArmoredKitInstance.class);
		ConfigurationSerialization.registerClass(MaterialContainer.class);
	}

	/**
	 * 
	 */
	private void setMetrics() {
		try {
			metrics = new Metrics(this);
		} catch (final IOException e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Stats problem", e);
		}
		ACPluginManager.setMetrics(metrics);

		metrics.addCustomData(new Metrics.Plotter() {
			@Override
			public String getColumnName() {
				return "Total Banned Players";
			}

			@Override
			public int getValue() {
				return worker.countBannedPlayers();
			}
		});
		metrics.addCustomData(new Metrics.Plotter() {
			@Override
			public String getColumnName() {
				return "Total Kits";
			}

			@Override
			public int getValue() {
				return worker.getNbKit();
			}
		});
		metrics.addCustomData(new Metrics.Plotter() {
			@Override
			public String getColumnName() {
				return "Total Blacklisted Items";
			}

			@Override
			public int getValue() {
				return worker.countBlackListedItems();
			}
		});
		metrics.addCustomData(new Metrics.Plotter() {
			@Override
			public String getColumnName() {
				return "Total Invisible Players";
			}

			@Override
			public int getValue() {
				return InvisibleWorker.getInstance().nbInvisibles();
			}
		});
		metrics.addCustomData(new Metrics.Plotter() {
			@Override
			public String getColumnName() {
				return "Total Afk Players";
			}

			@Override
			public int getValue() {
				return AFKWorker.getInstance().nbAfk();
			}
		});
		getServer().getScheduler().runTaskLaterAsynchronously(this,
				new Runnable() {

					@Override
					public void run() {
						metrics.start();
						DebugLog.INSTANCE.info("Stats started");
					}
				}, 30 * Utils.secInTick);
	}

	@Override
	public void registerCmds() {
		final PluginManager pm = getServer().getPluginManager();
		boolean banCommands = false;
		boolean lockCommand = false;
		final CommandManager cmdManager = CommandManager.getInstance();
		DebugLog.beginInfo("Register all Commands");
		cmdManager.registerCommand(Day.class);
		cmdManager.registerCommand(Repair.class);
		cmdManager.registerCommand(RepairAll.class);
		cmdManager.registerCommand(More.class);
		cmdManager.registerCommand(PlayerList.class);
		cmdManager.registerCommand(PlayerLocation.class);
		if (cmdManager.registerCommand(God.class)) {
			pm.registerEvents(new ACGodListener(), this);
		}
		if (cmdManager.registerCommand(Thor.class)) {
			pm.registerEvents(new ACThorListener(), this);
		}
		cmdManager.registerCommand(Kill.class);
		cmdManager.registerCommand(Heal.class);
		cmdManager.registerCommand(ClearSky.class);
		cmdManager.registerCommand(Storm.class);
		cmdManager.registerCommand(SetSpawn.class);
		cmdManager.registerCommand(Spawn.class);
		cmdManager.registerCommand(Memory.class);
		if (cmdManager.registerCommand(SetTime.class)) {
			pm.registerEvents(new ACTimePausedListener(), this);
		}
		cmdManager.registerCommand(ClearInventory.class);
		cmdManager.registerCommand(Give.class);
		cmdManager.registerCommand(AddBlackList.class);
		cmdManager.registerCommand(RemoveBlackList.class);
		cmdManager.registerCommand(TpHere.class);
		cmdManager.registerCommand(TpTo.class);
		cmdManager.registerCommand(Coloring.class);
		cmdManager.registerCommand(Strike.class);
		cmdManager.registerCommand(RemoveAlias.class);
		cmdManager.registerCommand(SpawnMob.class);

		cmdManager.registerCommand(KickPlayer.class);
		cmdManager.registerCommand(PrivateMessage.class);
		cmdManager.registerCommand(AddAlias.class);
		cmdManager.registerCommand(TpPlayerToPlayer.class);
		cmdManager.registerCommand(TpLoc.class);
		cmdManager.registerCommand(KickAllPlayers.class);
		if (cmdManager.registerCommand(Vulcan.class)) {
			pm.registerEvents(new ACVulcanListener(), this);
		}
		cmdManager.registerCommand(Drop.class);
		if (cmdManager.registerCommand(Invisible.class)) {
			pm.registerEvents(new ACInvisibleListener(), this);
		}
		cmdManager.registerCommand(SpyMsg.class);
		if (cmdManager.registerCommand(Fireball.class)) {
			pm.registerEvents(new ACFireballListener(), this);
		}
		cmdManager.registerCommand(Home.class);
		cmdManager.registerCommand(SetHome.class);
		cmdManager.registerCommand(AddWarp.class);
		cmdManager.registerCommand(RemoveWarp.class);
		cmdManager.registerCommand(TpToWarp.class);
		cmdManager.registerCommand(Ip.class);
		if (cmdManager.registerCommand(BanPlayer.class)) {
			banCommands = true;
		}
		if (cmdManager.registerCommand(UnBan.class)) {
			banCommands = true;
		}
		if (banCommands) {
			pm.registerEvents(new ACBanListener(), this);
		}
		cmdManager.registerCommand(KillMob.class);
		if (cmdManager.registerCommand(Fly.class)) {
			pm.registerEvents(new ACFlyListener(), this);
		}
		cmdManager.registerCommand(DeleteHome.class);
		cmdManager.registerCommand(ListHomes.class);
		if (cmdManager.registerCommand(Freeze.class)) {
			pm.registerEvents(new ACFrozenPlayerListener(), this);
		}
		cmdManager.registerCommand(Mute.class);
		cmdManager.registerCommand(UnMute.class);
		if (cmdManager.registerCommand(MobLimit.class)) {
			pm.registerEvents(new ACCreatureSpawnListener(), this);
		}
		cmdManager.registerCommand(NoPickup.class);
		cmdManager.registerCommand(FreezeWeather.class);
		cmdManager.registerCommand(MOTD.class);
		cmdManager.registerCommand(Execution.class);
		cmdManager.registerCommand(News.class);
		cmdManager.registerCommand(Rain.class);
		cmdManager.registerCommand(Roll.class);
		cmdManager.registerCommand(Extinguish.class);
		cmdManager.registerCommand(Reload.class);
		cmdManager.registerCommand(ReplaceBlock.class);
		cmdManager.registerCommand(Undo.class);
		cmdManager.registerCommand(ReloadAll.class);
		cmdManager.registerCommand(RepeatCmd.class);
		cmdManager.registerCommand(Afk.class);
		cmdManager.registerCommand(MoreAll.class);
		cmdManager.registerCommand(TpToggle.class);
		if (cmdManager.registerCommand(TpAtSee.class)) {
			pm.registerEvents(new ACTpAtSeeListener(), this);
		}
		cmdManager.registerCommand(Uptime.class);
		cmdManager.registerCommand(Kit.class);
		cmdManager.registerCommand(Version.class);
		cmdManager.registerCommand(ListValues.class);
		if (cmdManager.registerCommand(LastLocation.class)) {
			pm.registerEvents(new ACTeleportBackListener(), this);
		}
		if (cmdManager.registerCommand(SuperBreaker.class)) {
			pm.registerEvents(new ACSuperBreaker(), this);
		}
		cmdManager.registerCommand(Help.class);
		cmdManager.registerCommand(Played.class);
		if (cmdManager.registerCommand(LockServer.class)) {
			lockCommand = true;
		}
		cmdManager.registerCommand(Set.class);
		cmdManager.registerCommand(Rules.class);
		if (cmdManager.registerCommand(Eternal.class)) {
			pm.registerEvents(new ACFoodListener(), this);
		}
		cmdManager.registerCommand(FakeQuit.class);
		cmdManager.registerCommand(Feed.class);
		cmdManager.registerCommand(GameModeSwitch.class);
		cmdManager.registerCommand(Whois.class);
		cmdManager.registerCommand(ChangeMobSpawner.class);
		cmdManager.registerCommand(Reply.class);
		cmdManager.registerCommand(WorldDifficulty.class);
		cmdManager.registerCommand(Presentation.class);
		cmdManager.registerCommand(Experience.class);
		cmdManager.registerCommand(Broadcast.class);
		if (cmdManager.registerCommand(StopServer.class)) {
			lockCommand = true;
		}
		if (lockCommand) {
			pm.registerEvents(new ACLockedServerListener(), this);
		}
		if (cmdManager.registerCommand(NoDrop.class)) {
			pm.registerEvents(new ACNoDropListener(), this);
		}
		if (cmdManager.registerCommand(EggSpawner.class)) {
			EggTypeClassLoader.addPackage(this, "be.Balor.Tools.Egg.Types");
			pm.registerEvents(new ACEggListener(), this);
		}
		cmdManager.registerCommand(GetItemId.class);
		cmdManager.registerCommand(Enchant.class);
		cmdManager.registerCommand(Potion.class);
		cmdManager.registerCommand(TpWorld.class);
		cmdManager.registerCommand(TpAll.class);
		cmdManager.registerCommand(Quit.class);
		cmdManager.registerCommand(BanList.class);
		cmdManager.registerCommand(Search.class);
		cmdManager.registerCommand(Withdraw.class);
		if (ConfigEnum.SUPER_BLACKLIST.getBoolean()) {
			pm.registerEvents(new ACSuperBlacklistListener(), this);
		}
		cmdManager.registerCommand(MuteList.class);
		cmdManager.registerCommand(UnMuteAll.class);
		cmdManager.registerCommand(ReloadTxt.class);
		if (cmdManager.registerCommand(OpenInventory.class)) {
			InventoryManager.createInstance();
			pm.registerEvents(new ACOpenInvListener(), this);
		}

		if (ConfigEnum.GSPAWN.getString().equalsIgnoreCase("group")) {
			cmdManager.registerCommand(SetGroupSpawn.class);
			cmdManager.registerCommand(GroupSpawn.class);
		}
		cmdManager.registerCommand(PlayerTime.class);
		cmdManager.registerCommand(AsUser.class);
		cmdManager.registerCommand(WalkSpeed.class);
		cmdManager.registerCommand(DynKit.class);
		cmdManager.registerCommand(Enderchest.class);
		cmdManager.registerCommand(RemoveSuperPowers.class);
		cmdManager.registerCommand(Workbench.class);
		cmdManager.registerCommand(Head.class);
		cmdManager.registerCommand(RemoveStatusEffects.class);
		cmdManager.registerCommand(PluginsList.class);
                cmdManager.registerCommand(TpUp.class);
                cmdManager.registerCommand(TpDown.class);
		DebugLog.endInfo();
	}

	@Override
	protected void registerPermParents() {
		permissionLinker.addPermParent(new PermParent("admincmd.item.*"));
		final PermParent player = new PermParent("admincmd.player.*");
		permissionLinker.addPermParent(player);
		permissionLinker.addPermParent(new PermParent("admincmd.mob.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.spawn.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.time.*"));
		final PermParent tp = new PermParent("admincmd.tp.*");
		final PermParent worldTp = new PermParent("admincmd.tp.world.*");
		permissionLinker.addPermParent(tp);
		permissionLinker.addChildPermParent(worldTp, tp);
		permissionLinker.addChildPermParent(new PermParent(
				"admincmd.tp.toggle.*"), tp);
		permissionLinker.addPermParent(new PermParent("admincmd.weather.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.warp.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.invisible.*"));
		final PermParent server = new PermParent("admincmd.server.*");
		permissionLinker.addPermParent(server);
		final PermParent sExec = new PermParent("admincmd.server.exec.*");
		permissionLinker.addChildPermParent(sExec, server);
		permissionLinker.addPermParent(new PermParent("admincmd.admin.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.egg.*"));
		final PermParent spec = new PermParent("admincmd.spec.*");
		permissionLinker.addPermParent(spec);

		player.addChild("admincmd.player.bypass");
		permissionLinker.addPermChild("admincmd.spec.noblacklist",
				PermissionDefault.OP);
		player.addChild("admincmd.player.noreset");
		permissionLinker.addPermChild("admincmd.spec.notprequest",
				PermissionDefault.OP);
		player.addChild("admincmd.player.noafkkick");
		final PermParent majorPerm = new PermParent("admincmd.*");

		permissionLinker.addPermChild("admincmd.admin.home",
				PermissionDefault.OP);
		permissionLinker.addPermChild("admincmd.item.infinity",
				PermissionDefault.OP);
		spec.addChild("admincmd.spec.noloss");
		spec.addChild("admincmd.spec.signedit");
		spec.addChild("admincmd.spec.ipbroadcast");
		spec.addChild("admincmd.spec.versionbcast");
		player.addChild("admincmd.player.fly.allowed");
		new PermChild("admincmd.immunityLvl.samelvl", PermissionDefault.FALSE);
		for (final World w : this.getServer().getWorlds()) {
			worldTp.addChild("admincmd.tp.world."
					+ w.getName().replace(' ', '_'));
		}
		majorPerm.addChild(new PermChild("admincmd.coloredsign.create"));
		for (int i = 0; i <= 150; i++) {
			new PermChild("admincmd.maxHomeByUser." + i,
					PermissionDefault.FALSE);
			new PermChild("admincmd.immunityLvl." + i, PermissionDefault.FALSE);
			new PermChild("admincmd.maxItemAmount." + i,
					PermissionDefault.FALSE);
		}
		for (final String group : worker.getGroupList()) {
			permissionLinker.addPermChild("admincmd.respawn." + group,
					PermissionDefault.FALSE);
		}
		permissionLinker.addPermChild("admincmd.respawn.admin",
				PermissionDefault.OP);
		permissionLinker.setMajorPerm(majorPerm);

	}

	@Override
	protected void setDefaultLocale() {
		LocaleManager.getInstance().addLocale(
				"playerNotFound",
				ChatColor.RED + "No such player: " + ChatColor.WHITE
						+ "%player");
		LocaleManager.getInstance().addLocale("kitNotFound",
				ChatColor.RED + "No such kit: " + ChatColor.WHITE + "%kit");
		LocaleManager.getInstance().addLocale(
				"pluginNotFound",
				ChatColor.RED + "No such Plugin: " + ChatColor.WHITE
						+ "%plugin");
		LocaleManager.getInstance().addLocale("worldNotFound",
				ChatColor.RED + "No such world: " + ChatColor.WHITE + "%world");
		LocaleManager.getInstance().addLocale(
				"unknownMat",
				ChatColor.RED + "Unknown Material : " + ChatColor.WHITE
						+ "%material");
		LocaleManager.getInstance().addLocale("onlinePlayers",
				ChatColor.RED + "Online players: ");
		LocaleManager.getInstance().addLocale("serverReload",
				ChatColor.YELLOW + "Server Reloaded.");
		LocaleManager
				.getInstance()
				.addLocale(
						"changedWorld",
						ChatColor.DARK_RED
								+ "All your powers have been deactivated because you teleported to an another world");
		LocaleManager.getInstance().addLocale("stillInv",
				ChatColor.RED + "You are still Invisible");
		LocaleManager.getInstance().addLocale(
				"errorNotPerm",
				ChatColor.RED + "You don't have the Permissions to do that "
						+ ChatColor.BLUE + "(%p)");
		LocaleManager.getInstance().addLocale(
				"dropItemOtherPlayer",
				ChatColor.RED + "[%sender]" + ChatColor.WHITE
						+ " dropped at your feet " + ChatColor.GOLD
						+ "%amount %material");
		LocaleManager.getInstance()
				.addLocale(
						"dropItemCommandSender",
						ChatColor.RED + "Dropped " + ChatColor.GOLD
								+ "%amount %material to " + ChatColor.WHITE
								+ "%target");
		LocaleManager.getInstance().addLocale(
				"dropItemYourself",
				ChatColor.RED + "Dropped " + ChatColor.GOLD
						+ "%amount %material");
		LocaleManager.getInstance().addLocale(
				"giveItemOtherPlayer",
				ChatColor.RED + "[%sender]" + ChatColor.WHITE + " send you "
						+ ChatColor.GOLD + "%amount %material");
		LocaleManager.getInstance().addLocale(
				"giveItemCommandSender",
				ChatColor.RED + "Added " + ChatColor.GOLD
						+ "%amount %material to " + ChatColor.WHITE
						+ "%target's inventory");
		LocaleManager.getInstance().addLocale(
				"giveItemYourself",
				ChatColor.RED + "Added " + ChatColor.GOLD + "%amount %material"
						+ ChatColor.WHITE + " to your inventory");
		LocaleManager.getInstance().addLocale("errorHolding",
				ChatColor.RED + "You have to be holding something!");
		LocaleManager.getInstance().addLocale(
				"moreTooMuch",
				"Excedent(s) item(s) (" + ChatColor.BLUE + "%amount"
						+ ChatColor.WHITE
						+ ") have been stored in your inventory");
		LocaleManager.getInstance().addLocale(
				"repairTarget",
				"Your item " + ChatColor.RED + "%type" + ChatColor.WHITE
						+ " has been successfully repaired.");
		LocaleManager.getInstance().addLocale(
				"repair",
				"%player" + "'s item " + ChatColor.RED + "%type"
						+ ChatColor.WHITE + " has been successfully repaired.");
		LocaleManager.getInstance().addLocale("errorRepair",
				"You can't repair this item : " + ChatColor.RED + "%type");
		LocaleManager.getInstance().addLocale("repairAll",
				"All %player's items have been repaired.");
		LocaleManager.getInstance().addLocale("repairAllTarget",
				"All your items have been repaired.");
		LocaleManager.getInstance()
				.addLocale(
						"errorMob",
						ChatColor.RED + "No such creature: " + ChatColor.WHITE
								+ "%mob");
		LocaleManager.getInstance().addLocale("spawnMob",
				ChatColor.BLUE + "Spawned " + ChatColor.WHITE + "%nb %mob");
		LocaleManager.getInstance().addLocale(
				"spawnMobOther",
				ChatColor.GOLD + "%player " + ChatColor.BLUE + "spawned "
						+ ChatColor.WHITE + "%nb %mob (s)" + ChatColor.BLUE
						+ " at your location.");
		LocaleManager.getInstance().addLocale("clear",
				ChatColor.RED + "Your inventory has been cleared");
		LocaleManager.getInstance().addLocale(
				"clearTarget",
				ChatColor.RED + "Inventory of " + ChatColor.WHITE + "%player"
						+ ChatColor.RED + " cleared");
		LocaleManager.getInstance().addLocale("fireballDisabled",
				ChatColor.DARK_RED + "Fireball mode disabled.");
		LocaleManager.getInstance().addLocale("fireballDisabledTarget",
				ChatColor.DARK_RED + "Fireball mode disabled for %player");
		LocaleManager.getInstance().addLocale("fireballEnabled",
				ChatColor.DARK_RED + "Fireball mode enabled.");
		LocaleManager.getInstance().addLocale("fireballEnabledTarget",
				ChatColor.DARK_RED + "Fireball mode enabled for %player");
		LocaleManager.getInstance().addLocale("godDisabled",
				ChatColor.DARK_AQUA + "GOD mode disabled.");
		LocaleManager.getInstance().addLocale("godDisabledTarget",
				ChatColor.DARK_AQUA + "GOD mode disabled for %player");
		LocaleManager.getInstance().addLocale("godEnabled",
				ChatColor.DARK_AQUA + "GOD mode enabled.");
		LocaleManager.getInstance().addLocale("godEnabledTarget",
				ChatColor.DARK_AQUA + "GOD mode enabled for %player");
		LocaleManager.getInstance().addLocale("noDropDisabled",
				ChatColor.DARK_AQUA + "NO DROP mode disabled.");
		LocaleManager.getInstance().addLocale("noDropDisabledTarget",
				ChatColor.DARK_AQUA + "NO DROP mode disabled for %player");
		LocaleManager.getInstance().addLocale("noDropEnabled",
				ChatColor.DARK_AQUA + "NO DROP mode enabled.");
		LocaleManager.getInstance().addLocale("noDropEnabledTarget",
				ChatColor.DARK_AQUA + "NO DROP mode enabled for %player");
		LocaleManager.getInstance().addLocale("thorDisabled",
				ChatColor.DARK_AQUA + "THOR mode disabled.");
		LocaleManager.getInstance().addLocale("thorDisabledTarget",
				ChatColor.DARK_AQUA + "THOR mode disabled for %player");
		LocaleManager.getInstance().addLocale("thorEnabled",
				ChatColor.DARK_AQUA + "THOR mode enabled.");
		LocaleManager.getInstance().addLocale("thorEnabledTarget",
				ChatColor.DARK_AQUA + "THOR mode enabled for %player");
		LocaleManager.getInstance().addLocale("vulcanDisabled",
				ChatColor.DARK_RED + "VULCAN mode disabled.");
		LocaleManager.getInstance().addLocale("vulcanDisabledTarget",
				ChatColor.DARK_RED + "VULCAN mode disabled for %player");
		LocaleManager.getInstance().addLocale("vulcanEnabled",
				ChatColor.DARK_RED + "VULCAN mode enabled.");
		LocaleManager.getInstance().addLocale("vulcanEnabledTarget",
				ChatColor.DARK_RED + "VULCAN mode enabled for %player");
		LocaleManager.getInstance().addLocale("spymsgDisabled",
				ChatColor.DARK_AQUA + "SPYMSG mode disabled.");
		LocaleManager.getInstance().addLocale("spymsgEnabled",
				ChatColor.DARK_AQUA + "SPYMSG mode enabled.");
		LocaleManager.getInstance().addLocale("invisibleEnabled",
				ChatColor.RED + "You are now Invisible");
		LocaleManager.getInstance().addLocale("invisibleEnabledTarget",
				ChatColor.DARK_AQUA + "INVISIBLE mode enabled for %player");
		LocaleManager.getInstance().addLocale("invisibleDisabled",
				ChatColor.GREEN + "You are now Visible");
		LocaleManager.getInstance().addLocale("invisibleDisabledTarget",
				ChatColor.DARK_AQUA + "INVISIBLE mode disabled for %player");
		LocaleManager.getInstance().addLocale(
				"errorMultiHome",
				ChatColor.DARK_GREEN + "Home " + ChatColor.RED + "%home"
						+ ChatColor.WHITE + " not set.");
		LocaleManager.getInstance().addLocale(
				"multiHome",
				ChatColor.DARK_GREEN + "Teleported" + ChatColor.WHITE
						+ " to your home " + ChatColor.DARK_AQUA + "%home.");
		LocaleManager.getInstance().addLocale(
				"setMultiHome",
				ChatColor.DARK_GREEN + "Home " + ChatColor.DARK_AQUA + "%home"
						+ ChatColor.WHITE + " set.");
		LocaleManager.getInstance().addLocale(
				"rmHome",
				ChatColor.RED + "Home " + ChatColor.DARK_AQUA + "%home"
						+ ChatColor.WHITE + " removed.");
		LocaleManager.getInstance().addLocale(
				"homeLimit",
				ChatColor.RED + "You have reached your " + ChatColor.DARK_GREEN
						+ "home limit");
		LocaleManager.getInstance().addLocale(
				"itemLimit",
				ChatColor.RED + "You have exceeded your "
						+ ChatColor.DARK_GREEN + "item limit" + ChatColor.RED
						+ " of %limit items per command.");
		LocaleManager.getInstance().addLocale("errorLocation",
				ChatColor.RED + "Location has to be formed by numbers");
		LocaleManager.getInstance().addLocale(
				"addWarp",
				ChatColor.GREEN + "WarpPoint %name" + ChatColor.WHITE
						+ " added.");
		LocaleManager.getInstance().addLocale(
				"rmWarp",
				ChatColor.RED + "WarpPoint %name" + ChatColor.WHITE
						+ " removed.");
		LocaleManager.getInstance().addLocale("errorWarp",
				ChatColor.DARK_RED + "WarpPoint %name not found");
		LocaleManager.getInstance().addLocale("tpWarp",
				ChatColor.GREEN + "Teleported to " + ChatColor.WHITE + "%name");
		LocaleManager.getInstance().addLocale("strike",
				"%player was striked by Thor");
		LocaleManager.getInstance().addLocale(
				"tp",
				"Successfully teleported " + ChatColor.BLUE + "%fromPlayer"
						+ ChatColor.WHITE + " to " + ChatColor.GREEN
						+ "%toPlayer");
		LocaleManager.getInstance().addLocale(
				"addBlacklistItem",
				ChatColor.GREEN + "Item (" + ChatColor.WHITE + "%material"
						+ ChatColor.GREEN
						+ ") added to the Black List for i, give and drop.");
		LocaleManager.getInstance().addLocale(
				"addBlacklistBlock",
				ChatColor.GREEN + "Block (" + ChatColor.WHITE + "%material"
						+ ChatColor.GREEN
						+ ") added to the BlockPlace Black List.");
		LocaleManager.getInstance().addLocale(
				"rmBlacklistItem",
				ChatColor.GREEN + "Item (" + ChatColor.WHITE + "%material"
						+ ChatColor.GREEN + ") removed from the Blacklist.");
		LocaleManager.getInstance().addLocale(
				"rmBlacklistBlock",
				ChatColor.GREEN + "Block (" + ChatColor.WHITE + "%material"
						+ ChatColor.GREEN + ") removed from the Blacklist.");
		LocaleManager.getInstance().addLocale(
				"inBlacklistItem",
				ChatColor.DARK_RED + "This item (" + ChatColor.WHITE
						+ "%material" + ChatColor.DARK_RED
						+ ") is black listed.");
		LocaleManager.getInstance().addLocale(
				"inBlacklistBlock",
				ChatColor.DARK_RED + "This block (" + ChatColor.WHITE
						+ "%material" + ChatColor.DARK_RED
						+ ") is black listed.");
		LocaleManager.getInstance().addLocale(
				"errorSpawn",
				ChatColor.DARK_GREEN + "spawn" + ChatColor.WHITE
						+ " not set for this world.");
		LocaleManager.getInstance().addLocale(
				"spawn",
				ChatColor.DARK_GREEN + "Teleported" + ChatColor.WHITE
						+ " to your spawn.");
		LocaleManager.getInstance().addLocale("setSpawn",
				ChatColor.DARK_GREEN + "spawn" + ChatColor.WHITE + " set.");
		LocaleManager.getInstance().addLocale("sClear",
				"Sky cleared in world :");
		LocaleManager.getInstance().addLocale("sStorm",
				"Storm set for %duration mins in world : ");
		LocaleManager.getInstance().addLocale("sRain",
				"Rain set for %duration mins in world : ");
		LocaleManager.getInstance().addLocale("afk",
				"%player " + ChatColor.RED + "is AFK");
		LocaleManager.getInstance().addLocale("online",
				"%player " + ChatColor.GREEN + "is Online");
		LocaleManager.getInstance().addLocale("afkTitle",
				ChatColor.BLUE + "[AFK]" + ChatColor.WHITE);
		LocaleManager.getInstance().addLocale(
				"ip",
				ChatColor.YELLOW + "IP adress of " + ChatColor.WHITE
						+ "%player - %ip");
		LocaleManager.getInstance().addLocale(
				"ban",
				ChatColor.YELLOW + "%player has been banned, reason: "
						+ ChatColor.RED + "%reason");
		LocaleManager.getInstance().addLocale("unban",
				ChatColor.YELLOW + "%player is now unbanned.");
		LocaleManager.getInstance().addLocale(
				"killMob",
				ChatColor.RED + "Killing mobs (" + ChatColor.WHITE + "%type"
						+ ChatColor.RED + ") of worlds : "
						+ ChatColor.DARK_PURPLE + "%worlds");
		LocaleManager.getInstance().addLocale("killedMobs",
				"%nbKilled" + ChatColor.DARK_RED + " mobs have been killed.");
		LocaleManager.getInstance().addLocale("flyDisabled",
				ChatColor.GOLD + "FLY mode disabled.");
		LocaleManager.getInstance().addLocale("flyDisabledTarget",
				ChatColor.GOLD + "FLY mode disabled for %player");
		LocaleManager.getInstance().addLocale("flyEnabled",
				ChatColor.GOLD + "FLY mode enabled.");
		LocaleManager.getInstance().addLocale("flyEnabledTarget",
				ChatColor.GOLD + "FLY mode enabled for %player");
		LocaleManager.getInstance().addLocale("npDisabled",
				ChatColor.GOLD + "No Pickup mode disabled.");
		LocaleManager.getInstance().addLocale("npDisabledTarget",
				ChatColor.GOLD + "No Pickup mode disabled for %player");
		LocaleManager.getInstance().addLocale("npEnabled",
				ChatColor.GOLD + "No Pickup mode enabled.");
		LocaleManager.getInstance().addLocale("npEnabledTarget",
				ChatColor.GOLD + "No Pickup mode enabled for %player");
		LocaleManager.getInstance().addLocale("afkKick",
				"You have been kicked because you were AFK");
		LocaleManager.getInstance().addLocale("freezeDisabled",
				ChatColor.DARK_GREEN + "You can now move again.");
		LocaleManager.getInstance().addLocale("freezeDisabledTarget",
				ChatColor.DARK_GREEN + "Freeze mode disabled for %player");
		LocaleManager.getInstance().addLocale("freezeEnabled",
				ChatColor.DARK_RED + "You can't move until you are defrozen.");
		LocaleManager.getInstance().addLocale("freezeEnabledTarget",
				ChatColor.DARK_RED + "Freeze mode enabled for %player");
		LocaleManager.getInstance().addLocale("muteDisabled",
				ChatColor.DARK_GREEN + "You can chat again.");
		LocaleManager.getInstance().addLocale("muteDisabledTarget",
				ChatColor.DARK_GREEN + "%player is unmuted.");
		LocaleManager.getInstance().addLocale("muteEnabled",
				ChatColor.DARK_RED + "You can't chat anymore. Reason: %reason");
		LocaleManager
				.getInstance()
				.addLocale(
						"tmpMuteEnabled",
						ChatColor.DARK_RED
								+ "You can't chat anymore for %minutes minutes. Reason: %reason");
		LocaleManager.getInstance().addLocale("muteEnabledTarget",
				ChatColor.DARK_RED + "%player is muted.");
		LocaleManager
				.getInstance()
				.addLocale(
						"alreadyMuted",
						ChatColor.DARK_AQUA
								+ "This player is already muted. To unmute him use the unmute command.");
		LocaleManager.getInstance().addLocale("commandMuteDisabled",
				ChatColor.DARK_GREEN + "You can use commands again.");
		LocaleManager.getInstance().addLocale("commandMuteDisabledTarget",
				ChatColor.DARK_GREEN + "%player can use commands again.");
		LocaleManager.getInstance().addLocale(
				"commandMuteEnabled",
				ChatColor.DARK_RED
						+ "You can't use commands anymore. Reason: %reason");
		LocaleManager
				.getInstance()
				.addLocale(
						"commandTmpMuteEnabled",
						ChatColor.DARK_RED
								+ "You can't use commands anymore for %minutes minutes. Reason: %reason");
		LocaleManager.getInstance().addLocale("commandMuteEnabledTarget",
				ChatColor.DARK_RED + "%player is now unable to use commands.");
		LocaleManager
				.getInstance()
				.addLocale(
						"alreadyCommandMuted",
						ChatColor.DARK_AQUA
								+ "This player already can't use commands. To let him use commands again use the unmute command.");
		LocaleManager.getInstance().addLocale("NaN",
				"%number " + ChatColor.DARK_RED + "is not a number.");
		LocaleManager.getInstance().addLocale("mobLimit",
				ChatColor.GOLD + "Mob limit (%number) set for world : %world");
		LocaleManager.getInstance().addLocale("mobLimitPerMob",
				"#mobLimit# " + ChatColor.RED + "for mob %mob");
		LocaleManager.getInstance().addLocale("mobLimitRemoved",
				ChatColor.GREEN + "Mob limit is removed for world : %world");
		LocaleManager.getInstance().addLocale("mobLimitRemovedPerMob",
				"#mobLimitRemoved# " + ChatColor.AQUA + " for mob %mob");
		LocaleManager.getInstance().addLocale("wFrozen",
				"Weather is frozen in world :");
		LocaleManager.getInstance().addLocale("wUnFrozen",
				"Weather can change in world :");
		LocaleManager.getInstance().addLocale("invTitle", "[INV]");
		LocaleManager.getInstance().addLocale(
				"roll",
				ChatColor.DARK_GREEN + "[%player] " + ChatColor.WHITE
						+ "rolled a " + ChatColor.GOLD + "%face dice : "
						+ ChatColor.YELLOW + "%result");
		LocaleManager.getInstance().addLocale(
				"extinguish",
				ChatColor.AQUA + "%nb blocks" + ChatColor.DARK_AQUA
						+ " have been extinguished.");
		LocaleManager.getInstance().addLocale(
				"pluginReloaded",
				ChatColor.YELLOW + "This plugin has been reloaded : "
						+ ChatColor.WHITE + "%plugin");
		LocaleManager.getInstance().addLocale(
				"replaced",
				ChatColor.RED + "%nb blocks of " + ChatColor.DARK_PURPLE
						+ "%mat" + ChatColor.DARK_AQUA + " are now AIR.");
		LocaleManager.getInstance().addLocale(
				"undo",
				ChatColor.GREEN + "%nb blocks " + ChatColor.DARK_GREEN
						+ "have been replaced");
		LocaleManager.getInstance().addLocale("nothingToUndo",
				ChatColor.DARK_PURPLE + "Nothing to undo.");
		LocaleManager.getInstance().addLocale("noRepeat",
				ChatColor.DARK_RED + "No command to repeat.");
		LocaleManager.getInstance().addLocale("reExec",
				ChatColor.YELLOW + "Repeating the last command.");
		LocaleManager.getInstance().addLocale(
				"timeSet",
				ChatColor.GOLD + "Time set to %type in world : "
						+ ChatColor.WHITE + "%world");
		LocaleManager.getInstance().addLocale("timeNotSet",
				ChatColor.RED + "%type doesn't exist.");
		LocaleManager.getInstance().addLocale(
				"timePaused",
				ChatColor.DARK_RED + "Time is paused in " + ChatColor.WHITE
						+ "%world. " + ChatColor.DARK_GREEN
						+ "To unpause : /time unpause .");
		LocaleManager.getInstance().addLocale(
				"moreAll",
				ChatColor.AQUA
						+ "All your items are now at their max stack size.");
		LocaleManager.getInstance().addLocale(
				"tpRequestTo",
				ChatColor.BLUE + "%player " + ChatColor.GOLD
						+ "wants to teleport to you. " + ChatColor.DARK_GREEN
						+ "Type " + ChatColor.GREEN + "/tpt yes "
						+ ChatColor.DARK_GREEN + "to accept.");
		LocaleManager.getInstance().addLocale(
				"tpRequestSend",
				ChatColor.DARK_PURPLE + "You send a Teleport request to "
						+ ChatColor.WHITE + "%player" + ChatColor.DARK_PURPLE
						+ " for a teleport " + ChatColor.AQUA + "%tp_type");
		LocaleManager.getInstance().addLocale(
				"tpRequestFrom",
				ChatColor.BLUE + "%player " + ChatColor.DARK_AQUA
						+ "wants to teleport you to their location. "
						+ ChatColor.DARK_GREEN + "Type " + ChatColor.GREEN
						+ "/tpt yes " + ChatColor.DARK_GREEN + "to accept.");
		LocaleManager.getInstance().addLocale("tpRequestOff",
				ChatColor.DARK_GREEN + "Tp Request system Disabled.");
		LocaleManager.getInstance().addLocale("tpRequestOn",
				ChatColor.DARK_RED + "Tp Request system Enabled.");
		LocaleManager.getInstance().addLocale("tpSeeEnabled",
				ChatColor.DARK_GREEN + "You Tp at see when you left click.");
		LocaleManager.getInstance().addLocale("tpSeeDisabled",
				ChatColor.DARK_RED + "TP AT SEE mode disabled.");
		LocaleManager.getInstance().addLocale("elapsedTime",
				"Uptime : " + ChatColor.YELLOW + "%d day(s) %h:%m:%s");
		LocaleManager.getInstance()
				.addLocale(
						"kitList",
						ChatColor.GOLD + "Available Kits : " + ChatColor.AQUA
								+ "%list");
		LocaleManager.getInstance().addLocale(
				"kitOtherPlayer",
				ChatColor.RED + "[%sender]" + ChatColor.WHITE
						+ " send you the kit : " + ChatColor.GOLD + "%kit");
		LocaleManager.getInstance().addLocale(
				"kitCommandSender",
				ChatColor.RED + "Added " + ChatColor.GOLD + "%kit to "
						+ ChatColor.WHITE + "%target's inventory");
		LocaleManager.getInstance().addLocale(
				"kitYourself",
				ChatColor.RED + "Added " + ChatColor.GOLD + "%kit"
						+ ChatColor.WHITE + " to your inventory");
		LocaleManager
				.getInstance()
				.addLocale(
						"tpRequestTimeOut",
						ChatColor.RED
								+ "This tp request has timed out and will not be executed.");
		LocaleManager.getInstance().addLocale("noTpRequest",
				ChatColor.GREEN + "There is no tp request to execute");
		LocaleManager.getInstance().addLocale(
				"noteAfk",
				ChatColor.DARK_RED + "Note: " + ChatColor.WHITE
						+ "%player is AFK at the moment:");
		LocaleManager.getInstance().addLocale("idleTime",
				ChatColor.DARK_AQUA + "Idle for %mins minute(s)");
		LocaleManager.getInstance().addLocale(
				"pluginVersion",
				ChatColor.YELLOW + "Version of " + ChatColor.WHITE
						+ "%plugin: " + ChatColor.GREEN + "%version");
		LocaleManager
				.getInstance()
				.addLocale(
						"emptyList",
						ChatColor.RED
								+ "Empty list or the selected type don't exists.");
		LocaleManager.getInstance()
				.addLocale(
						"telportSuccess",
						ChatColor.DARK_GREEN
								+ "You have been successfully teleported.");
		LocaleManager.getInstance().addLocale("noLastLocation",
				ChatColor.RED + "You don't have a last location to tp back");
		LocaleManager.getInstance().addLocale("super_breakerDisabled",
				ChatColor.GOLD + "Super Breaker mode disabled.");
		LocaleManager.getInstance().addLocale("super_breakerDisabledTarget",
				ChatColor.GOLD + "Super Breaker mode disabled for %player");
		LocaleManager.getInstance().addLocale("super_breakerEnabled",
				ChatColor.GOLD + "Super Breaker mode enabled.");
		LocaleManager.getInstance().addLocale("super_breakerEnabledTarget",
				ChatColor.GOLD + "Super Breaker mode enabled for %player");
		LocaleManager.getInstance().addLocale("airForbidden",
				ChatColor.DARK_RED + "You can't give AIR item.");
		LocaleManager.getInstance().addLocale(
				"playedTime",
				ChatColor.DARK_AQUA + "%player " + ChatColor.WHITE + "played "
						+ ChatColor.AQUA + "#elapsedTotalTime#");
		LocaleManager.getInstance().addLocale("serverUnlock",
				ChatColor.GREEN + "Server is now UnLocked.");
		LocaleManager
				.getInstance()
				.addLocale(
						"serverLock",
						ChatColor.RED
								+ "Server will be lock in 5 seconds,"
								+ " you'll be kicked if you don't have the Permission to stay.");
		LocaleManager.getInstance().addLocale("eternalDisabled",
				ChatColor.DARK_RED + "ETERNAL mode disabled.");
		LocaleManager.getInstance().addLocale("eternalDisabledTarget",
				ChatColor.DARK_RED + "ETERNAL mode disabled for %player");
		LocaleManager.getInstance().addLocale("eternalEnabled",
				ChatColor.DARK_RED + "ETERNAL mode enabled.");
		LocaleManager.getInstance().addLocale("eternalEnabledTarget",
				ChatColor.DARK_RED + "ETERNAL mode enabled for %player");
		LocaleManager
				.getInstance()
				.addLocale(
						"fakeQuitDisabled",
						ChatColor.DARK_AQUA
								+ "FakeQuit mode disabled, you are now listed online again.");
		LocaleManager.getInstance().addLocale("fakeQuitDisabledTarget",
				ChatColor.DARK_AQUA + "FakeQuit mode disabled for %player");
		LocaleManager
				.getInstance()
				.addLocale(
						"fakeQuitEnabled",
						ChatColor.DARK_AQUA
								+ "FakeQuit mode enabled, you are now not listed online anymore.");
		LocaleManager.getInstance().addLocale("fakeQuitEnabledTarget",
				ChatColor.DARK_AQUA + "FakeQuit mode enabled for %player");
		LocaleManager.getInstance().addLocale("noLoginInformation",
				"No login information available");
		LocaleManager.getInstance().addLocale(
				"insufficientLvl",
				ChatColor.DARK_RED
						+ "You don't have the sufficient lvl to do that.");
		LocaleManager.getInstance().addLocale(
				"gmSwitch",
				ChatColor.GREEN + "GameMode for " + ChatColor.GOLD + "%player "
						+ ChatColor.GREEN + "switched to : " + ChatColor.WHITE
						+ "%gamemode");
		LocaleManager.getInstance().addLocale(
				"kitDelayNotUp",
				ChatColor.RED + "You cannot use that kit for another "
						+ ChatColor.WHITE + "%delay");
		LocaleManager.getInstance().addLocale("days", "%d day(s)");
		LocaleManager.getInstance().addLocale("elapsedTotalTime",
				"#days# %h:%m:%s");
		LocaleManager.getInstance().addLocale("spawnerSetDelay",
				ChatColor.GREEN + "Delay set to: " + ChatColor.GOLD + "%delay");
		LocaleManager.getInstance().addLocale(
				"spawnerSetType",
				ChatColor.GREEN
						+ "CreatureType of the Mob Spawner changed to: "
						+ ChatColor.GOLD + "%type");
		LocaleManager.getInstance().addLocale(
				"spawnerGetData",
				ChatColor.DARK_AQUA + "This Mob Spawner spawns "
						+ ChatColor.GOLD + "%mob" + "s" + ChatColor.DARK_AQUA
						+ " with a delay of " + ChatColor.GOLD + "%delay"
						+ ChatColor.DARK_AQUA + ".");
		LocaleManager.getInstance().addLocale("spawnerNaN",
				ChatColor.RED + "Your input is not a number!");
		LocaleManager.getInstance().addLocale(
				"addSpawnWarp",
				ChatColor.GREEN + "Spawnpoint for the group "
						+ ChatColor.DARK_AQUA + " %name" + ChatColor.GREEN
						+ " added.");
		LocaleManager.getInstance().addLocale("tpTO", "to them.");
		LocaleManager.getInstance().addLocale("tpHERE", "to you.");
		LocaleManager.getInstance().addLocale("tpPLAYERSTO",
				"of %target to you.");
		LocaleManager.getInstance().addLocale("tpPLAYERSFROM",
				"you to %target.");
		LocaleManager.getInstance().addLocale("offline",
				"%player " + ChatColor.RED + "is Offline");
		LocaleManager
				.getInstance()
				.addLocale(
						"noPlayerToReply",
						ChatColor.RED
								+ "You can't reply to a message if none did send you a private message.");
		LocaleManager.getInstance().addLocale("mustBePlayer",
				"[AdminCmd] You must be a player to use this command.");
		LocaleManager
				.getInstance()
				.addLocale("errorInsufficientArguments",
						"You have to specify a %argument to use this command from the command line.");
		LocaleManager.getInstance().addLocale(
				"setDifficutly",
				ChatColor.DARK_AQUA + "The Difficulty of " + ChatColor.GOLD
						+ "%world" + ChatColor.DARK_AQUA + " has been set to: "
						+ ChatColor.GOLD + "%difficulty");
		LocaleManager.getInstance().addLocale(
				"getDifficulty",
				ChatColor.DARK_AQUA + "The Difficulty of " + ChatColor.GOLD
						+ "%world" + ChatColor.DARK_AQUA + " is set to: "
						+ ChatColor.GOLD + "%difficulty");
		LocaleManager.getInstance().addLocale("serverLockMessage",
				"The server is locked!");
		LocaleManager
				.getInstance()
				.addLocale(
						"errorMoved",
						ChatColor.RED
								+ "You have moved since you issued the %cmdname command, teleportation aborted!");
		LocaleManager.getInstance().addLocale("privateTitle",
				ChatColor.RED + "[Private]" + ChatColor.WHITE);
		LocaleManager.getInstance().addLocale(
				"privateMessageHeader",
				"#privateTitle# " + "%sender" + "-" + "%receiver"
						+ ChatColor.WHITE + ": ");
		LocaleManager.getInstance().addLocale("joinMessage",
				"%name" + ChatColor.YELLOW + " joined the game!");
		LocaleManager.getInstance().addLocale(
				"joinMessageFirstTime",
				"%name" + ChatColor.YELLOW + " joined the game "
						+ ChatColor.GOLD + "for the first time!");
		LocaleManager.getInstance().addLocale("quitMessage",
				"%name" + ChatColor.YELLOW + " left the game!");
		LocaleManager.getInstance().addLocale(
				"presSet",
				ChatColor.YELLOW + "Presentation for" + ChatColor.WHITE
						+ " %player" + ChatColor.YELLOW + " set to : "
						+ ChatColor.GOLD + "%pres");
		LocaleManager.getInstance().addLocale(
				"expAdded",
				ChatColor.GOLD + "%amount " + ChatColor.DARK_AQUA
						+ "has been added to your experience.");
		LocaleManager.getInstance().addLocale(
				"expLevelSet",
				ChatColor.DARK_AQUA + "Your current level has been set to "
						+ ChatColor.GOLD + "%amount");
		LocaleManager.getInstance().addLocale(
				"expProgressionSet",
				ChatColor.DARK_AQUA
						+ "Your current level progression has been set to "
						+ ChatColor.GOLD + "%amount");
		LocaleManager
				.getInstance()
				.addLocale(
						"expDropped",
						ChatColor.DARK_AQUA
								+ "An experience orb has been dropped near your location!");
		LocaleManager.getInstance().addLocale(
				"expTotal",
				ChatColor.DARK_AQUA + "Your total experience is: "
						+ ChatColor.GOLD + "%exp");
		LocaleManager.getInstance().addLocale(
				"expAddedTarget",
				ChatColor.GREEN + "You have added" + ChatColor.GOLD
						+ " %amount " + ChatColor.GREEN + "to %target"
						+ "s total experience.");
		LocaleManager.getInstance().addLocale(
				"expLevelSetTarget",
				ChatColor.GREEN + "%target" + "s level now is:"
						+ ChatColor.GOLD + " %amount");
		LocaleManager.getInstance().addLocale(
				"expProgressionSetTarget",
				ChatColor.GREEN + "You have set %target" + "s current "
						+ "progression to " + ChatColor.GOLD + "%amount");
		LocaleManager.getInstance().addLocale(
				"expDroppedTarget",
				ChatColor.GREEN + "You have dropped an experience"
						+ " orb at %target" + "s location.");
		LocaleManager.getInstance().addLocale(
				"expTotalTarget",
				ChatColor.DARK_AQUA + "%target" + "s total experience is: "
						+ ChatColor.GOLD + "%exp");
		LocaleManager.getInstance().addLocale(
				"kitOnce",
				ChatColor.RED + "The kit " + ChatColor.GOLD + "%kit"
						+ ChatColor.RED + " can be only used once.");
		LocaleManager.getInstance().addLocale("MOTDset",
				ChatColor.YELLOW + "The new Message Of The Day is : %motd");
		LocaleManager.getInstance().addLocale("NEWSset",
				ChatColor.YELLOW + "The News is : %news");
		LocaleManager.getInstance().addLocale("RulesSet",
				"The new rules are://n" + "%rules");
		LocaleManager.getInstance().addLocale(
				"timeOutPower",
				ChatColor.GOLD + "Time Out of the power %power. "
						+ ChatColor.DARK_RED + "You lost it.");
		LocaleManager.getInstance().addLocale(
				"timeOutPowerSender",
				ChatColor.DARK_RED + "Power " + ChatColor.GOLD + "%power "
						+ ChatColor.DARK_RED
						+ "disabled for %player, reason: %reason");
		LocaleManager.getInstance().addLocale("serverStop",
				"The server is stopping.");
		LocaleManager.getInstance().addLocale(
				"serverWillStop",
				ChatColor.RED + "[IMPORTANT] " + ChatColor.YELLOW
						+ "The server will " + ChatColor.DARK_RED + "STOP "
						+ ChatColor.YELLOW + "in " + ChatColor.GOLD
						+ "%sec seconds.");
		LocaleManager.getInstance().addLocale(
				"diffWorld",
				ChatColor.AQUA + "%player" + ChatColor.RED
						+ " is in a different world as "
						+ ChatColor.DARK_PURPLE + "%to" + ChatColor.DARK_RED
						+ " . He can't be tp there.");
		LocaleManager.getInstance().addLocale(
				"paramMissing",
				ChatColor.RED + "This command need the parameter "
						+ ChatColor.GOLD + "-%param .");
		LocaleManager.getInstance().addLocale(
				"eggDontExists",
				ChatColor.RED + "This Egg Type (" + ChatColor.GOLD + "%egg"
						+ ChatColor.RED + ") don't exists.");
		LocaleManager.getInstance().addLocale(
				"eggEnabled",
				ChatColor.DARK_AQUA + "EGG " + ChatColor.GOLD + "(%egg)"
						+ ChatColor.AQUA + " mode enabled.");
		LocaleManager.getInstance().addLocale("eggNormal",
				ChatColor.GREEN + "EGG return to normality.");
		LocaleManager
				.getInstance()
				.addLocale(
						"eggNoParamGiven",
						ChatColor.RED
								+ "You need to specify an egg-type for the /egg -E command.");
		LocaleManager.getInstance().addLocale(
				"entityDontExists",
				ChatColor.RED + "The Entity id " + ChatColor.GOLD + "%entity"
						+ ChatColor.RED + " don't exists.");
		LocaleManager.getInstance().addLocale(
				"eggCustomError",
				ChatColor.RED + "Problem with the egg " + ChatColor.GOLD
						+ "%egg" + ChatColor.RED + " : " + ChatColor.YELLOW
						+ "%error");
		LocaleHelper.addAllLocales();
		LocaleManager.getInstance().save();
	}

	private void checkModulableFeatures(final PluginManager pm) {
		DebugLog.beginInfo("Loading modulable features");
		if (ConfigEnum.EDIT_SIGN.getBoolean()) {
			pm.registerEvents(new ACSignEditListener(), this);
		}
		if (ConfigEnum.LOG_SAME_IP.getBoolean()) {
			pm.registerEvents(new ACIpCheckListener(), this);
		}
		if (ConfigEnum.COLSIGN.getBoolean()) {
			pm.registerEvents(new ACColorSignListener(), this);
		}
		if (ConfigEnum.RESET_POWERS.getBoolean()) {
			pm.registerEvents(new ACResetPowerListener(), this);
		}
		if (ConfigEnum.POWERS_OFF.getBoolean()) {
			pm.registerEvents(new ACPowerOffListener(), this);
		}
		if (!ConfigEnum.DEATH_MSG_OFF.getBoolean()) {
			pm.registerEvents(
					new ACDeathListener(!ConfigEnum.DEATH_MSG.getBoolean()),
					this);
		}
		pm.registerEvents(new ACChatListener(), this);
		if (ConfigEnum.RESPAWN_BEHAVIOR.getBoolean()) {
			pm.registerEvents(new ACRespawnWorldFeature(), this);
		}
		pm.registerEvents(new ACFrozenTimeWorldListener(), this);
		if (ConfigEnum.REMOVE_SP_PERMISSION.getBoolean()) {
			pm.registerEvents(new ACRemoveNoAccessPowers(), this);
		}
		if (ConfigEnum.USE_UNKNOWN_CMD.getBoolean()) {
			pm.registerEvents(new ACUnknownCommandListener(), this);
		}
		if (ConfigEnum.LOG_CMD.getBoolean()) {
			pm.registerEvents(new ACLogCommandListener(), this);
		}
		DebugLog.endInfo();
	}
}
