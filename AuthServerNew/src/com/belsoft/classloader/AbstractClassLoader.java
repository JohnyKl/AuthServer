/**
 *
 * Copyright (C) 2011 - Alexey Kolenko
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package com.belsoft.classloader;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractClassLoader extends ClassLoader
{
    private Map<String, Class> classes;
    private char classNameReplacementChar;

    public AbstractClassLoader()
    {
        classes = Collections.synchronizedMap(new HashMap<String, Class>());
    }

    @Override
    public Class loadClass(String className) throws ClassNotFoundException
    {
        return (loadClass(className, true));
    }
    
    @Override
    public Class loadClass(String className, boolean resolveIt)
            throws ClassNotFoundException
    {

        Class result;
        byte[] classBytes;

        result = classes.get(className);
        if (result != null)
            return result;

        try
        {
            //Return System class
            result = findSystemClass(className);
            return result;

        }
        catch (ClassNotFoundException e)
        {
        }

        classBytes = loadClassBytes(className);
        if (classBytes == null)
        {
            throw new ClassNotFoundException();
        }

        result = defineClass(className, classBytes, 0, classBytes.length);
        if (result == null)
        {
            throw new ClassFormatError();
        }

        if (resolveIt)
        {
            resolveClass(result);
        }

        classes.put(className, result);
        return result;
    }

    public void setClassNameReplacementChar(char replacement)
    {
        classNameReplacementChar = replacement;
    }

    public char getClassNameReplacementChar()
    {
        return classNameReplacementChar;
    }

    protected abstract byte[] loadClassBytes(String className);

    protected String formatClassName(String className)
    {
        if (classNameReplacementChar == '\u0000')
        {
            // '/' is used to map the package to the path
            return className.replace('.', '/') + ".class";
        }
        else
        {
            // Replace '.' with custom char, such as '_'
            return className.replace('.', classNameReplacementChar) + ".class";
        }
    }
}
