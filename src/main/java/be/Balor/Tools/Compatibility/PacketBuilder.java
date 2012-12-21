package be.Balor.Tools.Compatibility;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

public class PacketBuilder {

	/**
	 * Create a Packet201PlayerInfo
	 * 
	 * @param player
	 *            - Bukkit Player
	 * @param online
	 *            - online or offline
	 * @param ping
	 *            - ping
	 * @return
	 */
	public static Object buildPacket201PlayerInfo(final Player player,
			final boolean online, final int ping) {
		final Object playerHandler = MinecraftReflection.getHandle(player);
		try {
			final String listName = ClassUtils.getPrivateField(playerHandler,
					"listName");
			final Class<?> packetClass = MinecraftReflection
					.getMinecraftClass("Packet201PlayerInfo");
			final Constructor<?> packetConstructor = packetClass
					.getConstructor(String.class, boolean.class, int.class);
			return packetConstructor.newInstance(listName, online, ping);
		} catch (final Exception e) {
			throw new RuntimeException("Can't create the wanted packet", e);
		}
	}
}
