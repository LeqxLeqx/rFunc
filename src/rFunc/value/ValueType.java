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
public enum ValueType {

  // base values

  INT8  ("int8", 0x1),
  INT16 ("int16", 0x2),
  INT32 ("int32", 0x3),
  INT64 ("int64", 0x4),

  FLOAT32 ("float32", 0x5),
  FLOAT64 ("float64", 0x6),

  BOOLEAN ("bool", 0x7),

  // complex values

  STRING ("string", 0x81),

  ;

  public static ValueType getFromIndex(int index) {
    for(ValueType vt : values()) {
      if (vt.index == index)
        return vt;
    }

    return null;
  }

  /**
   * Parses the input value as a ValueType,
   *
   * @param string the string to be parsed
   * @return a ValueType or null if none can be found
   */
  public static ValueType parse(String string) {
    for(ValueType vt : values()) {
      if (vt.name.equals(string))
        return vt;
    }

    return null;
  }


  public final String name;
  public final int index;

  ValueType(String name, int index) {
    this.name = name;
    this.index = index;
  }

  @Override
  public String toString() {
    return name;
  }

}
