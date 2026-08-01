/*
 * Eternal Pixel Dungeon — E.D. T4 ability: Nothing At All
 * Based on Shattered Pixel Dungeon (GPL-3.0).
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ed;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RoyalTreasure;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class NothingAtAll extends ArmorAbility {
	{
		baseChargeUse = 50f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
		if (treasure == null || treasure.charge < 1) {
			GLog.w(Messages.get(this, "no_treasure"));
			return;
		}

		int spent = (int)treasure.charge;
		treasure.charge = 0;
		treasure.updateQuickslot();
		float duration = 3f + spent/2f + hero.pointsInTalent(Talent.SUSTAINED_RETRIBUTION);
		Buff.prolong(hero, Nothingness.class, duration);
		if (hero.pointsInTalent(Talent.SHRUG_IT_OFF) > 0) {
			Buff.affect(hero, Barrier.class).incShield(2*hero.pointsInTalent(Talent.SHRUG_IT_OFF));
		}

		hero.sprite.operate(hero.pos);
		armor.charge -= chargeUse(hero);
		Item.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.NOTHING_AT_ALL;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SUSTAINED_RETRIBUTION, Talent.SHRUG_IT_OFF, Talent.EVEN_THE_ODDS, Talent.HEROIC_ENERGY};
	}

	public static class Nothingness extends FlavourBuff {
		{
			type = buffType.POSITIVE;
		}

		@Override
		public boolean act() {
			if (target == Dungeon.hero && Dungeon.level != null) {
				Hero hero = Dungeon.hero;
				int radius = 2 + hero.pointsInTalent(Talent.EVEN_THE_ODDS);
				for (Char ch : Actor.chars()) {
					if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(hero.pos, ch.pos) <= radius) {
						Buff.prolong(ch, RoyalTreasure.DebtMark.class, 2f);
						Buff.prolong(ch, Vulnerable.class, 2f);
					}
				}
			}
			spend(TICK);
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0.75f, 0.15f);
		}
	}
}
