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

package rFunc.net;

import rFunc.net.packet.Packet;

import java.io.IOException;
import java.net.Socket;

/**
 * Author:    LeqxLeqx
 */
class ServerConnection extends Connection {

  final Server server;

  ServerConnection(Server server, Socket socket) throws IOException {
    super(socket);

    this.server = server;
  }

  @Override
  Packet getResponse(Packet packet) {
    return server.getResponse(this, packet);
  }
}
