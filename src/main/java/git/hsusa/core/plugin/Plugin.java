package git.hsusa.core.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import git.hsusa.core.json.JSONObject;
import git.hsusa.core.json.JSONString;

import static java.lang.Class.forName;

/**
 * Created by triston on 10/29/17.
 */


/*
    A bare-bones-extensible-out-of-the-box-environmental-plugin-system.

    Features: Plugins and Plugin loaders with startup "bundles" and object forwarding,
    serializable settings, scriptable command interface, & property access controller.

    There is also a secure property sharing implementation, with simple set-it-and-go-configuration.
    You can keep your variables private by using the settings object directly. However, if you
    call createSetting(NAME, VALUE, WRITABLE), that setting will be registered in the known value
    types registry, which will enable external access and type checking through get/put setting.
    if the WRITABLE setting is not WRITABLE == TRUE, then external put is filtered for
    the setting.

    private settings are not exported in the standard serialization.

    Roll your own interface. "I"-plugin-everything...

 */

public class Plugin implements IPlugin, JSONString {

  static Logger logger = Logger.getLogger("Plugin");

  private Object pluginLoader = null;
  // internal settings
  protected HashMap<String, Class> knownSettings = new HashMap<>();
  protected HashMap<String, Boolean> writableSettings = new HashMap<>();
  // plugin-settings
  protected JSONObject settings = new JSONObject();

  protected Plugin() {}

  public static Object create(Class<? extends Plugin> plugin) {return create(plugin, null, null);}
  public static Object create(String className) {return create(className, null, null);}

  final public static Object create(String className, Object loader, Object bundle) {
    Class<? extends Plugin> plugin = null;
    try {plugin = (Class<? extends Plugin>)forName(className);}
      catch (ClassNotFoundException oE) {oE.printStackTrace();}
    return create(plugin, loader, bundle);
  }

  final public static Object create(Class<? extends Plugin> plugin, Object loader, Object bundle) {
    Plugin pluginInstance = null;
    if (plugin.getEnclosingClass() != null && ! Modifier.isStatic(plugin.getModifiers())) {
      logger.severe(plugin.getName() + " is a nested class and must be declared static to resolve it's own-plugin-constructor");
    }
    try { // Get constructor
      Constructor build = plugin.getDeclaredConstructor();
      build.setAccessible(true);
      pluginInstance = (Plugin) build.newInstance();
      pluginInstance.pluginLoader = loader;
      // load the plugin
      if (pluginInstance instanceof IPluginLoadableVoid)
        IPluginLoadableVoid.class.cast(pluginInstance).onLoad();
           // load the plugin
      else if (pluginInstance instanceof IPluginLoadableBundle)
        IPluginLoadableBundle.class.cast(pluginInstance).onLoad(bundle);
           // load the plugin
      else if (pluginInstance instanceof IPluginLoadableAny)
        IPluginLoadableAny.class.cast(pluginInstance).onLoad(bundle);
      //------------------ notify the loader with any forwarding
      if (loader instanceof IPluginLoader) {
        IPluginLoader.class.cast(loader).onLoadPlugin(pluginInstance);
      }
    } catch (Exception oE) {oE.printStackTrace();}
    return pluginInstance;
  }

  @Override
  public Set<String> getSettingNames() {return knownSettings.keySet();}

  @Override
  final public Object getPluginLoader(){return pluginLoader;}

  @Override
  final public String getPluginName() {return getClass().getName();}

  @Override
  final public void putSetting(String name, Object value) {
    if (this instanceof IPluginSettingsController) {
      IPluginSettingsController.class.cast(this).onPutSetting(name, value);
      return;
    }
    if (!settingIsKnown(name)) return;
    if (!knownSettingIsWritable(name)) return;
    if (!knownSettingIsTypeOf(name, value)) {
      throw new ClassCastException(
        "wrong value type for this setting: "+getPluginName()+": "+name
        +"; new value is type: "+value.getClass().getName()
          +". this setting is configured for handling type: "+knownSettings.get(name).getName()
      );
    }
    settings.put(name, value);
  }

  @Override
  final public Object getSetting(String name) {
    if (this instanceof IPluginSettingsController) {
      Object data = IPluginSettingsController.class.cast(this).onGetSetting(name);
      if (data == null) return JSONObject.NULL;
      return data;
    }
    if (!settingIsKnown(name)) return JSONObject.NULL;
    return settings.get(name);
  }

  protected void protectSetting(String name) {
    knownSettings.remove(name); writableSettings.remove(name);
  }

  protected void publishSetting(String name, Class value, boolean writable) {
    knownSettings.put(name, value); writableSettings.put(name, writable);
  }

  protected boolean settingIsProtected(String name) {
    if (settingIsKnown(name)) return false; return settings.has(name);
  }

  protected boolean settingIsKnown(String name) {return knownSettings.containsKey(name);}

  protected boolean knownSettingIsWritable(String name) {return writableSettings.get(name);}

  protected  boolean knownSettingIsTypeOf(String name, Object value) {
    if (value == null) return false;
    return knownSettings.get(name).equals(value.getClass());
  }

  // override this to perform your plugin-specific-logic
  public boolean checkSetting(String name, Object value) {
    return settings.has(name) && getSetting(name).equals(value);
  }

  /* use this function to setup your IPC if you don't have a settings-controller-interface.
  *
  *  after you create a setting with this method, the get/set setting calls on the public
  *  interface using the name supplied here will be forwarded
  *  to the settings object.
  *
  *
  * */final protected boolean createSetting(String name, Object value, boolean writable) {
    publishSetting(name, value.getClass(), writable);
    settings.put(name, value);
    return true;
  }

  /* call this if you feel like new beginnings are in your favor */
  /* you might want to override this, so you can catch any configuration modification corner cases */
  protected void pluginReset(){
    settings = new JSONObject();
    knownSettings.clear();
    writableSettings.clear();
  }

  @Override /* override this to perform custom property serialization. @return string */
  public String toJSONString() {return toJSONString(0);}

  final public String toJSONString(int depth)
  {
    Set<String> names = getSettingNames();
    JSONObject out = new JSONObject(names.size());
    if (this instanceof IPluginSerializationFilter) {
      for (String item : names) {
        try {out.putOnce(item, ((IPluginSerializationFilter)this).onPluginSerialize(item));}
          catch (Exception fault) {
          logger.info(
            IPluginSerializationFilter.class.getName()
              // don't be smart as hell, for nothing...
              +": plugin serialization failure in "+getPluginName()+": "+item
              +"; reason: "+fault.getClass().getName() +"; message: "+fault.getMessage()
          );
        }
      }
    }
    else for (String item : names) try {out.putOnce(item, settings.opt(item));}
      catch (Exception ignore) {}
    return out.toString(depth);
  }
}
