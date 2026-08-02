/*
 * Eternal Pixel Dungeon — E.D. T4 ability: Empty Throne
 * Based on Shattered Pixel Dungeon (GPL-3.0).
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ed;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
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
import com.watabou.noosa.Image;

public class EmptyThrone extends ArmorAbility {
	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		Buff.prolong(hero, ThroneField.class, 8f);
		hero.sprite.operate(hero.pos);
		armor.charge -= chargeUse(hero);
		Item.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.EMPTY_THRONE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.EXPANDING_WAVE, Talent.STRIKING_WAVE, Talent.SHOCK_FORCE, Talent.HEROIC_ENERGY};
	}

	public static class ThroneField extends FlavourBuff {
		{
			type = buffType.POSITIVE;
		}

		@Override
		public boolean act() {
			if (target == Dungeon.hero && Dungeon.level != null) {
				Hero hero = Dungeon.hero;
				int radius = 2 + hero.pointsInTalent(Talent.EXPANDING_WAVE);
				for (Char ch : Actor.chars()) {
					if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(hero.pos, ch.pos) <= radius) {
						Buff.prolong(ch, RoyalTreasure.DebtMark.class,
								2f + hero.pointsInTalent(Talent.STRIKING_WAVE));
						Buff.prolong(ch, Vulnerable.class, 2f);
						int shockForce = hero.pointsInTalent(Talent.SHOCK_FORCE);
						if (shockForce >= 3) {
							Buff.prolong(ch, Paralysis.class, shockForce >= 4 ? 2f : 1f);
						} else if (shockForce > 0) {
							Buff.prolong(ch, Cripple.class, shockForce >= 2 ? 3f : 2f);
						}
					}
				}
			}
			spend(TICK);
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.25f, 0.45f, 1f);
		}
	}
}
