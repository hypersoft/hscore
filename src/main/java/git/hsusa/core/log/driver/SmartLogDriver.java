package git.hsusa.core.log.driver;

import java.util.Date;

import git.hsusa.core.io.IODriver;
import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.SmartLogItem;

/**
 * Created by triston on 10/29/17.
 */

public abstract class SmartLogDriver extends IODriver<SmartLogContext, Boolean, SmartLogItem>  {

  public static final String SERVICE_ONLINE = "service-online";
  public static final String AUTOMATIC_SERVICE_ACTIVATION = "automatic-driver-activation";

  public static final String IMPORTS_FOREIGN_SMART_TAGS = "imports-foreign-smart-tags";

  private long lastWriteTime, lastReadTime;

  public long getLastWriteTime() {
    return lastWriteTime;
  }
  public long getLastReadTime() {
    return lastReadTime;
  }

  @Override
  public Boolean writeData(SmartLogContext runtime, SmartLogItem output) {
    lastWriteTime = new Date().getTime();
    Boolean status = dataWriter.writeData(runtime, output);
    return status;
  }

  @Override
  public Object readData(SmartLogContext runtime) {
    lastReadTime = new Date().getTime();
    Object value = dataReader.readData(runtime);
    return value;
  }

}
