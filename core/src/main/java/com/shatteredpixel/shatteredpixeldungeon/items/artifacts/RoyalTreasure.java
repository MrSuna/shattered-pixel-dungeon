/*
 * 永远的像素地牢 — E.D. 专属神器「独一无二的王之宝藏」
 * 基于 Shattered Pixel Dungeon (GPL-3.0)，本文件同样以 GPL-3.0 发布。
 * 放置到: core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/RoyalTreasure.java
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class RoyalTreasure extends Artifact {

	{
		// 占位图标：先复用荆棘斗篷的图标；做好自己的 16x16 图标后
		// 在 items.png 空位画上并在 ItemSpriteSheet.java 登记新常量，再替换这里
		image = ItemSpriteSheet.ARTIFACT_CAPE;

		levelCap = 10;

		charge = 0;        // charge = 掠夺物数量
		chargeCap = 10;    // 基础上限 10，升级不加上限、加每件掠夺物的强度

		unique = true;
		bones = false;

		defaultAction = "NONE";
	}

	// ===== 核心数值（想调平衡改这里） =====
	// 苦痛 = 已损失生命比例 (0~1)
	public static float pain(Hero hero){
		return 1f - (hero.HP / (float)hero.HT);
	}

	// 王之威压：苦痛 >= 0.25 后开始生效，满苦痛时基础 +50% 伤害，神器每级再 +5%
	// 掠夺：每件掠夺物 +2% 伤害，受苦痛加成放大（×(1+苦痛)）
	public static int empowerDamage(Hero hero, int dmg){
		RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
		if (treasure == null || !treasure.isEquipped(hero)) return dmg;

		float p = pain(hero);
		float mult = 1f;
		if (p >= 0.25f){
			mult += p * 0.5f * (1f + 0.05f * treasure.level());
		}
		mult += treasure.charge * 0.02f * (1f + p);
		return Math.round(dmg * mult);
	}

	// 掠夺：英雄的攻击将杀死敌人时调用（见 Hero.java 的集成 diff）
	public static void tryPlunder(Hero hero, Char enemy, int damage){
		if (enemy == hero || enemy.alignment != Char.Alignment.ENEMY) return;
		if (damage < enemy.HP) return; // 不是致命一击

		RoyalTreasure treasure = hero.belongings.getItem(RoyalTreasure.class);
		if (treasure == null || !treasure.isEquipped(hero)) return;

		if (treasure.charge < treasure.chargeCap){
			treasure.charge++;
			treasure.exp++;
			GLog.p( Messages.get(RoyalTreasure.class, "plundered", treasure.charge, treasure.chargeCap) );
			// 每掠夺 5 件升一级：宝藏越用越锋利
			if (treasure.exp >= 5 && treasure.level() < treasure.levelCap){
				treasure.exp = 0;
				treasure.upgrade();
				GLog.p( Messages.get(RoyalTreasure.class, "levelup") );
			}
			treasure.updateQuickslot();
		}
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
					Math.round(pain(Dungeon.hero)*100), charge, chargeCap);
		}
		return desc;
	}

	public class Royalty extends ArtifactBuff {
		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}
	}
}
