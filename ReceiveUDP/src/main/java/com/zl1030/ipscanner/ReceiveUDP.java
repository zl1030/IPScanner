package com.zl1030.ipscanner;

import com.zl1030.ipscanner.consts.IPScannerConst;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author by lei zhou on 2017/11/23 11:10.
 */
public class ReceiveUDP {
    public static void main(String[] args) throws Exception {
        int listenPort = IPScannerConst.DISCOVERY_PORT;

        DatagramSocket responseSocket = new DatagramSocket(listenPort, InetAddress.getByName("0.0.0.0"));
        responseSocket.setBroadcast(true);
        System.out.println("Server started, Listen port: " + listenPort);
        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            responseSocket.receive(packet);
            String receiveData = new String(packet.getData(), 0, packet.getLength());
            String rcvd = "Received "
                    + receiveData
                    + " from address: " + packet.getSocketAddress();
            System.out.println(rcvd);

            // Send a response packet to sender
            String backData = receiveData;
            byte[] data = backData.getBytes();
            System.out.println("Send " + backData + " to " + packet.getSocketAddress());
            DatagramPacket backPacket = new DatagramPacket(data, 0,
                    data.length, packet.getAddress(), packet.getPort());
            responseSocket.send(backPacket);
        }
    }
}
