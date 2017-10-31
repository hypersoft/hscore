package git.hsusa.core.plugin;

import git.hsusa.core.json.JSONObject;

/**
 * Created by triston on 10/30/17.
 */
public interface IPluginSettingsController {
  void onPutSetting(String name, Object value);

  Object onGetSetting(String name);
}
