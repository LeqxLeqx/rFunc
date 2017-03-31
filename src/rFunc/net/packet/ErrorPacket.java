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

import java.nio.charset.StandardCharsets;

/**
 * Author:    LeqxLeqx
 */
public class ErrorPacket extends Packet {

  public static ErrorPacket parse(byte[] data) {
    return new ErrorPacket(new String(data, StandardCharsets.UTF_8));
  }

  public final String errorMessage;

  public ErrorPacket(String errorMessage) {
    super(PacketType.ERROR);

    this.errorMessage = errorMessage;
  }

  @Override
  public byte[] getData() {
    return errorMessage.getBytes(StandardCharsets.UTF_8);
  }
}
