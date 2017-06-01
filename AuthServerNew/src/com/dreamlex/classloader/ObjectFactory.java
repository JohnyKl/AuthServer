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

package com.dreamlex.classloader;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class ObjectFactory
{
    private static ObjectFactory objectFactory = new ObjectFactory();


    private ObjectFactory() {}

    public static ObjectFactory getInstance()
    {
        return objectFactory;
    }

    public Object create(JarClassLoader jcl, String className)
            throws IllegalArgumentException, SecurityException, IOException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException
    {
        return create(jcl, className, null);
    }

    public Object create(JarClassLoader jcl, String className,
            Object[] args) throws IOException, ClassNotFoundException,
                                  InstantiationException, IllegalAccessException,
                                  IllegalArgumentException, SecurityException,
                                  InvocationTargetException, NoSuchMethodException
    {
        if (args == null || args.length == 0)
        {
            return jcl.loadClass(className).newInstance();
        }

        Class[] types = new Class[args.length];

        for (int i = 0; i < args.length; i++)
        {
            types[i] = args[i].getClass();
        }

        return jcl.loadClass(className).getConstructor(types).newInstance(args);
    }
}
