package git.hsusa.core;

import org.junit.Test;

import java.util.Stack;

import git.hsusa.core.log.SmartLog;
import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.controller.AndroidLogController;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

  SmartLogContext runtime = SmartLog.getContextFrom(this);
  @Test
  public void getFactoryDriverName() throws Exception {
    // Context of the app under test.
//    Context appContext = InstrumentationRegistry.getTargetContext();

    assertEquals(AndroidLogController.class.getName(), runtime.getFactoryLogControllerName());

  }

  @Test
  public void getReadWriteStack() {

    assertNotEquals(null, runtime.logExaminationMessage("logExaminationMessage", "test"));

    assertEquals(1, runtime.getSmartTagCount());

    Stack<String> result = (Stack)runtime.readData();

    assertNotEquals(result, null);

  }

}
