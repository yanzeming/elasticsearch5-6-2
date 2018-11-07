package com.elasticsearch.elasticsearch562;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String [] args) throws UnknownHostException {
        /*InetAddress inetAddress = InetAddress.getByAddress("127.0.0.1".getBytes());
        System.out.println(inetAddress.getHostAddress().toString());*/
        String str = "127.0.0.1";       String[] ipStr = str.split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }

        InetAddress ia = InetAddress.getByAddress(ipBuf);
        System.out.println(ia.getHostAddress() + "    " + ia.getHostName());
    }
}
