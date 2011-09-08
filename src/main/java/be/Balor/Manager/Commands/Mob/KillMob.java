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
import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.MobCheck;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KillMob extends CoreCommand {

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
	public void execute(CommandSender sender, CommandArgs args) {
		final List<LivingEntity> mobs = new ArrayList<LivingEntity>();
		HashMap<String, String> replace = new HashMap<String, String>();
		String type = "all";
		if (args.length >= 1)
			type = args.getString(0);
		replace.put("type", type);

		if (Utils.isPlayer(sender, false)) {
			mobs.addAll(((Player) sender).getWorld().getLivingEntities());
			replace.put("worlds", ((Player) sender).getWorld().getName());
			Utils.sI18n(sender, "killMob", replace);
		} else {
			String worlds = "";
			for (World w : sender.getServer().getWorlds()) {
				mobs.addAll(w.getLivingEntities());
				worlds += w.getName() + ", ";
			}
			if (!worlds.equals("")) {
				if (worlds.endsWith(", "))
					worlds = worlds.substring(0, worlds.lastIndexOf(","));
				replace.put("worlds", worlds);
				Utils.sI18n(sender, "killMob", replace);
			}
		}
		final String finalType = type;
		final CommandSender finalSender = sender;
		pluginCommand.getPlugin().getServer().getScheduler()
				.scheduleAsyncDelayedTask(pluginCommand.getPlugin(), new Runnable() {

					public void run() {
						killMobs(mobs, finalType,finalSender);
					}
				});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

	private void killMobs(List<LivingEntity> mobs, String type, CommandSender sender) {
		int mobKilled = 0;
		for (Iterator<?> iterator = mobs.iterator(); iterator.hasNext();) {
			LivingEntity m = (LivingEntity) iterator.next();
			if (m instanceof HumanEntity) {
				continue;				
			}
			if (type.equalsIgnoreCase("all") && (MobCheck.isAnimal(m) || MobCheck.isMonster(m))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isAnimal(m) && (type.equalsIgnoreCase("animals"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isMonster(m) && (type.equalsIgnoreCase("monsters"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isZombie(m) && (type.equalsIgnoreCase("zombie"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isCreeper(m) && (type.equalsIgnoreCase("creeper"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isSkeleton(m) && (type.equalsIgnoreCase("skeleton"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isSpider(m) && (type.equalsIgnoreCase("spider"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isGhast(m) && (type.equalsIgnoreCase("ghast"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isSlime(m) && (type.equalsIgnoreCase("slime"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isGiant(m) && (type.equalsIgnoreCase("giant"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isPigZombie(m) && (type.equalsIgnoreCase("pigzombie"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.Monster(m) && (type.equalsIgnoreCase("pigzombie"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isSheep(m) && (type.equalsIgnoreCase("sheep"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isChicken(m) && (type.equalsIgnoreCase("chicken"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isSquid(m) && (type.equalsIgnoreCase("squid"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isCow(m) && (type.equalsIgnoreCase("cow"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isPig(m) && (type.equalsIgnoreCase("pig"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
			if (MobCheck.isWolf(m) && (type.equalsIgnoreCase("wolf"))) {
				m.setHealth(0);
				mobKilled++;
				continue;
			}
		}
		Utils.sI18n(sender, "killedMobs", "nbKilled", String.valueOf(mobKilled));
	}

}
