package git.hsusa.core.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Logger;

import git.hsusa.core.json.JSONObject;
import git.hsusa.core.json.JSONString;

import static java.lang.Class.forName;

/**
 * Created by triston on 10/29/17.
 */


/*
    A bare-bones-extensible-plugin-system.

    Features: Plugins and Plugin loaders with startup "bundles" and object forwarding,
    serializable settings, scriptable command interface, & property access controller.

    There is also a secure property sharing implementation, with simple set-it-and-go-configuration.
    You can keep your variables private by using the settings object directly. However, if you
    call createSetting(NAME, VALUE, WRITEABLE), that setting will be registered in the known value
    types registry, which will enable external access and type checking through get/put setting or
    boolean configuration. if the setting is not WRITEABLE == TRUE, then external put is filtered for
    the setting.

    private settings currently are; but should not be; exported in the serialization.

 */

public class Plugin implements IPlugin, JSONString {

  static Logger logger = Logger.getLogger("Plugin");

  protected Object pluginLoader = null;
  protected JSONObject settings = new JSONObject();
  private HashMap<String, Class> knownSettings = new HashMap<>();
  private HashMap<String, Boolean> writableSettings = new HashMap<>();

  protected Plugin() {}

  public static Object create(Class<? extends Plugin> plugin) {
    return create(plugin, null, null);
  }

  public static Object create(String className) {
    return create(className, null, null);
  }

    public static Object create(String className, Object loader, Object bundle) {
    Class<? extends Plugin> plugin = null;
    try {
      plugin = (Class<? extends Plugin>)forName(className);
    } catch (ClassNotFoundException oE) {
      oE.printStackTrace();
    }
    return create(plugin, loader, bundle);
  }

  public static Object create(Class<? extends Plugin> plugin, Object loader, Object bundle) {

    Plugin pluginInstance = null;

    if (plugin.getEnclosingClass() != null && ! Modifier.isStatic(plugin.getModifiers())) {
      logger.severe(plugin.getName() + " is a nested class and must be declared static to resolve it's own-plugin-constructor");
    }

    try {

      // Get constructor
      Constructor build = plugin.getDeclaredConstructor();
      build.setAccessible(true);
      pluginInstance = (Plugin) build.newInstance();

      // load the plugin
      if (pluginInstance instanceof IPluginLoadable)
        IPluginLoadable.class.cast(pluginInstance)
          .onLoad(pluginInstance.pluginLoader = loader, bundle);

      // notify the loader with any forwarding
      if (loader instanceof IPluginLoader) {
        IPluginLoader.class.cast(loader).onLoadPlugin(pluginInstance);
      }

    } catch (Exception oE) {
      oE.printStackTrace();
    }

    return pluginInstance;

  }

  @Override
  final public Object getPluginLoader(){return pluginLoader;}

  @Override
  final public String getPluginName() {
    return getClass().getName();
  }

  final public void putSetting(String name, Object value) {
    boolean knownKey = knownSettings.containsKey(name);
    if (knownKey && ! writableSettings.get(name)) return;
    if (knownKey && ! knownSettings.get(name).equals(value.getClass())) {
      throw new ClassCastException("wrong value type for this setting: "+getPluginName()+": "+name);
    }
    if (this instanceof IPluginSettingsController) {
      IPluginSettingsController.class.cast(this).onPutSetting(name, value);
      return;
    }
    if (!knownKey) return;
    settings.put(name, value);
  }

  final public Object getSetting(String name) {
    if (this instanceof IPluginSettingsController) {
      Object data = IPluginSettingsController.class.cast(this).onGetSetting(name);
      if (data == null) return JSONObject.NULL;
      return data;
    }
    if (!knownSettings.containsKey(name)) return JSONObject.NULL;
    return settings.get(name);
  }

  final public void setBooleanStatus(String name, boolean value) {
    if (!knownSettings.containsKey(name)) return;
    if (!writableSettings.get(name)) return;
    if (knownSettings.get(name).equals(Boolean.TYPE)) putSetting(name, value);
    else throw new ClassCastException("wrong value type for this setting: "+getPluginName()+": "+name);
  }

  // override this to perform your plugin-specific-logic
  public boolean checkSetting(String name, Object value) {
    return settings.has(name) && getSetting(name).equals(value);
  }

  /* use this function to setup your IPC
  *
  *  after you create a setting with this method, the get/set setting calls on the public
  *  interface using the name supplied here will be forwarded
  *  to the settings object, if you don't have a settings controller interface.
  *
  *
  * */final protected boolean createSetting(String name, Object value, boolean writable) {
    if (knownSettings.containsKey(name)) settings.remove(name);
    knownSettings.put(name, value.getClass());
    writableSettings.put(name, writable);
    settings.put(name, value);
    return true;
  }

  /* call this if you feel like new beginnnings are in your favor */
  /* you might want to override this, so you can catch any configuration modification corner cases */
  protected void resetPlugin(){
    settings = new JSONObject();
    knownSettings.clear();
    writableSettings.clear();
  }

  @Override /* override this to perform custom property serialization. @return string */
  public String toJSONString() {
    return settings.toString();
  }

  public String toJSONString(int depth) {
    return settings.toString(depth);
  }

}
