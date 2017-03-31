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

/**
 * Author:    LeqxLeqx
 */
public class BooleanValue extends Value {

  static BooleanValue parseFromBytes(byte[] data) {
    return new BooleanValue((data[0] & 0xFF) == 0xFF);
  }


  public final boolean value;

  public BooleanValue(boolean b) {
    super(ValueType.BOOLEAN);

    value = b;
  }

  @Override
  byte[] generateData() {
    return new byte[] { (byte) (value ? 0xFF : 0) } ;
  }

  @Override
  public String toString() {
    return String.format("%s", value);
  }
}
