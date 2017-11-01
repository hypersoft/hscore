package git.hsusa.core.log;

import java.util.Date;

/**
 * Created by triston on 10/29/17.
 */
public class SmartLogItem {

  public static enum MessageType {CRITICAL, WARNING, EXAMINATION;}

  public final SmartLogContext smartLogContext;
  public final Exception fault;
  public final String message;
  public final String componentName;
  public final MessageType messageType;

  private boolean representation = false;

  private long creationTime = new Date().getTime();

  public long getCreationTime() {
    return creationTime;
  }

  private String faultContent = null;

  public String getFaultContent() {
    return faultContent;
  }

  SmartLogItem(SmartLogContext oSmartLogContext, String oComponentName, MessageType oMessageType, String oMessage, Exception oFault) {

    fault = oFault;
    message = oMessage;
    messageType = oMessageType;
    componentName = oComponentName;
    smartLogContext = oSmartLogContext;

    smartLogContext.factoryLogController.writeData(smartLogContext, this);

  }

  /*
   * Create a constructive log entry representation
   */
  public static SmartLogItem createRepresentation(SmartLogContext oSmartLogContext, String oTag, MessageType oMessageType, String oMessage, long oCreationTime, String oFaultContent) {
    SmartLogItem mSmartLogItem = new SmartLogItem(oSmartLogContext, oTag, oMessageType, oMessage, null);
    mSmartLogItem.representation = true;
    mSmartLogItem.creationTime = oCreationTime;
    mSmartLogItem.faultContent = oFaultContent;
    return mSmartLogItem;
  }

  public boolean isRepresentation() {
    return representation;
  }

  public String getSmartTag() {
    return (smartLogContext.getSmartTagFor(this));
  }

}
