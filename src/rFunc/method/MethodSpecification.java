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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Author:    LeqxLeqx
 */
public class MethodSpecification {


  /**
   * Parses the method specification from the bytes provided
   *
   * @param bytes the bytes representing the method specification
   * @return the method specification
   */
  public static MethodSpecification parseFromBytes(byte[] bytes) {
    ByteArrayOutputStream
            name = new ByteArrayOutputStream(),
            description = new ByteArrayOutputStream(),
            argSpec = new ByteArrayOutputStream()
                    ;

    int top = 0;
    do {
      if (bytes[top] == 0) continue;
      name.write(bytes[top] & 0xFF);
    } while(bytes[top++] != 0);

    do {
      if (bytes[top] == 0) continue;
      description.write(bytes[top] & 0xFF);
    } while(bytes[top++] != 0);

    argSpec.write(bytes, top, bytes.length - top);


    return new MethodSpecification(
            ArgumentSpecification.parseFromBytes(argSpec.toByteArray()),
            new String(name.toByteArray(), StandardCharsets.UTF_8),
            new String(description.toByteArray(), StandardCharsets.UTF_8)
     );
  }


  public final ArgumentSpecification argumentSpecification;
  public final String name, description;

  /**
   * Creates a method specification from the provided arguments
   *
   * @param argumentSpecification argument specification of the method
   * @param name name of the method
   * @param description description of the method
   */
  public MethodSpecification(
          ArgumentSpecification argumentSpecification,
          String name,
          String description
          ) {
    if (argumentSpecification == null)
      throw new IllegalArgumentException("Argument specification array cannot be null");
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");
    if (description == null)
      throw new IllegalArgumentException("Description cannot be null");
    if (name.contains("\0"))
      throw new IllegalArgumentException("Name cannot contain null character");
    if (description.contains("\0"))
      throw new IllegalArgumentException("Description cannot contain null character");

    this.argumentSpecification = argumentSpecification;
    this.name = name;
    this.description = description;

  }

  /**
   * Gets a byte data representation of the method specification
   *
   * @return a byte data representation of the method specification
   */
  public byte[] getByteData() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      baos.write(name.getBytes(StandardCharsets.UTF_8));
      baos.write(0);

      baos.write(description.getBytes(StandardCharsets.UTF_8));
      baos.write(0);

      baos.write(argumentSpecification.getData());

      return baos.toByteArray();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return String.format("%s(%s)", name, argumentSpecification);
  }

}
