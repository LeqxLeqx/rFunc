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

import rFunc.method.Environment;
import rFunc.method.InvocationException;
import rFunc.method.MethodSpecification;
import rFunc.method.Namespace;
import rFunc.net.packet.*;
import rFunc.value.Value;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author:    LeqxLeqx
 */
public class Server implements Runnable{

  public static final int SERVER_TIMEOUT = 300;



  public final Environment environment = new Environment();


  private ServerSocket serverSocket;

  private ReentrantLock runLock = new ReentrantLock();
  private Thread thread = new Thread(this);
  private boolean started = false, ended = false, endRequested = false;

  public boolean dirtyMouth = false;

  public final ServerLog log = new ServerLog();

  public Server() {}

  /**
   * Threaded operation of this server. Should not be called directly
   */
  @Override
  public void run() { // Operation to be run threaded

    boolean er;
    runLock.lock();


    log.addInfo("Server thread started");

    do {

      synchronized (this) {
        er = endRequested;
      }

      if (er) continue;

      boolean serverWaiting = true;

      try {

        log.addVerbose("Awaiting connection...");
        Socket socket = serverSocket.accept();
        serverWaiting = false;

        socket.setSoTimeout(SERVER_TIMEOUT); // should do good things...
        ServerConnection connection = new ServerConnection(this, socket);
        log.addTrace(String.format("Connected to '%s'", connection.toString()));

        log.addTrace("Performing connection handshake...");
        connection.shakeHands();
        log.addTrace("Connection handshake complete");

        Packet incomingPacket;
        do {
          log.addTrace("Awaiting incoming packet...");
          incomingPacket = connection.awaitIncomingPacket(true);
          log.addTrace("Received and replied to incoming packet");
        } while (incomingPacket.type != PacketType.TERMINATE);

        log.addTrace(String.format("Closing connection '%s'", connection.toString()));
        connection.close();
        log.addTrace("Connection successfully closed");


      } catch (SocketTimeoutException e) {
        if (serverWaiting)
          log.addVerbose(String.format("Connection timed out after '%s' milliseconds", SERVER_TIMEOUT));
        else {
          log.addException(e);
        }
      } catch (Exception e) {
        log.addException(e);
        if (dirtyMouth)
          e.printStackTrace();
        continue;
      }

    } while(!er);


    runLock.unlock();

    log.addInfo("Server thread completed");

  }

  /**
   * Terminates the server's background
   * process. This locks until the
   * background thread terminates
   */
  public void terminate() {

    log.addTrace("Terminating...");

    synchronized (this) {
      endRequested = true;
    }

    runLock.lock(); // will hold until thread ends
    runLock.unlock();


    synchronized (this) {
      ended = true;
    }

    log.addTrace("Server terminated successfully");
  }

  /**
   * Gets whether or not the server's background process
   * has been started
   *
   * @return true if the server has been started
   */
  public boolean started() {
    return started;
  }

  /**
   * Gets whether or not the server's background process
   * has been terminated
   *
   * @return true if the server has been terminated
   */
  public synchronized boolean terminated() {
    return ended;
  }

  /**
   * Gets whether or not a termination has
   * been requested of the background thread
   *
   * @return true if a terminate has been requested
   */
  public synchronized boolean terminateRequested() {
    return endRequested;
  }


  Packet getResponse(ServerConnection connection, Packet packet) {
    Packet ret;
    switch (packet.type) {

      case FUNCTION_CALL:
        ret = functionCall((FunctionCallPacket) packet);
        break;

      case NAMESPACE_LIST_REQUEST:
        ret = namespaceListRequest((NamespaceListRequestPacket) packet);
        break;

      case FUNCTION_LIST_REQUEST:
        ret = functionListRequest((FunctionListRequestPacket) packet);
        break;

      case TERMINATE:
        ret = null;
        break;

      default:
        log.addWarning(String.format("Server cannot process package of type '%s'", packet.type));
        ret = new ErrorPacket(String.format("Server cannot process package of type '%s'", packet.type));
        break;

    }

    if (ret != null)
      ret.setTransactionID(packet.getTransactionID());

    return ret;
  }

  /**
   * Starts the background process for the server
   *
   * @param port port for the server-socket
   * @throws IOException
   */
  public void start(int port) throws IOException {

    log.addInfo("Starting server...");

    try {

      if (started)
        throw new IllegalStateException("Cannot start server as it has already been started");

      started = true;

      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(SERVER_TIMEOUT);
      thread.start();

    } catch (Exception e) {
      log.addException(e);
      throw e;
    }

    log.addInfo("Server successfully started");

  }

  private Packet functionCall(FunctionCallPacket packet) {
    String namespace = packet.namespace;
    MethodSpecification mSpec = packet.methodSpecification;
    Value[] values = packet.arguments;

    try {

      log.addInfo(String.format("Invoking %s.%s...", namespace, mSpec.toString()));
      Value retVal = environment.invoke(namespace, mSpec, values);
      log.addInfo(String.format("Invocation complete. Returned value: '%s'", retVal));

      return new FunctionReturnPacket(retVal);
    } catch (InvocationException e) {

      log.addWarning(String.format("Invocation exception incurred: %s", e.getMessage()));

      return new ErrorPacket("Invocation exception: " + e.getMessage());
    }

  }

  private Packet namespaceListRequest(NamespaceListRequestPacket packet) {

    log.addInfo("Processing namespace list request");

    return new NamespaceListPacket(environment.listNamespaces());
  }

  private Packet functionListRequest(FunctionListRequestPacket packet) {
    Namespace namespace = environment.getNamespace(packet.namespace);

    log.addInfo(String.format("Processing function list request for namespace '%s'", packet.namespace));

    if (namespace == null)
      return new FunctionListPacket(new MethodSpecification[0]);
    else
      return new FunctionListPacket(namespace.list());
  }

}
