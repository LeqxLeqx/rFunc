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

import java.nio.charset.StandardCharsets;

/**
 * Author:    LeqxLeqx
 */
public class FunctionListRequestPacket extends Packet {

  static FunctionListRequestPacket parse(byte[] data) {
    return new FunctionListRequestPacket(new String(data, StandardCharsets.UTF_8));
  }


  public final String namespace;

  public FunctionListRequestPacket(String namespace) {
    super(PacketType.FUNCTION_LIST_REQUEST);
    if (namespace == null)
      throw new IllegalArgumentException("Namespace cannot be null");

    this.namespace = namespace;
  }

  @Override
  public byte[] getData() {
    return namespace.getBytes(StandardCharsets.UTF_8);
  }
}
