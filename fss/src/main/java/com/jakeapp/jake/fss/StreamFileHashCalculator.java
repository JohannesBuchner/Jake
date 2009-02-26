package com.jakeapp.jake.fss;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StreamFileHashCalculator{
	private MessageDigest md;
	
	public StreamFileHashCalculator() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("SHA-512");
	}
	
	public String calculateHash(File f) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		return calculateHash(fis);
	}
	
	public String calculateHash(InputStream is) {
		DigestInputStream dis = new DigestInputStream(is, md);
		byte[] tmp = new byte[1024];
		try {
			while(dis.read(tmp) >= 0);
			dis.close();
		} catch (IOException e) {
			return null;
		}
		byte[] b = md.digest();
		
		String s = "";
		for(int i=0;i<b.length;i++){
			int c = b[i];
			if ( b[i] < 0 )
				c = c + 256;
			s = s.concat( halfbyte2str(c/16) + halfbyte2str(c%16));
		}
		return s;
	}

	private String halfbyte2str(int i) {
		switch(i){
			case  0: return "0";
			case  1: return "1";
			case  2: return "2";
			case  3: return "3";
			case  4: return "4";
			case  5: return "5";
			case  6: return "6";
			case  7: return "7";
			case  8: return "8";
			case  9: return "9";
			case 10: return "a";
			case 11: return "b";
			case 12: return "c";
			case 13: return "d";
			case 14: return "e";
			case 15: return "f";
			default: throw new NullPointerException();
		}
	}
	
	public int getHashLength() {
		return md.getDigestLength()*2;
	}
}
