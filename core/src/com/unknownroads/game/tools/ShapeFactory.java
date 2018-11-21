package com.unknownroads.game.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.unknownroads.game.Constants.PPM;

public class ShapeFactory {

    private  ShapeFactory(){}

    public static Body createRectangle(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, boolean sensor) {
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / PPM, position.y /PPM);
        bdef.type = type;
        final Body body = world.createBody(bdef);

        //define fixture
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / PPM, size.y / PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape= shape;
        fdef.density = density;
        fdef.isSensor = sensor;

        body.createFixture(fdef);
        shape.dispose();

        return body;

    }
}
