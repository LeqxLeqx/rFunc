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

package rFunc.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Author:    LeqxLeqx
 */
public abstract class Value {


  /**
   * Gets a 'Value' wrapper for the provided object if
   * such a wrapper exists for the provided object type.
   * Otherwise the return value is null.
   *
   * @param object the object to wrap
   * @return the wrapped value
   */
  public static Value get(Object object) {
    if (object == null)
      return null;

    if (object instanceof Boolean)
      return get(((Boolean) object).booleanValue());
    else if (object instanceof Float)
      return get(((Float) object).floatValue());
    else if (object instanceof Double)
      return get(((Double) object).doubleValue());
    else if (object instanceof Byte)
      return get(((Byte) object).byteValue());
    else if (object instanceof Short)
      return get(((Short) object).shortValue());
    else if (object instanceof Integer)
      return get(((Integer) object).intValue());
    else if (object instanceof Long)
      return get(((Long) object).longValue());
    else if (object instanceof String)
      return get(((String) object));
    else
      return null;

  }


  /**
   * Gets a 'BooleanValue' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static BooleanValue get(boolean value) {
    return new BooleanValue(value);
  }

  /**
   * Gets a 'Float32' wrapper for the provided float value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static Float32 get(float value) {
    return new Float32(value);
  }

  /**
   * Gets a 'Float64' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static Float64 get(double value) {
    return new Float64(value);
  }

  /**
   * Gets a 'Int8' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static Int8 get(byte value) {
    return new Int8(value);
  }

  /**
   * Gets a 'Int16' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static Int16 get(short value) {
    return new Int16(value);
  }

  /**
   * Gets a 'Int32' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static Int32 get(int value) {
    return new Int32(value);
  }

  /**
   * Gets a 'Int64' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static Int64 get(long value) {
    return new Int64(value);
  }

  /**
   * Gets a 'StringValue' wrapper for the provided boolean value
   * @param value the value to wrap
   * @return the wrapped value
   */
  public static StringValue get(String value) {
    return new StringValue(value);
  }


  /**
   * Parses a value from a data stream
   *
   * @param is input stream
   * @return a value parsed from the input stream
   * @throws IOException
   */
  public static Value parseFromStream(InputStream is) throws IOException {
    int typeInteger = is.read(), length;
    byte[] lengthData = new byte[4];
    is.read(lengthData);

    length = ByteBuffer.wrap(lengthData).getInt();
    ValueType type = ValueType.getFromIndex(typeInteger);

    byte[] data = new byte[length];

    is.read(data);

    switch(type) {

      case BOOLEAN:
        return BooleanValue.parseFromBytes(data);
      case FLOAT32:
        return Float32.parseFromBytes(data);
      case FLOAT64:
        return Float64.parseFromBytes(data);
      case INT8:
        return Int8.parseFromBytes(data);
      case INT16:
        return Int16.parseFromBytes(data);
      case INT32:
        return Int32.parseFromBytes(data);
      case INT64:
        return Int64.parseFromBytes(data);
      case STRING:
        return StringValue.parseFromBytes(data);

      default:
        throw new RuntimeException();

    }


  }


  public final ValueType type;

  Value(ValueType t) {
    type = t;
  }

  abstract byte[] generateData();

  /**
   * Gets the data representation of this value
   *
   * @return The data representation of this value
   */
  public byte[] getData() {
    byte[] instanceData = generateData();

    ByteBuffer bb = ByteBuffer.allocate(instanceData.length + 5);

    bb.put((byte) type.index);
    bb.putInt(instanceData.length);
    bb.put(instanceData);

    return bb.array();
  }

}
