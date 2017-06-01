package com.dreamlex.operations.process;

import com.dreamlex.DreamlexServer;
import java.util.Collection;

import com.dreamlex.dao.factory.DaoFactory;
import com.dreamlex.entity.ReportMessage;
import com.dreamlex.entity.User;
import com.dreamlex.exceptions.CardFileException;
import com.dreamlex.packet.Packet;
import java.util.Iterator;
import java.util.Map;
import org.apache.mina.core.session.IoSession;

/**
 * The class that interacts with used and asks for operations with books on the base.
 * Also class that contains methods for adding books from a text file such
 * as interaction with users, parsing the textfile and checking its lines for 
 * correctness (which is defined by a regular expression, loaded from the base) 
 * @author ag
 *
 */
public class ProcessPackets {

    public static void sendPacket(User user, Packet packet) {

        synchronized (DreamlexServer.protocolHandler.sessions) {
            Iterator<Map.Entry<IoSession, Long>> iter = DreamlexServer.protocolHandler.sessions.iterator();                                   
                        
            while (iter.hasNext()){
                Map.Entry<IoSession, Long> entry  = (Map.Entry<IoSession, Long>) iter.next();
                
                IoSession s = entry.getKey();
                
                if (s.isConnected()) {
                    User targetUser = (User) s.getAttribute("user");
                    if (user.getId().intValue() == targetUser.getId().intValue()) {
                        DreamlexServer.protocolHandler.sessionWrite(s, packet);
                    }
                }
            }
        }
    }
}
