package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jackkillian.heatwaves.Constants;
import com.jackkillian.heatwaves.GameData;

import java.util.HashSet;

public class MapRenderSystem extends EntitySystem {
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private World world;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Box2DDebugRenderer debugRenderer;

    private HashSet<BodyDef> bodiesToAdd = new HashSet<>();
    private HashSet<Body> bodiesToRemove = new HashSet<>();

    //moving clouds
    private float cloudOffset = 0;
    private Texture cloudTexture;

    public MapRenderSystem() {
        Box2D.init();
        batch = GameData.getInstance().getBatch();
        camera = GameData.getInstance().getCamera();
        world = GameData.getInstance().getWorld();
        debugRenderer = new Box2DDebugRenderer();
        map = new TmxMapLoader().load("gameMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Constants.PPM);


        for (MapObject object : map.getLayers().get("building").getObjects()) {
            Shape shape;
            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject)object);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }
            BodyDef bdef = new BodyDef();
            FixtureDef fdef = new FixtureDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bdef);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        cloudTexture = new Texture("cloud.png");

    }

    public void update(float deltaTime) {
        //check queues before world update
        for (BodyDef bodyDef : bodiesToAdd) {Body body = world.createBody(bodyDef);}
        for (Body body : bodiesToRemove) {world.destroyBody(body);}

        bodiesToRemove.clear();
        bodiesToAdd.clear();

        world.step(1 / 60f, 6, 2);

//        cloudOffset += deltaTime * 80f;
//        if (cloudOffset > Gdx.graphics.getWidth()) {
//            cloudOffset = 0;
//        }
//
//        batch.begin();
//        batch.draw(cloudTexture, -cloudOffset, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        batch.draw(cloudTexture, -cloudOffset + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        batch.end();

        camera.update();
        renderer.setView(camera);
        renderer.render();
//        debugRenderer.render(world, camera.combined);
    }

    public void queueBodyForAdd(BodyDef bodyDef) {
        bodiesToAdd.add(bodyDef);
    }
    public void queueBodyForDelete(Body body) {
        bodiesToRemove.add(body);
    }

    // https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object
    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / Constants.PPM,
                (rectangle.y + rectangle.height * 0.5f ) / Constants.PPM);
        polygon.setAsBox(rectangle.width * 0.5f /Constants.PPM,
                rectangle.height * 0.5f / Constants.PPM,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / Constants.PPM);
        circleShape.setPosition(new Vector2(circle.x / Constants.PPM, circle.y / Constants.PPM));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            System.out.println(vertices[i]);
            worldVertices[i] = vertices[i] / Constants.PPM;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / Constants.PPM;
            worldVertices[i].y = vertices[i * 2 + 1] / Constants.PPM;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
