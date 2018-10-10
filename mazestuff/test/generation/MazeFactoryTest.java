package generation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs near identical black box tests for the deterministic and non deterministic versions of all the files
 * @author Katson, Andrew
 *
 */

@RunWith(Suite.class)
@SuiteClasses({MazeFactoryDetTest.class, MazeFactoryNonDetTest.class})
public class MazeFactoryTest {

}