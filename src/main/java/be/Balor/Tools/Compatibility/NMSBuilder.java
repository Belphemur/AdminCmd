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
package be.Balor.Tools.Compatibility;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.entity.Player;

import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;

public class NMSBuilder {

	/**
	 * Create a Packet201PlayerInfo
	 * 
	 * @param player
	 *            - Bukkit Player
	 * @param online
	 *            - online or offline
	 * @param ping
	 *            - ping
	 * @return instance of Packet201PlayerInfo
	 */
	public static Object buildPacket201PlayerInfo(final Player player, final boolean online, final int ping) {
		final Object playerHandle = ACMinecraftReflection.getHandle(player);
		try {
			final String listName = FieldUtils.getField(playerHandle, "listName");
			final Class<?> packetClass = ACMinecraftReflection.getPacket201PlayerInfoClass();
			final Constructor<?> packetConstructor = packetClass.getConstructor(String.class, boolean.class, int.class);
			return packetConstructor.newInstance(listName, online, ping);
		} catch (final Exception e) {
			throw new RuntimeException("Can't create the wanted packet", e);
		}
	}

	/**
	 * Build a NMS PlayerInventory for the given Bukkit Player.
	 * 
	 * @param player
	 *            - bukkit player
	 * @return instance of PlayerInventory
	 */
	public static Object buildPlayerInventory(final Player player) {
		final Object playerHandle = ACMinecraftReflection.getHandle(player);
		try {
			final Class<?> playerInventoryClass = ACMinecraftReflection.getPlayerInventoryClass();
			final Constructor<?> invConstructor = playerInventoryClass.getConstructor(ACMinecraftReflection.getEntityHumanClass());
			return invConstructor.newInstance(playerHandle);
		} catch (final Exception e) {
			throw new RuntimeException("Can't build PlayerInventory", e);
		}
	}

	/**
	 * Build a FAKE entity player
	 * 
	 * @param name
	 *            - name of the player
	 * @return instance NMS of EntityPlayer
	 */
	public static Object buildEntityPlayer(final String name) {
		final Object minecraftServer = ACMinecraftReflection.getMinecraftServer();
		try {
			Object worldServer = null;
			try {
				final MethodHandler getWorldServer = new MethodHandler(minecraftServer.getClass(), "getWorldServer", int.class);
				worldServer = getWorldServer.invoke(minecraftServer, 0);
			} catch (final RuntimeException rex) {
				final List<Object> worlds = FieldUtils.getField(minecraftServer, "worlds");
				worldServer = worlds.get(0);
			}
			final Class<?> entityPlayer = MinecraftReflection.getEntityPlayerClass();
			final Object itemInWorldManager = buildItemInWorldManager(worldServer);
			final Constructor<?> ePConstructor = entityPlayer.getConstructor(ACMinecraftReflection.getMinecraftServerClass(),
					ACMinecraftReflection.getNMSWorldClass(), String.class, ACMinecraftReflection.getItemInWorldManagerClass());
			return ePConstructor.newInstance(minecraftServer, worldServer, name, itemInWorldManager);

		} catch (final Exception e) {
			throw new RuntimeException("Can't build PlayerInventory", e);
		}
	}

	/**
	 * Build a ItemInWorldManager
	 * 
	 * @param world
	 * @return return instance of ItemInWorldManager
	 */
	public static Object buildItemInWorldManager(final Object world) {
		if (!ACMinecraftReflection.getNMSWorldClass().isAssignableFrom(world.getClass())) {
			throw new RuntimeException("The constructor need a " + ACMinecraftReflection.getNMSWorldClass().getSimpleName());
		}
		final Class<?> clazz = ACMinecraftReflection.getItemInWorldManagerClass();
		try {
			final Constructor<?> constructor = clazz.getConstructor(ACMinecraftReflection.getNMSWorldClass());
			return constructor.newInstance(world);
		} catch (final Exception e) {
			try {
				final Constructor<?> constructor = clazz.getConstructor(MinecraftReflection.getWorldServerClass());
				return constructor.newInstance(world);

			} catch (final Exception e2) {
				throw new RuntimeException("Can't build ItemInWorldManager", e2);
			}
		}

	}
}
