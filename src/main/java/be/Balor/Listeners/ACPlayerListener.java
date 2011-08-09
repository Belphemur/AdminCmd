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
package be.Balor.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;

import be.Balor.Manager.ACCommand;
import be.Balor.Manager.CommandManager;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.FilesManager;
import be.Balor.Tools.Type;
import be.Balor.Tools.ShootFireball;
import be.Balor.Tools.UpdateInvisible;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPlayerListener extends PlayerListener {

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (ACHelper.getInstance().isValueSet(Type.BANNED, event.getPlayer().getName())) {
			event.disallow(Result.KICK_BANNED,
					ACHelper.getInstance().getValue(Type.BANNED, event.getPlayer().getName())
							.toString());
			return;
		}
		if (PermissionManager.hasPerm(event.getPlayer(), "admincmd.player.bypass", false))
			event.allow();
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if ((Boolean) ACHelper.getInstance().getConfValue("autoAfk")) {
			AFKWorker.getInstance().updateTimeStamp(p);
			if (AFKWorker.getInstance().isAfk(p))
				AFKWorker.getInstance().setOnline(p);
		}
		if (ACHelper.getInstance().isValueSet(Type.FROZEN, p)) {
			event.setCancelled(true);
			return;
		}
		Float power = (Float) ACHelper.getInstance().getValue(Type.FLY, p.getName());
		if (power != null)
			if (p.isSneaking())
				p.setVelocity(p.getLocation().getDirection().multiply(power));
			else if ((Boolean) ACHelper.getInstance().getConfValue("glideWhenFallingInFlyMode")) {
				Vector vel = p.getVelocity();
				vel.add(p.getLocation().getDirection()
						.multiply(ACHelper.getInstance().getFloat("glinding.multiplicator"))
						.setY(0));
				if (vel.getY() < ACHelper.getInstance().getFloat("glinding.YvelocityCheckToGlide")) {
					vel.setY(ACHelper.getInstance().getFloat("glinding.newYvelocity"));
					p.setVelocity(vel);
				}
			}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Utils.sParsedLocale(p, "MOTD");
		Utils.sParsedLocale(p, "NEWS");
		if (playerRespawnOrJoin(event.getPlayer())) {
			event.setJoinMessage(null);
			Utils.sI18n(event.getPlayer(), "stillInv");
		}
		Configuration conf = FilesManager.getInstance().getYml(p.getName(), "home");
		if (conf.getBoolean("infos.firstTime", true)) {
			conf.setProperty("infos.firstTime", false);
			conf.save();
			if ((Boolean) ACHelper.getInstance().getConfValue("firstConnectionToSpawnPoint"))
				ACHelper.getInstance().spawn(p);
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (InvisibleWorker.getInstance().hasInvisiblePowers(p.getName()))
			event.setQuitMessage(null);
		if ((Boolean) ACHelper.getInstance().getConfValue("autoAfk")) {
			AFKWorker.getInstance().removePlayer(p);
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		playerRespawnOrJoin(event.getPlayer());

	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		String playername = event.getPlayer().getName();
		if (ACHelper.getInstance().isValueSet(Type.FROZEN, playername)) {
			event.setCancelled(true);
			return;
		}
		if ((Boolean) ACHelper.getInstance().getConfValue("resetPowerWhenTpAnotherWorld")
				&& !from.getWorld().equals(to.getWorld())
				&& !PermissionManager.hasPerm(event.getPlayer(), "admincmd.player.noreset", false)) {
			if (ACHelper.getInstance().removeKeyFromValues(playername)
					|| InvisibleWorker.getInstance().hasInvisiblePowers(playername)) {
				InvisibleWorker.getInstance().reappear(event.getPlayer());
				Utils.sI18n(event.getPlayer(), "changedWorld");
			}

		} else
			playerRespawnOrJoin(event.getPlayer());
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if ((Boolean) ACHelper.getInstance().getConfValue("autoAfk")) {
			AFKWorker.getInstance().updateTimeStamp(p);
			if (AFKWorker.getInstance().isAfk(p))
				AFKWorker.getInstance().setOnline(p);
		}
		if (ACHelper.getInstance().isValueSet(Type.FROZEN, p)) {
			event.setCancelled(true);
			return;
		}
		if (((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))) {
			String playerName = p.getName();
			if ((ACHelper.getInstance().hasThorPowers(playerName)))
				p.getWorld().strikeLightning(p.getTargetBlock(null, 600).getLocation());
			Float power = null;
			if ((power = ACHelper.getInstance().getVulcainExplosionPower(playerName)) != null)
				p.getWorld()
						.createExplosion(p.getTargetBlock(null, 600).getLocation(), power, true);
			power = null;
			if ((power = (Float) ACHelper.getInstance().getValue(Type.FIREBALL, playerName)) != null)
				ShootFireball.shoot(p, power);
		}
	}

	private boolean playerRespawnOrJoin(Player newPlayer) {
		AdminCmd.getBukkitServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
						new UpdateInvisibleOnJoin(newPlayer), 25);
		if (InvisibleWorker.getInstance().hasInvisiblePowers(newPlayer.getName())) {
			AdminCmd.getBukkitServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
							new UpdateInvisible(newPlayer), 25);
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		Player p = event.getPlayer();
		if ((Boolean) ACHelper.getInstance().getConfValue("autoAfk")) {
			AFKWorker.getInstance().updateTimeStamp(p);
			if (AFKWorker.getInstance().isAfk(p))
				AFKWorker.getInstance().setOnline(p);
		}
		if (ACHelper.getInstance().isValueSet(Type.MUTED, p)) {
			event.setCancelled(true);
			Utils.sI18n(p, "muteEnabled");
		}
	}

	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (ACHelper.getInstance().isValueSet(Type.NO_PICKUP, event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] split = event.getMessage().split("\\s+");
		if (split.length == 0)
			return;
		String cmdName = split[0].substring(1).toLowerCase();
		ACCommand cmd = CommandManager.getInstance().getCommand(cmdName);
		if (cmd != null) {
			event.setCancelled(true);
			System.out.print("[AdminCmd] Command " + cmdName + " intercepted.");
			CommandManager.getInstance().executeCommand(event.getPlayer(), cmd,
					Utils.Arrays_copyOfRange(split, 1, split.length));
			event.setMessage("/AdminCmd took the control");
		}
	}

	protected class UpdateInvisibleOnJoin implements Runnable {
		Player newPlayer;

		/**
		 * 
		 */
		public UpdateInvisibleOnJoin(Player p) {
			newPlayer = p;
		}

		public void run() {
			for (Player toVanish : InvisibleWorker.getInstance().getAllInvisiblePlayers())
				InvisibleWorker.getInstance().invisible(toVanish, newPlayer);
		}
	}

}
