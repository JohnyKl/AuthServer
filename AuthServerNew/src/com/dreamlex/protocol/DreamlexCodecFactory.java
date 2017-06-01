package com.dreamlex.protocol;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.core.session.IoSession;

public class DreamlexCodecFactory implements ProtocolCodecFactory {
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public DreamlexCodecFactory(boolean client) {
        encoder = new DreamlexResponseEncoder();
        decoder = new DreamlexRequestDecoder();
    }

    public ProtocolEncoder getEncoder(IoSession is) throws Exception {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession is) throws Exception {
        return decoder;
    }
}

