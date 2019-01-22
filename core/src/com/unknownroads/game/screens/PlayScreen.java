package com.unknownroads.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unknownroads.game.HUD;
import com.unknownroads.game.entities.Car;
import com.unknownroads.game.tools.MapLoader;
import com.unknownroads.game.tools.MyRayCastCallback;
import com.unknownroads.game.tools.PlayerContactListener;
import com.unknownroads.game.tools.TimeManager;

import static com.unknownroads.game.Constants.*;
import static com.unknownroads.game.entities.Car.*;

public class PlayScreen implements Screen {

    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    private final Viewport mViewport;
    private final Car mPlayer;
    private final MapLoader mMapLoader;

    private Vector2 rayOrigin;
    private Vector2 rayLeft;
    private Vector2 rayRight;
    private ShapeRenderer sr;

    private MyRayCastCallback rayLeftCallback;
    private MyRayCastCallback rayRightCallback;

    //TODO create audio manager?
    private Sound sound;
    private long soundId;

    private TimeManager tm;

    private HUD hud;

    public PlayScreen(){
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mB2dr = new Box2DDebugRenderer();
        mCamera= new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new StretchViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = new Car(120.0f, 0.5f, 20.0f, mMapLoader, Car.DRIVE_2WD, mWorld); //TODO check speed

        sr = new ShapeRenderer();
        rayOrigin = new Vector2(0, 0);
        rayLeft = new Vector2(0, 0);
        rayRight = new Vector2(0, 0);
        rayLeftCallback = new MyRayCastCallback();
        rayRightCallback = new MyRayCastCallback();

        //TODO working android sound
        //TODO crossgoal sound
        //TODO short raycast in front to warn of impending walls
        sound = Gdx.audio.newSound(Gdx.files.internal("motor.wav"));
        soundId = sound.play(0.0f);
        sound.setLooping(soundId,true);

        tm = new TimeManager();
        mWorld.setContactListener(new PlayerContactListener(tm));

        hud = new HUD(tm);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //TODO pedreiro codigo repetido
        switch (Gdx.app.getType()) {
            case Android:
                handleInputAndroid();
                break;
            case Desktop:
                handleInputDesktop();
                break;
        }

        update(delta);

        handleAudio();

        hud.render();

        if(tm.getLapTime() != -1) {
            tm.update(delta);
        }

        draw();

    }

    //TODO raycasts collide w goal
    private void handleAudio() {
        double angle = mPlayer.getmBody().getAngle();
        Vector2 pos = mPlayer.getmBody().getPosition();
        rayOrigin.set( (float) (2.6f * Math.cos(angle+Math.PI/2) + pos.x) , (float) (2.6f * Math.sin(angle+Math.PI/2) + pos.y));

        int n = 0;
        float cl = -1;
        float cr = -1;
        sr.setProjectionMatrix(mCamera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        Vector2 prevL = rayOrigin.cpy();
        Vector2 prevR = rayOrigin.cpy();
        while(n<18){

            float leftX = (float) ((RAY_LENGTH) * Math.cos(angle+Math.PI-1.30 + ((Math.PI/12)*n)) + prevL.x); //1.30rad = 75º
            float leftY = (float) ((RAY_LENGTH) * Math.sin(angle+Math.PI-1.30 + ((Math.PI/12)*n)) + prevL.y); //ciclo Math.cos(angle+Math.PI-1.30 + PI/12(rad)*n) 180-75+15*n
            float rightX = (float) ((RAY_LENGTH) * Math.cos(angle+1.30 - ((Math.PI/12)*n)) + prevR.x);
            float rightY = (float) ((RAY_LENGTH) * Math.sin(angle+1.30 - ((Math.PI/12)*n)) + prevR.y);

            rayLeft.set(leftX, leftY);
            rayRight.set(rightX, rightY);

            //sr.line(prevL, rayLeft);
//            sr.line(prevR, rayRight);

            mWorld.rayCast(rayLeftCallback,prevL,rayLeft);
            mWorld.rayCast(rayRightCallback, prevR, rayRight);

            if(rayLeftCallback.finished() && cl == -1){ //n-1 offset da diagonal do carro á parede
                cl = (n-1)*RAY_LENGTH + rayLeftCallback.hitPos.dst(prevL);
                sr.line(rayLeftCallback.hitPos, new Vector2(rayLeftCallback.hitNormal).add(rayLeftCallback.hitPos));
            }

            if(rayRightCallback.finished() && cr == -1){
                cr = (n-1)*RAY_LENGTH + rayRightCallback.hitPos.dst(prevR);
                sr.line(rayRightCallback.hitPos, new Vector2(rayRightCallback.hitNormal).add(rayRightCallback.hitPos));
            }

            prevL = rayLeft.cpy();
            prevR = rayRight.cpy();

            n++;

        }

        rayLeftCallback.setFinished(false);
        rayRightCallback.setFinished(false);

        sr.end();

        float panValue = (-cl/(cl+cr)) + (cr/(cl+cr));
//        System.out.println("pan: " + panValue);
        
        float linVelocity = mPlayer.getmBody().linVelLoc.len();
        sound.setPan(soundId, panValue ,(20 + linVelocity)/120); //USE MAXSPEED IN PLAYSCREEN CONSTRUCTOR
        sound.setPitch(soundId, (20 + linVelocity)/60);

    }

    private void handleInputDesktop() {

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mPlayer.setmDriveDirection(DRIVE_DIRECTION_FORWARD);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {

            mPlayer.setmDriveDirection(DRIVE_DIRECTION_BACKWARD);
        } else {

            mPlayer.setmDriveDirection(DRIVE_DIRECTION_NONE);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            mPlayer.setmTurnDirection(TURN_DIRECTION_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mPlayer.setmTurnDirection(TURN_DIRECTION_RIGHT);
        } else {
            mPlayer.setmTurnDirection(TURN_DIRECTION_NONE);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    private void handleInputAndroid() {
        float accelZ = Gdx.input.getAccelerometerZ();
        float accelY = Gdx.input.getAccelerometerY();

        if (accelZ> 6.0f){
            mPlayer.setmDriveDirection(DRIVE_DIRECTION_FORWARD);
        }else if (accelZ< -1.0f){
            mPlayer.setmDriveDirection(DRIVE_DIRECTION_BACKWARD);
        }else {
            mPlayer.setmDriveDirection(DRIVE_DIRECTION_NONE);
        }

        if (accelY < -2.0f){
            mPlayer.setmTurnDirection(TURN_DIRECTION_LEFT);
        }else if (accelY >  2.0f){
            mPlayer.setmTurnDirection(TURN_DIRECTION_RIGHT);
        }else {
            mPlayer.setmTurnDirection(TURN_DIRECTION_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
    }

    }

    private void draw(){//TODO add sprites
        mBatch.setProjectionMatrix(mCamera.combined);
        mB2dr.render(mWorld, mCamera.combined);
    }


    private void update(final float delta) {
        mPlayer.update(delta);
        mCamera.position.set(mPlayer.getmBody().getPosition(), 0);
        mCamera.update();
        mWorld.step(delta, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        mViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mBatch.dispose();
        mWorld.dispose();
        mB2dr.dispose();
        mMapLoader.dispose();

        sr.dispose();

        sound.dispose();

        hud.dispose();

    }
}
