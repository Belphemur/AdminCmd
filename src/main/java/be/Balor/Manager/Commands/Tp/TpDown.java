/** **********************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *********************************************************************** */
package be.Balor.Manager.Commands.Tp;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public class TpDown extends TeleportCommand {

        public TpDown() {
                permNode = "admincmd.tp.down";
                cmdName = "bal_down";
        }

        @Override
        public void execute(CommandSender sender, CommandArgs args) throws ActionNotPermitedException, PlayerNotFound {
                if (!Users.isPlayer(sender)) return;
                final Player player = Users.getUserParam(sender, args, permNode);

                Location loc = player.getLocation();
                Location target = loc.clone();
                Location target2 = loc.clone();

                boolean found = false;

                Location underFeet = loc.getBlock().getRelative(BlockFace.DOWN, 2).getLocation();

                boolean air = (underFeet.getBlock().getType() == Material.AIR);

                if (air) {
                        for (int y = underFeet.getBlockY() - 1; y >= 0; y--) {
                                target.setY(y);
                                target2.setY(y + 1);
                                Location target3 = target2.clone();
                                target3.setY(y + 2);

                                if (target.getBlock().getType() != Material.AIR && target2.getBlock().getType() == Material.AIR && target3.getBlock().getType() == Material.AIR) {
                                        found = true;
                                        target.setY(y + 1);
                                        break;
                                }
                        }
                } else {

                        for (int y = loc.getBlockY() - 2; y >= 0; y--) {
                                target.setY(y);
                                target2.setY(y + 1);

                                if (target.getBlock().getType() == Material.AIR && target2.getBlock().getType() == Material.AIR) {
                                        found = true;
                                        break;
                                }
                        }
                }

                if (found) {
                        ACPluginManager.scheduleSyncTask(new TeleportTask(player, target));
                        LocaleHelper.TP_DOWN_SUCCESS.sendLocale(sender);
                } else {
                        LocaleHelper.TP_NO_FREE_BLOCK.sendLocale(sender);
                }
        }

        @Override
        public boolean argsCheck(String... args) {
                return args != null;
        }

}
