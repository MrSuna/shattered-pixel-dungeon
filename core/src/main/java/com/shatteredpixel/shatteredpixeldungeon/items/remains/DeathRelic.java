/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RoyalTreasure;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

/** E.D.'s signature item, created in the remains of a fallen run. */
public class DeathRelic extends RemainsItem {

	{
		image = ItemSpriteSheet.DEATH_RELIC;
	}

	@Override
	protected void doEffect(Hero hero) {
		int recovered = RoyalTreasure.recoverFromDeathRelic(hero, 2 + hero.lvl/5);
		if (recovered > 0){
			int shield = 3 + recovered;
			Buff.affect(hero, Barrier.class).incShield(shield);
			GLog.p(Messages.get(this, "recovered", recovered, shield));
		} else {
			int shield = 5 + hero.lvl/5;
			Buff.affect(hero, Barrier.class).incShield(shield);
			GLog.p(Messages.get(this, "shielded", shield));
		}
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
	}
}
