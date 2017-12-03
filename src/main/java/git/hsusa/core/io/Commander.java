package git.hsusa.core.io;

import java.io.*;
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
    String encoding;

    StreamHandler(Object source, Object oDestination, String sEncoding) {
      this.source = source; this.destination = oDestination;
      encoding = sEncoding;
    }

    public void run() {
      if (source instanceof InputStream) {
        String line;
        BufferedReader br = null;
        try {
          br = new BufferedReader(new InputStreamReader((InputStream) source, encoding));
          while ((line = br.readLine()) != null) ((StringBuilder)
            destination).append(line + System.lineSeparator());
        } catch (IOException oE) {}
        finally {
          if (br != null) try {
            br.close();
          } catch (IOException e) {}
        }
      } else {
        PrintWriter pw = null;
        try {
          pw = new PrintWriter(
            new OutputStreamWriter((OutputStream) destination, encoding));
          pw.print((String) source);
          pw.flush();
        } catch (IOException e) {}
        finally {
          if (pw != null) pw.close();
        }
      }
    }

    public static Thread read(InputStream source, StringBuilder dest, String encoding) {
      Thread thread = new Thread(new StreamHandler(source, dest, encoding));
      (thread).start();
      return thread;
    }

    public static Thread write(String source, OutputStream dest, String encoding) {
      Thread thread = new Thread(new StreamHandler(source, dest, encoding));
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
      Thread errorBranch = StreamHandler.read(process.getErrorStream(), error, null);

      // start the output reader
      Thread outputBranch = StreamHandler.read(process.getInputStream(), output, null);

      // start the input
      Thread inputBranch = StreamHandler.write(input, process.getOutputStream(), null);

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
