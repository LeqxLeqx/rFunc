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
public class JavaMethod extends Method {


  final JavaMethodInterface javaMethodInterface;

  /**
   * Creates a method wrapper for a class which
   * implements the interface 'JavaMethodInterface'
   *
   * @param methodSpecification the method specificaton
   * @param javaMethodInterface the class implementing the interface
   */
  public JavaMethod(MethodSpecification methodSpecification, JavaMethodInterface javaMethodInterface) {
    super(methodSpecification);

    this.javaMethodInterface = javaMethodInterface;
  }

  /**
   * Invokes the provided object's implementation
   * of the method
   *
   * @param values arguments to the method
   * @return the return values
   * @throws InvocationException
   */
  @Override
  public Value invoke(Value[] values) throws InvocationException{
    Value ret = javaMethodInterface.invoke(values);

    if (ret == null)
      throw new InvocationException("Invoked method cannot return null");
    else
      return ret;

  }
}
