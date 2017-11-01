package git.hsusa.core;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Stack;

import git.hsusa.core.log.SmartLog;
import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.controller.AndroidLogController;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SmartLogContextTests {
  SmartLogContext runtime = SmartLog.getContextFrom(this);

  @Test
  public void getFactoryDriverName() throws Exception {
    // Context of the app under test.
//    Context appContext = InstrumentationRegistry.getTargetContext();

    assertEquals(AndroidLogController.class.getName(), runtime.getFactoryLogControllerName());

  }

  @Test
  public void testLogExaminationMessage() {

    assertNotEquals(null, runtime.logExaminationMessage("logExaminationMessage", "test"));
    testSmartTags();
    testReadData();

  }

  public void testSmartTags() {
    assertEquals(1, runtime.getSmartTagCount());

  }

  public void testReadData() {

    Stack<String> result = (Stack)runtime.readData();
    assertNotNull(result);
    assertNotEquals(true, result.isEmpty());

    // test that we are not getting more data, because we have not written more messages.
    result = (Stack)runtime.readData();
    assertNotNull(result);
    assertEquals(true, result.isEmpty());

  }

}
