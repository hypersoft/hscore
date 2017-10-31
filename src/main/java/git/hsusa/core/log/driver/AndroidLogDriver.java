package git.hsusa.core.log.driver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.SmartLogItem;
import git.hsusa.core.log.SmartLogItem.MessageType;

/**
 * Created by triston on 10/28/17.
 */

public class AndroidLogDriver extends SmartLogDriver {

  // http://www.java2s.com/Tutorial/Java/0040__Data-Type/SimpleDateFormat.htm
  private static final String ANDROID_LOG_TIME_FORMAT = "MM-dd kk:mm:ss.SSS";
  private static SimpleDateFormat logCatDate = new SimpleDateFormat(ANDROID_LOG_TIME_FORMAT);

  public AndroidLogDriver() {

    setBooleanStatus(IMPORTS_FOREIGN_SMART_TAGS, true);

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

      String line = "";
      while ((line = bufferedReader.readLine()) != null) {
        if (logCatDate.parse(line).getTime() > oLogTime) {
          input.push(line);
          break; // stop checking for date matching
        }
      }

      // continue collecting
      while ((line = bufferedReader.readLine()) != null) input.push(line);

      return input;

    } catch (Exception e) {
      return null;
    }
  }

}
