package git.hsusa.core.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by triston on 11/2/17.
 */

public class Commander {

  public static class StreamHandler implements Runnable {

    Object source;
    Object destination;

    StreamHandler(Object source, Object oDestination) {
      this.source = source; this.destination = oDestination;
    }

    public void run() {
      if (source instanceof InputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) source));
        String line;
        try {
          while ((line = br.readLine()) != null) ((StringBuilder) destination).append(line + '\n');
        } catch (IOException oE) {
        }
      } else {
        PrintWriter pw = new PrintWriter((OutputStream)destination);
        pw.print((String)source);
        pw.flush(); pw.close();
      }
    }

    public static Thread read(InputStream source, StringBuilder dest) {
      Thread thread = new Thread(new StreamHandler(source, dest));
      (thread).start();
      return thread;
    }

    public static Thread write(String source, OutputStream dest) {
      Thread thread = new Thread(new StreamHandler(source, dest));
      (thread).start();
      return thread;
    }

  }

  static Map<String, String> environment = loadEnvironment();

  static String workingDirectory = ".";

  static Map<String, String> loadEnvironment() {
    ProcessBuilder x = new ProcessBuilder();
    return x.environment();
  }

  static public void resetEnvironment() {
    environment = loadEnvironment();
    workingDirectory = ".";
  }

  static public void loadEnvirons(HashMap input) {
    environment.putAll(input);
  }

  static public String getEnviron(String name) {
    return environment.get(name);
  }

  static public void setEnviron(String name, String value) {
    environment.put(name, value);
  }

  static public boolean clearEnviron(String name) {
    return environment.remove(name) != null;
  }

  static public boolean setWorkingDirectory(String path) {
    File test = new File(path);
    if (!test.isDirectory()) return false;
    workingDirectory = path;
    return true;
  }

  static public String getWorkingDirectory() {
    return workingDirectory;
  }

  static public class Command {

    ProcessBuilder processBuilder = new ProcessBuilder();
    Process process;

    public Command(List<String> parameters) {
      processBuilder.environment().putAll(environment);
      processBuilder.directory(new File(workingDirectory));
      processBuilder.command(parameters);
    }

    public Command(String... parameters) {
      processBuilder.environment().putAll(environment);
      processBuilder.directory(new File(workingDirectory));
      processBuilder.command(parameters);
    }

    public int start(String input, StringBuilder output, StringBuilder error) throws IOException {

      // start the process
      process = processBuilder.start();

      // start the error reader
      Thread errorBranch = StreamHandler.read(process.getErrorStream(), error);

      // start the output reader
      Thread outputBranch = StreamHandler.read(process.getInputStream(), output);

      // start the input
      Thread inputBranch = StreamHandler.write(input, process.getOutputStream());

      int rValue = 254;
      try {
        inputBranch.join(); rValue--;
        outputBranch.join(); rValue--;
        errorBranch.join(); rValue--;
        return process.waitFor();
      } catch (InterruptedException oE) {
        oE.printStackTrace();
        return rValue;
      }
    }

  }

}
