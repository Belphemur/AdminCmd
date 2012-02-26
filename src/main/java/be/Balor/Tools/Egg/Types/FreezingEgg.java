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
package be.Balor.Tools.Egg.Types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.SimplifiedLocation;
import be.Balor.Tools.SynchronizedStack;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Blocks.BlockRemanence;
import be.Balor.Tools.Blocks.BlockRemanenceFactory;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FreezingEgg extends EggType<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6781269132940660439L;
	private int eggNb = 0;
	private final Map<Integer, SynchronizedStack<BlockRemanence>> blocksPerEvent = Collections
			.synchronizedMap(new HashMap<Integer, SynchronizedStack<BlockRemanence>>());

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#onEvent(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(PlayerEggThrowEvent event) {
		final int radius = value / 2;
		final Integer eventId = eggNb++;
		event.getEgg().remove();
		event.setHatching(false);
		Location loc = event.getEgg().getLocation();
		SynchronizedStack<BlockRemanence> blocks = new SynchronizedStack<BlockRemanence>();
		World w = loc.getWorld();
		for (int x = loc.getBlockX() - radius; x < loc.getBlockX() + radius; x++)
			for (int z = loc.getBlockZ() - radius; z < loc.getBlockZ() + radius; z++)
				for (int y = loc.getBlockY() - radius; y < loc.getBlockY() + radius; y++) {
					int blckId = w.getBlockTypeIdAt(x, y, z);
					if (blckId != Material.AIR.getId() && blckId != Material.SNOW.getId())
						continue;
					BlockRemanence blk = BlockRemanenceFactory.FACTORY
							.createBlockRemanence(new SimplifiedLocation(w, x, y, z));
					blk.setBlockType(Material.ICE.getId());
					blocks.add(blk);

				}
		blocksPerEvent.put(eventId, blocks);
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(ACPluginManager.getCorePlugin(),
				new Runnable() {

					@Override
					public void run() {
						SynchronizedStack<BlockRemanence> blocks = blocksPerEvent.get(eventId);
						while (!blocks.empty())
							blocks.pop().returnToThePast();
						blocksPerEvent.remove(eventId);

					}
				}, ConfigEnum.FREEZE_TIMEOUT.getInt() * Utils.secInTick);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(org.bukkit.entity.Player,
	 * be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	protected void processArguments(Player sender, CommandArgs args) throws ProcessingArgsException {
		int radius = ConfigEnum.DFREEZE_RADIUS.getInt();
		String valFlag = args.getValueFlag('r');
		if (valFlag != null)
			try {
				radius = Integer.parseInt(valFlag);
			} catch (NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", valFlag);
				return;
			}
		value = radius;

	}

}
