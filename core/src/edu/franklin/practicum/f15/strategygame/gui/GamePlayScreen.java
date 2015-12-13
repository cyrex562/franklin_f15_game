package edu.franklin.practicum.f15.strategygame.gui;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import edu.franklin.practicum.f15.strategygame.*;
import edu.franklin.practicum.f15.strategygame.interaction.ActionQueue;
import edu.franklin.practicum.f15.strategygame.interaction.ActionType;
import edu.franklin.practicum.f15.strategygame.interaction.RobotAction;
import edu.franklin.practicum.f15.strategygame.map.Item;
import edu.franklin.practicum.f15.strategygame.map.MapManager;
import edu.franklin.practicum.f15.strategygame.map.RandomEncounter;
import edu.franklin.practicum.f15.strategygame.map.TerrainType;
import edu.franklin.practicum.f15.strategygame.robot.Robot;


public class GamePlayScreen implements Screen {
    public static final int SAND_DELAY = 1000;
    private static final float DEBUG_UDPATE_RATE = 1.0f;
	private static final float STATUS_UPDATE_RATE = 1.0f;
    public static final int GRASS_SPEED = 4;
    public static final int GRASS_DELAY = 0;
    public static final int SAND_SPEED = 3;
    public static final int BRUSH_SPEED = 2;
    public static final int BRUSH_DELAY = 2000;
    public static final int SWAMP_SPEED = 1;
    public static final int SWAMP_DELAY = 3000;
    private final OrthographicCamera camera;
    private final StrategyGame game;
    private final Skin skin;
    private HexagonalTiledMapRenderer mapRenderer;
    private final OrthoCamController cameraController;
    private final Stage stage;
    private Label debugLabel;
    private Table debugMenu;
    private float timeSinceLastDebugUpdate;
    private float timeSinceLastOrdersPop;
	private float timeSinceStatusUpdate;
    private PlayPausedState playPausedState = PlayPausedState.PAUSED;
    private List<String> ordersList;
    private final ActionQueue actionQueue = new ActionQueue();
    private Robot robot;
    private final MapManager mapManager;
    private Queue moveQueue;
	private Label robotPos;
	private Label playerScore;
	private Label robotMoves;
	private Label robotHealth;
	private Label robotWeight;
	private Label robotSpeed;
	private Dialog terrainDialog;
	private Dialog mapedgeDialog;
	private boolean hasItem;
	private Dialog itemDialog;
	private Dialog randomEncDialog;
	private boolean hasRandomEnc;
	private Dialog midPointDialog;
	private boolean midPointFound;
	private Dialog endPointDialog;
	private boolean endPointFound;
    private Table tcTbl;

    public GamePlayScreen(StrategyGame inGame) {
        this.game = inGame;
        mapManager = MapManager.getInstance();
        moveQueue = new LinkedList<RobotAction>();
        
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        cameraController = new OrthoCamController(camera);

        skin = game.uiSkin;
        stage = new Stage(new ScreenViewport());
    }

    private void initUI() {
        Logger.logMsg("init game play screen UI");
        genSideMenuUI();
        genStateUI();
        genDebugMenu();
        genDialogs();
    }

