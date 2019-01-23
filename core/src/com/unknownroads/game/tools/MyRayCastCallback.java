package com.unknownroads.game.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class MyRayCastCallback implements RayCastCallback {

    public Fixture hitFixture;
    public Vector2 hitPos;
    public Vector2 hitNormal;
    public boolean finished;

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

        //TODO performance?
        if (fixture.isSensor()) { //Avoids raycast collision w goal
            return -1;
        }

        this.hitFixture = fixture;
        this.hitPos = new Vector2(point);
        this.hitNormal = new Vector2(normal);
        this.finished = true;

        return 0;
    }

    //Says if the arc is done drawing to invalidate previous hit pos
    public void setFinished(boolean b) {
        this.finished = b;
    }

    public boolean finished() {
        return this.finished;
    }

}
