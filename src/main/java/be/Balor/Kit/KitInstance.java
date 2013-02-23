package be.Balor.Kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Tools.MaterialContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

public class KitInstance {

	protected final String name;
	protected int delay = 0;
	protected final List<MaterialContainer> items;
	private static final PermParent perm;
	static {
		perm = new PermParent("admincmd.kit.*");
		ACPluginManager.getCorePlugin().getPermissionLinker()
				.addPermParent(perm);
	}

	public KitInstance(final String name, final int delay,
			final List<MaterialContainer> items) {
		this.name = name;
		this.delay = delay;
		this.items = items;
		perm.addChild("admincmd.kit." + this.name);
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
			items.add(new MaterialContainer(item));
		}
		perm.addChild("admincmd.kit." + this.name);
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

}