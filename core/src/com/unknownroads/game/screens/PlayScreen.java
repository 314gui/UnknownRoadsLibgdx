package com.unknownroads.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unknownroads.game.tools.MapLoader;

import static com.unknownroads.game.Constants.DEFAULT_ZOOM;
import static com.unknownroads.game.Constants.GRAVITY;
import static com.unknownroads.game.Constants.PPM;



public class PlayScreen implements Screen {

    private static final int DRIVE_DIRECTION_NONE = 0;
    private static final int DRIVE_DIRECTION_FORWARD = 1;
    private static final int DRIVE_DIRECTION_BACKWARD = 2;

    private static final int TURN_DIRECTION_NONE = 0;
    private static final int TURN_DIRECTION_LEFT = 1;
    private static final int TURN_DIRECTION_RIGHT = 2;

    private static final float DRIFT = 0.0f;
    private static final float TURN_SPEED = 2.0f;
    private static final float DRIVE_SPEED = 120.0f;
    private static final float MAX_SPEED = 35.0f;

    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    private final Viewport mViewport;
    private final Body mPlayer;
    private final MapLoader mMapLoader;

    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

    //TODO raycasting, current lines are visualizations
    private Vector2 rayOrigin;
    private Vector2 rayLeft;
    private Vector2 rayRight;
    private ShapeRenderer sr;

    public PlayScreen(){
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mB2dr = new Box2DDebugRenderer();
        mCamera= new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new StretchViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = mMapLoader.getPlayer();
        mPlayer.setLinearDamping(0.5f);

        sr = new ShapeRenderer();
        rayLeft = new Vector2(0, 0);
        rayRight = new Vector2(0, 0);

    }

    @Override
    public void show() {


    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleAudio();

        //TODO pedreiro codigo repetido
        switch (Gdx.app.getType()) {
            case Android:
                handleInputAndroid();
                break;
            case Desktop:
                handleInputDesktop();
                break;
        }

        processInput();
        update(delta);
        handleDrift();

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
        rayOrigin = mPlayer.getPosition();

        //TODO mudar para arcos....
        //Fetches the point 15m to the left and right of rayOrigin, rotated according to 'mPlayer.getAngle()'
        float leftX = (float) ((15) * Math.cos(mPlayer.getAngle()) + rayOrigin.x);
        float leftY = (float) ((15) * Math.sin(mPlayer.getAngle()) + rayOrigin.y);
        float rightX = (float) ((-15) * Math.cos(mPlayer.getAngle()) + rayOrigin.x);
        float rightY = (float) ((-15) * Math.sin(mPlayer.getAngle()) + rayOrigin.y);

        rayLeft.set(leftX, leftY);
        rayRight.set(rightX, rightY);

    }

    private void handleDrift() {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        mPlayer.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * DRIFT, forwardSpeed.y + lateralSpeed.y * DRIFT );
    }

    private void processInput() {
        Vector2 baseVector = new Vector2(0, 0);

        if (mTurnDirection == TURN_DIRECTION_RIGHT){
            mPlayer.setAngularVelocity(-TURN_SPEED);

        }else if (mTurnDirection == TURN_DIRECTION_LEFT){
            mPlayer.setAngularVelocity(TURN_SPEED);

        }else if (mTurnDirection == TURN_DIRECTION_NONE && mPlayer.getAngularVelocity() != 0){
            mPlayer.setAngularVelocity(0.0f);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD){
            baseVector.set(0, DRIVE_SPEED);

        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD){
            baseVector.set(0, -DRIVE_SPEED);

        }

        if (!baseVector.isZero() && mPlayer.getLinearVelocity().len() < MAX_SPEED){
            mPlayer.applyForceToCenter(mPlayer.getWorldVector(baseVector), true);
        }
    }

    private Vector2 getForwardVelocity(){
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return multiply(dotProduct, currentNormal);

    }

    private Vector2 getLateralVelocity(){
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return multiply(dotProduct, currentNormal);

    }



    private Vector2 multiply(float a, Vector2 v){
        return new Vector2(a * v.x, a* v.y);
    }

    private void handleInputDesktop() {

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mDriveDirection = DRIVE_DIRECTION_FORWARD;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mDriveDirection = DRIVE_DIRECTION_BACKWARD;
        } else {
            mDriveDirection = DRIVE_DIRECTION_NONE;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mTurnDirection = TURN_DIRECTION_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mTurnDirection = TURN_DIRECTION_RIGHT;
        } else {
            mTurnDirection = TURN_DIRECTION_NONE;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    private void handleInputAndroid() {
        float accelZ = Gdx.input.getAccelerometerZ();
        float accelY = Gdx.input.getAccelerometerY();

        if (accelZ> 6.0f){
            mDriveDirection = DRIVE_DIRECTION_FORWARD;
        }else if (accelZ< -1.0f){
            mDriveDirection = DRIVE_DIRECTION_BACKWARD;
        }else {
            mDriveDirection = DRIVE_DIRECTION_NONE;
        }

        if (accelY < -2.0f){
            mTurnDirection = TURN_DIRECTION_LEFT;
        }else if (accelY >  2.0f){
            mTurnDirection = TURN_DIRECTION_RIGHT;
        }else {
            mTurnDirection = TURN_DIRECTION_NONE;
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
        mCamera.position.set(mPlayer.getPosition(), 0);
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
