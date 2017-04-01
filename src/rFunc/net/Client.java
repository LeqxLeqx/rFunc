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

import rFunc.method.ArgumentSpecification;
import rFunc.method.InvocationException;
import rFunc.method.MethodSpecification;
import rFunc.method.NamespaceSpecification;
import rFunc.net.packet.*;
import rFunc.value.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class Client {



  private String ip;
  private int port;
  private boolean initialized = false;

  private long transactionID = 0;

  private final LinkedList<NamespaceSpecification> namespaceSpecifications = new LinkedList<>();


  public Client() {

  }

  /**
   * Initializes the client object to be connected
   * to the a server by getting data related to
   * the server's method environment
   *
   * @throws IOException
   */
  public void initialize() throws IOException {

    if (ip == null || port == 0)
      throw new IllegalStateException("IP and port values must be set");

    namespaceSpecifications.clear();

    ClientConnection connection = new ClientConnection(ip, port);
    connection.shakeHands();

    NamespaceListRequestPacket namespaceListRequestPacket = new NamespaceListRequestPacket();
    namespaceListRequestPacket.setTransactionID(transactionID++);

    Packet namespaceListPacket = connection.sendAndAwaitReply(namespaceListRequestPacket);

    if (namespaceListPacket.type != PacketType.NAMESPACE_LIST)
      throw new IOException("Received invalid packet response of type: " + namespaceListPacket.type);

    for(String namespaceName : ((NamespaceListPacket) namespaceListPacket).getNames()) {

      FunctionListRequestPacket functionListRequestPacket = new FunctionListRequestPacket(namespaceName);
      functionListRequestPacket.setTransactionID(transactionID++);

      Packet functionListPacket = connection.sendAndAwaitReply(functionListRequestPacket);

      if (functionListPacket.type != PacketType.FUNCTION_LIST)
        throw new IOException("Received invalid packet response of type: " + functionListPacket.type);

      NamespaceSpecification nSpec = new NamespaceSpecification(namespaceName);

      for(MethodSpecification method : ((FunctionListPacket) functionListPacket).getMethods()) {
        nSpec.addMethodSpecification(method);
      }

      namespaceSpecifications.add(nSpec);
    }

    connection.close(transactionID++);

    initialized = true;
  }

  /**
   * Invokes the method of the provided name from the server
   *
   * @param name the method name
   * @param values the value array to provide to the method as arguments
   * @return the value returned by the method
   * @throws IOException
   * @throws InvocationException
   */
  public Value invoke(String name, Value... values) throws IOException, InvocationException{
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");

    String[] split = name.split("\\.", 2);

    if (split.length != 2)
      return invoke("", name, values);
    else
      return invoke(split[0], split[1], values);
  }

  /**
   * Invokes the method of the provided namespace and method name from the server
   *
   * @param namespace the namespace name
   * @param name the method name
   * @param values the value array to provide to the method as arguments
   * @return the value returned by the method
   * @throws IOException
   * @throws InvocationException
   */
  public Value invoke(String namespace, String name, Value[] values) throws IOException, InvocationException {
    return invoke(namespace, name, values, true);
  }

  /**
   * Invokes the method of the provided namespace and method name from the server
   *
   * @param namespace the namespace name
   * @param name the method name
   * @param values the value array to provide to the method as arguments
   * @param affirmMethodValidity whether or not to confirm the method exists on the server
   * @return the value returned by the method
   * @throws IOException
   * @throws InvocationException
   */
  public Value invoke(String namespace, String name, Value[] values, boolean affirmMethodValidity) throws IOException, InvocationException {
    if (!initialized && affirmMethodValidity)
      throw new IllegalStateException("Client must be initialized before methods can be invoked");
    if (ip == null || port == 0)
      throw new IllegalStateException("IP and port values must be set");

    if (namespace == null)
      throw new IllegalArgumentException("Namespace cannot be null");
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");
    if (values == null)
      throw new IllegalArgumentException("Values array cannot be null");
    if (Arrays.asList(values).contains(null))
      throw new IllegalArgumentException("Values array cannot contain null");


    ArgumentSpecification argumentSpecification = ArgumentSpecification.get(values);

    if (affirmMethodValidity) {

      MethodSpecification[] mSpecs = getMethodSpecifications(namespace);
      if (mSpecs == null)
        throw new IllegalArgumentException("No such namespace: " + namespace);

      boolean found = false;
      for(MethodSpecification mSpec : mSpecs) {
        if (
                mSpec.name.equals(name) &&
                mSpec.argumentSpecification.equals(argumentSpecification)
                )
          found = true;
      }

      if (!found)
        throw new IllegalArgumentException("No such method: " + name + "(" + argumentSpecification + ")");

    }

    Packet outgoingPacket = new FunctionCallPacket(namespace, new MethodSpecification(argumentSpecification, name, ""), values);
    outgoingPacket.setTransactionID(transactionID++);

    ClientConnection connection = new ClientConnection(ip, port);
    connection.shakeHands();
    Packet incomingPacket = connection.sendAndAwaitReply(outgoingPacket);

    connection.close(transactionID++);

    if (incomingPacket.type == PacketType.FUNCTION_RETURN) {
      return ((FunctionReturnPacket) incomingPacket).value;
    }
    else if (incomingPacket.type == PacketType.ERROR) {
      throw new InvocationException(((ErrorPacket) incomingPacket).errorMessage);
    }
    else
      throw new IOException("Server returned invalid packet type: " + incomingPacket.type);
  }


  /**
   * Gets an array of the namespace representations on the
   * server at the time of initialization.
   *
   * @return the server's namespace representations
   */
  public NamespaceSpecification[] getNamespaceSpecifications() {
    return namespaceSpecifications.toArray(new NamespaceSpecification[namespaceSpecifications.size()]);
  }

  public MethodSpecification[] getMethodSpecifications(String namespace) {
    for(NamespaceSpecification nSpec : namespaceSpecifications) {
      if (nSpec.name.equals(namespace))
        return nSpec.getMethods();
    }

    System.out.println("NoneFound");
    return null;
  }


  /**
   * Gets the IP of the server
   *
   * @return the IP of the server
   */
  public String getIp() {
    return ip;
  }

  /**
   * Sets the IP of the server to
   * which this will connect when
   * invoking a method
   *
   * @param ip string representation of an IP
   */
  public void setIp(String ip) {
    if (ip == null)
      throw new IllegalArgumentException("Cannot set IP to null");
    this.ip = ip;
    initialized = false;
  }

  /**
   * Gets the port of the server
   *
   * @return the port of the server
   */
  public int getPort() {
    return port;
  }

  /**
   * Sets the port of the server
   * to which this will connect
   * when invoking a method
   *
   * @param port port number
   */
  public void setPort(int port) {
    if (port < 1 || port > 65535)
      throw new IllegalArgumentException("Cannot set port to: " + port);
    this.port = port;
    initialized = false;
  }



}
