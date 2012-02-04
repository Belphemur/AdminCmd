package be.Balor.Tools.Files;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Permissions.PermissionLinker;
import be.Balor.Tools.MaterialContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

public class KitInstance {

	private final String name;
	private int delay = 0;
	private final List<MaterialContainer> items;
	private static final PermissionLinker perm = ACPluginManager.getPluginInstance("Core")
			.getPermissionLinker();

	public KitInstance(String name, int delay, List<MaterialContainer> items) {
		this.name = name;
		this.delay = delay;
		this.items = items;
		perm.addPermChild("admincmd.kit." + this.name);
	}

	/**
	 * Get ItemStacks for given kit
	 * 
	 * @param kit
	 * @return
	 */
	public List<ItemStack> getItemStacks() {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		try {
			// return Utils.oddItem.getItemGroup(kit, -1));
		} catch (Throwable e) {
		}
		for (MaterialContainer mc : items)
			result.add(mc.getItemStack());
		return result;
	}

	/**
	 * Adding a parent to the kit
	 * 
	 * @param parent
	 */
	public void addParent(KitInstance parent) {
		for (MaterialContainer mc : parent.items) {
			int index = items.indexOf(mc);
			if (index == -1)
				items.add(mc);
			else {
				MaterialContainer mat = items.get(index);
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

}