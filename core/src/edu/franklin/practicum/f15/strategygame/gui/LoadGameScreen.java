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
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.franklin.practicum.f15.strategygame.*;

import java.util.ArrayList;

@SuppressWarnings("UnusedParameters")
public class LoadGameScreen implements Screen {
    private final OrthographicCamera camera;
    private Skin skin;
    private final StrategyGame game;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final Stage stage;
    private List loadGameList;

    public LoadGameScreen(StrategyGame inGame) {
        game = inGame;
        font = game.font;
        batch = game.batch;
        skin = game.uiSkin;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        skin = game.uiSkin;
        stage = new Stage();
    }

    private void updateSaveGameList()
    {
        SaveGameManager saveGameManager = SaveGameManager.getInstance();
        ArrayList saveGames = (ArrayList)saveGameManager.LoadSaveGames();
        Object[] listItems = saveGames.toArray();
        loadGameList.setItems(listItems);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table loadGameTbl = new Table();
        loadGameTbl.defaults().pad(4f);
        loadGameTbl.setDebug(Defines.DEBUG_UI);
        loadGameTbl.setWidth(400);
        loadGameTbl.setHeight(400);
        loadGameTbl.setPosition((Gdx.graphics.getWidth() - loadGameTbl.getWidth()) / 2,
                (Gdx.graphics.getHeight() - loadGameTbl.getHeight()) / 2);
        loadGameTbl.setSkin(skin);
        loadGameTbl.setBackground(skin.getDrawable("dght"));

        loadGameList = new List(skin);
        updateSaveGameList();
        loadGameTbl.add(loadGameList).expand().fill().colspan(2).row();

        final TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("back button clicked");
                backBtnClicked(event, actor);
            }
        });
        loadGameTbl.add(backBtn).expandX().fill();

        final TextButton loadBtn = new TextButton("Load", skin);
        loadBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("load button clicked");
                SaveGameManager saveGameManager = SaveGameManager.getInstance();
                SaveGame sg = (SaveGame)loadGameList.getSelected();
                if (sg != null) {
                    saveGameManager.loadGame(game, sg);
                    if (game.prevScreen == game.mainMenuScreen || game.prevScreen == game.inGameMenuScreen) {
                        game.setGameScreen(game.loadGameScreen, game.gamePlayScreen);
                    }
                }

            }
        });
        loadGameTbl.add(loadBtn).expandX().fill();
        stage.addActor(loadGameTbl);

    }

    private void backBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.prevScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.ORANGE.r, Color.ORANGE.g, Color.ORANGE.b,
                Color.ORANGE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Load Game", 32, 32);
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
    }
}
