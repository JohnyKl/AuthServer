package com.dreamlex.packet;

public class NetHandler {

    public NetHandler() {}

    public void protocolError(Packet packet) {}

    public void lostConnection(String s, Object[] aobject) {}

    public void ping(Ping ping) {
        this.protocolError((Packet) ping);
    }

    public void login(Login login) {
        this.protocolError((Packet) login);
    }
}
