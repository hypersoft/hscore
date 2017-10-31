package git.hsusa.core.plugin;

/**
 * Created by triston on 10/30/17.
 */
public interface IPluginLoadable<LOADER, BUNDLE> {
  void onLoad(LOADER loader, BUNDLE bundle);
}
