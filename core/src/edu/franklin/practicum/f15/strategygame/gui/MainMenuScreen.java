package edu.franklin.practicum.f15.strategygame.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.franklin.practicum.f15.strategygame.Defines;
import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.StrategyGame;


@SuppressWarnings("UnusedParameters")
public class MainMenuScreen implements Screen {
    private final OrthographicCamera camera;
    private final Skin skin;
    private final StrategyGame game;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final Stage stage;
    private final Sprite background;

    public MainMenuScreen(StrategyGame game) {
        this.game = game;
        this.font = game.font;
        this.batch = game.batch;
        this.skin = game.uiSkin;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());

        stage = new Stage(new ScreenViewport());

        Texture tex = new Texture(Gdx.files.internal("ui/f15_strategy_game.png"));
        background = new Sprite(tex);
        background.scale(1.0f);
        float bblx = (Gdx.graphics.getWidth() - background.getWidth()) / 2;
        float bbly = (Gdx.graphics.getHeight() - background.getHeight()) / 2;
        background.setPosition(bblx, bbly);
    }

    private void newGameBtnClicked(ChangeListener.ChangeEvent e, Actor a) {
        Logger.logMsg("new game button clicked");
        game.setGameScreen(this, game.newGameScreen);
    }

    private void loadGameBtnClicked(ChangeListener.ChangeEvent e, Actor a) {
        Logger.logMsg("load game button clicked");
        game.setGameScreen(this, game.loadGameScreen);
    }

    private void optionsBtnClicked(ChangeListener.ChangeEvent e, Actor a) {
        Logger.logMsg("options button clicked");
        game.setGameScreen(this, game.optionsScreen);
    }

    private void exitBtnClicked(ChangeListener.ChangeEvent e, Actor a) {
        Logger.logMsg("exit button clicked");
        Gdx.app.exit();
    }

    @Override
    public void show() {
        Logger.logMsg("showing main menu");
        Gdx.input.setInputProcessor(stage);

        float mainMenuTableW = 200;
        float mainMenuTableH = 200;
        float mainMenuTableX = (Gdx.graphics.getWidth() - mainMenuTableW) / 2;
        float mainMenuTableY = (Gdx.graphics.getHeight() - mainMenuTableH) / 2;

        Table table = new Table();
        table.setSkin(skin);
        table.setDebug(Defines.DEBUG_UI);
        table.defaults().expand().fill().pad(4f);
        table.setBackground(skin.getDrawable("dght"));
        table.setWidth(mainMenuTableW);
        table.setHeight(mainMenuTableH);
        table.setPosition(mainMenuTableX, mainMenuTableY);


        final TextButton newGameBtn = new TextButton("New Game", skin);
        table.add(newGameBtn).row();

        final TextButton loadGameBtn = new TextButton("Load Game", skin);
        table.add(loadGameBtn).row();

        final TextButton optionsBtn = new TextButton("Options", skin);
        table.add(optionsBtn).row();

        final TextButton exitBtn = new TextButton("Exit", skin);
        table.add(exitBtn).row();
        stage.addActor(table);

        newGameBtn.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                newGameBtnClicked(event, actor);
            }
        });

        loadGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadGameBtnClicked(event, actor);
            }
        });

        optionsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                optionsBtnClicked(event, actor);
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitBtnClicked(event, actor);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b,
                Color.GREEN.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        background.draw(batch);
        font.draw(batch, "Main Menu", 32, 32);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        // never called automatically
    }

    @Override
    public void hide() {
        // called when current screen changes from this to another screen
    }

    @Override
    public void dispose() {
        Logger.logMsg("disposing of main menu screen");
        stage.dispose();
    }
}
