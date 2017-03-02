package kiet.nguyentuan.libgdxdemo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by kiettuannguyen on 02/03/2017.
 */

public class TurtleLevel extends BaseScreen {
    private BaseActor ocean;
    private ArrayList<BaseActor> rockList;
    private ArrayList<BaseActor> starfishList;
    private PhysicsActor turtle;
    private int mapWidth = 800;
    private int mapHeight = 600;

    private Label starfishLeftLabel;
    private Table pauseOverlay;

    private float audioVolume;
    private Sound waterDrop;
    private Music instrumental;
    private Music oceanSurf;

    public TurtleLevel(Game g)
    {  super(g);  }

    public void create()
    {
        ocean = new BaseActor();
        ocean.setTexture( new Texture("water.jpg") );
        ocean.setPosition( 0, 0 );
        mainStage.addActor( ocean );

        BaseActor overlay = ocean.clone();
        overlay.setPosition(-50,-50);
        overlay.setColor(1,1,1,0.25f);
        uiStage.addActor(overlay);
        overlay.toBack(); // necessary so it doesn't overlay and block access to buttons in UI

        BaseActor rock = new BaseActor();
        rock.setTexture( new Texture("rock.png"));
        rock.setEllipseBoundary();

        rockList = new ArrayList<BaseActor>();
        int[] rockCoords = {200,0, 200,100, 250,200, 360,200, 470,200};
        for (int i = 0; i < 5; i++)
        {
            BaseActor r = rock.clone();
            r.setPosition( rockCoords[2*i], rockCoords[2*i+1] );
            mainStage.addActor( r );
            rockList.add( r );
        }

        BaseActor starfish = new BaseActor();
        starfish.setTexture( new Texture("starfish.png") );
        starfish.setEllipseBoundary();

        starfishList = new ArrayList<BaseActor>();
        int[] starfishCoords = {400,100, 100,400, 650,400};
        for (int i = 0; i < 3; i++)
        {
            BaseActor s = starfish.clone();
            s.setPosition( starfishCoords[2*i], starfishCoords[2*i+1] );
            mainStage.addActor( s );
            starfishList.add( s );
        }

        turtle = new PhysicsActor();
        TextureRegion[] frames = new TextureRegion[6];
        for (int n = 1; n <= 6; n++)
        {
            String fileName = "turtle-" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames[n-1] = new TextureRegion( tex );
        }
        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

        Animation anim = new Animation(0.1f, framesArray, Animation.PlayMode.LOOP);
        turtle.storeAnimation( "swim", anim );

        Texture frame1 = new Texture("turtle-1.png");
        turtle.storeAnimation( "rest", frame1 );

        turtle.setOrigin( turtle.getWidth()/2, turtle.getHeight()/2 );
        turtle.setPosition( 20, 20 );
        turtle.setRotation( 90 );
        turtle.setEllipseBoundary();
        turtle.setMaxSpeed(100);
        turtle.setDeceleration(200);
        mainStage.addActor(turtle);

        // audio

        waterDrop    = Gdx.audio.newSound(Gdx.files.internal("Water_Drop.ogg"));
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("Master_of_the_Feast.ogg"));
        oceanSurf    = Gdx.audio.newMusic(Gdx.files.internal("Ocean_Waves.ogg"));

        audioVolume = 0.80f;
        instrumental.setLooping(true);
        instrumental.setVolume(audioVolume);
        instrumental.play();
        oceanSurf.setLooping(true);
        oceanSurf.setVolume(audioVolume);
        oceanSurf.play();

        /// user interface

