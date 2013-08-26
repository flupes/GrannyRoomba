package org.flupes.ljf.grannyroomba.messages.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.flupes.ljf.grannyroomba.messages.LocomotionProto.LocomotionCmd.Command;
import org.flupes.ljf.grannyroomba.messages.LocomotionProto.*;
import org.flupes.ljf.grannyroomba.messages.StopProto.*;

import org.junit.Test;

public class TestLocomotionCmd {

	@Test
	public void test() {
		LocomotionCmd.Builder builder = LocomotionCmd.newBuilder();
		builder.setCmd(Command.STOP);
		builder.setStop(
//				StopMsg.newBuilder().setMode(Mode.EMMERGENCY).build()
				StopMsg.newBuilder().build()
				);
		LocomotionCmd cmd2send = builder.build();

		assertTrue ("Command not initialized!", cmd2send.isInitialized() );

		System.out.println("command to send: " + cmd2send );

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write( cmd2send.toByteArray() );
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
		LocomotionCmd msg = null;
		try {
			msg = LocomotionCmd.parseFrom( in );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertNotNull ("Could not read message!", msg );
		assertTrue("Message does not contain stop!", msg.hasStop() );
	
		System.out.println("message read" + msg);
		System.out.println("mode = " + msg.getStop().getMode() );
	}

}
