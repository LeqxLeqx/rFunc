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

package rFunc.method;

import rFunc.value.Value;
import rFunc.value.ValueType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class ArgumentSpecification {

  public static final ArgumentSpecification
                    NONE = new ArgumentSpecification(new ValueType[0], new String[0])
                    ;


  /**
   * Parses the provided string as an argument specification. Any string
   * produced by the invocation of the toString() method on an
   * Argument specification object will be parsable
   *
   * @param string string to be parsed
   * @return an ArgumentSpecification object represented by the string
   */
  public static ArgumentSpecification parse(String string) {
    if (string == null)
      throw new IllegalArgumentException("Cannot parse null string as argument specification");

    string = string.trim();

    if (string.isEmpty())
      return NONE;

    String[]
            split = string.split("\\,"),
            names = new String[split.length];
    ValueType[]
            valueTypes = new ValueType[split.length];


    for(int k = 0; k < split.length; k++) {

      String substring = split[k].trim();

      String[] substringSplit = substring.split(" ");

      if (substringSplit.length == 1) {
        valueTypes[k] = ValueType.parse(substringSplit[0]);
        if (valueTypes[k] == null)
          throw new IllegalArgumentException("Cannot parse '" + substringSplit[0] + "' as a value type");
        names[k] = "arg" + k;
      }
      else if (substringSplit.length == 2) {
        valueTypes[k] = ValueType.parse(substringSplit[0]);
        if (valueTypes[k] == null)
          throw new IllegalArgumentException("Cannot parse '" + substringSplit[0] + "' as a value type");
        names[k] = substringSplit[1];
      }
      else
        throw new IllegalArgumentException("Cannot parse '" + string + "' as argument specification");

    }

    return new ArgumentSpecification(valueTypes, names);
  }

  /**
   * Parses a byte representation of an argument specification
   *
   * @param data the data to parse
   * @return the argument specification represented by the data
   */
  public static ArgumentSpecification parseFromBytes(byte[] data) {

    int length = data[0];

    ValueType[] valueTypes = new ValueType[length];
    for(int k = 1; k < length + 1; k++) {
      valueTypes[k - 1] = ValueType.getFromIndex(data[k] & 0xFF);
    }

    LinkedList<String> names = new LinkedList<>();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    for(int k = length + 1; k < data.length; k++) {
      if (data[k] == 0) {
        names.add(new String(buffer.toByteArray(), StandardCharsets.UTF_8));
        buffer.reset();
      }
      else {
        buffer.write(data[k] & 0xFF);
      }
    }

    return new ArgumentSpecification(valueTypes, names.toArray(new String[names.size()]));
  }

  /**
   * Gets an argument-specification associated with the argument
   * list provided
   *
   * @param values argument list
   * @return argument specification of the provided list
   */
  public static ArgumentSpecification get(Value[] values) {
    ValueType[] vts = new ValueType[values.length];
    for(int k = 0; k < values.length; k++) {
      vts[k] = values[k].type;
    }

    return new ArgumentSpecification(vts, defaultValueNames(vts.length));
  }

  private static String[] defaultValueNames(int size) {
    String[] ret = new String[size];

    for(int k = 0; k < size; k++) {
      ret[k] = String.format("arg%d", k);
    }

    return ret;
  }



  private ValueType[] array;
  private String[] names;

  /**
   * Creates an argument specification
   * @param valueTypes the ordered value types
   * @param valueNames the names of the variables
   */
  public ArgumentSpecification(ValueType[] valueTypes, String[] valueNames) {
    if (valueTypes == null)
      throw new IllegalArgumentException("Value types array cannot be null");
    if (Arrays.asList(valueTypes).contains(null))
      throw new IllegalArgumentException("Value types array cannot contain nulls");
    if (valueNames == null)
      throw new IllegalArgumentException("Value names array cannot be null");
    if (Arrays.asList(valueNames).contains(null))
      throw new IllegalArgumentException("Value names array cannot contain nulls");
    if (valueTypes.length != valueNames.length)
      throw new IllegalArgumentException("Array length mismatch");

    this.array = valueTypes.clone();
    this.names = valueNames.clone();
  }

  /**
   * Gets a clone of the value type array
   *
   * @return clone of the value type array
   */
  public ValueType[] valueTypeArray() {
    return array.clone();
  }

  /**
   * Gets a clone of the argument names array
   *
   * @return clone of the argument names array
   */
  public String[] namesArray() {
    return names.clone();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    else if (o instanceof ArgumentSpecification)
      return equals((ArgumentSpecification) o);
    else
      return false;
  }

  /**
   * Compares the argument specification to another.
   *
   * @param e other argument specification
   * @return true of the two argument specifications have the same ordered value types
   */
  public boolean equals(ArgumentSpecification e) {
    if (e == null)
      return false;

    if(e.array.length != array.length)
      return false;

    for(int k = 0; k < array.length; k++) {
      if (!e.array[k].equals(array[k]))
        return false;

    }

    return true;
  }

  /**
   * Gets a byte representation of the argument specification
   *
   * @return a byte representation of the argument specification
   */
  public byte[] getData() {
    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      baos.write(array.length);
      for(ValueType vt : array) {
        baos.write(vt.index);
      }

      for(String name : names) {
        baos.write(name.getBytes(StandardCharsets.UTF_8));
        baos.write(0);
      }

      return baos.toByteArray();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks each value type against the provided value type
   *
   * @param valueType the provided value type
   * @return true if all values of the specification are of the provided type
   */
  public boolean isAll(ValueType valueType) {
    for(ValueType vt : array) {
      if (vt != valueType)
        return false;
    }

    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for(int k = 0; k < array.length; k++) {

      sb.append(array[k].toString());
      sb.append(' ');
      sb.append(names[k]);

      if(k != array.length - 1)
        sb.append(", ");

    }

    return sb.toString();
  }

}
