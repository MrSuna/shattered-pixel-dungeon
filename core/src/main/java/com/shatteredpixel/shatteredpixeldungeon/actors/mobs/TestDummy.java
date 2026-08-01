/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;

/** A stationary enemy used by the debug menu for testing E.D.'s abilities. */
public class TestDummy extends Mob {

	{
		spriteClass = StatueSprite.class;
		HP = HT = 250;
		defenseSkill = 0;
		EXP = 0;
		state = PASSIVE;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.INORGANIC);
	}

	@Override
	protected boolean act(){
		spend(TICK);
		return true;
	}

	@Override
	public int damageRoll(){
		return 0;
	}

	@Override
	public int attackSkill(Char target){
		return 0;
	}

	@Override
	public int drRoll(){
		return 0;
	}
}
