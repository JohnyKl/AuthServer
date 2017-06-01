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


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class JarResx
{
    protected Map<String, byte[]> jarEntryContents;

    public JarResx()
    {
        jarEntryContents = new HashMap<String, byte[]>();
    }

    public byte[] getResource(String name)
    {
        return jarEntryContents.get(name);
    }

    public Map<String, byte[]> getResources()
    {
        return jarEntryContents;
    }

    public void loadJar(String jarFile) throws IOException, ClassLoaderException
    {
        FileInputStream fis = new FileInputStream(jarFile);
        loadJar(fis);
        fis.close();
    }

    public void loadJar(URL url) throws IOException, ClassLoaderException
    {
        InputStream in = url.openStream();
        loadJar(in);
        in.close();
    }

    public void loadJar(InputStream jarStream) throws IOException, ClassLoaderException
    {

        BufferedInputStream bis = null;
        JarInputStream jis = null;

        try
        {
            bis = new BufferedInputStream(jarStream);
            jis = new JarInputStream(bis);

            JarEntry jarEntry = null;
            while ((jarEntry = jis.getNextJarEntry()) != null)
            {

                if (jarEntry.isDirectory())
                {
                    continue;
                }

                if (jarEntryContents.containsKey(jarEntry.getName()))
                {
                    if (!Config.supressCollisionException())
                    {
                        throw new ClassLoaderException("Class/Resource " + jarEntry.getName() + " already loaded");
                    }
                    else
                    {
                        continue;
                    }
                }


                byte[] b = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len = 0;
                while ((len = jis.read(b)) > 0)
                {
                    out.write(b, 0, len);
                }

                // add to internal resource HashMap
                jarEntryContents.put(jarEntry.getName(), out.toByteArray());


                out.close();
            }
        }
        catch (NullPointerException e)
        {

        }
        finally
        {
            jis.close();
            bis.close();
        }
    }

    private String dump(JarEntry je)
    {
        StringBuffer sb = new StringBuffer();
        if (je.isDirectory())
        {
            sb.append("d ");
        }
        else
        {
            sb.append("f ");
        }

        if (je.getMethod() == JarEntry.STORED)
        {
            sb.append("stored   ");
        }
        else
        {
            sb.append("defalted ");
        }

        sb.append(je.getName());
        sb.append("\t");
        sb.append("" + je.getSize());
        if (je.getMethod() == JarEntry.DEFLATED)
        {
            sb.append("/" + je.getCompressedSize());
        }

        return (sb.toString());
    }
}
