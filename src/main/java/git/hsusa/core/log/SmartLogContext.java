package git.hsusa.core.log;

import java.util.Collection;
import java.util.Date;
import java.util.Stack;

import git.hsusa.core.log.SmartLogItem.MessageType;
import git.hsusa.core.log.driver.SmartLogDriver;

/**
 * Created by triston on 10/29/17.
 */
public class SmartLogContext {

  SmartLogDriver factoryDriver = SmartLog.platformDriver;

  final long creationTime = new Date().getTime();

  public long getCreationTime() {
    return creationTime;
  }

  // for the context tracking in the logging and coding
  final String masterKey;
  private Stack<String> smartTags = new Stack<>();

  private SmartLogContext(Class<?> oMasterClass) {
    this(oMasterClass.getName());
  }

  SmartLogContext(String oMasterKey) {
    this.masterKey = oMasterKey;
    SmartLog.contextHashMap.put(oMasterKey, this);
    setFactoryDriver(SmartLog.platformDriver);
  }

  public boolean setFactoryDriver(SmartLogDriver oFactoryDriver) {
    if (smartTags.size() > 1) throw
      new IllegalAccessError("cannot set factory driver after context logging has commenced");
    factoryDriver = oFactoryDriver;
    return true;
  }

  public String getFactoryDriverName() {
    return factoryDriver.getPluginName();
  }

  public Object readData() {
    return factoryDriver.readData(this);
  }

  public SmartLogItem logCriticalMessage(String byName, String message) {
    return new SmartLogItem(this, byName, MessageType.CRITICAL, message, null);
  }

  public SmartLogItem logCriticalMessage(String byName, String message, Exception failure) {
    return new SmartLogItem(this, byName, MessageType.CRITICAL, message, failure);
  }

  public SmartLogItem logWarningMessage(String byName, String message) {
    return new SmartLogItem(this, byName, MessageType.WARNING, message, null);
  }

  public SmartLogItem logWarningMessage(String byName, String message, Exception failure) {
    return new SmartLogItem(this, byName, MessageType.WARNING, message, failure);
  }

  public SmartLogItem logExaminationMessage(String byName, String message) {
    return new SmartLogItem(this, byName, MessageType.EXAMINATION, message, null);
  }

  public SmartLogItem logExaminationMessage(String byName, String message, Exception failure) {
    return new SmartLogItem(this, byName, MessageType.EXAMINATION, message, failure);
  }

  /*
      SMART TAG SECTION
   */

  private String selectSmartTag(String thisTag) {
    if (smartTags.contains(thisTag)) return thisTag;
    else smartTags.push(thisTag);
    return thisTag;
  }

  public Collection<String> getSmartTags() {
    return smartTags;
  }

  public int getSmartTagCount() {
    return smartTags.size();
  }

  // a reader calls this whenever a new componentName has been found in the log data.
  public void claimSmartTag(String tag) {
    if (tag.startsWith(masterKey)) selectSmartTag(tag); // use any custom master key entry
    else selectSmartTag(masterKey + "." + tag); // create a custom master key entry
  }

  public void importTag(String rawTag) {
    if (factoryDriver.checkSetting(SmartLogDriver.IMPORTS_FOREIGN_SMART_TAGS, true)) selectSmartTag(rawTag);
    else throw new IllegalAccessError("log driver: " + getFactoryDriverName() + " does not accept raw tag imports");
  }

  public void importTags(Collection<String> tags) {
    if (factoryDriver.checkSetting(SmartLogDriver.IMPORTS_FOREIGN_SMART_TAGS, true))
      for (String rawTag: tags) {
        selectSmartTag(rawTag);
    } else
      throw new IllegalAccessError("log driver: " + getFactoryDriverName() + " does not accept raw tag imports");
  }

  String getSmartTagFor(SmartLogItem oSmartLogItem) {
    if (oSmartLogItem.componentName.startsWith(masterKey))
      return selectSmartTag(oSmartLogItem.componentName); // use any custom master key entry
    else
      return selectSmartTag(masterKey + "." + oSmartLogItem.componentName); // create a custom master key entry
  }

  /*
      END SMART TAG SECTION
   */

}
