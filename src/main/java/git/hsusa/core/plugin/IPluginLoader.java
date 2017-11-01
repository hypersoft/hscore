package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */

/**
 * IPluginPluginThings
 * @param <TYPE> the kind of plugins this loader finds particular.
 */
public interface IPluginLoader<TYPE> {
  void onLoadPlugin(TYPE loadable);
}
