package com.dreamlex.protocol;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.core.session.IoSession;

public class BelsoftCodecFactory implements ProtocolCodecFactory {
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public BelsoftCodecFactory(boolean client) {
        encoder = new BelsoftResponseEncoder();
        decoder = new BelsoftRequestDecoder();
    }

    public ProtocolEncoder getEncoder(IoSession is) throws Exception {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession is) throws Exception {
        return decoder;
    }
}