       /*starfishLeftLabel = new Label("Starfish Left: 8", game.skin, "uiLabelStyle");

        Texture pauseTexture = new Texture(("pause.png"));
        game.skin.add("pauseImage", pauseTexture );

        ButtonStyle pauseStyle = new ButtonStyle();
        pauseStyle.up = game.skin.getDrawable("pauseImage");

        Button pauseButton = new Button( pauseStyle );

        pauseButton.addListener(
                new InputListener()
                {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
                    {
                        togglePaused();
                        pauseOverlay.setVisible( isPaused() );
                        return true;
                    }
                });

        uiTable.pad(10);
        uiTable.add(starfishLeftLabel);
        uiTable.add().expandX();
        uiTable.add(pauseButton);
        uiTable.row();
        uiTable.add().colspan(3).expandY();

        // pause overlay menu

        pauseOverlay = new Table();
        pauseOverlay.setFillParent(true);

        Stack stacker = new Stack();
        stacker.setFillParent(true);
        uiStage.addActor(stacker);
        stacker.add(uiTable);
        stacker.add(pauseOverlay);

        game.skin.add("white", new Texture( ("white4px.png")) );
        Drawable pauseBackground = game.skin.newDrawable("white", new Color(0,0,0,0.8f) );

        Label pauseLabel = new Label("Paused", game.skin, "uiLabelStyle");

        TextButton resumeButton = new TextButton("Resume", game.skin, "uiTextButtonStyle");
        resumeButton.addListener(
                new InputListener()
                {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
                    {  return true;  }

                    public void touchUp (InputEvent event, float x, float y, int pointer, int button)
                    {
                        togglePaused();
                        pauseOverlay.setVisible( isPaused() );
                    }
                });

        TextButton quitButton = new TextButton("Quit", game.skin, "uiTextButtonStyle");
        quitButton.addListener(
                new InputListener()
                {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
                    {  return true;  }

                    public void touchUp (InputEvent event, float x, float y, int pointer, int button)
                    {
                        Gdx.app.exit();
                    }
                });

        Label volumeLabel = new Label("Volume", game.skin, "uiLabelStyle");

        Slider audioSlider = new Slider(0, 1, 0.005f, false, game.skin, "uiSliderStyle" );
        audioSlider.setValue( audioVolume );
        audioSlider.addListener( new ChangeListener()
        {
            public void changed(ChangeEvent event, Actor actor)
            {
                audioVolume = audioSlider.getValue();
                instrumental.setVolume(audioVolume);
                oceanSurf.setVolume(audioVolume);
            }
        });

        float buttonWidth = resumeButton.getWidth();

        pauseOverlay.setBackground( pauseBackground );
        pauseOverlay.add(pauseLabel).pad(20);
        pauseOverlay.row();
        pauseOverlay.add(resumeButton).width(buttonWidth);
        pauseOverlay.row();
        pauseOverlay.add(quitButton).width(buttonWidth);
        pauseOverlay.row();
        pauseOverlay.add(volumeLabel).padTop(100);
        pauseOverlay.row();
        pauseOverlay.add(audioSlider).width(400);
        pauseOverlay.setVisible(false);*/
    }

    public void update(float dt)
    {
        // process input
        turtle.setAccelerationXY(0,0);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            turtle.rotateBy(90 * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            turtle.rotateBy(-90 * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            turtle.accelerateForward(100);

        // set correct animation
        if ( turtle.getSpeed() > 1 && turtle.getAnimationName().equals("rest") )
            turtle.setActiveAnimation("swim");
        if ( turtle.getSpeed() < 1 && turtle.getAnimationName().equals("swim") )
            turtle.setActiveAnimation("rest");

        // bound turtle to the screen
        turtle.setX( MathUtils.clamp( turtle.getX(), 0,  mapWidth - turtle.getWidth() ));
        turtle.setY( MathUtils.clamp( turtle.getY(), 0,  mapHeight - turtle.getHeight() ));

        for (BaseActor r : rockList)
        {
            turtle.overlaps(r, true);
        }

        ArrayList<BaseActor> removeList = new ArrayList<BaseActor>();
        for (BaseActor s : starfishList)
        {
            if ( turtle.overlaps(s, false) )
                removeList.add(s);
        }

        for (BaseActor b : removeList)
        {
            b.remove();             // remove from stage
            starfishList.remove(b); // remove from list used by update
            waterDrop.play(audioVolume);
        }

        // update UI
       // starfishLeftLabel.setText( "Starfish Left: " + starfishList.size() );
    }

    public void dispose()
    {
        waterDrop.dispose();
        instrumental.dispose();
        oceanSurf.dispose();
    }
}