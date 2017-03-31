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

/**
 * Author:    LeqxLeqx
 */
public abstract class Method {


  final MethodSpecification methodSpecification;

  Method(MethodSpecification methodSpecification) {
    if (methodSpecification == null)
      throw new IllegalArgumentException("Method specification cannot be null");

    this.methodSpecification = methodSpecification;
  }

  /**
   * Invokes the method
   *
   * @param values arguments to the method
   * @return return value of the method
   * @throws InvocationException
   */
  public abstract Value invoke(Value[] values) throws InvocationException;


}
