package git.hsusa.core.log.controller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.SmartLogDriver;
import git.hsusa.core.log.SmartLogItem;
import git.hsusa.core.log.SmartLogItem.MessageType;

/**
 * Created by triston on 10/28/17.
 */

public class AndroidLogController extends SmartLogDriver {

  /*
  *
  * Simple coding logic: a driver, does not add features to a controller, but a controller, adds
  * features to a driver.
  *
  * */

  // http://www.java2s.com/Tutorial/Java/0040__Data-Type/SimpleDateFormat.htm
  private static final String ANDROID_LOG_TIME_FORMAT = "MM-dd kk:mm:ss.SSS";
  private static SimpleDateFormat logCatDate = new SimpleDateFormat(ANDROID_LOG_TIME_FORMAT);

  public AndroidLogController() {

    createSetting(IMPORTS_FOREIGN_SMART_TAGS, true, false); // value: true, readable = (create for publish),
    // but not writable[cause:= 'false']

    dataWriter = new DataWriter<SmartLogContext, Boolean, SmartLogItem>() {

      public Boolean writeData(SmartLogContext runtime, SmartLogItem data) {

        if (data.isRepresentation())
          throw new IllegalArgumentException("data cannot be a representation");

        int platformWritingPlugin = Log.DEBUG; // FALLBACK

        if (data.messageType == MessageType.CRITICAL) {
          Log.e(data.getSmartTag(), data.message); platformWritingPlugin = Log.ERROR;
        } else if (data.messageType == MessageType.WARNING) {
          Log.w(data.getSmartTag(), data.message);
          platformWritingPlugin = Log.WARN;
        } else Log.d(data.getSmartTag(), data.message); // MessageType.(EXAMINATION == DEBUG): FAIL-THROUGH

        if (data.fault != null)
          Log.println(platformWritingPlugin, data.getSmartTag(), Log.getStackTraceString(data.fault));

        return true;

      }

    };

    dataReader = new DataReader<SmartLogContext, Object>() {
      public Stack<String> readData(SmartLogContext runtime) {
        return getLogCat(getLastReadTime(), runtime.getSmartTags());
      }
    };

  }

  /**
   * Get the logcat output in time format from a buffer for a set of log-keys; since a specified time.
   * @param oLogTime time at which to start capturing log data, or null for all data
   * @param oLogKeys logcat tags to capture
   * @return a line input stack.
   */
  private static Stack<String> getLogCat(long oLogTime, Collection<String> oLogKeys) {
    try {

      List<String>sCommand = new ArrayList<String>();
      sCommand.add("logcat");
      sCommand.add("-bmain");
      sCommand.add("-vtime");
      sCommand.add("-s");
      sCommand.add("-d");

      if (oLogTime != 0) { // make this quick...
        // dump-from-date-to-now
        sCommand.add("-T"+logCatDate.format(new Date(oLogTime)));
      }

      for (String item : oLogKeys) sCommand.add(item+":V"); // log level: ALL
      Process process = new ProcessBuilder().command(sCommand).start();

      BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(process.getInputStream()));

      Stack<String> input = new Stack<>();

      if (oLogTime > 0) {
        // Synchronize with "NO YEAR CLOCK" @ unix epoch-year: 1970
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(oLogTime));
        calendar.set(Calendar.YEAR, 1970);
        Date calDate = calendar.getTime();
        oLogTime = calDate.getTime();
      }

      String line = "";
      while ((line = bufferedReader.readLine()) != null) {
        long when = logCatDate.parse(line).getTime();
        if (when > oLogTime) {
          input.push(line);
          break; // stop checking for date matching
        }
      }

      // continue collecting
      while ((line = bufferedReader.readLine()) != null) input.push(line);

      return input;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
