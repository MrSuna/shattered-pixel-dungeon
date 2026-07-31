/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RoyalTreasure;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Debug-build-only controls for exercising E.D.'s subclass talents, pain,
 * treasure and armor abilities without playing through an entire run.
 */
public class WndEDDebug extends WndOptions {

	private static final int BROKEN_CROWN = 0;
	private static final int USURPER = 1;
	private static final int HEROIC_LEAP = 2;
	private static final int SHOCKWAVE = 3;
	private static final int ENDURE = 4;
	private static final int HALF_HEALTH = 5;
	private static final int FILL_TREASURE = 6;
	private static final int RESTORE = 7;
	private static final int SAVE_TEST = 8;

	public WndEDDebug() {
		super(
				Messages.get(WndEDDebug.class, "title"),
				Messages.get(WndEDDebug.class, "desc"),
				Messages.get(WndEDDebug.class, "broken_crown"),
				Messages.get(WndEDDebug.class, "usurper"),
				Messages.get(WndEDDebug.class, "heroic_leap"),
				Messages.get(WndEDDebug.class, "shockwave"),
				Messages.get(WndEDDebug.class, "endure"),
				Messages.get(WndEDDebug.class, "half_health"),
				Messages.get(WndEDDebug.class, "fill_treasure"),
				Messages.get(WndEDDebug.class, "restore"),
				Messages.get(WndEDDebug.class, "save_test")
		);
	}

	@Override
	protected void onSelect(int index) {
		Hero hero = Dungeon.hero;
		if (hero == null) return;

		switch (index){
			case BROKEN_CROWN:
				prepareSubclass(hero, HeroSubClass.BROKEN_CROWN);
				break;
			case USURPER:
				prepareSubclass(hero, HeroSubClass.USURPER);
				break;
			case HEROIC_LEAP:
				prepareArmorAbility(hero, new HeroicLeap());
				break;
			case SHOCKWAVE:
				prepareArmorAbility(hero, new Shockwave());
				break;
			case ENDURE:
				prepareArmorAbility(hero, new Endure());
				break;
			case HALF_HEALTH:
				hero.HP = Math.max(1, hero.HT / 2);
				break;
			case FILL_TREASURE:
				RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
				if (treasure != null) treasure.fillForDebug();
				break;
			case RESTORE:
				hero.HP = hero.HT;
				if (hero.belongings.armor() instanceof ClassArmor){
					((ClassArmor)hero.belongings.armor()).charge = 100;
				}
				break;
			case SAVE_TEST:
				try {
					Dungeon.saveAll();
					GLog.p(Messages.get(WndEDDebug.class, "save_ok"));
				} catch (IOException e) {
					ShatteredPixelDungeon.reportException(e);
					GLog.n(Messages.get(WndEDDebug.class, "save_failed"));
				}
				break;
		}

		Item.updateQuickslot();
		if (hero.sprite != null) hero.sprite.update();
		GameScene.show(new WndEDDebug());
	}

	private static void prepareSubclass(Hero hero, HeroSubClass subClass){
		raiseToLevel(hero, 30);
		hero.subClass = subClass;
		hero.metamorphedTalents.clear();
		for (LinkedHashMap<Talent, Integer> tier : hero.talents){
			tier.clear();
		}
		Talent.initClassTalents(hero);
		Talent.initSubclassTalents(hero);
		Talent.initArmorTalents(hero);
		maxTalents(hero);
	}

	private static void prepareArmorAbility(Hero hero, ArmorAbility ability){
		raiseToLevel(hero, 30);
		Armor armor = hero.belongings.armor();
		if (armor != null && !(armor instanceof ClassArmor)){
			ClassArmor classArmor = ClassArmor.upgrade(hero, armor);
			hero.belongings.armor = classArmor;
			classArmor.activate(hero);
		}

		hero.armorAbility = ability;
		while (hero.talents.size() < Talent.MAX_TALENT_TIERS){
			hero.talents.add(new LinkedHashMap<>());
		}
		hero.talents.get(3).clear();
		Talent.initArmorTalents(hero);
		maxTalents(hero);

		if (hero.belongings.armor() instanceof ClassArmor){
			((ClassArmor)hero.belongings.armor()).charge = 100;
		}
		if (hero.sprite instanceof HeroSprite){
			((HeroSprite)hero.sprite).updateArmor();
		}
	}

	private static void raiseToLevel(Hero hero, int target){
		while (hero.lvl < target){
			hero.earnExp(hero.maxExp(), WndEDDebug.class);
		}
	}

	private static void maxTalents(Hero hero){
		for (LinkedHashMap<Talent, Integer> tier : hero.talents){
			for (Talent talent : new ArrayList<>(tier.keySet())){
				tier.put(talent, talent.maxPoints());
				Talent.onTalentUpgraded(hero, talent);
			}
		}
	}
}
