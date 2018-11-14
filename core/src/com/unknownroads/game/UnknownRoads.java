package com.unknownroads.game;

import com.badlogic.gdx.Game;
import com.unknownroads.game.screens.PlayScreen;

public class UnknownRoads extends Game {

	
	@Override
	public void create () {
		setScreen(new PlayScreen());

	}

	@Override
	public void render () {
		super.render();

	}
	
	@Override
	public void dispose () {
		super.dispose();

	}
}
