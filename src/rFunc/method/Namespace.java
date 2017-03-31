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

import java.util.LinkedList;

/**
 * Author:    LeqxLeqx
 */
public class Namespace {

  public final String name;

  private LinkedList<Method> methods = new LinkedList<>();

  /**
   * Creates an empty namespace of the provided name
   *
   * @param name name of the namespace
   */
  public Namespace(String name) {
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");

    this.name = name;
  }

  /**
   * Checks whether or not the method specification is contained
   * within this namespace
   *
   * @param methodSpec the method specification
   * @return true of 'methodSpec' is contained within this
   */
  public boolean contains(MethodSpecification methodSpec) {
    if (methodSpec == null)
      throw new IllegalArgumentException("Method specification cannot be null");

    return contains(methodSpec.name, methodSpec.argumentSpecification);
  }

  /**
   * Checks whether or not the method of the provided
   * name and argument specification are within this
   * namespace
   *
   * @param name name of the method
   * @param argSpec argument specification of the method
   * @return true if a congruent method is contained within this
   */
  public boolean contains(String name, ArgumentSpecification argSpec) {
    if (name == null)
      throw new IllegalArgumentException("Name cannot be null");
    if (argSpec == null)
      throw new IllegalArgumentException("Argument specification cannot be null");

    for(Method method : methods) {
      if (
              method.methodSpecification.argumentSpecification.equals(argSpec) &&
              method.methodSpecification.name.equals(name)
              )
        return true;
    }

    return false;
  }

  /**
   * Lists the method specification contained within the namespace
   *
   * @return an array of the method specifications
   */
  public MethodSpecification[] list() {
    LinkedList<MethodSpecification> ret = new LinkedList<>();

    for(Method m : methods) {
      ret.add(m.methodSpecification);
    }

    return ret.toArray(new MethodSpecification[ret.size()]);
  }

  /**
   * Gets the method of the indicated name and
   * the argument specification
   *
   * @param string the method name
   * @param argSpec the argument specification
   * @return the method indicated
   */
  public Method get(String string, ArgumentSpecification argSpec) {
    for(Method method : methods) {
      if (method.methodSpecification.argumentSpecification.equals(argSpec) && method.methodSpecification.name.equals(string))
        return method;
    }

    return null;
  }

  /**
   * Adds the provided method to this namespace
   * @param method the method to add
   */
  public void add(Method method) {
    if (method == null)
      throw new IllegalArgumentException("Method cannot be null");

    if (contains(method.methodSpecification))
      throw new IllegalArgumentException("Namespace already contains method");

    methods.add(method);
  }


  /**
   * Removes the method associated with the provided
   * method specification
   *
   * @param methodSpecification specification of the method to remove
   */
  public void remove(MethodSpecification methodSpecification) {
    if (methodSpecification == null)
      throw new IllegalArgumentException("Method spec cannot be null");

    remove(methodSpecification.name, methodSpecification.argumentSpecification);
  }

  /**
   * Removes the method associated with the provided
   * name and the provided argument specification
   *
   * @param name the name of the method to remove
   * @param argSpec argument specification of the method to remove
   */
  public void remove(String name, ArgumentSpecification argSpec) {
    for(int k = 0; k < methods.size(); k++) {
      if (
          methods.get(k).methodSpecification.argumentSpecification.equals(argSpec) &&
          methods.get(k).methodSpecification.name.equals(name)
          ) {
        methods.remove(k);
        k = methods.size();
      }
    }
  }

  /**
   * Removes all the methods from this namespace
   */
  public void removeAll() {
    methods.clear();
  }


}
