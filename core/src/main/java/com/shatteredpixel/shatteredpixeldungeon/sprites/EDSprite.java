/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.noosa.TextureFilm;

/**
 * E.D.'s source artwork is a 4x4 sheet of 28x28 cells, not a standard
 * 12x15 Shattered hero sheet. Only the two complete middle cells of each
 * direction row are used; the partial figures at the left and right edges
 * never enter an animation.
 */
public class EDSprite extends HeroSprite {

	public EDSprite(){
		super();
		// The source figure is larger than the vanilla 12x15 hero frame. Keep
		// enough of that silhouette to make E.D. readable in the dungeon.
		scale.set(0.75f);
	}

	@Override
	public void updateArmor() {
		TextureFilm film = new TextureFilm(texture);
		for (int row = 0; row < 4; row++){
			int y = row * 28;
			// The uploaded contact sheet has complete figures at x=34..56 and
			// x=78..100. The x=56..78 column is a connected spill from the
			// neighbouring figure and must never be used as an animation frame.
			film.add(row * 4 + 1, 34, y, 56, y + 28);
			film.add(row * 4 + 2, 78, y, 100, y + 28);
		}

		idle = new Animation(2, true);
		idle.frames(film, 9, 10, 9, 10);

		run = new Animation(12, true);
		run.frames(film, 9, 10, 9, 10, 9, 10);

		die = new Animation(8, false);
		die.frames(film, 1, 2, 5, 6, 13);

		attack = new Animation(12, false);
		attack.frames(film, 9, 10, 9);
		zap = attack.clone();

		operate = new Animation(8, false);
		operate.frames(film, 1, 2, 1, 2);

		fly = new Animation(1, true);
		fly.frames(film, 9);

		read = new Animation(12, false);
		read.frames(film, 13, 14, 14, 14, 13);

		if (Dungeon.hero != null && Dungeon.hero.isAlive()){
			idle();
		} else {
			die();
		}
	}

	@Override
	public void disguise(HeroClass cls) {
		texture(cls.spritesheet());
		if (cls == HeroClass.ED){
			updateArmor();
		} else {
			super.updateArmor();
		}
	}
}
