package edu.franklin.practicum.f15.strategygame.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

/* TODO: finish implementing in-game menu */
@SuppressWarnings("UnusedParameters")
public class InGameMenuScreen implements Screen {
    private  OrthographicCamera camera;
    private final Skin skin;
    private  StrategyGame game;
    private final BitmapFont font;
    private  SpriteBatch batch;
    private  Stage stage;

    public InGameMenuScreen(StrategyGame inGame) {
        this.game = inGame;
        this.font = game.font;
        this.batch = game.batch;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());

        skin = game.uiSkin;

        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        float gw = Gdx.graphics.getWidth();
        float gh = Gdx.graphics.getHeight();
        Logger.logMsg("showing main menu");
        Gdx.input.setInputProcessor(stage);

        Table menu = new Table();
        menu.defaults().expandX().fill().pad(4f);
        menu.setWidth(100);
        menu.setHeight(100);
        menu.setPosition((gw - menu.getWidth()) / 2,
                (gh - menu.getHeight()) / 2);
        menu.setDebug(Defines.DEBUG_UI);
        stage.addActor(menu);



        final TextButton backBtn = new TextButton("Back", skin);
        backBtn.setPosition(32, 32);
        menu.add(backBtn).row();
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backBtnClicked(event, actor);
            }
        });

        final TextButton saveGameBtn = new TextButton("Save Game", skin);
        menu.add(saveGameBtn).row();
        saveGameBtn.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                saveGameBtnClicked(event, actor);
            }
        });

        final TextButton loadGameBtn = new TextButton("Load Game", skin);
        menu.add(loadGameBtn).row();
        loadGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadGameBtnClicked(event, actor);
            }
        });

        final TextButton optionsBtn = new TextButton("Options", skin);
        menu.add(optionsBtn).row();
        optionsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                optionsBtnClicked(event, actor);
            }
        });

        final TextButton endGameBtn = new TextButton("End Game", skin);
        menu.add(endGameBtn).row();
        endGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                endGameBtnClicked(event, actor);
            }
        });


        final TextButton exitBtn = new TextButton("Exit Game", skin);
        menu.add(exitBtn).row();
        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitBtnClicked(event, actor);
            }
        });


    }

    private void backBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.gamePlayScreen);
    }

    private void endGameBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.endGameScreen);
    }

    private void exitBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        Gdx.app.exit();
    }

    private void optionsBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.optionsScreen);
    }

    private void loadGameBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.loadGameScreen);
    }

    private void saveGameBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.saveGameScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.CHARTREUSE.r, Color.CHARTREUSE.g, Color.CHARTREUSE.b,
                Color.CHARTREUSE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "In-Game Menu", 32, 32);
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

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
