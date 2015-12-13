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
import edu.franklin.practicum.f15.strategygame.Defines;
import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.StrategyGame;

public class OptionsScreen implements Screen {
    private final OrthographicCamera camera;
    private final Skin skin;
    private final StrategyGame game;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final Stage stage;

    public OptionsScreen(StrategyGame inGame) {
        this.game = inGame;
        this.font = game.font;
        this.batch = game.batch;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());

        skin = game.uiSkin;

        stage = new Stage();
    }

    @Override
    public void show() {
        Logger.logMsg("showing new game screen");
        Gdx.input.setInputProcessor(stage);

        Table blTbl = new Table();
        blTbl.setDebug(Defines.DEBUG_UI);
        blTbl.defaults().expand().fill().pad(4f);
        blTbl.setSkin(skin);
        blTbl.setBackground(skin.getDrawable("dght"));
        blTbl.setWidth(75);
        blTbl.setHeight(25);
        blTbl.setPosition(game.SafeSpace, game.SafeSpace);

        final TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("back button clicked");
                backBtnClicked(event, actor);
            }
        });
        blTbl.add(backBtn);
        stage.addActor(blTbl);
    }

    @SuppressWarnings("UnusedParameters")
    private void backBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.prevScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.BROWN.r, Color.BROWN.g, Color.BROWN.b,
                Color.BROWN.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Options", 32, 32);
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

    }
}
