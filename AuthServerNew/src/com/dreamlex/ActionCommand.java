/**
 * Copyright (C) 2011 - Alexey Kolenko
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package com.dreamlex;


public class ActionCommand
{

//        public static final int PING = 0x0000;
//        public static final int LOGIN = 0x0001;
//        public static final int HANDSHAKE = 0x0002;
//        public static final int CHATMESSAGE = 0x0003;
//        public static final int PONG = 0xFFFF;
        public static final int PREPAREAUTOLOGIN = 0x10000000;
        public static final int REGISTRATION = 0x10000002;
        public static final int DONATION = 0x10000004;

        public static final int LOGIN = 0x00010000;
        public static final int TOWNLIST = 0x00010004;
        public static final int CITYMAP = 0x00010006;
        public static final int RESOURCE = 0x00010008;
        public static final int WORLDMAP = 0x0001000A;
        public static final int ACTIONLIST = 0x0001000C;
        public static final int POPULATION = 0x0001000E;
        public static final int ITEMLIST = 0x00010010;
        public static final int FINANCESTATUS = 0x00010012;
        public static final int LOGOUT = 0x00010014;
        
        public static final int ACTIONATTACK = 0x00050000;
        public static final int ACTIONDEFENSE = 0x00050002;
        public static final int ACTIONTRADE = 0x00050004;
        public static final int ACTIONHIRE = 0x00050006;
        public static final int ACTIONCANCEL = 0x00054000;

        public static final int CHATCHANNELS = 0x00020000;
        public static final int CHATMESSAGE = 0x00020002;
        public static final int CHATLASTMESSAGES = 0x00020004;

        public static final int NEWMAILMESSAGE = 0x00020008;
        public static final int MAILMESSAGELIST = 0x0002000A;
        public static final int MAILMESSAGE = 0x0002000C;
        public static final int DELETEMAILMESSAGE = 0x0002000E;
        public static final int MAILSTATUS = 0x00020010;

        public static final int REPORTMESSAGELIST = 0x00020020;
        public static final int REPORTMESSAGE = 0x00020022;
        public static final int DELETEREPORTMESSAGE = 0x00020024;
        public static final int REPORTSTATUS = 0x00020026;

        public static final int CLANMESSAGESLIST = 0x00020030;
        public static final int CLANSETMESSAGESTATUS = 0x00020032;
        
        public static final int BUILDINGSLIST = 0x00040000;
        public static final int NEWBUILDING = 0x00040002;
        public static final int UPGRADEBUILDING = 0x00040004;
        public static final int DOWNGRADEBUILDING = 0x00040006;
        public static final int MOVEBUILDING = 0x00040008;
        public static final int REMOVEBUILDING = 0x0004000A;
        public static final int BUILDING = 0x0004000C;

        public static final int SQUADRONLIST = 0x00058000;
        
        public static final int OFFICERLIST = 0x00060000;
        public static final int OFFICERHIRE = 0x00060002;
        public static final int OFFICERHIRELIST = 0x00060004;
        public static final int OFFICERFIRE = 0x00060006;

        public static final int OFFICERSQUAD = 0x00060016;
        
        public static final int RESEARCHLIST = 0x00070000;
        public static final int STARTRESEARCH = 0x00070002;

        public static final int PRODUCTIONLIST = 0x00071000;
        public static final int STARTPRODUCTION = 0x00071002;

        public static final int QUESTLIST = 0x00080000;
        public static final int QUESTSTATUSSEND = 0x00080002;

        public static final int AUCTIONLIST = 0x00073000;
        public static final int AUCTIONSELFLIST = 0x00073002;
        public static final int AUCTIONDISTRIBUTE = 0x00073004;
        public static final int AUCTIONWITHDRAW = 0x00073006;
        public static final int AUCTIONBUY = 0x00073008;
        public static final int AUCTIONBID = 0x0007300A;
        public static final int AUCTIONBIDLIST = 0x0007300C;

        public static final int SHOPLIST = 0x00074000;
        public static final int SHOPBUY = 0x00074002;
        
        public static final int ITEMTRANSFER = 0x00075000;

        public static final int CLANSTATUS = 0x00090000;
        public static final int CLANCREATE = 0x00090002;
        public static final int CLANMEMBERSHIPREQUEST = 0x00090004;
        public static final int CLANLEAVE = 0x00090006;
        public static final int CLANLIST = 0x00090008;
        public static final int CLANADDMEMBER = 0x0009000A;
        public static final int CLANREMOVEMEMBER = 0x0009000C;

        public static final int INFOMESSAGE = 0xFFFFFFFA;
        public static final int DIALOGMESSAGE = 0xFFFFFFFC;
        public static final int PING = 0xFFFFFFFE;

}