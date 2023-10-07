package santiaguero;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {
  private ConsoleManager consoleManager;

  public ConsoleOutputStream(ConsoleManager consoleManager) {
    this.consoleManager = consoleManager;
  }

  @Override
  public void write(int b) throws IOException {
    // Append the character as a String to the ConsoleManager
    consoleManager.appendToConsole(String.valueOf((char) b));
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    String s = new String(b, off, len);
    // Append the string to the ConsoleManager
    consoleManager.appendToConsole(s);
  }
}
