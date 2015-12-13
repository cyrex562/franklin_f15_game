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
import edu.franklin.practicum.f15.strategygame.Player;
import edu.franklin.practicum.f15.strategygame.StrategyGame;

@SuppressWarnings("UnusedParameters")
public class NewGameScreen implements Screen {
    private final OrthographicCamera camera;
    private final Skin skin;
    private final StrategyGame game;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final Stage stage;
    private TextField mapWidthTF;
    private TextField mapHeightTF;
    private TextField playerNameTF;
	private Label mapWidthStatusLabel;
    private Label mapHeightStatusLabel;
	private Label playerNameStatusLabel;

    public NewGameScreen(StrategyGame inGame) {
        this.game = inGame;
        this.font = game.font;
        this.batch = game.batch;
        this.skin = game.uiSkin;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics
                .getHeight());
        stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table wizTbl = new Table();
        wizTbl.setDebug(Defines.DEBUG_UI);
        wizTbl.defaults().fill().pad(4f);
        wizTbl.setSkin(skin);
        wizTbl.setBackground(skin.getDrawable("dght"));
        wizTbl.setWidth(Gdx.graphics.getWidth() - (game.SafeSpace * 2));
        wizTbl.setHeight(Gdx.graphics.getHeight() - (game.SafeSpace * 3) - 25);
        wizTbl.setPosition(game.SafeSpace, game.SafeSpace * 2 + 25);

        final Label mapWizLabel = new Label("New Game - Options", skin);
        wizTbl.add(mapWizLabel).expandX().colspan(9);
        wizTbl.row();

	    final Label playerNameLabel = new Label("Player Name", skin);
	    wizTbl.add(playerNameLabel).expandX().colspan(1);
	    playerNameTF = new TextField("", skin);
	    playerNameTF.setMessageText("Enter Your Name Here");
	    wizTbl.add(playerNameTF).expandX().colspan(1);
	    playerNameStatusLabel = new Label("", skin);
	    wizTbl.add(playerNameStatusLabel).expandX().colspan(2);
	    wizTbl.row();

        final Label mapWidthLabel = new Label("Map Width", skin);
        wizTbl.add(mapWidthLabel).expandX().colspan(1);
        mapWidthTF = new TextField("", skin);
        mapWidthTF.setText(String.format("%d", game.DefaultMapWidth));
        wizTbl.add(mapWidthTF).expandX().colspan(1);
        mapWidthStatusLabel = new Label("", skin);
        wizTbl.add(mapWidthStatusLabel).expandX().colspan(2);
        wizTbl.row();

        final Label mapHeightLabel = new Label("Map Height", skin);
        wizTbl.add(mapHeightLabel).expandX().colspan(1);
        mapHeightTF = new TextField("", skin);
        mapHeightTF.setText(String.format("%d", game.DefaultMapHeight));
        wizTbl.add(mapHeightTF).expandX().colspan(1);
        mapHeightStatusLabel = new Label("", skin);
        wizTbl.add(mapHeightStatusLabel).expandX().colspan(2);
        wizTbl.row();

        stage.addActor(wizTbl);

        Table blTbl = new Table();
        blTbl.setDebug(Defines.DEBUG_UI);
        blTbl.defaults().expand().fill().pad(4f);
        blTbl.setSkin(skin);
        blTbl.setBackground(skin.getDrawable("dght"));
        blTbl.setWidth(75);
        blTbl.setHeight(25);
        blTbl.setPosition(game.SafeSpace, game.SafeSpace);

        final TextButton backBtn = new TextButton("Back", skin);
        backBtn.setBackground("dght");
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("back button clicked");
                backBtnClicked(event, actor);
            }
        });
        blTbl.add(backBtn);
        blTbl.center();
        stage.addActor(blTbl);

        Table brTbl = new Table();
        brTbl.setDebug(Defines.DEBUG_UI);
        brTbl.defaults().expand().fill().pad(4f);
        brTbl.setSkin(skin);
        brTbl.setBackground(skin.getDrawable("dght"));
        brTbl.setWidth(75);
        brTbl.setHeight(25);
        brTbl.setPosition(Gdx.graphics.getWidth() - brTbl.getWidth() - game.SafeSpace, game.SafeSpace);

        final TextButton playBtn = new TextButton("Play", skin);
        playBtn.setBackground("dght");
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("play button clicked");
                playBtnClicked(event, actor);
            }
        });
        brTbl.add(playBtn);
        stage.addActor(brTbl);

    }

    private void playBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        String mapWidthStr = mapWidthTF.getText();
        String mapHeightStr = mapHeightTF.getText();
        boolean mapHeightValid = false;
        boolean mapWidthValid = false;
        int mapWidth;
        int mapHeight;

        try {
            mapWidth = Integer.parseInt(mapWidthStr);
            if (mapWidth >= Defines.MIN_MAP_WIDTH && mapWidth <= Defines.MAX_MAP_WIDTH) {
                game.MapWidth = mapWidth;
                mapWidthValid = true;
            } else {
                mapWidthStatusLabel.setColor(Color.YELLOW);
                mapWidthStatusLabel.setText(String.format("tileMap width must be between %d and %d", Defines.MIN_MAP_WIDTH, Defines.MAX_MAP_WIDTH));
                Logger.logMsg(String.format("invalid tileMap width: %d out of bounds", mapWidth));
                mapWidthValid = false;
            }
        } catch (NumberFormatException ex) {
            Logger.logMsg(String.format("exception occurred processing tileMap size values: %s", ex.toString()));
            mapWidthStatusLabel.setColor(Color.RED);
            mapWidthStatusLabel.setText("value is not a number");
            Logger.logMsg(String.format("invalid tileMap width: %s is not a number", mapWidthStr));
        }

        try {
            mapHeight = Integer.parseInt(mapHeightStr);
            if (mapHeight >= Defines.MIN_MAP_HEIGHT && mapHeight <= Defines.MAX_MAP_HEIGHT) {
                game.MapHeight = mapHeight;
                mapHeightValid = true;
            } else {
                mapHeightStatusLabel.setColor(Color.YELLOW);
                mapHeightStatusLabel.setText(String.format("tileMap height must be between %d and %d", Defines.MIN_MAP_HEIGHT, Defines.MAX_MAP_HEIGHT));
                Logger.logMsg(String.format("invalid tileMap height: %d out of bounds", mapHeight));
                mapHeightValid = false;
            }
        } catch (NumberFormatException ex) {
            Logger.logMsg(String.format("exception occurred processing tileMap size values: %s", ex.toString()));
            mapHeightStatusLabel.setColor(Color.RED);
            mapHeightStatusLabel.setText("value is not a number");
            Logger.logMsg(String.format("invalid tileMap height: %s is not a number", mapHeightStr));
        }

	    boolean playerNameValid = true;
	    String playerName = playerNameTF.getText();
	    if (playerName.length() == 0) {
		    playerNameStatusLabel.setColor(Color.YELLOW);
		    playerNameStatusLabel.setText("player name cannot be empty");
		    playerNameValid = false;
	    } else {
		    Player player = new Player();
		    player.name = playerName;
		    game.currentPlayer = player;
	    }


        if (mapWidthValid && mapHeightValid && playerNameValid) {
            Logger.logMsg("starting game");
            game.setGameScreen(this, game.gamePlayScreen);
        } else {
	        Logger.logMsg("invalid new game settings");
        }
    }

    private void backBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
        game.setGameScreen(this, game.mainMenuScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b,
                Color.YELLOW.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "New Game", 32, 32);
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
        Logger.logMsg("disposing of new game screen");
        stage.dispose();
    }
}
