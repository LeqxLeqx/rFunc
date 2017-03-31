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

import rFunc.RFunc;
import rFunc.net.packet.HandshakePacket;
import rFunc.net.packet.Packet;
import rFunc.net.packet.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Author:    LeqxLeqx
 */
abstract class Connection {


  final Socket socket;
  final InputStream inputStream;
  final OutputStream outputStream;

  boolean handshakeComplete = false;

  Connection(Socket socket) throws IOException {

    if (socket == null)
      throw new IllegalArgumentException("Socket for connection cannot be null");

    this.socket = socket;
    inputStream = socket.getInputStream();
    outputStream = socket.getOutputStream();

  }

  abstract Packet getResponse(Packet packet);



  void close() {
    try {
      socket.close();
    } catch (IOException e) {}
  }


  synchronized void shakeHands() throws IOException {

    privateSend(new HandshakePacket());
    Packet packet = awaitIncomingPacket();
    if (packet.type != PacketType.HANDSHAKE)
      throw new IOException("Error in handshake. Invalid packet type sent by respondent");
    else if (!((HandshakePacket) packet).isValid())
      throw new IOException(String.format("Error in handshake. Version mismatch between this library and respondent library (%s != %s)", RFunc.getVersion(), ((HandshakePacket) packet).version));


    handshakeComplete = true;
  }


  boolean incomingDataReady() throws IOException{
    return inputStream.available() > 0;
  }

  synchronized Packet awaitIncomingPacket(boolean sendResponse) throws IOException {

    if (!handshakeComplete)
      throw new RuntimeException("Cannot await incoming packet until handshake is complete");


    Packet incoming = awaitIncomingPacket();

    if (sendResponse) {

      Packet outgoing = getResponse(incoming);
      if (outgoing != null)
        privateSend(outgoing);

    }

    return incoming;
  }

  private Packet awaitIncomingPacket() throws IOException{
    return Packet.parse(inputStream);
  }

  synchronized Packet sendAndAwaitReply(Packet packet) throws IOException {
    if (packet == null)
      throw new IllegalArgumentException("Packet cannot be null");

    privateSend(packet);
    return awaitIncomingPacket();
  }

  synchronized void send(Packet packet) throws IOException {
    if (packet == null)
      throw new IllegalArgumentException("Packet cannot be null");

    privateSend(packet);
  }

  private void privateSend(Packet packet) throws IOException{
    packet.writeData(outputStream);
  }



  @Override
  public String toString() {
    return socket.getInetAddress().toString();
  }



}
