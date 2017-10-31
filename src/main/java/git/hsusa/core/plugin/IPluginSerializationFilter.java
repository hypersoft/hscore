package git.hsusa.core.plugin;

import git.hsusa.core.json.JSONObject;

/**
 * Created by triston on 10/31/17.
 */

public interface IPluginSerializationFilter {
  /**
  * @param name the name of the setting to compose
  * @return An object which is the value, or null if there is no value.
  **/
  Object onPluginSerialize(String name);
}
