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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.franklin.practicum.f15.strategygame.Defines;
import edu.franklin.practicum.f15.strategygame.Logger;
import edu.franklin.practicum.f15.strategygame.SaveGameManager;
import edu.franklin.practicum.f15.strategygame.StrategyGame;

import java.util.ArrayList;

@SuppressWarnings("UnusedParameters")
public class SaveGameScreen implements Screen {
    private final OrthographicCamera camera;
    private final Skin skin;
    private final StrategyGame game;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final Stage stage;
    private final SaveGameManager saveGameManager;
	private List saveGameList;
	private TextField saveGameNameTF;
	private Label saveGameNameStatusLabel;

    public SaveGameScreen(StrategyGame inGame) {
        this.game = inGame;
        this.font = game.font;
        this.batch = game.batch;
        this.skin = game.uiSkin;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());

        stage = new Stage();
        saveGameManager = SaveGameManager.getInstance();
    }

    @Override
    public void show() {
        Logger.logMsg("showing save game screen");
        Gdx.input.setInputProcessor(stage);

        Table saveGameTbl = new Table();
        saveGameTbl.defaults().pad(4f);
        saveGameTbl.setDebug(Defines.DEBUG_UI);
        saveGameTbl.setWidth(400);
        saveGameTbl.setHeight(400);
        saveGameTbl.setPosition((Gdx.graphics.getWidth() - saveGameTbl.getWidth()) / 2,
                (Gdx.graphics.getHeight() - saveGameTbl.getHeight()) / 2);
        saveGameTbl.setSkin(skin);
        saveGameTbl.setBackground(skin.getDrawable("dght"));

        saveGameList = new List(skin);
	    updateSaveGameList();
        saveGameTbl.add(saveGameList).expand().fill().colspan(9).row();

        final TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("back button clicked");
                backBtnClicked(event, actor);
            }
        });
        saveGameTbl.add(backBtn).fill();

        saveGameNameTF = new TextField("save_game_name", skin);
        saveGameTbl.add(saveGameNameTF).colspan(6).fill();

	    saveGameNameStatusLabel = new Label("", skin);

        final TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
	            saveGameBtnClicked();
            }
        });
        saveGameTbl.add(saveBtn).fill();

        stage.addActor(saveGameTbl);
    }

	private void saveGameBtnClicked()
	{
		// Save Game to disk
		Logger.logMsg("save button clicked");
		String saveGameName = saveGameNameTF.getText();
		if (saveGameName.length() == 0) {
			saveGameNameStatusLabel.setColor(Color.YELLOW);
			saveGameNameStatusLabel.setText("invalid save game name");
		} else {
			saveGameManager.saveGame(game, saveGameName);
			updateSaveGameList();
		}
	}

	private void updateSaveGameList()
	{
		ArrayList saveGames = (ArrayList)saveGameManager.LoadSaveGames();
		Object[] listItems = saveGames.toArray();
		saveGameList.setItems(listItems);
	}

    private void backBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.prevScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.CORAL.r, Color.CORAL.g, Color.CORAL.b,
                Color.CORAL.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Save Game", 32, 32);
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
        Logger.logMsg("disposing of save game screen");
        stage.dispose();
        skin.dispose();
    }
}
