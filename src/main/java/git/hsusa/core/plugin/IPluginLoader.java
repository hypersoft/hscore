package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */
public interface IPluginLoader<TYPE> {
  void onLoadPlugin(TYPE loadable);
}
