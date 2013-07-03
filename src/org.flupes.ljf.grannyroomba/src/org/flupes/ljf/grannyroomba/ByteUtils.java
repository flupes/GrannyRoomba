package org.flupes.ljf.grannyroomba;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtils {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected static byte[] word = new byte[2];
	
	static public boolean writeByte(int b, OutputStream output) {
		try {
			output.write( b );
		} catch (IOException e) {
			s_logger.error("write error" + e);
			return false;
		}
		return true;
	}

	static public boolean writeWord(int b, OutputStream output) {
		try {
			// Note: Java bytes are signed, 
			// so writing a signed or unsigned word 
			// is equivalent
			word[0] = (byte) (b >> 8);
			word[1] = (byte) (b & 0xFF);
			output.write(word, 0, 2);
		} catch (IOException e) {
			s_logger.error("write error" + e);
			return false;
		}
		return true;
	}
	
	static public int readByte(InputStream input) {
		int b = 0;
		try {
			b = input.read();
		} catch (IOException e) {
			s_logger.error("read error" + e);
		}
		return b;
	}

	static public int readUnsignedWord(InputStream input) {
		int u = 0;
		try {
			int high = input.read(); 
			int low = input.read(); 
			u = (high << 8) | low;
		} catch (IOException e) {
			s_logger.error("read error" + e);
		}
		return u;
	}

	static public int readSignedWord(InputStream input) {
		int s = 0;
		try {
			int high = input.read(); 
			int low = input.read(); 
			s = (high << 8) | low;
			if ( s > 0x7FFF) {
				s -= 0x10000;
			}
		} catch (IOException e) {
			s_logger.error("read error" + e);
		}
		return s;
	}
}
