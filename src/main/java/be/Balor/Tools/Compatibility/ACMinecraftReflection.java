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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.AbstractFuzzyMatcher;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyFieldContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyMatchers;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyMethodContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyReflection;

/**
 * @author Antoine
 * 
 */
public class ACMinecraftReflection extends MinecraftReflection {

	/**
	 * 
	 */
	public static final AbstractFuzzyMatcher<Class<?>> INVENTORY_REGEX = FuzzyMatchers.matchRegex("(net\\.minecraft(\\.\\w+)+)((.+|)Inventory(.+|))", 50);
	public static final FuzzyFieldContract INVENTORY_CONTRACT = FuzzyFieldContract.newBuilder().typeMatches(INVENTORY_REGEX).build();
	public static final FuzzyFieldContract INVENTORY_ITEMSTACK_CONTRACT = FuzzyFieldContract.newBuilder().typeExact(getItemStackArrayClass()).build();

	/**
	 * 
	 */
	protected ACMinecraftReflection() {
	}

	/**
	 * Retrieve the NMS PlayerInventory class.
	 * 
	 * @return The PlayerInventory class.
	 */
	public static Class<?> getPlayerInventoryClass() {
		try {
			return getMinecraftClass("PlayerInventory");
		} catch (final RuntimeException e) {
			final Field inv = FieldUtils.getMatchedField(getEntityPlayerClass(), INVENTORY_CONTRACT);
			return setMinecraftClass("PlayerInventory", inv.getType());
		}

	}

	/**
	 * Retrieve the NMS IInventory class.
	 * 
	 * @return The IInventory class.
	 */
	public static Class<?> getIInventoryClass() {
		try {
			return getMinecraftClass("IInventory");
		} catch (final RuntimeException e) {
			final Class<?> playerInventory = getPlayerInventoryClass();
			return setMinecraftClass("IInventory", playerInventory.getInterfaces()[0]);
		}

	}

	/**
	 * Retrieve the NMS EntityHuman class.
	 * 
	 * @return The EntityHuman class.
	 */
	public static Class<?> getEntityHumanClass() {
		return getMinecraftClass("EntityHuman");
	}

	/**
	 * Retrieve the CraftBukkit CraftHumanEntity class.
	 * 
	 * @return The CraftHumanEntity class.
	 */
	public static Class<?> getCraftHumanEntityClass() {
		return getCraftBukkitClass("entity.CraftHumanEntity");
	}

	/**
	 * Get the NMS inventory of the given player
	 * 
	 * @param p
	 *            - player
	 * @return {@link PlayerInventory}
	 */
	public static Object getInventory(final Player p) {
		return FieldUtils.getAttribute(ACMinecraftReflection.getHandle(p), INVENTORY_CONTRACT);
	}

	/**
	 * Get the NMS Server
	 * 
	 * @return instance of MinecraftServer
	 */
	public static Object getMinecraftServer() {
		final Server server = Bukkit.getServer();
		try {
			return server.getClass().getMethod("getServer").invoke(server);
		} catch (final Exception e) {
			throw new RuntimeException("Can't get server  from " + server, e);
		}
	}

	/**
	 * Determine if the given object is a NMS object of the given class
	 * 
	 * @param obj
	 *            - the given object.
	 * @param clazz
	 *            - name of the class to test
	 * @return TRUE if it is, FALSE otherwise.
	 */
	public static boolean instanceOfNMS(final Object obj, final String clazz) {
		return getMinecraftClass(clazz).isAssignableFrom(obj.getClass());
	}

	/**
	 * Get the Handeling Object from a Bukkit Object
	 * 
	 * @param bukkitObject
	 *            - bukkit object
	 * @return an Handler on success.
	 * @throws RuntimeException
	 *             If we were unable to retrieve the entity.
	 */
	public static Object getHandle(final @Nonnull Object bukkitObject) {
		if (bukkitObject == null) {
			return null;
		}

		// We will have to do this dynamically, unfortunately
		try {
			return bukkitObject.getClass().getMethod("getHandle").invoke(bukkitObject);
		} catch (final Exception e) {
			try {
				return FieldUtils.getAttribute(bukkitObject, "handle");
			} catch (final Exception e1) {
				throw new RuntimeException("Cannot get Handle from " + bukkitObject, e1);
			}
		}
	}

