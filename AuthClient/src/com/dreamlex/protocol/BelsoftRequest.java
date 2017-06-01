package com.dreamlex.protocol;

import com.dreamlex.clientauth.Packet;

public class BelsoftRequest
{
  private Packet packet;

  public BelsoftRequest(Packet packet)
  {
    this.packet = packet;
  }

  public Packet getPacket()
  {
    return packet;
  }

}
