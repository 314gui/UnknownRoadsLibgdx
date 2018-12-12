package com.unknownroads.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unknownroads.game.tools.TimeManager;

public class HUD implements Disposable {

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera cam;
    private TimeManager tm;

    public HUD(TimeManager tm) {

        batch = new SpriteBatch();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font = new BitmapFont(Gdx.files.internal("comicsans.fnt"));
        this.tm = tm;

    }

    public void resize(int screenWidth, int screenHeight) {
        cam = new OrthographicCamera(screenWidth, screenHeight);
        cam.translate(screenWidth / 2, screenHeight / 2);
        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }


    public void render(){

       batch.begin();

        if(tm.getLapTime() > 0)
            font.draw(batch, "Time: " + tm.getLapTime(), 3, Gdx.graphics.getHeight() - 3);
        else
            font.draw(batch, "Time: --", 3, Gdx.graphics.getHeight() - 3);

        if(tm.getBestLap() != -1)
            font.draw(batch, "PB: " + tm.getBestLap(), 3, Gdx.graphics.getHeight() - 20);

        batch.end();

    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }

}
