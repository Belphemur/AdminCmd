/**
 * **********************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * AdminCmd is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 * **********************************************************************
 */
package be.Balor.Manager.Commands.Player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Help.String.Str;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author JeterLP
 *
 */
public class RemoveStatusEffects extends PlayerCommand {

    private final static List<String> potions = new ArrayList<String>();

    static {
        for (final PotionEffectType type : PotionEffectType.values()) {
            if (type != null && type.getName() != null) {
                potions.add(type.getName());
            }
        }
    }

    /**
     *
     */
    public RemoveStatusEffects() {
        permNode = "admincmd.player.rse";
        cmdName = "bal_rse";
        other = true;
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

        final Player target = Users.getUserParam(sender, args, permNode);
        if (removeEffect(target, args)) {
            LocaleHelper.EFFECT_REMOVE_SUCCESS.sendLocale(sender, "effect", args.getString(0));
        } else {
            LocaleHelper.ERROR_EFFECT.sendLocale(sender, "effect", args.getString(0));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
     */
    @Override
    public boolean argsCheck(final String... args) {
        return args.length != 0;
    }

    public boolean removeEffect(final Player target, final CommandArgs args) {
        final String potion = args.getString(0);

        if (potion == null){
            return false;
        }
        
        if (potion.equalsIgnoreCase("all")) {
            for (PotionEffect effect : target.getActivePotionEffects()) {
                target.removePotionEffect(effect.getType());
            }
            return true;

        } else {

            PotionEffectType type = null;

            try {
                int id = Integer.parseInt(potion);
                type = PotionEffectType.getById(id);
            } catch (NumberFormatException e) {
            }

            if (type == null) {
                final String potionFound = Str.matchString(potions, potion);
                if (potionFound == null) {
                    return false;
                }
                type = PotionEffectType.getByName(potionFound);
            }

            if (target.hasPotionEffect(type)) {
                target.removePotionEffect(type);
                return true;
            }
            return false;
        }
    }
}
