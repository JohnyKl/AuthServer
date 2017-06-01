package com.dreamlex.protocol;

import com.dreamlex.packet.*;

public class DreamlexRequest
{
  private Packet packet;

  public DreamlexRequest(Packet packet)
  {
    this.packet = packet;
  }

  public Packet getPacket()
  {
    return packet;
  }

}
