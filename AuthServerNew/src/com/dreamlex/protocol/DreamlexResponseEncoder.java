package com.dreamlex.protocol;

import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.buffer.IoBuffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.dreamlex.packet.*;

public class DreamlexResponseEncoder extends ProtocolEncoderAdapter
{
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
    {
        int capacity = 48;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.setAutoExpand(true);
        if (message instanceof String) {
            Packet.sendPolicy(buffer);
            buffer.flip();
        } else {
            ((Packet) message).savePacket((Packet) message, buffer);
            buffer.flip();
        }
//        byte[] bytearr = new byte[buffer.remaining()];
//        bytearr = buffer.array();
//        for (int i=0; i<buffer.remaining();i++) {
//            System.out.print(Integer.toHexString(bytearr[i] & 0xff)+" ");
//        }
//            System.out.println(buffer.toString());
            out.write(buffer);
            out.flush();
    }

//    private byte[] getBytes(BufferedImage image) throws IOException
//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, "PNG", baos);
//        return baos.toByteArray();
//    }
}
