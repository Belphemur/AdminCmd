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
package be.Balor.JUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.inventory.CraftItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.Balor.Kit.ArmoredKitInstance;
import be.Balor.Kit.KitInstance;
import be.Balor.Manager.Exceptions.CantEnchantItemException;
import be.Balor.Manager.Exceptions.EnchantmentConflictException;
import be.Balor.Player.BannedPlayer;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type.ArmorPart;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;

import com.avaje.ebean.config.ServerConfig;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExtendedConfigurationTest {
	private File file;
	static {
		Bukkit.setServer(new Server() {

			@Override
			public void sendPluginMessage(final Plugin source,
					final String channel, final byte[] message) {
				// TODO Auto-generated method stub

			}

			@Override
			public Set<String> getListeningPluginChannels() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean useExactLoginLocation() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean unloadWorld(final World world, final boolean save) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean unloadWorld(final String name, final boolean save) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void unbanIP(final String address) {
				// TODO Auto-generated method stub

			}

			@Override
			public void shutdown() {
				// TODO Auto-generated method stub

			}

			@Override
			public void setWhitelist(final boolean value) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setSpawnRadius(final int value) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setDefaultGameMode(final GameMode mode) {
				// TODO Auto-generated method stub

			}

			@Override
			public void savePlayers() {
				// TODO Auto-generated method stub

			}

			@Override
			public void resetRecipes() {
				// TODO Auto-generated method stub

			}

			@Override
			public void reloadWhitelist() {
				// TODO Auto-generated method stub

			}

			@Override
			public void reload() {
				// TODO Auto-generated method stub

			}

			@Override
			public Iterator<Recipe> recipeIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Player> matchPlayer(final String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isPrimaryThread() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isHardcore() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasWhitelist() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public List<World> getWorlds() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getWorldType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public File getWorldContainer() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public World getWorld(final UUID uid) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public World getWorld(final String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<OfflinePlayer> getWhitelistedPlayers() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getWaterAnimalSpawnLimit() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public WarningState getWarningState() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getViewDistance() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getVersion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public File getUpdateFolderFile() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getUpdateFolder() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getTicksPerMonsterSpawns() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getTicksPerAnimalSpawns() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getSpawnRadius() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getShutdownMessage() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServicesManager getServicesManager() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServerName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServerId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BukkitScheduler getScheduler() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Recipe> getRecipesFor(final ItemStack result) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getPort() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public PluginManager getPluginManager() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public PluginCommand getPluginCommand(final String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Player getPlayerExact(final String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Player getPlayer(final String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<OfflinePlayer> getOperators() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Player[] getOnlinePlayers() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean getOnlineMode() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public OfflinePlayer[] getOfflinePlayers() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public OfflinePlayer getOfflinePlayer(final String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getMotd() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMonsterSpawnLimit() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Messenger getMessenger() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMaxPlayers() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public MapView getMap(final short id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Logger getLogger() {
				return Logger.getAnonymousLogger();
			}

			@Override
			public ItemFactory getItemFactory() {
				return CraftItemFactory.instance();
			}

			@Override
			public String getIp() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<String> getIPBans() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HelpMap getHelpMap() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean getGenerateStructures() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public GameMode getDefaultGameMode() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConsoleCommandSender getConsoleSender() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getConnectionThrottle() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Map<String, String[]> getCommandAliases() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBukkitVersion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<OfflinePlayer> getBannedPlayers() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getAnimalSpawnLimit() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getAmbientSpawnLimit() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean getAllowNether() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean getAllowFlight() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean getAllowEnd() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean dispatchCommand(final CommandSender sender,
					final String commandLine) throws CommandException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public World createWorld(final WorldCreator creator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MapView createMap(final World world) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Inventory createInventory(final InventoryHolder owner,
					final int size, final String title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Inventory createInventory(final InventoryHolder owner,
					final int size) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Inventory createInventory(final InventoryHolder owner,
					final InventoryType type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void configureDbConfig(final ServerConfig config) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearRecipes() {
				// TODO Auto-generated method stub

			}

			@Override
			public int broadcastMessage(final String message) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int broadcast(final String message, final String permission) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void banIP(final String address) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addRecipe(final Recipe recipe) {
				// TODO Auto-generated method stub
				return false;
			}
		});

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ExtendedConfiguration.setClassLoader(this.getClass().getClassLoader());
		file = new File("test.yml");
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		conf.add("test", "blah");
		conf.createSection("yatta").set("test", "blah");
		conf.save();
		getClass().getClassLoader().loadClass(
				"net.minecraft.server.Enchantment");

	}

	@Test
	public void loadTest() {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		assertEquals("blah", conf.get("test"));
		assertFalse(conf.add("test", "test"));
		assertEquals("blah", conf.get("test"));
	}

	@Test
	public void serializeMaterialContainerTest() throws IOException,
			InvalidConfigurationException, EnchantmentConflictException,
			CantEnchantItemException {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		final MaterialContainer mat = new MaterialContainer(new ItemStack(
				Material.DIAMOND_AXE, 5));
		conf.set("serial.item", mat);
		conf.save();
		conf.reload();
		assertEquals(mat, conf.get("serial.item"));
	}

	@Test
	public void serializeKitInstanceTest() throws IOException,
			InvalidConfigurationException, EnchantmentConflictException,
			CantEnchantItemException {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		final MaterialContainer mat = new MaterialContainer(new ItemStack(
				Material.DIAMOND_AXE, 5));
		final List<MaterialContainer> mats = new ArrayList<MaterialContainer>();
		mats.add(mat);
		final KitInstance kit = new KitInstance("Test", 0, mats);
		conf.set("serial.kit", kit);
		conf.save();
		conf.reload();
		assertEquals(kit, conf.get("serial.kit"));
	}

	@Test
	public void serializeAmoredKitInstanceTest() throws IOException,
			InvalidConfigurationException, EnchantmentConflictException,
			CantEnchantItemException {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		final MaterialContainer mat = new MaterialContainer(new ItemStack(
				Material.DIAMOND_AXE, 5));
		final List<MaterialContainer> mats = new ArrayList<MaterialContainer>();
		mats.add(mat);
		final Map<ArmorPart, MaterialContainer> armor = new HashMap<ArmorPart, MaterialContainer>();
		armor.put(ArmorPart.BOOTS, new MaterialContainer(new ItemStack(
				Material.LEATHER_BOOTS)));
		final ArmoredKitInstance kit = new ArmoredKitInstance("Test", 0, mats,
				armor);
		kit.setColor("blue");
		conf.set("serial.armKit", kit);
		conf.save();
		conf.reload();
		assertEquals(kit, conf.get("serial.armKit"));
	}

	@Test
	public void serializeAdminCmdTest() throws IOException,
			InvalidConfigurationException {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		conf.set("serial.banPlayer", new BannedPlayer("Test", "testing"));
		conf.save();
		conf.reload();
		assertEquals("Test",
				((BannedPlayer) conf.get("serial.banPlayer")).getPlayer());
	}

	@After
	public void tearDown() throws Exception {
		file.deleteOnExit();
	}

}
