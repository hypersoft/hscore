package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */
public interface IPluginLoadable<LOADER, BUNDLE> {
  // What you return from this, is what the plugin uses to represent itself.
  void onLoad(LOADER loader, BUNDLE bundle);
}
