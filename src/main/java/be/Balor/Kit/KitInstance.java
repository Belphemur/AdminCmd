package be.Balor.Kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Tools.MaterialContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

public class KitInstance implements ConfigurationSerializable {

	protected final String name;
	protected int delay = 0;
	protected final List<MaterialContainer> items;
	private final PermChild permission;
	private static final PermParent perm;
	static {
		perm = new PermParent("admincmd.kit.*");
		if (ACPluginManager.getCorePlugin() != null) {
			ACPluginManager.getCorePlugin().getPermissionLinker()
					.addPermParent(perm);
		}
	}

	public KitInstance(final String name, final int delay,
			final List<MaterialContainer> items) {
		this.name = name;
		this.delay = delay;
		this.items = items;
		permission = new PermChild("admincmd.kit." + this.name);
		perm.addChild(permission);
	}

	/**
	 * @param name
	 * @param delay
	 */
	public KitInstance(final String name, final int delay, final Player player) {
		super();
		this.name = name;
		this.delay = delay;
		this.items = new ArrayList<MaterialContainer>();
		for (final ItemStack item : player.getInventory()) {
			if (item == null) {
				continue;
			}
			items.add(new MaterialContainer(item));
		}
		permission = new PermChild("admincmd.kit." + this.name);
		perm.addChild(permission);
	}

	/**
	 * Check if the sender have the permission to use the kit.
	 * 
	 * @param sender
	 * @return
	 */
	public boolean canUse(final CommandSender sender) {
		return permission.hasPermission(sender);
	}

	/**
	 * Get ItemStacks for given kit
	 * 
	 * @param kit
	 * @return
	 */
	public List<ItemStack> getItemStacks() {
		final ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		for (final MaterialContainer mc : items) {
			result.add(mc.getItemStack());
		}
		return result;
	}

	/**
	 * Adding a parent to the kit
	 * 
	 * @param parent
	 */
	public void addParent(final KitInstance parent) {
		for (final MaterialContainer mc : parent.items) {
			final int index = items.indexOf(mc);
			if (index == -1) {
				items.add(mc);
			} else {
				final MaterialContainer mat = items.get(index);
				mat.setAmount(mat.getAmount() + mc.getAmount());
			}
		}
	}

	public int getDelay() {
		return delay;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KitInstance [name=" + name + ", delay=" + delay + ", items="
				+ Arrays.toString(items.toArray()) + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.configuration.serialization.ConfigurationSerializable#serialize
	 * ()
	 */
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> serialized = new LinkedHashMap<String, Object>();
		serialized.put("name", name);
		serialized.put("delay", delay);
		serialized.put("items", items);
		return serialized;
	}

	/**
	 * Required method for configuration serialization
	 * 
	 * @param args
	 *            map to deserialize
	 * @return deserialized item stack
	 * @see ConfigurationSerializable
	 */
	@SuppressWarnings("unchecked")
	public static KitInstance deserialize(final Map<String, Object> args) {
		if (!args.containsKey("name")) {
			return null;
		}
		final String name = (String) args.get("name");
		int delay = 0;
		List<MaterialContainer> items = new ArrayList<MaterialContainer>();
		if (args.containsKey("delay")) {
			delay = (Integer) args.get("delay");
		}
		if (args.containsKey("items")) {
			items = (List<MaterialContainer>) args.get("items");
		}
		return new KitInstance(name, delay, items);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delay;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final KitInstance other = (KitInstance) obj;
		if (delay != other.delay) {
			return false;
		}
		if (items == null) {
			if (other.items != null) {
				return false;
			}
		} else if (!items.equals(other.items)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}