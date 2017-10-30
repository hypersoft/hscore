package git.hsusa.core;

/**
 * Created by triston on 10/29/17.
 */

/*
    A loadable-plugin-event-interface
 */

public interface PluginActivation<RUNTIME> {
  Object onPluginActivate(RUNTIME runtime);
}
