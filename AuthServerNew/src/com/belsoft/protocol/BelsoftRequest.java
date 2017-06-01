package com.belsoft.protocol;

import com.belsoft.packet.*;

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
