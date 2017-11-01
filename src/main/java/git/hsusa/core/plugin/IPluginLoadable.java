package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */

/**
 * IKnowMyEnvironment
 * @param <LOADER> the object which is requesting this plugin.
 * @param <BUNDLE> whatever kind of data the request would like the plugin to handle.
 */
public interface IPluginLoadable<LOADER, BUNDLE> {
  void onLoad(LOADER loader, BUNDLE bundle);
}
