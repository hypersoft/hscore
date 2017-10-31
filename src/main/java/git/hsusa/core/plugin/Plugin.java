package git.hsusa.core.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import git.hsusa.core.json.JSONObject;
import git.hsusa.core.json.JSONString;

import static java.lang.Class.forName;

/**
 * Created by triston on 10/29/17.
 */


/*
    A bare-bones-extensible-plugin-system.

    Features: Plugins and Plugin loaders with startup "bundles" and object forwarding

 */

public class Plugin implements IPlugin, JSONString {

  static Logger logger = Logger.getLogger("Plugin");

  protected Object pluginLoader = null;
  protected JSONObject settings = new JSONObject();

  protected Plugin() {}

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
      logger.severe(plugin.getName() + " is a nested class and must be declared static to resolve its constructor");
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
  final public String toString() {
    return getPluginName();
  }

  @Override
  final public String getPluginName() {
    return getClass().getName();
  }

  final public void putSetting(String name, Object value) {
    if (this instanceof IPluginSettingsController)
      IPluginSettingsController.class.cast(this).onPutSetting(name, value);
    else settings.put(name, value);
  }

  final public Object getSetting(String name) {
    if (this instanceof IPluginSettingsController)
      return IPluginSettingsController.class.cast(this).onGetSetting(name);
    else return settings.get(name);
  }

  final public void enableSetting(String name) {
    putSetting(name, true);
  }

  final public void disableSetting(String name) {
    putSetting(name, false);
  }

  final public boolean checkSetting(String name, Object value) {
    return settings.has(name) && getSetting(name).equals(value);
  }

  @Override
  final public String toJSONString() {
    return settings.toString();
  }

  final public String toJSONString(int depth) {
    return settings.toString(depth);
  }

}
