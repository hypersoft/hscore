package git.hsusa.core.plugin;

import java.util.Set;

/**
 * Created by triston on 10/30/17.
 */

/**
 * Public Interface
 */
public interface IPlugin {
  String getPluginName();
  Object getPluginLoader();
  Set<String> getSettingNames();
  Object getSetting(String name);
  void putSetting(String name, Object value);
  boolean checkSetting(String name, Object value);
  String toJSONString();
  String toJSONString(int depth);
}
