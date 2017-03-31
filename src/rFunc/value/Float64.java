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

import java.nio.ByteBuffer;

/**
 * Author:    LeqxLeqx
 */
public class Float64 extends Value {

  static Float64 parseFromBytes(byte[] data) {
    return new Float64(ByteBuffer.wrap(data).getDouble());
  }

  public final double value;

  public Float64(double d) {
    super(ValueType.FLOAT64);

    value = d;
  }

  @Override
  byte[] generateData() {
    return ByteBuffer.allocate(8).putDouble(value).array();
  }

  @Override
  public String toString() {
    return String.format("%s", value);
  }
}
