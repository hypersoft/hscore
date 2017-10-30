package git.hsusa.core.log.driver;

import java.util.Stack;

import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.SmartLogItem;

/**
 * Created by triston on 10/28/17.
 */

public class JavaStackLogDriver extends SmartLogDriver {

  private Stack<SmartLogItem> logItems;

  JavaStackLogDriver() {

    enableSupportFeature(SmartLogDriver.AUTOMATIC_SERVICE_ACTIVATION);

    dataReader = new DataReader<SmartLogContext, Object>() {
      @Override
      public Stack<SmartLogItem> readData(SmartLogContext context) {
        Stack oLogItems = (Stack)logItems.clone();
        return oLogItems;
      }
    } ;

    dataWriter = new DataWriter<SmartLogContext, Boolean, SmartLogItem>() {
      @Override
      public Boolean writeData(SmartLogContext context, SmartLogItem data) {
        SmartLogItem cache = SmartLogItem.createRepresentation(
          context, data.componentName, data.messageType, data.message,
          data.getCreationTime(), data.getFaultContent());
        logItems.push(cache);
        return true;
      }
    } ;

  }

  @Override
  public Object onPluginActivate(SmartLogContext oSmartLogContext) {
    enableSupportFeature(SmartLogDriver.SERVICE_ONLINE);
    return logItems = new Stack<>();
  }
}
