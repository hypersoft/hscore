package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */
public interface IPlugin {
  String getPluginName();

  Object getPluginLoader();

  String toString();
}
