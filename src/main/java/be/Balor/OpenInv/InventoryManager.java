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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerInventory;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.Filters.DatFilter;
import be.Balor.World.ACWorld;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class InventoryManager {
	public static InventoryManager INSTANCE;
	private final Map<String, ACPlayerInventory> replacedInv = new MapMaker()
			.makeMap();
	private final Map<String, ACPlayerInventory> offlineInv = new MapMaker()
			.makeMap();

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
		final ACPlayerInventory inv = replacedInv.get(name);
		if (inv == null) {
			return;
		}
		if (inv.getViewers().isEmpty()) {
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
		ACPlayerInventory inv = offlineInv.get(name);
		if (inv == null) {
			inv = replacedInv.get(name);
		}
		if (inv == null) {
			return;
		}
		final CraftPlayer cp = (CraftPlayer) p;
		final PlayerInventory mcInv = ((CraftInventoryPlayer) cp.getInventory())
				.getInventory();
		mcInv.items = inv.items;
		mcInv.armor = inv.armor;

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
	public void openOfflineInv(final Player sender, final String name,
			final String world) throws PlayerNotFound {
		Player target = null;
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", name);
		// Offline inv here...
		// See if the player has data files

		// Find the player folder
		final ACWorld acworld = ACWorld.getWorld(world);
		final File playerfolder = new File(acworld.getHandler()
				.getWorldFolder(), "players");
		if (!playerfolder.exists()) {
			throw new PlayerNotFound(Utils.I18n("playerNotFound", replace),
					sender);
		}

		final String playername = matchUser(
				Arrays.asList(DatFilter.INSTANCE.listRecursively(playerfolder)),
				name);
		if (playername == null) {
			throw new PlayerNotFound(Utils.I18n("playerNotFound", replace),
					sender);
		}

		// Create an entity to load the player data
		final MinecraftServer server = ((CraftServer) Bukkit.getServer())
				.getServer();
		final EntityPlayer entity = new EntityPlayer(server,
				server.getWorldServer(0), playername, new ItemInWorldManager(
						server.getWorldServer(0)));
		target = (entity == null) ? null : (Player) entity.getBukkitEntity();
		if (target != null) {
			target.loadData();
		} else {
			throw new PlayerNotFound(Utils.I18n("playerNotFound", replace),
					sender);
		}
		if (Utils.checkImmunity(sender, target)) {
			openInv(sender, target, true);
		} else {
			Utils.sI18n(sender, "insufficientLvl");
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

	private void openInv(final Player sender, final Player target,
			final boolean offline) {
		final ACPlayerInventory inventory = getInventory(target, offline);
		final EntityPlayer eh = ((CraftPlayer) sender).getHandle();
		eh.openContainer(inventory);
	}

	private ACPlayerInventory getInventory(final Player player,
			final boolean offline) {
		ACPlayerInventory inventory = replacedInv.get(player.getName());
		if (inventory == null) {
			if (offline) {
				inventory = new ACOfflinePlayerInventory(player);
				offlineInv.put(player.getName(), inventory);
			} else {
				inventory = new ACPlayerInventory(player);
				replacedInv.put(player.getName(), inventory);
			}
		}
		return inventory;

	}

	private String matchUser(final Collection<File> container,
			final String search) {
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
