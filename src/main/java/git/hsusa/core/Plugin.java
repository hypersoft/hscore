package git.hsusa.core;

import java.util.HashMap;

/**
 * Created by triston on 10/29/17.
 */

public class Plugin {

  protected HashMap<String, Object> featureSet = new HashMap<>();

  public Object getSupportFeature(String key) {
    return featureSet.get(key);
  }

  public boolean getSupportStatus(String key) {
    return featureSet.containsKey(key) && ((boolean)featureSet.get(key));
  }

  protected void enableSupportFeature(String key) {
    setSupportFeature(key, true);
  }

  protected void disableSupportFeature(String key) {
    setSupportFeature(key, false);
  }

  protected void setSupportFeature(String key, Object value) {
    featureSet.put(key, value);
  }

  public String getPluginName() {
    return getClass().getName();
  }

  @Override
  public String toString() {
    return getPluginName();
  }

}
