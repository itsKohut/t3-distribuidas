package com.trabalho.tasks;

import com.trabalho.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.trabalho.Node.SPACE_REGEX;
import static com.trabalho.SocketService.GROUP;
import static com.trabalho.tasks.FetchTimeTask.*;

public class ReceiverTask extends Thread {

    DatagramSocket socket;
    Node node;

    public ReceiverTask(final DatagramSocket socket, final Node node) {
        this.socket = socket;
        this.node = node;
    }

    @Override
    public void run() {

        while (true) {

            DatagramPacket packet = new DatagramPacket(new byte[1024], 0, new byte[1024].length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final String response = new String(packet.getData()).trim();
            //nodos slaves vão receber um ping do nodo master para dizer que irão participar da troca de mensagens
            if (response.contains(PING_FROM_MASTER_MASTER)) {
                System.out.println(response);
                final Integer port = Integer.valueOf(response.split(SPACE_REGEX)[1]);
                final String message = String.format("%s from node with id %s", PING_OK_MESSAGE, this.node.id);
                sendMessage(message, port);
            }

            //nodo master vai receber um ok dos nodos slaves conectados
            if (response.contains(PING_OK_MESSAGE)) {
                System.out.println(response);
            }

            // nodo slave vai receber a mensagem e enviar o id e o time para o nodo master
            if (response.contains(FETCH_TIME_MESSAGE)) {
                System.out.println(response);
                final Integer port = Integer.valueOf(response.split(SPACE_REGEX)[1]);
                final String message = String.format("%s %s %s", RECEIVE_TIME, this.node.id, this.node.time.toString());
                sendMessage(message, port);
            }

            if (response.contains(RECEIVE_TIME)) {
                System.out.println(response);
            }
        }
    }

    public void sendMessage(final String message, final Integer port) {
        byte[] buffer = message.getBytes();
        final DatagramPacket packet;
        try {
            packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
            this.socket.send(packet);
        } catch (Exception e) {
            System.out.println("Não foi possível enviar a mensagem para algum dos nodos");
            System.exit(1);
        }
    }
}
