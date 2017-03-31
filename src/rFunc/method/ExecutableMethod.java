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

import rFunc.value.Int32;
import rFunc.value.Value;
import rFunc.value.ValueType;

import java.io.File;
import java.io.IOException;

/**
 * Author:    LeqxLeqx
 */
public class ExecutableMethod extends Method {

  final File executablePath;

  /**
   * Creates an executable method wrapper
   *
   * @param methodSpecification method specification
   * @param executablePath the path of the executable to be run
   */
  public ExecutableMethod(MethodSpecification methodSpecification, File executablePath) {
    super(methodSpecification);

    if (!methodSpecification.argumentSpecification.isAll(ValueType.STRING))
      throw new IllegalArgumentException("Executable type method argument specification cannot contain non-string value types");

    if (executablePath == null)
      throw new IllegalArgumentException("Executable path cannot be null");

    this.executablePath = executablePath;
  }

  /**
   * Invokes the method
   *
   * @param values Arguments to the method
   * @return the return code of the executable
   */
  @Override
  public Int32 invoke(Value[] values) {

    String[] arguments = new String[values.length + 1];
    arguments[0] = executablePath.getAbsolutePath();
    for(int k = 0; k < values.length; k++) {
      arguments[k + 1] = values[k].toString();
    }

    ProcessBuilder processBuilder = new ProcessBuilder(arguments);

    try {

      Process process = processBuilder.start();
      int ret = process.waitFor();
      return new Int32(ret);

    } catch (IOException e) {

      return new Int32(-1);

    } catch (InterruptedException e) {

      return new Int32(-2);

    }

  }
}
