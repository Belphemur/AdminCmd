package be.Balor.Manager.Commands.Items;

import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.MaterialContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class KitInstance {

        private String name = null;
        private int delay = 0;
        private List<MaterialContainer> items = null;
        private Map<String, Long> delays = new HashMap<String, Long>(); // <PlayerName, last used system time>

        public KitInstance(String name, int delay, List<MaterialContainer> items) {
                this.name = name;
                this.delay = delay;
                this.items = items;
        }  
    
    	/**
	 * Get ItemStacks for given kit
	 *
	 * @param kit
	 * @return
	 */
        public ArrayList<ItemStack> getItemStacks() {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		try {
                        //return Utils.oddItem.getItemGroup(kit, -1));		
		} catch (Throwable e) {
		}
		for (MaterialContainer mc : items)
                        result.add(mc.getItemStack());
		return result;
	}

        public void setDelays(Map<String, Long> delays) {
                this.delays = delays;
        }

        public int getDelay() {
                return delay;
        }

        public Long getLastUse(String name) {
                return delays.get(name) == null? 0:delays.get(name);
        }

        public void setLastUse(String playername, long systemtime) {
                delays.put(playername, systemtime);
                FileManager.getInstance().saveKitInstanceUse(name, playername, systemtime);
        }
}
