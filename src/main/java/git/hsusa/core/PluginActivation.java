package git.hsusa.core;

/**
 * Created by triston on 10/29/17.
 */

public interface PluginActivation<RUNTIME> {
  Object onPluginActivate(RUNTIME runtime);
}
