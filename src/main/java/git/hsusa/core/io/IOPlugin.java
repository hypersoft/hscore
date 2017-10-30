package git.hsusa.core.io;

import git.hsusa.core.Plugin;

/**
 * Created by triston on 10/28/17.
 */
public class IOPlugin<RUNTIME, TRANSMISSION_RESULT, TRANSMISSION_TYPE> extends Plugin {

  public interface DataWriter<RUNTIME, FLAGABLE, TYPE> {
    FLAGABLE writeData(RUNTIME runtime, TYPE data);}
  public interface DataReader<RUNTIME, READABLE> {
    READABLE readData(RUNTIME runtime);}

  protected DataReader<RUNTIME, Object> dataReader;
  protected DataWriter<RUNTIME, TRANSMISSION_RESULT, TRANSMISSION_TYPE> dataWriter;

  public TRANSMISSION_RESULT writeData(RUNTIME runtime, TRANSMISSION_TYPE output) {
    return dataWriter.writeData(runtime, output);
  }

  public Object readData(RUNTIME runtime) {
    return dataReader.readData(runtime);
  }

}
