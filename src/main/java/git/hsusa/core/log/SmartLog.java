package git.hsusa.core.log;

import git.hsusa.core.log.controller.AndroidLogController;

/**
 * Created by triston on 10/27/17.
 */

public class SmartLog {

  // change this to your default behavior
  static final SmartLogDriver platformDriver = new AndroidLogController();

  //static final SmartLogDriver platformDriver = (SmartLogDriver)Plugin.create(JavaStackLogDriver.class);

  static java.util.HashMap<String, SmartLogContext> contextHashMap = new java.util.HashMap<>();

  // Get/Create a logging context by name
  public static SmartLogContext getContextFor(String oMasterKey) {
    if (contextHashMap.containsKey(oMasterKey)) return contextHashMap.get(oMasterKey);
    else return new SmartLogContext(oMasterKey);
  }

  // Get/Create a logging context by class name
  public static SmartLogContext getContextFor(Class<?> oMasterClass)
    {return getContextFor(oMasterClass.getName());}

  // Get/Create a logging context by object class name
  public static SmartLogContext getContextFrom(Object oObject) {
    return getContextFor(oObject.getClass());
  }

}
