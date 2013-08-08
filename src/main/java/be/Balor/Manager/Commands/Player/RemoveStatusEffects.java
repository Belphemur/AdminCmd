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
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import org.bukkit.potion.PotionEffectType;

/**
 * @author JeterLP
 *
 */
public class RemoveStatusEffects extends PlayerCommand {

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
        if (!Users.isPlayer(sender)) {
            return;
        }

        final Player player = (Player) sender;
        final Player target = Users.getUser(sender, args, permNode);
        if(removeEffect(target, player, args)) {
            LocaleHelper.EFFECT_REMOVE_SUCCESS.sendLocale(sender, "effect", args.getString(1));
        } else {
            LocaleHelper.ERROR_EFFECT.sendLocale(sender, "effect", args.getString(1));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
     */
    @Override
    public boolean argsCheck(final String... args) {
        return args.length == 2;
    }

    public boolean removeEffect(final Player target, final CommandSender sender, final CommandArgs args) {
        if (args.getString(1).equalsIgnoreCase("1") || args.getString(1).equalsIgnoreCase("speed")) {
            if (target.hasPotionEffect(PotionEffectType.SPEED)) {
                target.removePotionEffect(PotionEffectType.SPEED);
                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("2") || args.getString(1).equalsIgnoreCase("slowness")) {
            if (target.hasPotionEffect(PotionEffectType.SLOW)) {
                target.removePotionEffect(PotionEffectType.SLOW);
                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("3") || args.getString(1).equalsIgnoreCase("haste")) {
            if (target.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                target.removePotionEffect(PotionEffectType.FAST_DIGGING);
                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("4") || args.getString(1).equalsIgnoreCase("mining_fatigue")) {
            if (target.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                target.removePotionEffect(PotionEffectType.SLOW_DIGGING);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("5") || args.getString(1).equalsIgnoreCase("strength")) {
            if (target.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                target.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("6") || args.getString(1).equalsIgnoreCase("instant_health")) {
            if (target.hasPotionEffect(PotionEffectType.HEAL)) {
                target.removePotionEffect(PotionEffectType.HEAL);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("7") || args.getString(1).equalsIgnoreCase("instant_damage")) {
            if (target.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                target.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("8") || args.getString(1).equalsIgnoreCase("jump_boost")) {
            if (target.hasPotionEffect(PotionEffectType.JUMP)) {
                target.removePotionEffect(PotionEffectType.JUMP);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("9") || args.getString(1).equalsIgnoreCase("nausea")) {
            if (target.hasPotionEffect(PotionEffectType.CONFUSION)) {
                target.removePotionEffect(PotionEffectType.CONFUSION);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("11") || args.getString(1).equalsIgnoreCase("regeneration")) {
            if (target.hasPotionEffect(PotionEffectType.REGENERATION)) {
                target.removePotionEffect(PotionEffectType.REGENERATION);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("11") || args.getString(1).equalsIgnoreCase("resistance")) {
            if (target.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("12") || args.getString(1).equalsIgnoreCase("fire_resistance")) {
            if (target.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                target.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("13") || args.getString(1).equalsIgnoreCase("water_breathing")) {
            if (target.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
                target.removePotionEffect(PotionEffectType.WATER_BREATHING);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("14") || args.getString(1).equalsIgnoreCase("invisibility")) {
            if (target.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                target.removePotionEffect(PotionEffectType.INVISIBILITY);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("15") || args.getString(1).equalsIgnoreCase("blindness")) {
            if (target.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                target.removePotionEffect(PotionEffectType.BLINDNESS);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("16") || args.getString(1).equalsIgnoreCase("night_vision")) {
            if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                target.removePotionEffect(PotionEffectType.NIGHT_VISION);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("17") || args.getString(1).equalsIgnoreCase("hunger")) {
            if (target.hasPotionEffect(PotionEffectType.HUNGER)) {
                target.removePotionEffect(PotionEffectType.HUNGER);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("18") || args.getString(1).equalsIgnoreCase("weakness")) {
            if (target.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                target.removePotionEffect(PotionEffectType.WEAKNESS);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("19") || args.getString(1).equalsIgnoreCase("poison")) {
            if (target.hasPotionEffect(PotionEffectType.POISON)) {
                target.removePotionEffect(PotionEffectType.POISON);

            } else {
            }
        } else if (args.getString(1).equalsIgnoreCase("21") || args.getString(1).equalsIgnoreCase("wither")) {
            if (target.hasPotionEffect(PotionEffectType.WITHER)) {
                target.removePotionEffect(PotionEffectType.WITHER);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("21") || args.getString(1).equalsIgnoreCase("health_boost")) {
            if (target.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
                target.removePotionEffect(PotionEffectType.HEALTH_BOOST);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("22") || args.getString(1).equalsIgnoreCase("absorption")) {
            if (target.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                target.removePotionEffect(PotionEffectType.ABSORPTION);

                return true;
            }
            return false;
        } else if (args.getString(1).equalsIgnoreCase("23") || args.getString(1).equalsIgnoreCase("saturation")) {
            if (target.hasPotionEffect(PotionEffectType.SATURATION)) {
                target.removePotionEffect(PotionEffectType.SATURATION);

                return true;
            }
            return false;
        }
        return false;
    }
}
