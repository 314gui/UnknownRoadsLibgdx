package com.unknownroads.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.unknownroads.game.BodyHolder;
import com.unknownroads.game.tools.MapLoader;

import static com.unknownroads.game.Constants.PPM;


public class Car extends BodyHolder {

    public static final int DRIVE_2WD = 0;
    public static final int DRIVE_4WD = 1;

    public static final int DRIVE_DIRECTION_NONE = 0;
    public static final int DRIVE_DIRECTION_FORWARD = 1;
    public static final int DRIVE_DIRECTION_BACKWARD = 2;

    public static final int TURN_DIRECTION_NONE = 0;
    public static final int TURN_DIRECTION_LEFT = 1;
    public static final int TURN_DIRECTION_RIGHT = 2;


    private static final Vector2 WHEEL_SIZE = new Vector2(16, 32);
    private static final float LINEAR_DUMPING = 0.5f;
    private static final float RESTITUTION = 0.2f;
    private static final float MAX_WHEEL_ANGLE = 20.0f;
    private static final float WHEEL_TURN_INCREMENT = 1.0f;

    private float mCurrentMaxSpeed;
    private final float mRegularMaxSpeed;
    private float mAcceleration;

    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

    private float mCurrentWheelAngle = 0;
    final private Array<Wheel> mAllWheels = new Array<Wheel>();
    //Wheels que rodam / traçao
    final private Array<Wheel> mRevolvingWheels = new Array<Wheel>();
    private float mDrift;


    public Car(final float maxSpeed, final float drift, final float acceleration, final MapLoader mapLoader, int wheelDrive, World world) {
        super(mapLoader.getPlayer());
        this.mRegularMaxSpeed = maxSpeed;
        this.mDrift = drift;
        this.mAcceleration = acceleration;
        getmBody().setLinearDamping(LINEAR_DUMPING);
        //Bouncing effect
        getmBody().getFixtureList().get(0).setRestitution(RESTITUTION);
        createWheels(world, wheelDrive);


        /*
        //STOPS DRIFT
        getmBody().setLinearDamping(0.5f);
        //DRIFT O NO DRIFT / 1 HAS DRIFT
        setnDrift(0);
        */

    }

    private void createWheels(World world, int wheelDrive) {
        for (int i = 0; i < 4; i++) {
            float xOffset = 0;
            float yOffset = 0;

            //localizaçao das rodas
            switch (i) {
                case Wheel.UPPER_LEFT:
                    xOffset = -64;
                    yOffset = 80;
                    break;
                case Wheel.UPPER_RIGHT:
                    xOffset = 64;
                    yOffset = 80;
                    break;
                case Wheel.DOWN_LEFT:
                    xOffset = -64;
                    yOffset = -80;
                    break;
                case Wheel.DOWN_RIGHT:
                    xOffset = 64;
                    yOffset = -80;
                    break;

                default:


            }
            boolean powered = wheelDrive == DRIVE_4WD || (wheelDrive == DRIVE_2WD && i < 2);
            Wheel wheel = new Wheel(new Vector2(getmBody().getPosition().x * PPM + xOffset, getmBody().getPosition().y * PPM + yOffset), WHEEL_SIZE, BodyDef.BodyType.DynamicBody, world, 0.4f, i, this, powered);

            if (i < 2) {
                RevoluteJointDef jointDef = new RevoluteJointDef();
                jointDef.initialize(getmBody(), wheel.getmBody(), wheel.getmBody().getWorldCenter());
                jointDef.enableMotor = false;
                world.createJoint(jointDef);
            } else {
                PrismaticJointDef jointDef = new PrismaticJointDef();
                jointDef.initialize(getmBody(), wheel.getmBody(), wheel.getmBody().getWorldCenter(), new Vector2(1, 0));
                jointDef.enableLimit = true;
                jointDef.lowerTranslation = jointDef.upperTranslation = 0;
                world.createJoint(jointDef);
            }
            mAllWheels.add(wheel);
            if (i < 2) {
                mRevolvingWheels.add(wheel);
            }
            wheel.setnDrift(mDrift);
        }
    }







    private void processInput() {
        Vector2 baseVector = new Vector2(0, 0);


        if (mTurnDirection == TURN_DIRECTION_LEFT) {
            //se tiver a esquerda
            if (mCurrentWheelAngle < 0) {
                mCurrentWheelAngle = 0;
            }

            mCurrentWheelAngle = Math.min(mCurrentWheelAngle += WHEEL_TURN_INCREMENT, MAX_WHEEL_ANGLE);


        } else if (mTurnDirection == TURN_DIRECTION_RIGHT) {
            if (mCurrentWheelAngle > 0) {
                mCurrentWheelAngle = 0;
            }

            mCurrentWheelAngle = Math.max(mCurrentWheelAngle -= WHEEL_TURN_INCREMENT, -MAX_WHEEL_ANGLE);


        } else {
            mCurrentWheelAngle = 0;
        }

        for (Wheel wheel : mRevolvingWheels) {
            wheel.setAngle(mCurrentWheelAngle);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, mAcceleration);

        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            if (direction() == DIRECTION_BACKWARD) {
                baseVector.set(0, -mAcceleration * 0.7f);
            } else if (direction() == DIRECTION_FORWARD) {
                baseVector.set(0, -mAcceleration * 1.3f);
            } else
                baseVector.set(0, -mAcceleration);


        }

        //andar mais lento para tras
        if (direction() == DRIVE_DIRECTION_BACKWARD) {
            mCurrentMaxSpeed = mRegularMaxSpeed / 2;
        } else {
            mCurrentMaxSpeed = mRegularMaxSpeed;
        }


        if (getmBody().getLinearVelocity().len() < mCurrentMaxSpeed) {
            for (Wheel wheel : mAllWheels) {
                wheel.getmBody().applyForceToCenter(wheel.getmBody().getWorldVector(baseVector), true);
            }
        }

    }

    public float getmAcceleration(){return this.mAcceleration;}
    public void setmDriveDirection(int driveDirection) {
        this.mDriveDirection = driveDirection;
    }

    public void setmTurnDirection(int turnDirection) {
        this.mTurnDirection = turnDirection;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        processInput();
        for (Wheel wheel : mAllWheels) {
            wheel.update(delta);
        }

    }
}
