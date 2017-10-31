package git.hsusa.core.plugin;

import git.hsusa.core.json.JSONObject;

/**
 * Created by triston on 10/31/17.
 */

public interface IPluginSerializationFilter {
  /*
  * @return raw composed objects
  *
  * */
  Object onPluginSerialize(String name);
}
