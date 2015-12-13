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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.franklin.practicum.f15.strategygame.Defines;
import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.StrategyGame;

public class GameLoadingScreen implements Screen {
    private final OrthographicCamera camera;
    private final StrategyGame game;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final Sprite background;
    private final Stage stage;
    private boolean licenseVerified;

    public GameLoadingScreen(StrategyGame inGame) {

        this.game = inGame;
        this.font = game.font;
        this.batch = game.batch;

        stage = new Stage(new ScreenViewport());

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());

        Texture tex = new Texture(Gdx.files.internal("ui/game_loading.png"));
        background = new Sprite(tex);
        background.scale(1.0f);
        background.setPosition((Gdx.graphics.getWidth() - background.getWidth()) / 2,
                (Gdx.graphics.getHeight() - background.getHeight()) / 2);
    }

    @Override
    public void show() {
        if (Defines.DEMO_MODE == false) {
            licenseVerified = game.verifyLicense();
        } else {
            licenseVerified = true;
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, Color.BLUE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Loading Game", 32, 32);
        background.draw(batch);
        batch.end();

        if (licenseVerified) {
            if (Gdx.input.isTouched()) {
                Logger.logMsg("transitioning to main menu screen");
                game.setGameScreen(this, game.mainMenuScreen);
                dispose();
            }
        } else {
            Dialog dialog = new Dialog("license key invalid", game.uiSkin) {
                protected void result(Object object) {
                    Logger.logMsg("exiting");
                    Gdx.app.exit();
                }
            };
            dialog.text("Your license key is invalid, exiting...");
            dialog.button("OK", true);
            dialog.show(stage);
        }

    }

    @Override
    public void resize(int width, int height) {

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
