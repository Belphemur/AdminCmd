/************************************************************************
 * This file is part of SpawnMob.																								
 ************************************************************************/
package be.Balor.Tools;

import org.bukkit.entity.*;
import org.bukkit.entity.LivingEntity;

/**
 * @author jordanneil23
 * 
 */
public class MobCheck {
	public static boolean isMonster(LivingEntity e) {
		return (e instanceof Creeper) || (e instanceof Monster) || (e instanceof Skeleton)
				|| (e instanceof Spider) || (e instanceof Zombie) || (e instanceof PigZombie)
				|| (e instanceof Ghast) || (e instanceof Giant) || (e instanceof Slime);
	}

	public static boolean isAnimal(LivingEntity e) {
		return (e instanceof Chicken) || (e instanceof Cow) || (e instanceof Sheep)
				|| (e instanceof Squid) || (e instanceof Pig)|| (e instanceof Wolf) ;
	}

	public static boolean isZombie(LivingEntity e) {
		return (e instanceof Zombie);
	}

	public static boolean Monster(LivingEntity e) {
		return (e instanceof Monster);
	}

	public static boolean isCreeper(LivingEntity e) {
		return (e instanceof Creeper);
	}

	public static boolean isSkeleton(LivingEntity e) {
		return (e instanceof Skeleton);
	}

	public static boolean isSpider(LivingEntity e) {
		return (e instanceof Spider);
	}

	public static boolean isPigZombie(LivingEntity e) {
		return (e instanceof PigZombie);
	}

	public static boolean isGiant(LivingEntity e) {
		return (e instanceof Giant);
	}

	public static boolean isSlime(LivingEntity e) {
		return (e instanceof Slime);
	}

	public static boolean isGhast(LivingEntity e) {
		return (e instanceof Ghast);
	}

	public static boolean isCow(LivingEntity e) {
		return (e instanceof Cow);
	}

	public static boolean isPig(LivingEntity e) {
		return (e instanceof Pig);
	}

	public static boolean isSheep(LivingEntity e) {
		return (e instanceof Sheep);
	}

	public static boolean isChicken(LivingEntity e) {
		return (e instanceof Chicken);
	}

	public static boolean isWolf(LivingEntity e) {
		return (e instanceof Wolf);
	}

	public static boolean isSquid(LivingEntity e) {
		return (e instanceof Squid);
	}
}
