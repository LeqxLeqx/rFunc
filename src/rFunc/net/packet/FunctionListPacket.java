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

import rFunc.method.MethodSpecification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class FunctionListPacket extends Packet {

  static FunctionListPacket parse(byte[] data) {

    ByteBuffer bb = ByteBuffer.wrap(data);
    LinkedList<MethodSpecification> specs = new LinkedList<>();

    int length;

    while(bb.position() < data.length) {

      length = bb.getInt();
      byte[] mSpecData = new byte[length];
      bb.get(mSpecData);

      specs.add(MethodSpecification.parseFromBytes(mSpecData));
    }

    return new FunctionListPacket(specs.toArray(new MethodSpecification[specs.size()]));
  }

  private final MethodSpecification[] methods;

  public FunctionListPacket(MethodSpecification[] methods) {
    super(PacketType.FUNCTION_LIST);

    if (methods == null)
      throw new IllegalArgumentException("Method specifications cannot be null");
    if (Arrays.asList(methods).contains(null))
      throw new IllegalArgumentException("Method specifications cannot contain a null");

    this.methods = methods.clone();
  }

  public MethodSpecification[] getMethods() {
    return methods.clone();
  }

  @Override
  public byte[] getData() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {

      for(MethodSpecification mSpec : methods) {
        byte[] data = mSpec.getByteData();

        baos.write(ByteBuffer.allocate(4).putInt(data.length).array());
        baos.write(data);

      }

      return baos.toByteArray();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
