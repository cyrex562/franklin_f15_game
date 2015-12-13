package edu.franklin.practicum.f15.strategygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;
import edu.franklin.practicum.f15.strategygame.gui.*;
import edu.franklin.practicum.f15.strategygame.robot.Robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StrategyGame extends Game {
	private int WinWidth = -1;
	private int WinHeight = -1;
	public int DefaultMapWidth = -1;
	public int DefaultMapHeight = -1;
	public int SafeSpace = -1;
	public SpriteBatch batch;
	public BitmapFont font;
	public Skin uiSkin;
	public MainMenuScreen mainMenuScreen;
	public NewGameScreen newGameScreen;
	public GamePlayScreen gamePlayScreen;
	public LoadGameScreen loadGameScreen;
	public OptionsScreen optionsScreen;
	public InGameMenuScreen inGameMenuScreen;
	public SaveGameScreen saveGameScreen;
	public EndGameScreen endGameScreen;
	public Screen prevScreen;
	private final AssetLoader assets = AssetLoader.instance();
	public int MapWidth;
	public int MapHeight;
	public Robot currentRobot;
	public Player currentPlayer;


	private void loadConfiguration() {
		Logger.logMsg("loading configuration file");
		FileHandle fh = Gdx.files.internal("config/config.json");
		String configString = fh.readString();
		JsonValue root = new JsonReader().parse(configString);
		JsonValue graphicsJson = root.get("graphics");
		JsonValue graphicsWindowJson = graphicsJson.get("window");
		WinWidth = graphicsWindowJson.getInt("width", WinWidth);
		WinHeight = graphicsWindowJson.getInt("height", WinHeight);
		SafeSpace = graphicsJson.getInt("safeSpace", SafeSpace);
		JsonValue gameplayJson = root.get("gameplay");
		int ordersDrainRate = gameplayJson.getInt("ordersDrainRate");
		JsonValue defaultMapSizeJson = gameplayJson.get("defaultMapSize");
		DefaultMapWidth = defaultMapSizeJson.getInt("width", DefaultMapWidth);
		DefaultMapHeight = defaultMapSizeJson.getInt("height", DefaultMapHeight);
		MapWidth = DefaultMapWidth;
		MapHeight = DefaultMapHeight;
	}

	private void initSkin() {
		uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

		Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pm.setColor(Color.WHITE);
		pm.fill();
		uiSkin.add("white", new Texture(pm));

		uiSkin.add("default", new BitmapFont());

		Texture tex = new Texture(Gdx.files.internal("ui/dark_gray.png"));
		NinePatch dgnp = new NinePatch(tex, 10, 10, 10, 10);
		uiSkin.add("dght", dgnp);

		TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
		tbs.up = uiSkin.newDrawable("white", Color.DARK_GRAY);
		tbs.down = uiSkin.newDrawable("white", Color.DARK_GRAY);
		tbs.checked = uiSkin.newDrawable("white", Color.BLUE);
		tbs.over = uiSkin.newDrawable("white", Color.LIGHT_GRAY);
		tbs.font = uiSkin.getFont("default");
		uiSkin.add("default", tbs);

//        logBuffer = "";
	}

	public boolean verifyLicense() {
		boolean result = false;
		// TODO: Load Information from license.json file
		FileHandle fh = Gdx.files.internal("config/license.json");
		String configString = fh.readString();
		JsonValue root = new JsonReader().parse(configString);
		String license = root.getString("licenseKey", "");
		String uniqueID = root.getString("uniqueId", "");

		String params = "response=verify&license=LICENSE&id=UNIQUE_ID";

		if (license.length() <= 0) {
			Logger.logMsg("failed to get licenseKey from license.JSON");
			return false;
		}

		if (uniqueID.length() <= 0) {
			Logger.logMsg("failed to get uniqueID from license.JSON");
			return false;
		}


		params = params.replaceFirst("LICENSE", license);
		params = params.replaceFirst("UNIQUE_ID", uniqueID);

		try {

			String licenseServerURL = "http://f15slic.franklinpracticum.com/api/license_response.php";
			URL url = new URL(licenseServerURL + "?" + params);
			byte[] postData = params.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String response;
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			response = sb.toString();
			if (response.contains("<valid_key>true")) {
				result = true;
				Logger.logMsg("license key is valid");
			} else {
				Logger.logMsg("license key is invalid");
			}
			conn.disconnect();
		} catch (Exception exc) {
			Logger.logMsg("exception occurred validating license: " + exc.toString());
		}

		return result;
	}

	private void initScreens() {
		GameLoadingScreen gameLoadingScreen = new GameLoadingScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		newGameScreen = new NewGameScreen(this);
		gamePlayScreen = new GamePlayScreen(this);
		loadGameScreen = new LoadGameScreen(this);
		optionsScreen = new OptionsScreen(this);
		inGameMenuScreen = new InGameMenuScreen(this);
		saveGameScreen = new SaveGameScreen(this);
		endGameScreen = new EndGameScreen(this);
		prevScreen = null;

		this.setScreen(gameLoadingScreen);
	}

	public void setGameScreen(Screen prevScreen, Screen nextScreen) {
		this.prevScreen = prevScreen;
		this.setScreen(nextScreen);
	}

	@Override
	public void create() {
		AssetManager assetManager = new AssetManager();

		assets.init(assetManager);


		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);

		loadConfiguration();

		Gdx.graphics.setDisplayMode(WinWidth, WinHeight, false);

		Logger.logMsg(String.format("game width: %d, game height: %d", Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		initSkin();

		initScreens();
	}

	@Override
	public void dispose() {
		assets.dispose();
		batch.dispose();
		font.dispose();
	}
}
