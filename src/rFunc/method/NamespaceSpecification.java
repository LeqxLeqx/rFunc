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

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class NamespaceSpecification {

  public final String name;
  private LinkedList<MethodSpecification> methodSpecifications = new LinkedList<>();

  /**
   * Creates a new namespace specification
   *
   * @param name the name of the namespace
   */
  public NamespaceSpecification(String name) {
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");

    this.name = name;
  }

  /**
   * Adds a method specification to this namespace specification
   *
   * @param ms method specification to add
   */
  public void addMethodSpecification(MethodSpecification ms) {
    if (ms == null)
      throw new IllegalArgumentException("Cannot add null method specification");

    methodSpecifications.add(ms);
  }


  /**
   * Returns an array of all of the method specifications in
   * the namespace specification
   *
   * @return an array of the method specifications
   */
  public MethodSpecification[] getMethods() {
    return methodSpecifications.toArray(new MethodSpecification[methodSpecifications.size()]);
  }

  /**
   * Returns the method specification associated with the
   * name and argument list provided
   *
   * @param name the method name
   * @param arguments the arguments to the method
   * @return the method specification indicated
   */
  public MethodSpecification getMethod(String name, Value[] arguments) {
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");
    if (arguments == null)
      throw new IllegalArgumentException("Arguments array cannot be null");
    if (Arrays.asList(arguments).contains(null))
      throw new IllegalArgumentException("Arguments array cannot contain a null");

    ArgumentSpecification argSpec = ArgumentSpecification.get(arguments);

    for(MethodSpecification mspec : methodSpecifications) {
      if (
              mspec.name.equals(name) &&
              mspec.argumentSpecification.equals(argSpec)
              )
        return mspec;
    }

    return null;
  }


  /**
   * Clones the namespace specification
   *
   * @return the cloned specification
   */
  @Override
  public NamespaceSpecification clone() {
    NamespaceSpecification ret = new NamespaceSpecification(name);

    for(MethodSpecification mspec : methodSpecifications) {
      ret.addMethodSpecification(mspec);
    }

    return ret;
  }



}
