package edu.franklin.practicum.f15.strategygame.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.franklin.practicum.f15.strategygame.Defines;
import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.StrategyGame;

public class EndGameScreen implements Screen {
    private final OrthographicCamera camera;
    private final Skin skin;
    private final StrategyGame game;
    private final Stage stage;

    public EndGameScreen(StrategyGame inGame) {
        this.game = inGame;
        float gw = Gdx.graphics.getWidth();
        float gh = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(gw, gh);
        camera.setToOrtho(false, gw, gh);
        skin = game.uiSkin;
        stage = new Stage();

        Texture tex = new Texture(Gdx.files.internal("ui/game_over.png"));
        Sprite background = new Sprite(tex);
        background.scale(1.0f);
        background.setPosition((gw - background.getWidth()) / 2, (gh - background.getHeight()) / 2);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        final Label playerName = new Label(game.currentPlayer.name, skin);
        int score = game.currentPlayer.score;

        final Label playerScore = new Label("Your Score: " + Integer.toString(score), skin);

        Table wizTbl = new Table();
        wizTbl.setDebug(Defines.DEBUG_UI);
        wizTbl.setSkin(skin);
        wizTbl.setBackground(skin.getDrawable("dght"));
        wizTbl.setWidth(Gdx.graphics.getWidth() / 2);
        wizTbl.setHeight(Gdx.graphics.getHeight() / 2);
        wizTbl.setPosition(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);

        wizTbl.add(playerName).expandX().colspan(9);
        wizTbl.add(playerScore).expandX().colspan(9);
        wizTbl.row();
        stage.addActor(wizTbl);


        Table blTbl = new Table();
        blTbl.setDebug(Defines.DEBUG_UI);
        blTbl.defaults().expand().fill().pad(4f);
        blTbl.setSkin(skin);
        blTbl.setBackground(skin.getDrawable("dght"));
        blTbl.setWidth(120);
        blTbl.setHeight(25);
        blTbl.setPosition(game.SafeSpace, game.SafeSpace);
        final TextButton exitToMenuBtn = new TextButton("Exit To Main Menu", skin);
        exitToMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitToMenuBtnClicked(event, actor);
            }
        });
        blTbl.add(exitToMenuBtn);
        stage.addActor(blTbl);

        Table brTbl = new Table();
        brTbl.setDebug(Defines.DEBUG_UI);
        brTbl.defaults().expand().fill().pad(4f);
        brTbl.setSkin(skin);
        brTbl.setBackground(skin.getDrawable("dght"));
        brTbl.setWidth(120);
        brTbl.setHeight(25);
        brTbl.setPosition(Gdx.graphics.getWidth() - brTbl.getWidth() - game.SafeSpace, game.SafeSpace);
        final TextButton exitToDesktopBtn = new TextButton("Exit To Desktop",
                skin);
        exitToDesktopBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("back button clicked");
                exitToDesktopBtnClicked(event, actor);
            }
        });
        brTbl.add(exitToDesktopBtn);
        stage.addActor(brTbl);
    }

    @SuppressWarnings("UnusedParameters")
    private void exitToDesktopBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        Gdx.app.exit();
    }

    @SuppressWarnings("UnusedParameters")
    private void exitToMenuBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.mainMenuScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.FIREBRICK.r, Color.FIREBRICK.g, Color.FIREBRICK.b, Color.FIREBRICK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
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
