package com.unknownroads.game.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import static com.unknownroads.game.Constants.MAP_NAME;

public class MapLoader implements Disposable {

    private static final String MAP_WALL = "wall";
    private static final String MAP_PLAYER = "player";
    private static final String MAP_GOAL = "goal";

    private final World mWorld;
    private final TiledMap mMap;

    public MapLoader(World world){
        this.mWorld = world;

        mMap = new TmxMapLoader().load(MAP_NAME);

        final Array<RectangleMapObject> walls = mMap.getLayers().get(MAP_WALL).getObjects().getByType(RectangleMapObject.class);

        for (RectangleMapObject rObject : walls){
            Rectangle rectangle = rObject.getRectangle();
            ShapeFactory.createRectangle(
                    new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2 ), //position
                    new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2), //size
                    BodyDef.BodyType.StaticBody, mWorld, 1f, false, "wall");

        }

        //TODO iniciar goal onde?
        getGoal();

    }

    public Body getPlayer(){
        final Rectangle rectangle = mMap.getLayers().get(MAP_PLAYER).getObjects().getByType(RectangleMapObject.class).get(0).getRectangle();
        return ShapeFactory.createRectangle(
                new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2 ), //position
                new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2), //size
                BodyDef.BodyType.DynamicBody, mWorld, 0.4f, false, "player");
    }


    public Body getGoal(){
        final Rectangle rectangle = mMap.getLayers().get(MAP_GOAL).getObjects().getByType(RectangleMapObject.class).get(0).getRectangle();
        return ShapeFactory.createRectangle(
                new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2 ), //position
                new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2), //size
                BodyDef.BodyType.StaticBody, mWorld, 0.4f, true, "goal");
    }

    @Override
    public void dispose() {
        mMap.dispose();
    }
}
