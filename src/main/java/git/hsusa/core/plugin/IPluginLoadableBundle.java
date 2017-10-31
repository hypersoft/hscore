package git.hsusa.core.plugin;

/**
 * Created by triston on 10/31/17.
 */

public interface IPluginLoadableBundle<BUNDLE> {
  void onLoad(BUNDLE data);
}
