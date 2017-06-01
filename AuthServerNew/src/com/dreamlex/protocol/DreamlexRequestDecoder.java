package com.dreamlex.protocol;

import java.io.IOException;

import com.dreamlex.DebugLog;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.buffer.IoBuffer;

import com.dreamlex.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.HexDumpEncoder;

public class DreamlexRequestDecoder extends CumulativeProtocolDecoder
{
    //private static final Logger log = LoggerFactory.getLogger(DreamlexRequestDecoder.class);

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
    {
        if (in.remaining() >= 4)
        {
        byte[] bytearr = new byte[in.remaining()];
        bytearr = in.array();

        String arrStr = "";
        for (int i=0; i<in.remaining();i++) {
            arrStr += bytearr[i]+" ";
        }
//            HexDumpEncoder hex = new HexDumpEncoder();
            DebugLog.debug(arrStr);
            DebugLog.debug(in.toString());

        int size = 0x3C706F6C;//in.getInt();
            if (size == in.remaining()) {
                DebugLog.debug("Policy request");
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

                    @Override
                    public String toString() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
//                packet = Packet.getPacketClass(0x0000);
                packet.setSize(0x3C706F6C);
                DreamlexRequest request = new DreamlexRequest(packet);
                while(in.remaining()>0) {
                    in.get();
                }
                out.write(request);
                return true;
            } else {
                if (in.remaining() >= 0) {
                    try{
                        Packet packet = Packet.loadPacket(in);
                        //packet.setSize(size);

                        DreamlexRequest request = new DreamlexRequest(packet);
                        out.write(request);

                        return true;
                    }
                    catch (Exception e) {
                        DebugLog.error("line 78: " + e.getMessage());

                        session.write(new DialogMessage(0, e.getMessage()));

                        session.close(true);

                        return false;
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
