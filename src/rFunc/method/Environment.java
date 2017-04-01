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
public class Environment {

  private LinkedList<Namespace> namespaces = new LinkedList<>();


  /**
   * Creates a new environment object
   */
  public Environment() {
    namespaces.add(new Namespace(""));
  }


  /**
   * Invokes the specified method with the provided
   * arguments
   *
   * @param methodString method's name
   * @param values arguments for the method
   * @return the value returned by the method
   * @throws InvocationException
   */
  public Value invoke(String methodString, Value[] values) throws InvocationException{

    if (methodString == null)
      throw new IllegalArgumentException("Method string cannot be null");
    if (values == null)
      throw new IllegalArgumentException("Values array cannot be null");
    if (Arrays.asList(values).contains(null))
      throw new IllegalArgumentException("Values array cannot contain nulls");

    String[] methodStringSplit = methodString.split("\\.", 2);

    String namespaceName, methodName;

    if(methodStringSplit.length == 2) {
      namespaceName = methodStringSplit[0];
      methodName = methodStringSplit[1];
    }
    else {
      namespaceName = "";
      methodName = methodString;
    }


    Namespace namespace = getNamespace(namespaceName);
    if (namespace == null)
      throw new InvocationException("No such method \'%s\'", methodString);
    Method method = namespace.get(methodName, ArgumentSpecification.get(values));
    if (method == null)
      throw new InvocationException("No such method \'%s\'", methodString);

    return method.invoke(values);
  }

  /**
   * Invokes the specified method with the provided arguments
   *
   * @param namespaceString containing namespace's name
   * @param methodSpecification method's name
   * @param values arguments to the method
   * @return the value returned by the method
   * @throws InvocationException
   */
  public Value invoke(String namespaceString, MethodSpecification methodSpecification, Value[] values) throws InvocationException{
    if (namespaceString == null)
      throw new IllegalArgumentException("Namespace string cannot be null");
    if (methodSpecification == null)
      throw new IllegalArgumentException("Method spec cannot be null");
    if (values == null)
      throw new IllegalArgumentException("Values array cannot be null");
    if (Arrays.asList(values).contains(null))
      throw new IllegalArgumentException("Values array cannot contain nulls");

    Namespace namespace = getNamespace(namespaceString);

    if (namespace == null)
      throw new InvocationException(String.format("No such namespace \'%s\'", namespaceString));

    Method method = namespace.get(
            methodSpecification.name,
            methodSpecification.argumentSpecification
      );

    if (method == null)
      throw new InvocationException(String.format("No such method \'%s.%s\'", namespaceString, methodSpecification));

    return method.invoke(values);
  }


  /**
   * Gets an array representing the names of the
   * namespaces
   *
   * @return an array of namespace names
   */
  public String[] listNamespaces() {
    String[] ret = new String[namespaces.size()];
    int top = 0;
    for(Namespace n : namespaces) {
      ret[top++] = n.name;
    }

    return ret;
  }

  /**
   * Gets the namespace object of the provided names
   *
   * @param name name of the namespace
   * @return the namespace indicated, or null if none exists
   */
  public Namespace getNamespace(String name) {

    for(Namespace n : namespaces) {
      if (n.name.equals(name))
        return n;
    }

    return null;
  }

  /**
   * Adds a new namespace of the provided name. If
   * such a namespace already exists, this method
   * has no effect
   *
   * @param name the namespace name
   */
  public void addNamespace(String name) {
    if(getNamespace(name) == null) {
      namespaces.add(new Namespace(name));
    }
  }

  /**
   * Removes the namespace indicated
   *
   * @param name the namespace name
   */
  public void removeNamespace(String name) {
    for(int k = 0; k < namespaces.size(); k++) {
      if (namespaces.get(k).name.equals(name)) {
        namespaces.remove(k);
        k--;
      }
    }
  }


}
