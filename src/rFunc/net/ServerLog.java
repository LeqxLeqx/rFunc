/*
 * rFunc: Remote function call library
 * Copyright (C) 2017  LeqxLeqx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rFunc.net;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class ServerLog {

  public static final int
          STDOUT = 0x1 << 0,
          STDERR = 0x1 << 1,
          FILE =   0X1 << 2
          ;



  private LinkedList<ServerLogEntry> entries = new LinkedList<>();
  private LinkedList<ServerLogEntryType> traceTypeSuppression = new LinkedList<>();
  private int output = 0;
  private File outputFile;

  ServerLog() {}

  /**
   * Sets the output file to the provided path
   *
   * @param path the output path
   */
  public synchronized void setFile(String path) {
    outputFile = new File(path);
  }

  /**
   * Gets all server log entries as an array
   *
   * @return all server log entries
   */
  public synchronized ServerLogEntry[] getServerLogEntries() {
    return entries.toArray(new ServerLogEntry[entries.size()]);
  }

  /**
   * Sets output type based on the available output masks
   *
   * @param output output bit masks to use
   */
  public synchronized void setOutput(int output) {
    this.output = output;
  }

  private synchronized boolean isSuppressed(ServerLogEntryType slet) {
    return traceTypeSuppression.contains(slet);
  }

  /**
   * Gets whether or or not 'trace' type log entries
   * are suppressed
   *
   * @return true if 'trace' is suppressed
   */
  public boolean isTraceSuppressed() {
    return isSuppressed(ServerLogEntryType.TRACE);
  }
  /**
   * Gets whether or or not 'verbose' type log entries
   * are suppressed
   *
   * @return true if 'verbose' is suppressed
   */
  public boolean isVerboseSuppressed() {
    return isSuppressed(ServerLogEntryType.VERBOSE);
  }
  /**
   * Gets whether or or not 'info' type log entries
   * are suppressed
   *
   * @return true if 'info' is suppressed
   */
  public boolean isInfoSuppressed() {
    return isSuppressed(ServerLogEntryType.INFO);
  }
  /**
   * Gets whether or or not 'warning' type log entries
   * are suppressed
   *
   * @return true if 'warning' is suppressed
   */
  public boolean isWarningSuppressed() {
    return isSuppressed(ServerLogEntryType.WARNING);
  }
  /**
   * Gets whether or or not 'exception' type log entries
   * are suppressed
   *
   * @return true if 'exception' is suppressed
   */
  public boolean isExceptionSuppressed() {
    return isSuppressed(ServerLogEntryType.EXCEPTION);
  }



  private void setSuppressed(ServerLogEntryType type, boolean b) {
    if (b && !traceTypeSuppression.contains(type))
      traceTypeSuppression.add(type);
    else if (!b && traceTypeSuppression.contains(type))
      traceTypeSuppression.remove(type);
  }

  /**
   * Sets whether or not 'trace' type log entries
   * are suppressed
   *
   * @param b setting
   */
  public void setTraceSuppressed(boolean b) {
    setSuppressed(ServerLogEntryType.TRACE, b);
  }
  /**
   * Sets whether or not 'verbose' type log entries
   * are suppressed
   *
   * @param b setting
   */
  public void setVerboseSuppressed(boolean b) {
    setSuppressed(ServerLogEntryType.VERBOSE, b);
  }
  /**
   * Sets whether or not 'info' type log entries
   * are suppressed
   *
   * @param b setting
   */
  public void setInfoSuppressed(boolean b) {
    setSuppressed(ServerLogEntryType.INFO, b);
  }
  /**
   * Sets whether or not 'warning' type log entries
   * are suppressed
   *
   * @param b setting
   */
  public void setWarningSuppressed(boolean b) {
    setSuppressed(ServerLogEntryType.WARNING, b);
  }
  /**
   * Sets whether or not 'exception' type log entries
   * are suppressed
   *
   * @param b setting
   */
  public void setExceptionSuppressed(boolean b) {
    setSuppressed(ServerLogEntryType.EXCEPTION, b);
  }


  /**
   * Adds trace type log entry
   *
   * @param message content of log entry
   */
  public synchronized void addTrace(String message) {
    if (isTraceSuppressed()) return;

    ServerLogEntry e = new ServerLogEntry(ServerLogEntryType.TRACE, System.currentTimeMillis(), message);
    entries.add(e);
    print(e);
  }

  /**
   * Adds verbose type log entry
   *
   * @param message content of log entry
   */
  public synchronized void addVerbose(String message) {
    if (isVerboseSuppressed()) return;

    ServerLogEntry e = new ServerLogEntry(ServerLogEntryType.VERBOSE, System.currentTimeMillis(), message);
    entries.add(e);
    print(e);
  }

  /**
   * Adds info type log entry
   *
   * @param message content of log entry
   */
  public synchronized void addInfo(String message) {
    if (isInfoSuppressed()) return;

    ServerLogEntry e = new ServerLogEntry(ServerLogEntryType.INFO, System.currentTimeMillis(), message);
    entries.add(e);
    print(e);
  }

  /**
   * Adds warning type log entry
   *
   * @param message content of log entry
   */
  public synchronized void addWarning(String message) {
    if (isWarningSuppressed()) return;

    ServerLogEntry e = new ServerLogEntry(ServerLogEntryType.WARNING, System.currentTimeMillis(), message);
    entries.add(e);
    print(e);
  }

  /**
   * Adds exception type log entry
   *
   * @param e content of log entry
   */
  public synchronized void addException(Throwable e) {
    if (isExceptionSuppressed()) return;

    String message = e.getMessage() + "\n";
    for(StackTraceElement ste : e.getStackTrace()) {
      message += ste.toString() + "\n";
    }

    message = message.substring(0, message.length() - 1);

    ServerLogEntry entry = new ServerLogEntry(ServerLogEntryType.EXCEPTION, System.currentTimeMillis(), message);
    entries.add(entry);
    print(entry);
  }

  private void print(ServerLogEntry e) {

    String string = String.format("[ %s ] (%s) : %s", e.type.string, e.formattedTime(), e.string);

    if ((output & STDOUT) > 0) {
      System.out.println(string);
      System.out.flush();
    }
    if ((output & STDERR) > 0) {
      System.err.println(string);
      System.err.flush();
    }
    if ((output & FILE) > 0) {
      if (outputFile != null) {
        try {

          Files.write(outputFile.toPath(), (string + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        } catch (IOException er) {}
      }
    }

  }



  enum ServerLogEntryType {

    TRACE     (" TRACE "),
    VERBOSE   ("VERBOSE"),
    INFO      (" INFO  "),
    WARNING   ("WARNING"),
    EXCEPTION (" EXCPT "),

    ;

    String string;

    ServerLogEntryType(String string) {
      this.string = string;
    }

  }

  public class ServerLogEntry {

    final ServerLogEntryType type;
    final long time;
    final String string;

    public ServerLogEntry(
            ServerLogEntryType type,
            long time,
            String string
      ) {
      this.type = type;
      this.time = time;
      this.string = string;
    }

    String formattedTime() {

      LocalDateTime ldt = LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));

      return String.format("%02d/%02d/%02d-%02d:%02d:%02d", ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(), ldt.getSecond());

    }


  }

}
