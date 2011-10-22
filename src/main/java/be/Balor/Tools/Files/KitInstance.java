package be.Balor.Tools.Files;

import be.Balor.Tools.MaterialContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class KitInstance {

	private final String name;
	private int delay = 0;
	private final List<MaterialContainer> items;

	public KitInstance(String name, int delay, List<MaterialContainer> items) {
		this.name = name;
		this.delay = delay;
		this.items = items;
		ACPluginManager.getPluginInstance("Core").getPermissionLinker()
				.addOnTheFly("admincmd.kit." + name, "admincmd.kit.*");
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