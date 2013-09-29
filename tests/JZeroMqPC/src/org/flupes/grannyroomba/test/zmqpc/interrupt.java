package org.flupes.grannyroomba.test.zmqpc;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

// find process id with:
// ps -eaf |grep interrupt
// and send Ctrl-C (kill) with:
// kill -s INT <pid>

public class interrupt {
	public static void main (String[] args) {
		//  Prepare our context and socket
		final ZMQ.Context context = ZMQ.context(1);

		final Thread zmqThread = new Thread() {
			@Override
			public void run() {
				ZMQ.Socket socket = context.socket(ZMQ.REP);
				socket.bind("tcp://*:5555");

				System.out.println("thread is running");
				while (!Thread.currentThread().isInterrupted()) {
					try {
						socket.recv (0);
					} catch (ZMQException e) {
						if (e.getErrorCode () == ZMQ.Error.ETERM.getCode ()) {
							System.out.println("thread got interrupted");
							break;
						}
					}
				}
				System.out.println("closing socket");
				socket.close();
				System.out.println("thread terminated");
			}
		};

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("W: interrupt received, killing server...");
				context.term();
				System.out.println("term returned.");
				try {
					System.out.println("send interrupt to thread");
					zmqThread.interrupt();
					System.out.println("waiting for thread to join");
					zmqThread.join();
				} catch (InterruptedException e) {
				}
				System.out.println("thread joined");
			}
		});

		zmqThread.start();
		System.out.println("done");
	}
}
