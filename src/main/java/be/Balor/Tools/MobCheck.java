/************************************************************************
 * This file is part of SpawnMob.																								
 ************************************************************************/
package be.Balor.Tools;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

/**
 * @author jordanneil23
 * 
 */
public class MobCheck {
	public static boolean isMonster(final Entity e) {
		return (e instanceof Creeper) || (e instanceof Monster) || (e instanceof Skeleton)
				|| (e instanceof Spider) || (e instanceof Zombie) || (e instanceof PigZombie)
				|| (e instanceof Ghast) || (e instanceof Giant) || (e instanceof Slime)
				|| (e instanceof EnderDragon);
	}

	public static boolean isAnimal(final Entity e) {
		return (e instanceof Chicken) || (e instanceof Cow) || (e instanceof Sheep)
				|| (e instanceof Squid) || (e instanceof Pig) || (e instanceof Wolf)
				|| (e instanceof Animals);
	}

	public static boolean isNPC(final LivingEntity e) {
		return (e instanceof Villager);
	}

	public static boolean isZombie(final LivingEntity e) {
		return (e instanceof Zombie);
	}

	public static boolean Monster(final LivingEntity e) {
		return (e instanceof Monster);
	}

	public static boolean isCreeper(final LivingEntity e) {
		return (e instanceof Creeper);
	}

	public static boolean isSkeleton(final LivingEntity e) {
		return (e instanceof Skeleton);
	}

	public static boolean isSpider(final LivingEntity e) {
		return (e instanceof Spider);
	}

	public static boolean isPigZombie(final LivingEntity e) {
		return (e instanceof PigZombie);
	}

	public static boolean isGiant(final LivingEntity e) {
		return (e instanceof Giant);
	}

	public static boolean isSlime(final LivingEntity e) {
		return (e instanceof Slime);
	}

	public static boolean isGhast(final LivingEntity e) {
		return (e instanceof Ghast);
	}

	public static boolean isCow(final LivingEntity e) {
		return (e instanceof Cow);
	}

	public static boolean isPig(final LivingEntity e) {
		return (e instanceof Pig);
	}

	public static boolean isSheep(final LivingEntity e) {
		return (e instanceof Sheep);
	}

	public static boolean isChicken(final LivingEntity e) {
		return (e instanceof Chicken);
	}

	public static boolean isWolf(final LivingEntity e) {
		return (e instanceof Wolf);
	}

	public static boolean isSquid(final LivingEntity e) {
		return (e instanceof Squid);
	}

	public static boolean isEnderDragon(final LivingEntity e) {
		return (e instanceof EnderDragon);
	}
}
