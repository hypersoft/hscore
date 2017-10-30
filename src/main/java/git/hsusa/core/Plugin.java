package git.hsusa.core;

import java.util.HashMap;

/**
 * Created by triston on 10/29/17.
 */

/*
    A bare-bones-extensible-plugin-system, with managed-feature-settings.
 */

public class Plugin {

  protected HashMap<String, Object> featureSet = new HashMap<>();

  public boolean getSupportStatus(String key) {return featureSet.containsKey(key) && ((boolean)featureSet.get(key));}
  protected void enableSupportFeature(String key) {
    setSupportFeature(key, true);
  }
  protected void disableSupportFeature(String key) {
    setSupportFeature(key, false);
  }

  public Object getSupportFeature(String key) {
    return featureSet.get(key);
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
