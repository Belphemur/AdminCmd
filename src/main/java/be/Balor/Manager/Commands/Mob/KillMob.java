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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Villager;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Compatibility.ACMinecraftReflection;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

import com.google.common.base.Joiner;

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
	public void execute(final CommandSender sender, final CommandArgs args) throws ActionNotPermitedException, PlayerNotFound {
		final HashMap<String, String> replace = new HashMap<String, String>();
		final List<String> types = new ArrayList<String>();
		Integer range = ConfigEnum.MK_DEF_RADIUS.getInt();
		range *= range;
		if (args.hasFlag('r')) {
			range = Integer.parseInt(args.getValueFlag('r'));
			range *= range;
		}
		final String worldString = args.getValueFlag('w');
		if (args.length >= 1) {
			for (final String type : args) {
				types.add(type);
			}
		} else {
			types.add("all");
		}
		replace.put("type", Joiner.on(", ").join(types));

		final List<World> worldList = new ArrayList<World>();

		if (Users.isPlayer(sender, false)) {
			World w = ((Player) sender).getWorld();
			if (worldString != null) {
				w = getWorld(worldString);
			}
			worldList.add(w);
			replace.put("worlds", w.getName());
			LocaleManager.sI18n(sender, "killMob", replace);
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
					LocaleManager.sI18n(sender, "killMob", replace);
				}
			}
		}
		final CommandSender finalSender = sender;
		final Integer finalRange = range;
		ACPluginManager.getScheduler().runTaskAsynchronously(ACPluginManager.getCorePlugin(), new Runnable() {
			@Override
			public void run() {
				killMobs(worldList, types, finalSender, finalRange);
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

	@SuppressWarnings("unchecked")
	private void killMobs(final List<World> worlds, final List<String> types, final CommandSender sender, final Integer range) {
		int mobKilled = 0;
		final List<Class<? extends Entity>> classes = new ArrayList<Class<? extends Entity>>();
		if (!types.contains("all")) {
			for (final String type : types) {
				if (type.equalsIgnoreCase("monsters")) {
					classes.add(Monster.class);
					classes.add(Snowman.class);
					classes.add(ComplexLivingEntity.class);
					if (ConfigEnum.MK_V_G_KILL.getBoolean()) {
						classes.add(Golem.class);
					}
				} else if (type.equalsIgnoreCase("animals")) {
					classes.add(Animals.class);
				} else {
					EntityType ct = null;
					ct = EntityType.fromName(type);
					if (ct == null) {
						final HashMap<String, String> replace = new HashMap<String, String>();
						replace.put("mob", type);
						LocaleManager.sI18n(sender, "errorMob", replace);
						continue;
					}
					classes.add(ct.getEntityClass());
				}
			}
		} else {
			classes.add(Monster.class);
			classes.add(Animals.class);
			classes.add(ComplexLivingEntity.class);
			classes.add(Snowman.class);
			if (ConfigEnum.MK_V_G_KILL.getBoolean()) {
				classes.add(Golem.class);
				classes.add(Villager.class);
			}
		}

		final Class<Entity>[] array = (Class<Entity>[]) Array.newInstance(Entity.class.getClass(), classes.size());
		for (final World w : worlds) {
			for (final Entity m : w.getEntitiesByClasses(classes.toArray(array))) {
				if (killEntity(m, sender, range)) {
					mobKilled++;
				}
			}
		}

		LocaleManager.sI18n(sender, "killedMobs", "nbKilled", String.valueOf(mobKilled));
	}

	public static boolean killEntity(final Entity e, final CommandSender sender, final Integer range) {
		if (!checkKillCondition(e, sender, range)) {
			return false;
		}
		final Object entity = ACMinecraftReflection.getHandle(e);
		try {
			final MethodHandler die = new MethodHandler(entity.getClass(), "die");
			die.invoke(entity);
		} catch (final RuntimeException e2) {
			try {
				final MethodHandler die = new MethodHandler(entity.getClass(), "setDead");
				die.invoke(entity);
			} catch (final Exception e3) {
				e.remove();
			}
		}

		return true;
	}

	/**
	 * Check if the entity can be killed with the giving conditions
	 * 
	 * @param toCheck
	 *            entity to be checked
	 * @param sender
	 *            sender of the command
	 * @param range
	 *            distance between the entity and the player accepted for a
	 *            kill.
	 * @return
	 */
	public static boolean checkKillCondition(final Entity toCheck, final CommandSender sender, final Integer range) {
		boolean result = true;
		if (range != null && range != -1 && Users.isPlayer(sender, false)) {
			result = toCheck.getLocation().distanceSquared(((Player) sender).getLocation()) <= range;
		}
		return result;
	}
}
