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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class NamespaceListPacket extends Packet {

  static NamespaceListPacket parse(byte[] data) {

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    LinkedList<String> names = new LinkedList<>();

    for(int k = 0; k < data.length; k++) {
      if(data[0] == 0) {
        names.add(new String(buffer.toByteArray(), StandardCharsets.UTF_8));
        buffer.reset();
      }
      else {
        buffer.write(data[0] & 0xFF);
      }
    }

    return new NamespaceListPacket(names.toArray(new String[names.size()]));

  }

  private final String[] names;

  public NamespaceListPacket(String[] names) {
    super(PacketType.NAMESPACE_LIST);

    if (names == null)
      throw new IllegalArgumentException("Names cannot be null");
    if (Arrays.asList(names).contains(null))
      throw new IllegalArgumentException("Names array cannot contain null");

    this.names = names.clone();
  }

  public String[] getNames() {
    return names.clone();
  }


  @Override
  public byte[] getData() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {

      for(String s : names) {
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        baos.write(0);
      }

      return baos.toByteArray();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
