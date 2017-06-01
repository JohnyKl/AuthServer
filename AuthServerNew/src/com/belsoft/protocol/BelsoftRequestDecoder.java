package com.belsoft.protocol;

import java.io.IOException;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.buffer.IoBuffer;

import com.belsoft.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.HexDumpEncoder;

public class BelsoftRequestDecoder extends CumulativeProtocolDecoder
{
    private static final Logger log = LoggerFactory.getLogger(BelsoftRequestDecoder.class);

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
    {
        if (in.remaining() >= 4)
        {
        byte[] bytearr = new byte[in.remaining()];
        bytearr = in.array();

        String byteArrStr = "";

        for (int i=0; i<in.remaining();i++) {
            byteArrStr += bytearr[i]+" ";
        }
//            HexDumpEncoder hex = new HexDumpEncoder();
            log.debug(byteArrStr);
            log.debug(in.toString());

        int size = in.getInt();
            if (size == 0x3C706F6C) {
                log.debug("Policy request");
                Packet packet = new Packet() {

                    @Override
                    public void readPacket(IoBuffer in) throws IOException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void writePacket(IoBuffer out) throws IOException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public int size() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
//                packet = Packet.getPacketClass(0x0000);
                packet.setSize(0x3C706F6C);
                BelsoftRequest request = new BelsoftRequest(packet);
                while(in.remaining()>0) {
                    in.get();
                }
                out.write(request);
                return true;
            } else {
                if (in.remaining() >= size) {
                    Packet packet = Packet.loadPacket(in);
                    
                    if(packet == null) {
                        session.write(new DialogMessage(0,"Bad incoming packet id"));
                        
                        session.close(true);
                        
                        return false;
                    } else {
                        packet.setSize(size); 
                    
                        BelsoftRequest request = new BelsoftRequest(packet);
                        out.write(request);
                        
                        return true;   
                    }                                 
                } else {
                    in.rewind();
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
