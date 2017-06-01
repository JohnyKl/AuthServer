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


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;


public class JarClassLoader extends AbstractClassLoader
{
    private ClasspathResx classpathResources;

    private JarClassLoader()
    {
        classpathResources = new ClasspathResx();
    }

    @Deprecated
    public JarClassLoader(String resourceName) throws IOException, ClassLoaderException
    {
        this();
        classpathResources.loadResource(resourceName);
    }

    public JarClassLoader(String[] resourceNames) throws IOException, ClassLoaderException
    {
        this();

        for (String resource : resourceNames)
        {
            classpathResources.loadResource(resource);
        }
    }

    @Deprecated
    public JarClassLoader(InputStream jarStream) throws IOException,
                                                         ClassLoaderException
    {
        this();
        classpathResources.loadJar(jarStream);
    }

    public JarClassLoader(InputStream[] jarStreams) throws IOException,
                                                            ClassLoaderException
    {
        this();

        for (InputStream stream : jarStreams)
        {
            classpathResources.loadJar(stream);
        }
    }

    @Deprecated
    public JarClassLoader(URL url) throws IOException, ClassLoaderException,
                                           URISyntaxException
    {
        classpathResources = new ClasspathResx();
        classpathResources.loadResource(url);
    }

    public JarClassLoader(URL[] urls) throws IOException, ClassLoaderException,
                                              URISyntaxException
    {
        classpathResources = new ClasspathResx();

        for (URL url : urls)
        {
            classpathResources.loadResource(url);
        }
    }

    protected byte[] loadClassBytes(String className)
    {
        className = formatClassName(className);

        return (classpathResources.getResource(className));
    }
}
