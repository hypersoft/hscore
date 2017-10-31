package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */
public interface IPluginCommandable {
  Object doPluginCommand(String command, Object... parameters);
}
