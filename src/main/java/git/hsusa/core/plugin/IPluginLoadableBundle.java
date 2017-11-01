package git.hsusa.core.plugin;

/**
 * Created by triston on 10/31/17.
 */

/**
 * IKnowPackageTransportation
 * @param <BUNDLE> whatever kind of data packaging you like.
 */
public interface IPluginLoadableBundle<BUNDLE> {
  void onLoad(BUNDLE data);
}
