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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Author:    LeqxLeqx
 */
public abstract class Packet {

  public static Packet parse(InputStream is) throws IOException {
    Packet ret;

    long transID;
    int instanceDataLength;
    String typeString;
    PacketType type;
    byte[] transactionIDData = new byte[8], typeData = new byte[3], instanceDataLengthData = new byte[4], instanceData;

    is.read(transactionIDData);
    is.read(typeData);


    transID = ByteBuffer.wrap(transactionIDData).getLong();
    typeString = new String(typeData, StandardCharsets.US_ASCII);

    type = PacketType.parse(typeString);
    if (type == null)
      throw new RuntimeException(String.format("Un-parsable type string '%s'", typeString));

    if (!type.containsData()) {
      switch(type) {

        case NAMESPACE_LIST_REQUEST:
          ret = new NamespaceListRequestPacket();
          break;

        case TERMINATE:
          ret = new TerminatePacket();
          break;

        default:
          throw new RuntimeException();

      }
    }
    else {

      is.read(instanceDataLengthData);
      instanceDataLength = ByteBuffer.wrap(instanceDataLengthData).getInt();

      instanceData = new byte[instanceDataLength];
      is.read(instanceData);

      switch (type) {

        case FUNCTION_CALL:
          ret = FunctionCallPacket.parse(instanceData);
          break;

        case FUNCTION_LIST:
          ret = FunctionListPacket.parse(instanceData);
          break;

        case FUNCTION_LIST_REQUEST:
          ret = FunctionListRequestPacket.parse(instanceData);
          break;

        case FUNCTION_RETURN:
          ret = FunctionReturnPacket.parse(instanceData);
          break;

        case HANDSHAKE:
          ret = HandshakePacket.parse(instanceData);
          break;

        case NAMESPACE_LIST:
          ret = NamespaceListPacket.parse(instanceData);
          break;

        case ERROR:
          ret = ErrorPacket.parse(instanceData);
          break;


        case NAMESPACE_LIST_REQUEST:
        case TERMINATE:
        default:
          throw new RuntimeException();

      }

    }


    ret.setTransactionID(transID);

    return ret;
  }


  public final PacketType type;
  private long transactionID = -1;

  Packet(PacketType pt) {
    if (pt == null)
      throw new IllegalArgumentException("Type cannot be null");

    this.type = pt;
  }

  public void setTransactionID(long l) {
    if (l == -1)
      throw new IllegalArgumentException("Cannot set transaction id to -1");

    transactionID = l;
  }

  public long getTransactionID() {
    return transactionID;
  }


  public abstract byte[] getData();

  public void writeData(OutputStream os) throws IOException {

    if (transactionID == -1)
      throw new RuntimeException();


    byte[]
        instanceData,
        transactionIDArray = ByteBuffer.allocate(8).putLong(transactionID).array(),
        typeBytes = type.getBytes()
                    ;

    os.write(transactionIDArray);
    os.write(typeBytes);

    if (type.containsData()) {

      instanceData = getData();

      os.write(ByteBuffer.allocate(4).putInt(instanceData.length).array());
      os.write(instanceData);
    }

  }

}
