package com.unknownroads.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unknownroads.game.entities.Car;
import com.unknownroads.game.tools.MapLoader;
import com.unknownroads.game.tools.MyRayCastCallback;

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



    //TODO raycasting, current lines are visualizations
    private Vector2 rayOrigin;
    private Vector2 rayLeft;
    private Vector2 rayRight;
    private ShapeRenderer sr;

    private MyRayCastCallback rayLeftCallback;
    private MyRayCastCallback rayRightCallback;


    public PlayScreen(){
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mB2dr = new Box2DDebugRenderer();
        mCamera= new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new StretchViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = new Car(120.0f, 0.0f, 30.0f, mMapLoader, Car.DRIVE_2WD, mWorld);


        sr = new ShapeRenderer();
        rayLeft = new Vector2(0, 0);
        rayRight = new Vector2(0, 0);
        rayLeftCallback = new MyRayCastCallback();
        rayRightCallback = new MyRayCastCallback();

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

        draw();


        //TODO check renderer options
        //Draws audio lines
        sr.setProjectionMatrix(mCamera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(rayOrigin, rayLeft);
        sr.line(rayOrigin, rayRight);
        sr.end();


    }


    private void handleAudio() {

        //TODO mudar origem p nose
        rayOrigin = mPlayer.getmBody().getPosition();

        //TODO mudar para arcos....
        //Fetches the point 15m to the left and right of rayOrigin, rotated according to 'mPlayer.getAngle()'
        float leftX = (float) ((20) * Math.cos(mPlayer.getmBody().getAngle()) + rayOrigin.x);
        float leftY = (float) ((20) * Math.sin(mPlayer.getmBody().getAngle()) + rayOrigin.y);
        float rightX = (float) ((-20) * Math.cos(mPlayer.getmBody().getAngle()) + rayOrigin.x);
        float rightY = (float) ((-20) * Math.sin(mPlayer.getmBody().getAngle()) + rayOrigin.y);

        rayLeft.set(leftX, leftY);
        rayRight.set(rightX, rightY);

        //TODO check renderer options
        //Draws audio lines
        sr.setProjectionMatrix(mCamera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(rayOrigin, rayLeft);
        sr.line(rayOrigin, rayRight);


        mWorld.rayCast(rayLeftCallback,rayOrigin,rayLeft);
        mWorld.rayCast(rayRightCallback, rayOrigin, rayRight);

        //TODO check placement of rayCast related code
        if(rayLeftCallback.hitPos != null) {
            sr.line(rayLeftCallback.hitPos, mPlayer.getmBody().getLocalPoint(rayLeftCallback.hitNormal));
            //System.out.println("left: " + rayLeftCallback.hitPos.dst(mPlayer.getmBody().getPosition()));
        }
        if(rayRightCallback.hitPos != null) {
            sr.line(rayRightCallback.hitPos, mPlayer.getmBody().getLocalPoint(rayRightCallback.hitNormal));
            //System.out.println("right: " + rayRightCallback.hitPos.dst(mPlayer.getmBody().getPosition()));
        }

        sr.end();

        //TODO edge case: both raycasts have no hit

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

    private void draw(){
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

    }
}
