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

package rFunc.net.packet;

import rFunc.RFunc;

import java.nio.charset.StandardCharsets;

/**
 * Author:    LeqxLeqx
 */
public class HandshakePacket extends Packet {

  static HandshakePacket parse(byte[] data) {
    String string = new String(data, StandardCharsets.UTF_8);

    return new HandshakePacket(string);
  }


  public final String version;

  private HandshakePacket(String ver) {
    super(PacketType.HANDSHAKE);

    version = ver;
    setTransactionID(-2);
  }

  public HandshakePacket() {
    this(RFunc.getVersion());
  }


  public boolean isValid() {
    return version.equals(RFunc.getVersion());
  }


  @Override
  public byte[] getData() {
    return version.getBytes(StandardCharsets.UTF_8);
  }
}
