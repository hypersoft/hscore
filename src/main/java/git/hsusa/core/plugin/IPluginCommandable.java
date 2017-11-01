package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */

/**
 * IKnowMyPurposes
 * Just claim this interface, setup your method, and you are off to the races.
 */
public interface IPluginCommandable {
  Object doPluginCommand(String command, Object... parameters);
}
