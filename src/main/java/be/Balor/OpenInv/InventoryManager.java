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
package be.Balor.OpenInv;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.CommandUtils.Immunity;
import be.Balor.Tools.Compatibility.ACMinecraftReflection;
import be.Balor.Tools.Compatibility.NMSBuilder;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyMethodContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyReflection;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.Filters.DatFilter;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class InventoryManager {
	public static InventoryManager INSTANCE;
	private final Map<String, Object> replacedInv = new MapMaker().makeMap();
	private final Map<String, Object> offlineInv = new MapMaker().makeMap();

	/**
 * 
 */
	private InventoryManager() {
	}

	public static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new InventoryManager();
		}
	}

	public void onQuit(final Player p) {
		final String name = p.getName();
		final Object inv = replacedInv.get(name);
		if (inv == null) {
			return;
		}
		final MethodHandler getViewers = new MethodHandler(inv.getClass(), "getViewers");
		final Object viewers = getViewers.invoke(inv);
		final MethodHandler isEmpty = new MethodHandler(viewers.getClass(), "isEmpty");
		final boolean empty = isEmpty.invoke(viewers);
		if (empty) {
			replacedInv.remove(name);
		}
	}

	void closeOfflineInv(final Player p) {
		onQuit(p);
		offlineInv.remove(p.getName());
		p.saveData();
		DebugLog.INSTANCE.info("Saving Offline Inventory of " + p.getName());
	}

	public void onJoin(final Player p) {
		final String name = p.getName();
		Object inv = offlineInv.get(name);
		if (inv == null) {
			inv = replacedInv.get(name);
		}
		if (inv == null) {
			return;
		}
		setPlayerItemArmor(p, inv);

	}

	/**
	 * @param p
	 * @param inv
	 */
	private void setPlayerItemArmor(final Player p, final Object inv) {
		final PlayerInventoryProxy proxy = (PlayerInventoryProxy) Proxy.getInvocationHandler(inv);
		final Object inventory = ACMinecraftReflection.getInventory(p);
		final Object[] armor = proxy.getArmor();
		final Object[] items = proxy.getItems();
		setInventoryArmorItems(inventory, armor, items);
	}

	/**
	 * @param inventory
	 * @param armor
	 * @param items
	 */
	public static void setInventoryArmorItems(final Object inventory, final Object[] armor, final Object[] items) {
		final List<Field> fieldList = FuzzyReflection.fromObject(inventory).getFieldList(ACMinecraftReflection.INVENTORY_ITEMSTACK_CONTRACT);
		for (final Field field : fieldList) {
			try {
				final Object[] array = (Object[]) field.get(inventory);
				if (array.length == 4) {
					FieldUtils.setAttribute(inventory, armor, field);
				} else if (array.length == 36) {
					FieldUtils.setAttribute(inventory, items, field);
				}
			} catch (final Exception e) {
				throw new RuntimeException("Can't set armor and items of player ", e);
			}
		}
	}

	/**
	 * Open the inventory of an offline player
	 * 
	 * @param sender
	 * @param name
	 * @throws PlayerNotFound
	 * @author lishd {@link https
	 *         ://github.com/lishd/OpenInv/blob/master/src/lishid
	 *         /openinv/commands/OpenInvPluginCommand.java}
	 */
	public void openOfflineInv(final Player sender, final String name, final String world) throws PlayerNotFound {
		Player target = null;
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", name);
		// Offline inv here...
		// See if the player has data files

		// Find the player folder
		ACWorld acworld = null;
		try {
			acworld = ACWorld.getWorld(world);
		} catch (final WorldNotLoaded e) {
			replace.put("message", world);
			LocaleHelper.WORLD_NOT_LOADED.sendLocale(sender, replace);
			return;
		}
		final File playerfolder = new File(acworld.getHandle().getWorldFolder(), "players");
		if (!playerfolder.exists()) {
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace), sender);
		}

		final String playername = matchUser(Arrays.asList(DatFilter.INSTANCE.listRecursively(playerfolder)), name);
		if (playername == null) {
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace), sender);
		}

		// Create an entity to load the player data
		final Object entity = NMSBuilder.buildEntityPlayer(playername);
		if (entity == null) {
			target = null;
		} else {
			target = ACMinecraftReflection.getBukkitEntityCasted(entity);
		}
		if (target != null) {
			target.loadData();
		} else {
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace), sender);
		}
		if (Immunity.checkImmunity(sender, target)) {
			openInv(sender, target, true);
		} else {
			LocaleManager.sI18n(sender, "insufficientLvl");
		}
	}

	/**
	 * Open the inventory of the connected player
	 * 
	 * @param sender
	 *            the user who'll see the inventory
	 * @param target
	 *            player to have his inventory opened
	 */
	public void openInv(final Player sender, final Player target) {
		openInv(sender, target, false);
	}

	private void openInv(final Player sender, final Player target, final boolean offline) {
		final Object inventory = getInventory(target, offline);
		final Object eh = ACMinecraftReflection.getHandle(sender);
		final FuzzyMethodContract contract = FuzzyMethodContract.newBuilder().parameterExactType(ACMinecraftReflection.getIInventoryClass(), 0)
				.returnTypeVoid().build();
		final MethodHandler openContainer = new MethodHandler(eh.getClass(), contract);
		openContainer.invoke(eh, inventory);
	}

	private Object getInventory(final Player player, final boolean offline) {
		Object inventory = replacedInv.get(player.getName());
		if (inventory == null) {
			final Object playerInv = NMSBuilder.buildPlayerInventory(player);
			if (offline) {
				inventory = OfflinePlayerInventoryProxy.newInstance(player, playerInv);
				offlineInv.put(player.getName(), inventory);
			} else {
				inventory = PlayerInventoryProxy.newInstance(player, playerInv);
				replacedInv.put(player.getName(), inventory);
			}
		}
		return inventory;

	}

	private String matchUser(final Collection<File> container, final String search) {
		String found = null;
		if (search == null) {
			return found;
		}
		final String lowerSearch = search.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (final File file : container) {
			final String filename = file.getName();
			final String str = filename.substring(0, filename.length() - 4);
			if (!str.toLowerCase().startsWith(lowerSearch)) {
				continue;
			}
			final int curDelta = str.length() - lowerSearch.length();
			if (curDelta < delta) {
				found = str;
				delta = curDelta;
			}
			if (curDelta == 0) {
				break;
			}

		}
		return found;

	}

}
