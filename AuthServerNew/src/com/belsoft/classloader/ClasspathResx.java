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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;


public class ClasspathResx extends JarResx
{
    private void loadResourceContent(String resource) throws IOException,
                                                              ClassLoaderException
    {
        File resourceFile = new File(resource);

        FileInputStream fis = new FileInputStream(resourceFile);

        byte[] content = new byte[(int) resourceFile.length()];
        fis.read(content);

        if (jarEntryContents.containsKey(resourceFile.getName()))
        {
            if (!Config.supressCollisionException())
            {
                throw new ClassLoaderException("Resource " + resourceFile.getName() + " already loaded");
            }
            else
            {
                return;
            }
        }

        fis.close();

        jarEntryContents.put(resourceFile.getName(), content);
    }

    /**
     * Attempts to load a remote resource (jars, properties files, etc)
     * 
     * @param url
     * @throws IOException
     * @throws ClassLoaderException
     */
    private void loadRemoteResource(URL url) throws IOException, ClassLoaderException
    {
        if (url.toString().toLowerCase().endsWith(".jar"))
        {
            loadJar(url);
            return;
        }

        InputStream stream = url.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int byt;
        while (((byt = stream.read()) != -1))
        {
            out.write(byt);
        }

        byte[] content = out.toByteArray();

        if (jarEntryContents.containsKey(url.toString()))
        {
            if (!Config.supressCollisionException())
            {
                throw new ClassLoaderException("Resource " + url.toString() + " already loaded");
            }
            else
            {
                return;
            }
        }

        jarEntryContents.put(url.toString(), content);

        out.close();
        stream.close();
    }

    private void loadClassContent(String clazz, String pack)
            throws ClassLoaderException, IOException
    {
        File cf = new File(clazz);
        FileInputStream fis = new FileInputStream(cf);

        byte[] content = new byte[(int) cf.length()];
        fis.read(content);

        String entryName = pack + "/" + cf.getName();

        if (jarEntryContents.containsKey(entryName))
        {
            if (!Config.supressCollisionException())
            {
                throw new ClassLoaderException("Class " + entryName + " already loaded");
            }
            else
            {
                return;
            }
        }

        fis.close();

        jarEntryContents.put(entryName, content);
    }

    /**
     * Reads local and remote resources
     * 
     * @param url
     * @throws IOException
     * @throws ClassLoaderException
     * @throws URISyntaxException
     */
    public void loadResource(URL url) throws IOException, ClassLoaderException,
                                              URISyntaxException
    {
        try
        {
            // Is Local
            loadResource(new File(url.toURI()), "");
        }
        catch (IllegalArgumentException iae)
        {
            // Is Remote
            loadRemoteResource(url);
        }
    }

    /**
     * Reads local resources from
     * - Jar files
     * - Class folders
     * - Jar Library folders
     * 
     * @param path
     * @throws IOException
     * @throws ClassLoaderException
     */
    public void loadResource(String path) throws IOException, ClassLoaderException
    {
        loadResource(new File(path), "");
    }

    private void loadResource(File fol, String packName) throws IOException,
                                                                 ClassLoaderException
    {
        if (fol.isFile())
        {
            if (fol.getName().toLowerCase().endsWith(".class"))
            {
                loadClassContent(fol.getAbsolutePath(), packName);
            }
            else
            {
                if (fol.getName().toLowerCase().endsWith(".jar"))
                {
                    loadJar(fol.getAbsolutePath());
                }
                else
                {
                    loadResourceContent(fol.getAbsolutePath());
                }
            }

            return;
        }

        if (fol.list() != null)
        {
            for (String f : fol.list())
            {
                File fl = new File(fol.getAbsolutePath() + "/" + f);

                String pn = packName;

                if (fl.isDirectory())
                {

                    if (!pn.equals(""))
                    {
                        pn = pn + "/";
                    }

                    pn = pn + fl.getName();
                }

                loadResource(fl, pn);
            }
        }
    }
}
