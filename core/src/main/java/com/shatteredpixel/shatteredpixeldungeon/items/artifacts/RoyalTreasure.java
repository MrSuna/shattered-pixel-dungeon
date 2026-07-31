/*
 * 永恒的像素地牢 — E.D. 专属神器「独一无二的王之宝藏」
 * 基于 Shattered Pixel Dungeon (GPL-3.0)，本文件同样以 GPL-3.0 发布。
 * 放置到: core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/RoyalTreasure.java
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class RoyalTreasure extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_ROYAL_TREASURE;

		levelCap = 10;

		charge = 0;        // charge = 掠夺物数量
		chargeCap = 10;    // 显示用；实际上限见 capacity()

		unique = true;
		bones = false;

		defaultAction = "NONE";
	}

	// ===== 核心数值（想调平衡改这里） =====

	// 掠夺物上限：基础 10，「贪婪宝库」每级 +2
	public int capacity(){
		int cap = 10;
		if (Dungeon.hero != null) cap += 2*Dungeon.hero.pointsInTalent(Talent.GREEDY_VAULT);
		return cap;
	}

	// 苦痛 = 已损失生命比例 (0~1)
	public static float pain(Hero hero){
		return 1f - (hero.HP / (float)hero.HT);
	}

	// 王之威压：苦痛 >= 0.25 起效，满苦痛基础 +50% 伤害，神器每级再 +5%
	// 掠夺：每件掠夺物 +2% 伤害 ×(1+苦痛)；「血之贡赋」每级再 ×1.25
	public static int empowerDamage(Hero hero, int dmg){
		RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
		if (treasure == null || !treasure.isEquipped(hero)) return dmg;

		float p = pain(hero);
		float mult = 1f;
		if (p >= 0.25f){
			mult += p * 0.5f * (1f + 0.05f * treasure.level());
		}
		float plunderBonus = treasure.charge * 0.02f * (1f + p);
		plunderBonus *= 1f + 0.25f * hero.pointsInTalent(Talent.BLOOD_TRIBUTE);
		mult += plunderBonus;
		return Math.round(dmg * mult);
	}

	// 掠夺：英雄的攻击将杀死敌人时调用（Hero.attackProc 挂钩）
	public static void tryPlunder(Hero hero, Char enemy, int damage){
		if (enemy == hero || enemy.alignment != Char.Alignment.ENEMY) return;
		if (damage < enemy.HP) return; // 不是致命一击

		RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
		if (treasure == null || !treasure.isEquipped(hero)) return;

		treasure.chargeCap = treasure.capacity();
		if (treasure.charge < treasure.chargeCap){
			treasure.charge++;
			treasure.exp++;
			GLog.p( Messages.get(RoyalTreasure.class, "plundered", treasure.charge, treasure.chargeCap) );
			if (treasure.exp >= 5 && treasure.level() < treasure.levelCap){
				treasure.exp = 0;
				treasure.upgrade();
				GLog.p( Messages.get(RoyalTreasure.class, "levelup") );
			}
			treasure.updateQuickslot();
		}

		// 痛觉觉醒（破碎王冠）：每次掠夺回复 1/2/3 生命
		if (hero.hasTalent(Talent.PAINBORN) && hero.HP < hero.HT){
			int heal = hero.pointsInTalent(Talent.PAINBORN);
			hero.HP = Math.min(hero.HP + heal, hero.HT);
		}

		// 僭主税（僭主）：每次击杀获得 2/4/6 护盾
		if (hero.hasTalent(Talent.TYRANTS_TOLL)){
			Buff.affect(hero, Barrier.class).incShield(2*hero.pointsInTalent(Talent.TYRANTS_TOLL));
		}
	}

	// 荆棘王冠（破碎王冠）：苦痛≥50% 时，反弹近战伤害的 10/20/30%（Hero.defenseProc 挂钩）
	public static void onDefenseProc(Hero hero, Char enemy, int damage){
		if (damage <= 0 || enemy == null || enemy == hero) return;
		if (!hero.hasTalent(Talent.CROWN_OF_THORNS)) return;
		if (pain(hero) < 0.5f) return;
		if (!Dungeon.level.adjacent(hero.pos, enemy.pos)) return;

		int reflect = Math.round(damage * 0.1f * hero.pointsInTalent(Talent.CROWN_OF_THORNS));
		if (reflect > 0) enemy.damage(reflect, hero);
	}

	// 一无所有：英雄死亡（含复活）时清空宝藏
	public static void loseEverything(Hero hero){
		RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
		if (treasure != null && treasure.charge > 0){
			treasure.charge = 0;
			GLog.n( Messages.get(RoyalTreasure.class, "lost_all") );
			treasure.updateQuickslot();
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Royalty();
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		if (isEquipped(Dungeon.hero)){
			desc += "\n\n" + Messages.get(this, "desc_status",
					Math.round(pain(Dungeon.hero)*100), charge, capacity());
		}
		return desc;
	}

	public class Royalty extends ArtifactBuff {
		@Override
		public boolean act() {
			// 王座残响（破碎王冠）：苦痛≥50% 时每回合 +1 护盾，上限 3/6/9
			if (target instanceof Hero){
				Hero hero = (Hero)target;
				if (hero.hasTalent(Talent.THRONE_ECHO) && pain(hero) >= 0.5f){
					Barrier b = Buff.affect(hero, Barrier.class);
					int max = 3*hero.pointsInTalent(Talent.THRONE_ECHO);
					if (b.shielding() < max){
						b.incShield(1);
					} else {
						b.incShield(0); //重置护盾衰减
					}
				}
			}
			spend(TICK);
			return true;
		}
	}
}
