package com.zl1030.ipscanner;

import com.zl1030.ipscanner.consts.IPScannerConst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

/**
 * @author by lei zhou on 2017/11/23 11:09.
 */
public class SendUDP {
    public static void main(String[] args) throws Exception {

        // 随机分配一个本机端口用来发送报文
        final DatagramSocket detectSocket = new DatagramSocket();
        detectSocket.setBroadcast(true);

        // 发包线程
        new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("Send thread started.");
                while (true) {
                    try {
                        byte[] buf = new byte[1024];
                        int packetPort = IPScannerConst.DISCOVERY_PORT;

                        // Broadcast address
                        BufferedReader stdin = new BufferedReader(
                                new InputStreamReader(System.in));
                        String outMessage = stdin.readLine();

                        if (outMessage.equals("bye"))
                            break;

                        buf = outMessage.getBytes();

                        //Try the 255.255.255.255 first
                        try {
                            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), packetPort);
                            detectSocket.send(sendPacket);
                            System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
                        } catch (Exception e) {
                        }

                        // Broadcast the message over all the network interfaces
                        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                        while (interfaces.hasMoreElements()) {
                            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                                continue; // Don't want to broadcast to the loopback interface
                            }

                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                InetAddress broadcast = interfaceAddress.getBroadcast();
                                if (broadcast == null) {
                                    continue;
                                }

                                // Send the broadcast package!
                                try {
                                    DatagramPacket out = new DatagramPacket(buf,
                                            buf.length, broadcast, packetPort);
                                    detectSocket.send(out);
                                } catch (Exception e) {
                                }

                                System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                            }
                        }


                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 收包线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Receive thread started.");
                while (true) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        detectSocket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String rcvd = "Received from " + packet.getSocketAddress() + ", Data="
                            + new String(packet.getData(), 0, packet.getLength());
                    System.out.println(rcvd);
                }
            }
        }).start();
    }
}
