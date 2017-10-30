package git.hsusa.core.json;

/**
 * Created by triston on 10/27/17.
 */

public interface JSONBubble {
  /**
   * The <code>forJSONBubble</code> method allows a class to resolve its own JSON
   * serialization interface. This is a suitable interface when you have a custom class instance
   * layer or a custom property set you want JSONified.
   *
   * Bonus Game: Bubble Popping extravaganza.
   *
   * @return any object suitable for <code>new JSONObject()</code>.
   */
  public Object forJSONBubble();
}