    private void genSideMenuUI() {
        ordersList = new List<String>(skin);
        String[] orders = actionQueue.getStringActionArray();
        ordersList.setItems(orders);

        final TextField textField = new TextField("", skin);

        TextButton moveUpBtn = new TextButton("UP", skin);
        moveUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveUpBtnTriggered();
            }
        });

        TextButton moveDownBtn = new TextButton("DN", skin);
        moveDownBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveDownBtnTriggered();
            }
        });

        TextButton delOrderBtn = new TextButton("DEL", skin);
        delOrderBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                delOrderBtnTriggered();
            }
        });

        TextButton addOrderBtn = new TextButton("+", skin);
        /* when the add orders btn is changed, get the contents of the orders
         text field, then add it to the end of the items list and re-set the
          list */
        addOrderBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addOrderBtnTriggered(textField);
            }
        });


        TextButton togglePauseBtn = new TextButton("Play/Pause", skin);
        togglePauseBtn.setHeight(25);
        togglePauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                togglePauseBtnTriggered(tcTbl);
            }
        });


        ScrollPane scrollPane = genStatusTableUI();

        final TextButton menuBtn = new TextButton("Menu", skin);
        menuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuBtnClicked(event, actor);
            }
        });


        Table sideMenu = new Table();
        sideMenu.defaults().fill().pad(4f);
        sideMenu.setDebug(Defines.DEBUG_UI);
        sideMenu.setWidth(Gdx.graphics.getWidth() * 0.25f);
        sideMenu.setHeight(Gdx.graphics.getHeight() - game.SafeSpace * 2);
        sideMenu.setPosition(Gdx.graphics.getWidth() - sideMenu.getWidth() - game.SafeSpace, game.SafeSpace);
        sideMenu.setSkin(skin);
        sideMenu.setBackground(skin.getDrawable("dght"));
        sideMenu.add(menuBtn).expandX().colspan(9);
        sideMenu.row();
        sideMenu.add(ordersList).expand().colspan(9);
        sideMenu.row();
        sideMenu.add(moveUpBtn).expandX().colspan(3);
        sideMenu.add(moveDownBtn).expandX().colspan(3);
        sideMenu.add(delOrderBtn).expandX().colspan(3);
        sideMenu.row();
        sideMenu.add(textField).expandX().colspan(8);
        sideMenu.add(addOrderBtn).colspan(1);
        sideMenu.row();
        sideMenu.add(togglePauseBtn).expandX().colspan(9);
        sideMenu.row();
        sideMenu.add(scrollPane).expand().colspan(9);
        sideMenu.row();
        sideMenu.layout();

        stage.addActor(sideMenu);
    }

    private void delOrderBtnTriggered() {
        Logger.logMsg("delete order button clicked");

        int selItemIdx = ordersList.getSelectedIndex();
        actionQueue.removeAction(selItemIdx);
        ordersList.setItems(actionQueue.getStringActionArray());
    }

    private void moveDownBtnTriggered() {
        Logger.logMsg("move order down button clicked");
        int selItemIdx = ordersList.getSelectedIndex();
        actionQueue.moveDown(selItemIdx);
        ordersList.setItems(actionQueue.getStringActionArray());
    }

    private void moveUpBtnTriggered() {
        Logger.logMsg("move order up button clicked");
        int selItemIdx = ordersList.getSelectedIndex();
        actionQueue.moveUp(selItemIdx);
        ordersList.setItems(actionQueue.getStringActionArray());
    }

    private void genStateUI() {
        tcTbl = new Table();
        tcTbl.setDebug(Defines.DEBUG_UI);
        tcTbl.setSkin(skin);
        tcTbl.defaults().expand().fill().pad(4f);
        tcTbl.setWidth(75);
        tcTbl.setHeight(25);
        tcTbl.setPosition(
                Gdx.graphics.getWidth() / 2 - tcTbl.getWidth(),
                Gdx.graphics.getHeight() - game.SafeSpace - tcTbl.getHeight());

        Label pausedLabel = new Label("Paused", skin);
        pausedLabel.setColor(Color.RED);
        tcTbl.add(pausedLabel);
        stage.addActor(tcTbl);
        if (playPausedState == PlayPausedState.PAUSED) {
            tcTbl.setVisible(true);
        } else {
            tcTbl.setVisible(false);
        }
    }

    private void genDialogs() {
        terrainDialog = new Dialog("Impassable Terrain", skin) {
            {
                text("The robot cannot move further in this direction.");
                button("OK", "ok");
            }
        };

        mapedgeDialog = new Dialog("World's End", skin) {
        	{
        		text("The edge of the tileMap has been reached.");
        		button("OK", "ok");
        	}
        };

        itemDialog = new Dialog("Item Found!", skin) {
        	{
        		text("Pick up and open item that may have positive or negative results?");
        		button("Yes", "y");
        		button("No", "n");
        	}

        	@Override
        	protected void result(Object object) {
        		if (object.equals("y")) {
        			Item item = mapManager.getItem(robot.currPosition);
        			final String display = mapManager.processItem(item, robot, game);

        			new Dialog("Item Used", skin) {
        				{
        					text(display);
        					button("OK");
        				}
        			}.show(stage);
        		}
        		else {
        			new Dialog("Item Not Used", skin) {
        				{
        					text("Item was not picked up and opened.");
        					button("OK");
        				}
        			}.show(stage);
        		}
        	}

        };

        randomEncDialog = new Dialog("Random Encounter Found!", skin) {
        	{
        		text("The robot has encountered...");
        		button("Continue", "ok");
        	}

        	@Override
        	protected void result(Object object) {
    			RandomEncounter re = mapManager.getRandomEncounter(robot.currPosition);
    			final String displayRE = mapManager.processRandomEncounter(re, robot, game);

    			new Dialog("Random Encounter Found!", skin) {
    				{
    					text(displayRE);
    					button("OK");
    				}
    			}.show(stage);
        	}
        };

        midPointDialog = new Dialog("Midpoint Found!", skin) {
        	{
        		text("You have been awarded 100 points!");
        		button("OK", "ok");
        	}

        	@Override
        	protected void result(Object object) {
    			game.currentPlayer.score += 100;
    			mapManager.showFoundMidpoint(robot.midPoint);
        	}
        };

        endPointDialog = new Dialog("End Point Found!", skin) {
        	{
        		text("You have been awarded 200 points!");
        		button("OK", "ok");
        	}

        	@Override
        	protected void result(Object object) {
    			game.currentPlayer.score += Defines.END_POINT_SCORE;
                game.setGameScreen(game.gamePlayScreen, game.endGameScreen);
        	}
        };
    }

    private void genDebugMenu() {
        debugMenu = new Table();
        debugMenu.defaults().pad(4f);
        debugMenu.setHeight(200);
        debugMenu.setWidth(400);
        debugMenu.setPosition(game.SafeSpace, Gdx.graphics.getHeight() - debugMenu.getHeight() - game.SafeSpace);
        debugMenu.setDebug(Defines.DEBUG_UI);
        debugMenu.setSkin(skin);
        debugMenu.setBackground(skin.getDrawable("dght"));

        debugLabel = new Label(Logger.logBuffer, skin);
        ScrollPane debugTextSP = new ScrollPane(debugLabel, skin);
        debugTextSP.setHeight(100);
        debugMenu.add(debugTextSP).colspan(2).row();

        final TextField commandTF = new TextField("", skin);
        debugMenu.add(commandTF).expandX().colspan(1);
        Button runCmdBtn = new TextButton("+", skin);
        runCmdBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String command = commandTF.getText();
                // TODO: enable/disable fog of war.
            }
        });
        debugMenu.add(runCmdBtn).expandX();
        debugMenu.setVisible(false);
        stage.addActor(debugMenu);

        final Table ulTbl = new Table();
        ulTbl.setDebug(Defines.DEBUG_UI);
        ulTbl.defaults().expand().fill().pad(4f);
        ulTbl.setSkin(skin);
        ulTbl.setBackground(skin.getDrawable("dght"));
        ulTbl.setWidth(75);
        ulTbl.setHeight(25);
        ulTbl.setPosition(game.SafeSpace, Gdx.graphics.getHeight() - ulTbl.getHeight() - game.SafeSpace);
        final TextButton debugBtn = new TextButton("debug", skin);
        debugBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logMsg("debug button clicked");
                if (debugMenu.isVisible()) {
                    debugMenu.setVisible(false);
                    ulTbl.setPosition(ulTbl.getX(), ulTbl.getY() + debugMenu.getHeight());
                } else {
                    debugMenu.setVisible(true);
                    ulTbl.setPosition(ulTbl.getX(), ulTbl.getY() - debugMenu.getHeight());
                }
            }
        });
        ulTbl.add(debugBtn);
        stage.addActor(ulTbl);
    }

    private ScrollPane genStatusTableUI() {
        Table statusTable = new Table();
        statusTable.setDebug(Defines.DEBUG_UI);
        statusTable.setSkin(skin);

        Label scrollTableTitle = new Label("status", skin);
        statusTable.add(scrollTableTitle).expandX().colspan(2);
        statusTable.row();

        Label robotMovesLabel = new Label("robot moves", skin);
        statusTable.add(robotMovesLabel).expandX().colspan(1);
        robotMoves = new Label("", skin);
        statusTable.add(robotMoves).expandX().colspan(1);
        statusTable.row();

        Label robotPosLabel = new Label("robot position", skin);
        statusTable.add(robotPosLabel).expandX().colspan(1);
        robotPos = new Label("", skin);
        statusTable.add(robotPos).expandX().colspan(1);
        statusTable.row();

        Label playerScoreLabel = new Label("player score", skin);
        statusTable.add(playerScoreLabel).expandX().colspan(1);
        playerScore = new Label("", skin);
        statusTable.add(playerScore).expandX().colspan(1);
        statusTable.row();

        Label robotHealthLabel = new Label("robot health", skin);
        statusTable.add(robotHealthLabel).expandX().colspan(1);
        robotHealth = new Label("", skin);
        statusTable.add(robotHealth).expandX().colspan(1);
        statusTable.row();

        Label robotWeightLabel = new Label("robot weight", skin);
        statusTable.add(robotWeightLabel).expandX().colspan(1);
        robotWeight = new Label("", skin);
        statusTable.add(robotWeight).expandX().colspan(1);
        statusTable.row();

        Label robotSpeedLabel = new Label("robot speed", skin);
        statusTable.add(robotSpeedLabel).expandX().colspan(1);
        robotSpeed = new Label("", skin);
        statusTable.add(robotSpeed).expandX().colspan(1);
        statusTable.row();

        ScrollPane scrollPane = new ScrollPane(statusTable, skin);
        scrollPane.setHeight(100);
        return scrollPane;
    }

    private void togglePauseBtnTriggered(Table tcTbl) {
        if (playPausedState == PlayPausedState.PAUSED) {
            playPausedState = PlayPausedState.PLAY;
            tcTbl.setVisible(false);
        } else {
            playPausedState = PlayPausedState.PAUSED;
            tcTbl.setVisible(true);
        }
    }

    public void togglePause() {
        if (playPausedState == PlayPausedState.PAUSED) {
            playPausedState = PlayPausedState.PLAY;
            tcTbl.setVisible(false);
        } else {
            playPausedState = PlayPausedState.PAUSED;
            tcTbl.setVisible(true);
        }
    }

    private void addOrderBtnTriggered(TextField textField) {
        String order = textField.getText();
        actionQueue.addAction(order);
        ordersList.setItems(actionQueue.getStringActionArray());
    }

    @Override
    public void show() {
        Logger.logMsg("game play screen show()");
        timeSinceLastDebugUpdate = 0;
        timeSinceLastOrdersPop = 0;
        InputMultiplexer imux = new InputMultiplexer();
        imux.addProcessor(stage);
        imux.addProcessor(cameraController);
        initUI();

        Gdx.input.setInputProcessor(imux);

        if (game.prevScreen == game.newGameScreen) {
	        Logger.logMsg("creating tileMap");
            game.currentRobot = new Robot();
            robot = game.currentRobot;
            mapManager.createGameWorld(game, robot);
        } else if (game.prevScreen == game.loadGameScreen) {
	        Logger.logMsg("loading tileMap");
	        robot = game.currentRobot;
        } else {
	        robot = game.currentRobot;
        }

        mapRenderer = new HexagonalTiledMapRenderer(MapManager.gameMap.tileMap);
    }

    @SuppressWarnings("UnusedParameters")
    private void menuBtnClicked(ChangeListener.ChangeEvent event, Actor actor) {
	    game.currentRobot = robot;
        game.setGameScreen(this, game.inGameMenuScreen);
    }

	private void updatePlayerScore() {
		playerScore.setText(String.format("%d", game.currentPlayer.score));
	}



    @Override
    public void render(float delta) {
        // Update timers
        timeSinceLastDebugUpdate += delta;
        timeSinceLastOrdersPop += delta;
	    timeSinceStatusUpdate += delta;


        // update the camera and its projection matrix
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // update the debug log view
        updateDebugLogView();

        // check that robot health is not zero or below, or that robot is overencumbered.
        // If so, then game over.
        if (robot.currHealth < 1 || robot.currWeight > 100) {
            game.setGameScreen(this, game.endGameScreen);
        }

        // Need to ask user about found item before move to next tile.
		if (hasItem) {
			Logger.logMsg("this tile has item, launching dialog");
			hasItem = false;
            togglePause();
			itemDialog.show(stage);
		}
		// Alert user that random encounter was found.
		else if (hasRandomEnc) {
			Logger.logMsg("random encounter found, launching dialog");
			hasRandomEnc = false;
            togglePause();
			randomEncDialog.show(stage);
		}
		// Alert user that midpoint was found.
		else if (midPointFound) {
			Logger.logMsg("midpoint found, launching dialog");
			midPointFound = false;
            togglePause();
			midPointDialog.show(stage);
		}
		// Alert user that end point was found.
		else if (endPointFound) {
			Logger.logMsg("endpoint foud, launching dialog");
			endPointFound = false;
            togglePause();;
			endPointDialog.show(stage);
		}
        
    	// If moveQueue is not empty, process next mini-move
    	if (moveQueue.peek() != null) {
    		RobotAction nextAction = (RobotAction) moveQueue.remove();
    		Orientation moveDir = nextAction.direction;
            Logger.logMsg("inside moveQueue loop");
            System.out.println("current position: " + robot.currPosition.xPos + ", " + robot.currPosition.yPos);

    		// If next movement stays on game tileMap and is passable terrain
    		if ((mapManager.verifyMapMovementDistance(robot, 1, moveDir))) {
    	        Logger.logMsg("next move is inside tileMap");
    			GamePosition futurePos = mapManager.nextPosition(robot, nextAction.direction);
    			TerrainType terrain = mapManager.getTerrainType(futurePos);
    			
    			// Sleeps thread based on terrain type.
    			if (terrain.equals(TerrainType.GRASS) || terrain.equals(TerrainType.SAND)
    				|| terrain.equals(TerrainType.BRUSH) || terrain.equals(TerrainType.SWAMP)) {
    		        Logger.logMsg("next tile is passable");


                    int speed = -1;
                    int delay = 0;

                    if (terrain == TerrainType.GRASS) {
                        speed = GRASS_SPEED;
                        delay = GRASS_DELAY;
                    } else if (terrain == TerrainType.SAND) {
                        speed = SAND_SPEED;
                        delay = SAND_DELAY;
                    } else if (terrain == TerrainType.BRUSH) {
                        speed = BRUSH_SPEED;
                        delay = BRUSH_DELAY;
                    } else if (terrain == TerrainType.SWAMP) {
                        speed = SWAMP_SPEED;
                        delay = SWAMP_DELAY;
                    }
                    robot.setSpeed(speed);
                    try {
                        Thread.sleep(delay);
                    } catch(InterruptedException ie) {
                        Logger.logMsg("delay interrupted");
                    }

                    robot.ProcessAction(nextAction);
               		mapManager.changeRobotPosition(robot);
                    Logger.logMsg("move successful");
                    if (mapManager.getHasItem(robot.currPosition)) {
                    	hasItem = true;
                    }
                    else if (mapManager.getHasRE(robot.currPosition)) {
                    	hasRandomEnc = true;
                    }
                    else if (robot.currPosition.xPos == robot.midPoint.xPos &&
                    			robot.currPosition.yPos == robot.midPoint.yPos) {
                    	midPointFound = true;
                    }
                    else if (robot.currPosition.xPos == robot.endPoint.xPos &&
                			robot.currPosition.yPos == robot.endPoint.yPos) {
                    	endPointFound = true;
                    }
    			}
    			else {
        			// Remove rest of mini-moves from queue as the rest will be invalid
        			moveQueue = new LinkedList<RobotAction>();
        			terrainDialog.show(stage);
        	        Logger.logMsg("Impassable terrain reached. moveQueue cleared");
                    System.out.println("current position: " + robot.currPosition.xPos + ", " 
                    		+ robot.currPosition.yPos);
    			}
    		}
    		else {
    			// Remove rest of mini-moves from queue as the rest will be invalid
    			moveQueue = new LinkedList<RobotAction>();
    			mapedgeDialog.show(stage);
    	        Logger.logMsg("End of world reached. moveQueue cleared");
                System.out.println("current position: " + robot.currPosition.xPos + ", " 
                		+ robot.currPosition.yPos);
    		}
    	}
        // pop orders from the queue and process them as normal
    	else if (timeSinceLastOrdersPop > Defines.ORDERS_POP_RATE) {
    		timeSinceLastOrdersPop = 0;
    		if (playPausedState == PlayPausedState.PLAY
    				&& ordersList.getItems().size > 0) {
    			//pops next action from the queue and gives it to robot
               	/*robots next action = */
    			RobotAction nextAction = actionQueue.getNextAction();
    			if (nextAction.actionType == ActionType.MOVE) {
    				moveQueue = mapManager.createMoveQueue(nextAction);
    		        Logger.logMsg("created new moveQueue");
    				ordersList.setItems(actionQueue.getStringActionArray());
    			}
    			else {
//    				try {
//    					robot.ProcessAction(nextAction);
//    				}
//    				catch (InterruptedException e) {
//    					System.out.println("Interrupted Exception thrown.");
//    				}
                    robot.ProcessAction(nextAction);
    				ordersList.setItems(actionQueue.getStringActionArray());
                    if (nextAction.actionType == ActionType.TURN) {
                        mapManager.turnRobot(robot);
                    }                    
    			}
    		}
    	}   

         // clear the screen
        Gdx.gl.glClearColor(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b,
                Color.CYAN.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the tileMap
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Draw the UI
	    if (timeSinceStatusUpdate > STATUS_UPDATE_RATE) {
		    updatePlayerScore();
		    updateRobotPosition();
		    updateRobotMoves();
		    updateRobotHealth();
		    updateRobotWeight();
		    updateRobotSpeed();
	    }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void updateDebugLogView() {
        if (timeSinceLastDebugUpdate > DEBUG_UDPATE_RATE) {
            debugLabel.setText(Logger.logBuffer);
            timeSinceLastDebugUpdate = 0;
        }
    }

    private void updateRobotMoves() {
		robotMoves.setText(String.format("Moves: %d", robot.moveCount));
	}

	private void updateRobotPosition() {
		robotPos.setText(String.format("X: %d, Y: %d", robot.currPosition.xPos,
				robot.currPosition.yPos));
	}
	
	private void updateRobotHealth() {
		robotHealth.setText(String.format("Health: %d/100", robot.currHealth));
	}
	
	private void updateRobotWeight() {
		robotWeight.setText(String.format("Weight: %d/100", robot.currWeight));
	}
	
	private void updateRobotSpeed() {
        robotSpeed.setText(String.format("Speed: %d", robot.getSpeed()));
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

    enum PlayPausedState {
        PLAY,
        PAUSED
    }
}
