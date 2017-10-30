package git.hsusa.core.log;

import git.hsusa.core.log.driver.AndroidLogDriver;
import git.hsusa.core.log.driver.SmartLogDriver;

/**
 * Created by triston on 10/27/17.
 */

public class SmartLog {

  // change this to your default behavior
  static final SmartLogDriver platformDriver = new AndroidLogDriver();

  static java.util.HashMap<String, SmartLogContext> contextHashMap = new java.util.HashMap<>();

  // Get/Create a logging context by name
  public static SmartLogContext getContextFor(String oMasterKey) {
    if (contextHashMap.containsKey(oMasterKey)) return contextHashMap.get(oMasterKey);
    else return new SmartLogContext(oMasterKey);
  }

  // Get/Create a logging context by class name
  public static SmartLogContext getContextFor(Class<?> oMasterClass)
    {return getContextFor(oMasterClass.getName());}

  public static SmartLogContext getContextFrom(Object oObject) {
    return getContextFor(oObject.getClass());
  }

}
