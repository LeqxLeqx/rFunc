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
import rFunc.value.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class FunctionCallPacket extends Packet {

  static FunctionCallPacket parse(byte[] data) throws IOException {

    String namespace;
    MethodSpecification mSpec;
    LinkedList<Value> values = new LinkedList<>();

    byte[]
            namespaceLengthData = new byte[4],
            mSpecLengthData = new byte[4],
            namespaceData,
            mSpecData
            ;

    int namespaceLength, mSpecLength;

    ByteArrayInputStream bais = new ByteArrayInputStream(data);

    bais.read(namespaceLengthData);
    namespaceLength = ByteBuffer.wrap(namespaceLengthData).getInt();
    namespaceData = new byte[namespaceLength];
    bais.read(namespaceData);

    bais.read(mSpecLengthData);
    mSpecLength = ByteBuffer.wrap(mSpecLengthData).getInt();
    mSpecData = new byte[mSpecLength];
    bais.read(mSpecData);

    namespace = new String(namespaceData, StandardCharsets.UTF_8);
    mSpec = MethodSpecification.parseFromBytes(mSpecData);


    while(bais.available() > 0) {
      values.add(Value.parseFromStream(bais));
    }

    return new FunctionCallPacket(namespace, mSpec, values.toArray(new Value[values.size()]));
  }

  public final String namespace;
  public final MethodSpecification methodSpecification;
  public final Value[] arguments;

  public FunctionCallPacket(String namespace, MethodSpecification methodSpec, Value[] arguments) {
    super(PacketType.FUNCTION_CALL);

    if (namespace == null)
      throw new IllegalArgumentException("Namespace cannot be null");
    if (methodSpec == null)
      throw new IllegalArgumentException("Method specification cannot be null");
    if (arguments == null)
      throw new IllegalArgumentException("Arguments cannot be null");
    if (Arrays.asList(arguments).contains(null))
      throw new IllegalArgumentException("Arguments cannot contain null");

    this.namespace = namespace;
    methodSpecification = methodSpec;
    this.arguments = arguments.clone();
  }

  @Override
  public byte[] getData() {
    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      byte[] namespaceData = namespace.getBytes(StandardCharsets.UTF_8);

      baos.write(ByteBuffer.allocate(4).putInt(namespaceData.length).array());
      baos.write(namespaceData);

      byte[] methodSpecData = methodSpecification.getByteData();

      baos.write(ByteBuffer.allocate(4).putInt(methodSpecData.length).array());
      baos.write(methodSpecData);

      for(Value v : arguments) {
        baos.write(v.getData());
      }

      return baos.toByteArray();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
