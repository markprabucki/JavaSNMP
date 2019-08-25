package com.snmp;
import java.io.IOException;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpUtil extends Object implements Session, CommandResponder
{

   public static void main(String[] args) throws Exception
   {
	      snmpUtil();
   }

   public static void snmpUtil() throws Exception {
      try {
	      Address targetAddress = GenericAddress.parse("udp:10.211.55.6/162");
	      TransportMapping transport = new DefaultUdpTransportMapping();
	      Snmp snmp = new Snmp(transport);
	      USM usm = new USM(SecurityProtocols.getInstance(),
	                     new OctetString(MPv3.createLocalEngineID()), 0);
	      SecurityModels.getInstance().addSecurityModel(usm);
	      transport.listen();
	      CommunityTarget target = new CommunityTarget();
	      target.setCommunity(new OctetString("public"));
	      target.setAddress(targetAddress);
	      target.setRetries(2);
	      target.setTimeout(1500);
	      target.setVersion(SnmpConstants.version1);
	      PDU pdu = new PDU();
	      pdu.add(new VariableBinding(new OID(new int[] {1,3,6,1,2,1,1,1})));
	      pdu.add(new VariableBinding(new OID(new int[] {1,3,6,1,2,1,1,2})));
	      pdu.setType(PDU.GETNEXT);
	      pdu.add(new VariableBinding(new OID(new OID(new int[] {1,3,6,1,2,1,1,1})), new OctetString("Test Message from kohlSnmpSend")));

	      ResponseListener listener = new ResponseListener() {
	      public void onResponse(ResponseEvent event) {
	          ((Snmp)event.getSource()).cancel(event.getRequest(), this);
	          System.out.println("Received response PDU is: "+event.getResponse());
	      }
	   };
	   snmp.send(pdu, target, null, listener);
	}
	catch (Exception e)
	{
		e.printStackTrace();
    }

    }
	public void processPdu(CommandResponderEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void cancel(PDU arg0, ResponseListener arg1) {
		// TODO Auto-generated method stub

	}
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
	public ResponseEvent send(PDU arg0, Target arg1) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	public ResponseEvent send(PDU arg0, Target arg1, TransportMapping arg2)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	public void send(PDU arg0, Target arg1, Object arg2, ResponseListener arg3)
			throws IOException {
		// TODO Auto-generated method stub

	}
	public void send(PDU arg0, Target arg1, TransportMapping arg2, Object arg3,
			ResponseListener arg4) throws IOException {
		// TODO Auto-generated method stub

	}

}
