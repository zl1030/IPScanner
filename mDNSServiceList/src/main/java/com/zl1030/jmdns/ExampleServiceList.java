package com.zl1030.jmdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

/**
 * @author by lei zhou on 2017/11/23 17:57.
 */
public class ExampleServiceList {
    public static void main(String[] args) {
        JmDNS jmdns = null;
        try {
            jmdns = JmDNS.create();
            while (true) {
                ServiceInfo[] infos = jmdns.list("_http._tcp.local.");
                System.out.println("List _http._tcp.local.");
                for (int i = 0; i < infos.length; i++) {
                    System.out.println(infos[i]);
                }
                System.out.println();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jmdns != null) try {
                jmdns.close();
            } catch (IOException exception) {
                //
            }
        }
    }
}
