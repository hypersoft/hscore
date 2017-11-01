package git.hsusa.core;

import org.junit.Test;

import java.util.HashMap;
import java.util.logging.Logger;

import git.hsusa.core.plugin.IPluginLoadableBundle;
import git.hsusa.core.plugin.Plugin;
import git.hsusa.core.plugin.PluginLoader;

import static org.junit.Assert.*;

/**
 * Created by triston on 10/30/17.
 */

public class PluginTest {

  Logger logger = Logger.getLogger("PluginTest");

  static class TestPluginLoader extends PluginLoader {
    @Override
    public void onLoadPlugin(Plugin loadable) {
      assertTrue(loadable instanceof IPluginLoadableBundle);
    }
  }

  TestPluginLoader testPluginLoader = new TestPluginLoader();

  static class TestPlugin extends Plugin implements IPluginLoadableBundle<HashMap> {
    @Override
    public void onLoad(HashMap oHashMap) {
      createSetting("foul", "", true);
      settings.put("holy", "shit");
    }
  }

  TestPlugin testPlugin = null;

  @Test
  public void testPluginLoading() throws Exception {
    testPlugin = (TestPlugin) Plugin.create(TestPlugin.class.getName(), testPluginLoader, null);
    assertNotNull(testPlugin);
    assertTrue(testPlugin instanceof TestPlugin);
    testPlugin.putSetting("foul", "smelling");
    logger.info(testPlugin.toJSONString(4));
  }

}
