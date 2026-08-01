/*
 * Eternal Pixel Dungeon — E.D. T4 ability: Blood Debt Decree
 * Based on Shattered Pixel Dungeon (GPL-3.0).
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ed;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RoyalTreasure;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class BloodDebtDecree extends ArmorAbility {
	{
		baseChargeUse = 35f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public int targetedPos(Char user, int dst) {
		return new Ballistica(user.pos, dst, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET).collisionPos;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null) return;
		Char victim = Actor.findChar(targetedPos(hero, target));
		if (victim == null || victim.alignment != Char.Alignment.ENEMY) {
			GLog.w(Messages.get(this, "enemy_target"));
			return;
		}
		if (!Dungeon.level.heroFOV[victim.pos]) {
			GLog.w(Messages.get(this, "fov"));
			return;
		}

		hero.busy();
		armor.charge -= chargeUse(hero);
		Item.updateQuickslot();

		int markTurns = 8 + 2*hero.pointsInTalent(Talent.BODY_SLAM)
				+ hero.pointsInTalent(Talent.STRIKING_WAVE);
		Buff.prolong(victim, RoyalTreasure.DebtMark.class, markTurns);
		Buff.prolong(victim, Vulnerable.class, 4f + hero.pointsInTalent(Talent.IMPACT_WAVE));
		if (hero.pointsInTalent(Talent.IMPACT_WAVE) >= 2) {
			Buff.prolong(victim, Cripple.class, 2f + hero.pointsInTalent(Talent.IMPACT_WAVE));
		}

		hero.sprite.zap(victim.pos);
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.BLOOD_DEBT_DECREE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.BODY_SLAM, Talent.IMPACT_WAVE, Talent.DOUBLE_JUMP, Talent.HEROIC_ENERGY};
	}
}
