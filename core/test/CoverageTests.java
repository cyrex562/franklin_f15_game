import edu.franklin.practicum.f15.strategygame.StrategyGame;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import java.util.SplittableRandom;

import static org.junit.Assert.*;

//public class CoverageTests
//{
////    @Test
////    public void testStrategyGameInit() {
////        StrategyGame game = new StrategyGame();
////        assertNotNull("game should not be null", game);
////    }
////
////    @Test
////    public void testCreate() {
////        StrategyGame game = new StrategyGame();
////        game.create();
////        assertNotNull("game.batch should not be null", game.batch);
////        assertNotNull("game.font should not be null", game.font);
////    }
////
////    @Test
////    public void testLoadConfiguration() {
////        StrategyGame game = new StrategyGame();
////        game.create();
////        game.loadConfiguration();
////        assertNotEquals("game.WinHeight should not equal -1", -1, game.WinHeight);
////        assertNotEquals("game.WinWidthshould not equal -1", -1, game.WinWidth);
////        assertNotEquals("game.SafeSpace should not equal -1", -1, game.SafeSpace);
////        assertNotEquals("game.OrdersDrainRate should not equal -1", -1, game.OrdersDrainRate);
////        assertNotEquals("game.DefaultMapWidth should not equal -1", -1, game.DefaultMapWidth);
////        assertNotEquals("game.DefaultMapHeight should not equal -1", -1, game.DefaultMapHeight);
////    }
//}