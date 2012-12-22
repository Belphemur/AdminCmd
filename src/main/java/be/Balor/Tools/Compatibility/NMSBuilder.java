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

import net.minecraft.server.Packet201PlayerInfo;

import org.bukkit.entity.Player;

import be.Balor.Tools.Compatibility.Reflect.FieldUtils;

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
	 * @return {@link Packet201PlayerInfo}
	 */
	public static Object buildPacket201PlayerInfo(final Player player,
			final boolean online, final int ping) {
		final Object playerHandle = MinecraftReflection.getHandle(player);
		try {
			final String listName = FieldUtils.getField(playerHandle,
					"listName");
			final Class<?> packetClass = MinecraftReflection
					.getPacket201PlayerInfoClass();
			final Constructor<?> packetConstructor = packetClass
					.getConstructor(String.class, boolean.class, int.class);
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
	 * @return {@link PlayerInventory}
	 */
	public static Object buildPlayerInventory(final Player player) {
		final Object playerHandle = MinecraftReflection.getHandle(player);
		try {
			final Class<?> playerInventoryClass = MinecraftReflection
					.getPlayerInventoryClass();
			final Constructor<?> invConstructor = playerInventoryClass
					.getConstructor(MinecraftReflection.getEntityHumanClass());
			return invConstructor.newInstance(playerHandle);
		} catch (final Exception e) {
			throw new RuntimeException("Can't build PlayerInventory", e);
		}
	}
}
