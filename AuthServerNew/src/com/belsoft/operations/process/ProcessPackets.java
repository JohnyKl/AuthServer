package com.belsoft.operations.process;

import com.belsoft.BelsoftServer;
import java.util.Collection;

import com.belsoft.dao.factory.DaoFactory;
import com.belsoft.entity.ReportMessage;
import com.belsoft.entity.User;
import com.belsoft.exceptions.CardFileException;
import com.belsoft.packet.Packet;
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

        synchronized (BelsoftServer.protocolHandler.sessions) {
            Iterator<Map.Entry<IoSession, Long>> iter = BelsoftServer.protocolHandler.sessions.iterator();                                   
                        
            while (iter.hasNext()){
                Map.Entry<IoSession, Long> entry  = (Map.Entry<IoSession, Long>) iter.next();
                
                IoSession s = entry.getKey();
                
                if (s.isConnected()) {
                    User targetUser = (User) s.getAttribute("user");
                    if (user.getId().intValue() == targetUser.getId().intValue()) {
                        BelsoftServer.protocolHandler.sessionWrite(s, packet);
                    }
                }
            }
        }
    }
}
