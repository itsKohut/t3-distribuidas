package com.trabalho;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MessageSender {
    public static final String GROUP = "localhost";

    public synchronized static void sendMessage(final String message, final Integer port, final DatagramSocket socket) {
        try {
            byte[] buffer = message.getBytes();
            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Não foi possível enviar a mensagem");
        }
    }
}
