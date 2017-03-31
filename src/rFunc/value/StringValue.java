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

import java.nio.charset.StandardCharsets;

/**
 * Author:    LeqxLeqx
 */
public class StringValue extends Value {

  static StringValue parseFromBytes(byte[] data) {
    return new StringValue(new String(data, StandardCharsets.UTF_8));
  }


  public final String string;

  public StringValue(String string) {
    super(ValueType.STRING);

    this.string = string;
  }

  @Override
  byte[] generateData() {
    return string.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public String toString() {
    return string;
  }

}
