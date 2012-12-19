/************************************************************************
 * ThMobCheck.is file MobCheck.is part of AdminCmd.
 *
 * AdminCmd MobCheck.is free software: you can redMobCheck.istribute it and/or modify
 * it under the terms of the GNU General Public License as publMobCheck.ished by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd MobCheck.is dMobCheck.istributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Manager.Commands.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityMonster;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KillMob extends MobCommand {

	/**
	 *
	 */
	public KillMob() {
		permNode = "admincmd.mob.kill";
		cmdName = "bal_killmob";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final HashMap<String, String> replace = new HashMap<String, String>();
		String type = "all";
		if (args.length >= 1) {
			type = args.getString(0);
		}
		replace.put("type", type);
		final String worldString = args.getValueFlag('w');
		final List<World> worldList = new ArrayList<World>();

		if (Utils.isPlayer(sender, false)) {
			World w = ((Player) sender).getWorld();
			if (worldString != null) {
				w = getWorld(worldString);
			}
			worldList.add(w);
			replace.put("worlds", w.getName());
			Utils.sI18n(sender, "killMob", replace);
		} else {
			if (worldString != null) {
				worldList.add(getWorld(worldString));
			} else {
				String worlds = "";
				for (final World world : sender.getServer().getWorlds()) {
					worldList.add(world);
					worlds += world.getName() + ", ";
				}
				if (!worlds.equals("")) {
					if (worlds.endsWith(", ")) {
						worlds = worlds.substring(0, worlds.lastIndexOf(","));
					}
					replace.put("worlds", worlds);
					Utils.sI18n(sender, "killMob", replace);
				}
			}
		}
		final String finalType = type;
		final CommandSender finalSender = sender;
		ACPluginManager.getScheduler().runTaskAsynchronously(
				ACPluginManager.getCorePlugin(), new Runnable() {
					@Override
					public void run() {
						killMobs(worldList, finalType, finalSender);
					}
				});

	}

	private World getWorld(final String name) {
		final World w = Bukkit.getWorld(name);
		if (w == null) {
			throw new WorldNotLoaded("The World " + name + " is not loaded");
		}
		return w;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

	private void killMobs(final List<World> worlds, final String type,
			final CommandSender sender) {
		int mobKilled = 0;
		if (type.equalsIgnoreCase("all")) {
			for (final World w : worlds) {
				for (final LivingEntity m : w.getLivingEntities()) {
					if (m instanceof HumanEntity) {
						continue;
					}
					final Entity entity = ((CraftLivingEntity) m).getHandle();
					entity.die();
					mobKilled++;

				}
			}
		} else if (type.equalsIgnoreCase("monsters")) {
			for (final World w : worlds) {
				for (final LivingEntity m : w.getLivingEntities()) {
					if (m instanceof EntityMonster) {
						final Entity entity = ((CraftLivingEntity) m)
								.getHandle();
						entity.die();
						mobKilled++;
					}
				}
			}
		} else if (type.equalsIgnoreCase("animals")) {
			for (final World w : worlds) {
				for (final LivingEntity m : w.getLivingEntities()) {
					if (m instanceof EntityAnimal) {
						final Entity entity = ((CraftLivingEntity) m)
								.getHandle();
						entity.die();
						mobKilled++;
					}
				}
			}
		} else {
			EntityType ct = null;
			ct = EntityType.fromName(type);
			if (ct == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("mob", type);
				Utils.sI18n(sender, "errorMob", replace);
				return;
			}
			for (final World w : worlds) {
				for (final org.bukkit.entity.Entity m : w
						.getEntitiesByClasses(ct.getEntityClass())) {
					final Entity entity = ((CraftEntity) m).getHandle();
					entity.die();
					mobKilled++;
				}
			}
		}
		Utils.sI18n(sender, "killedMobs", "nbKilled", String.valueOf(mobKilled));
	}
}
