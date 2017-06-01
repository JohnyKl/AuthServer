/**
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

package com.belsoft;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class ServerProperties
{
    private static final Logger log = LoggerFactory.getLogger(ServerProperties.class);

    private final String policyFileName = "crossdomainpolicy.xml";
    private final String defaultPolicy = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>";
    
    public String LoadCrossDomainPolicy()
    {
        StringBuffer fileData = new StringBuffer(1000);
        boolean exists = (new File(policyFileName)).exists();
        
        try
        {        
            if(exists)
            {   
                BufferedReader reader = new BufferedReader(new FileReader(policyFileName));
                char[] buf = new char[1024];
                int numRead=0;

                while((numRead=reader.read(buf)) != -1)
                {
                    fileData.append(buf, 0, numRead);
                }
                reader.close();
            }
        }
        catch(Exception ex)
        {
            log.error("Couldn't load the crossdomainpolicy.xml file, using the default instead");
            return defaultPolicy;
        }
        
        return fileData.toString().trim();
    }    
}

  