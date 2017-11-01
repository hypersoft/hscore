package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */

import java.util.Set;

/**
 * IKnowMyPurposes
 * Just claim this interface, setup your methods, and you are off to the races.
 */
public interface IPluginCommandable {
  Object doPluginCommand(String command, Object... parameters);
  Set<String> getPluginCommands();
}
