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

/**
 * Author:    LeqxLeqx
 */
public enum PacketType {

  HANDSHAKE ("hds"),
  TERMINATE ("trm"),

  FUNCTION_LIST_REQUEST ("flr"),
  NAMESPACE_LIST_REQUEST   ("nlr"),

  FUNCTION_LIST ("fls"),
  NAMESPACE_LIST ("nls"),

  FUNCTION_CALL ("fcl"),
  FUNCTION_RETURN ("ret"),

  ERROR ("err"),

  ;

  static {
    for(int k = 0; k < values().length - 1; k++) {
      for(int i = k + 1; i < values().length; i++) {
        if (values()[k].identifier.equals(values()[i]))
          throw new RuntimeException();
      }
    }
  }


  public static PacketType parse(String id) {
    if (id == null)
      throw new IllegalArgumentException("Id cannot be null");
    if (id.length() != 3)
      throw new IllegalArgumentException("Id length must be 3");

    for(PacketType pt : values()) {
      if (pt.identifier.equals(id))
        return pt;
    }

    return null;
  }


  public final String identifier;

  PacketType(String identifier) {
    if (identifier.length() != 3)
      throw new RuntimeException();
    if (
            identifier.charAt(0) > 0x7F ||
            identifier.charAt(1) > 0x7F ||
            identifier.charAt(2) > 0x7F
            )
      throw new RuntimeException();

    this.identifier = identifier;
  }


  public boolean containsData() {
    return
            this != NAMESPACE_LIST_REQUEST &&
            this != TERMINATE
            ;
  }


  public byte[] getBytes() {
    return new byte[] {
            (byte) identifier.charAt(0),
            (byte) identifier.charAt(1),
            (byte) identifier.charAt(2),
    };
  }

}
