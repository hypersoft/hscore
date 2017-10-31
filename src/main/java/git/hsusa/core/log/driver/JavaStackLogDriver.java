package git.hsusa.core.log.driver;

import java.util.Stack;

import git.hsusa.core.log.SmartLogContext;
import git.hsusa.core.log.SmartLogItem;
import git.hsusa.core.plugin.IPluginLoader;

/**
 * Created by triston on 10/28/17.
 */

public class JavaStackLogDriver extends SmartLogDriver implements IPluginLoader<SmartLogContext> {

  private Stack<SmartLogItem> logItems;

  JavaStackLogDriver() {

    setBooleanStatus(SmartLogDriver.AUTOMATIC_SERVICE_ACTIVATION, true);

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
  public void onLoadPlugin(SmartLogContext loadable) {
    putSetting(SmartLogDriver.SERVICE_ONLINE, true);
    logItems = new Stack<>();

  }
}