	/**
	 * Get the NetServerHandler of the wanted player
	 * 
	 * @param player
	 *            - CraftPlayer
	 * @return the networkManager
	 * @throws RuntimeException
	 *             If we were unable to retrieve the entity.
	 */
	public static Object getNetServerHandler(final Player player) {
		final Field field = FuzzyReflection.fromClass(getEntityPlayerClass(), true).getFieldByType("NetServerHandler", getNetServerHandlerClass());
		return FieldUtils.readField(field, getHandle(player), true);
	}

	/**
	 * Teleport the wanted player to the given location
	 * 
	 * @param player
	 *            - Bukkit player
	 * @param toLocation
	 *            - Location
	 * @throws RuntimeException
	 *             If we were unable to retrieve the entity.
	 */
	public static void teleportPlayer(final Player player, final Location toLocation) {
		final Object networkManager = getNetServerHandler(player);
		try {
			final MethodHandler mh = new MethodHandler(getNetServerHandlerClass(), FuzzyMethodContract.newBuilder().parameterCount(1)
					.parameterExactType(Location.class, 0).build());
			mh.invoke(networkManager, toLocation);
		} catch (final Exception e) {
			throw new RuntimeException("Can't teleport the player " + player + " to " + toLocation, e);
		}
	}

	public static Class<?> getNMSWorldClass() {
		try {
			return getMinecraftClass("World");

		} catch (final RuntimeException e) {
			final Class<?> normalChunkGenerator = getCraftBukkitClass("generator.NormalChunkGenerator");

			// ChunkPosition a(net.minecraft.server.World world, String string,
			// int i, int i1, int i2) {
			final FuzzyMethodContract selected = FuzzyMethodContract.newBuilder().banModifier(Modifier.STATIC).parameterMatches(getMinecraftObjectMatcher(), 0)
					.parameterExactType(String.class, 1).parameterExactType(int.class, 2).parameterExactType(int.class, 3).parameterExactType(int.class, 4)
					.build();

			return setMinecraftClass("World", FuzzyReflection.fromClass(normalChunkGenerator).getMethod(selected).getParameterTypes()[0]);
		}
	}

	public static Class<?> getItemInWorldManagerClass() {
		try {
			return getMinecraftClass("ItemInWorldManager", "PlayerInteractManager");
		} catch (final Exception e) {
			// public EntityPlayerMP(MinecraftServer par1MinecraftServer, World
			// par2World, String par3Str, ItemInWorldManager
			// par4ItemInWorldManager)

			final FuzzyMethodContract selected = FuzzyMethodContract.newBuilder().parameterExactType(getMinecraftServerClass(), 0)
					.parameterExactType(getNMSWorldClass(), 1).parameterExactType(String.class, 2).parameterMatches(getMinecraftObjectMatcher(), 3).build();
			return setMinecraftClass("ItemInWorldManager", FuzzyReflection.fromClass(getEntityPlayerClass()).getConstructor(selected).getParameterTypes()[3]);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> T getBukkitEntityCasted(final Object nmsObject) {
		return (T) ACMinecraftReflection.getBukkitEntity(nmsObject);
	}

	/**
	 * Send the wanted packet to the player
	 * 
	 * @param player
	 *            player to receive the packet
	 * @param packet
	 *            packet to send
	 */
	public static void sendPacket(final Player player, final Object packet) {
		if (!getPacketClass().isAssignableFrom(packet.getClass())) {
			throw new RuntimeException(packet.getClass() + " is not a derived class of " + getPacketClass());
		}
		final MethodHandler mh = new MethodHandler(getNetServerHandlerClass(), FuzzyMethodContract.newBuilder().parameterExactType(getPacketClass(), 0)
				.parameterCount(1).nameRegex("(func_72567_b|sendPacket|sendPacketToPlayer)").build());
		mh.invoke(getNetServerHandler(player), packet);

	}
}
