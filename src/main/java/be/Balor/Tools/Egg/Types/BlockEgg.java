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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.SimplifiedLocation;
import be.Balor.Tools.SynchronizedStack;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Blocks.BlockRemanence;
import be.Balor.Tools.Blocks.IBlockRemanenceFactory;
import be.Balor.Tools.Egg.BlockChangeInfo;
import be.Balor.Tools.Egg.RadiusEgg;
import be.Balor.Tools.Egg.Exceptions.ExceptionType;
import be.Balor.Tools.Egg.Exceptions.ParameterMissingException;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BlockEgg extends RadiusEgg<BlockChangeInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1046757929821301999L;
	private int eggNb = 0;
	private final Map<Integer, SynchronizedStack<BlockRemanence>> blocksPerEvent = Collections
			.synchronizedMap(new HashMap<Integer, SynchronizedStack<BlockRemanence>>());

	/**
	 * 
	 */
	public BlockEgg() {
		super(ConfigEnum.DEGG_BLOCK_RADIUS.getInt(),
				ConfigEnum.MAXEGG_BLOCK_RADIUS.getInt());
	}

	/**
	 * @param defaultRadius
	 * @param maxRadius
	 */
	public BlockEgg(final int defaultRadius, final int maxRadius) {
		super(defaultRadius, maxRadius);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#onEvent(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(final PlayerEggThrowEvent event) {
		final int radius = value.getRadius() / 2;
		event.getEgg().remove();
		event.setHatching(false);
		final Location loc = event.getEgg().getLocation();
		final SynchronizedStack<BlockRemanence> blocks = new SynchronizedStack<BlockRemanence>();
		final World w = loc.getWorld();
		if (!Material.getMaterial(value.getBlockTypeId()).isBlock()) {
			LocaleHelper.NOT_A_BLOCK.sendLocale(event.getPlayer());
			return;
		}
		if (blockTimeOut() == 0) {
			for (int x = loc.getBlockX() - radius; x < loc.getBlockX() + radius; x++) {
				for (int z = loc.getBlockZ() - radius; z < loc.getBlockZ()
						+ radius; z++) {
					for (int y = loc.getBlockY() - radius; y < loc.getBlockY()
							+ radius; y++) {
						final Block block = w.getBlockAt(x, y, z);
						if (block.getTypeId() != Material.AIR.getId()
								&& block.getTypeId() != Material.SNOW.getId()) {
							continue;
						}
						IBlockRemanenceFactory.FACTORY.createBlockRemanence(
								new SimplifiedLocation(w, x, y, z))
								.setBlockType(value.getBlockTypeId());
					}
				}
			}
			return;
		}
		final Integer eventId = eggNb++;
		for (int x = loc.getBlockX() - radius; x < loc.getBlockX() + radius; x++) {
			for (int z = loc.getBlockZ() - radius; z < loc.getBlockZ() + radius; z++) {
				for (int y = loc.getBlockY() - radius; y < loc.getBlockY()
						+ radius; y++) {
					final int blckId = w.getBlockTypeIdAt(x, y, z);
					if (blckId != Material.AIR.getId()
							&& blckId != Material.SNOW.getId()) {
						continue;
					}
					final BlockRemanence blk = IBlockRemanenceFactory.FACTORY
							.createBlockRemanence(new SimplifiedLocation(w, x,
									y, z));
					blk.setBlockType(value.getBlockTypeId());
					blocks.add(blk);

				}
			}
		}
		blocksPerEvent.put(eventId, blocks);
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACPluginManager.getCorePlugin(), new Runnable() {

					@Override
					public void run() {
						final SynchronizedStack<BlockRemanence> blocks = blocksPerEvent
								.get(eventId);
						while (!blocks.empty()) {
							blocks.pop().returnToThePast();
						}
						blocksPerEvent.remove(eventId);

					}
				}, blockTimeOut() * Utils.secInTick);

	}

	protected int blockTimeOut() {
		return ConfigEnum.EGG_BLOCK_TIMEOUT.getInt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(org.bukkit.entity.Player,
	 * be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	protected void processArguments(final Player sender, final CommandArgs args)
			throws ProcessingArgsException {
		final String block = args.getValueFlag('b');
		if (block == null) {
			throw new ParameterMissingException('b',
					LocaleHelper.EGG_PARAM_BLOCK.getLocale());
		}
		final int radius = getRadius(sender, args);
		if (radius == -1) {
			return;
		}
		final MaterialContainer mat = ACHelper.getInstance().checkMaterial(
				sender, block);
		if (mat.isNull()) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("type", LocaleHelper.TYPE_MAT.getLocale());
			replace.put("value", block);
			throw new ProcessingArgsException(ExceptionType.CUSTOM,
					LocaleHelper.DONT_EXISTS.getLocale(replace));
		}
		value = new BlockChangeInfo(mat.getMaterial().getId(), radius);

	}

}
