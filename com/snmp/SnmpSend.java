package com.snmp;
import java.net.InetAddress;
import java.util.Date;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
public class kohlsSnmpSend
{
  public SnmpSend() {}
  public static void main(String[] args) throws Exception
  {
	  System.out.println("------- Sending SNMP Alert ---------");
	  if (args.length < 5)
	  {
		  System.out.println(" Invalid number of args " + args.length);
		  System.out.println("Arg 0: Send to IP");
		  System.out.println("Arg 1: Send to Port");
		  System.out.println("Arg 2: Priority ");
		  System.out.println("Arg 3: OID");
		  System.out.println("Arg 4: Community");
		  System.out.println("Arg 5: Message");
	  }
	  System.out.println("listenerIP (receiver is):" + args[0]);
	  System.out.println("listenerPort is:" + args[1]);
	  System.out.println("Priority is:" + args[2]);
	  System.out.println("oid to use on snmp:" + args[3]);
	  System.out.println("community to use on snmp:" + args[4]);
	  System.out.println("message to send:" + args[5]);

	  try
	  {
	     SnmpSend snmp4JTrap = new SnmpSend();
         ResponseEvent result = snmp4JTrap.sendSnmpV2Trap(args[0],args[1],args[2],args[3],args[4],args[5]);
         System.out.println("Result of send:" + result.toString());
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  finally
	  {
		  System.out.println("------- SNMP Alert Done ---------");
	  }
  }
  public ResponseEvent sendSnmpV2Trap(String ip
		  					,String sendToPort
					        ,String msgPriority
					        ,String snmpOID
					        ,String snmpCommunity
					        ,String msg) throws Exception {
	ResponseEvent responseEvent = null;
    try
    {
  	  InetAddress addr = InetAddress.getLocalHost();
	  System.out.println("Using Ip address of: " + addr.getHostAddress());

	  TransportMapping transport = new DefaultUdpTransportMapping();
      transport.listen();

      CommunityTarget comtarget = new CommunityTarget();
      comtarget.setCommunity(new OctetString(snmpCommunity));
      comtarget.setVersion(SnmpConstants.version2c);
      comtarget.setAddress(new UdpAddress(ip + "/" + sendToPort));
      comtarget.setRetries(2);
      comtarget.setTimeout(5000);

      PDU pdu = new PDU();
      pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
      pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(snmpOID)));
      pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(addr.getHostAddress())));
      pdu.add(new VariableBinding(new OID(snmpOID), new OctetString(msgPriority)));
      pdu.add(new VariableBinding(new OID(snmpOID), new OctetString(msg)));
      pdu.setType(PDU.NOTIFICATION);
	  System.out.println("---------------------------------");

      System.out.println("Sending V2 Trap to " + ip + " on Port " + sendToPort);

      Snmp snmp = new Snmp(transport);
//      snmp.send(pdu, comtarget);

      responseEvent = snmp.send(pdu, comtarget);
      // extract the response PDU (could be null if timed out)
//      PDU responsePDU = responseEvent.getResponse();
//
//    responsePDU.toString());
//      Vector vbs = responsePDU.getVariableBindings();
//      if (vbs.size()>0)
//      {
//          VariableBinding vb = (VariableBinding) vbs.get(0);
//          ret = vb.getVariable().toString();
//          System.out.println("snmp.send result:" + ret + "" + responsePDU.toString());
//      }

      snmp.close();
	  System.out.println("---------------------------------");

      if (responseEvent == null)
      {
	      throw new Exception("Response is null possible timeout or no response sent from resceiver.");
      }
      else
      {
          PDU responsePDU = responseEvent.getResponse();
          Address peerAddress = responseEvent.getPeerAddress();
     //     System.out.println("snmp.send result:" + responsePDU.toString());
          System.out.println("peer Address:" + peerAddress.toString());
      }
    }
    catch (Exception e)
    {
      System.err.println("Error in Sending V2 Trap to " + ip + " on Port " + sendToPort);
      System.err.println("Exception Message = " + e.getMessage());
    }
    return responseEvent;
  }
}