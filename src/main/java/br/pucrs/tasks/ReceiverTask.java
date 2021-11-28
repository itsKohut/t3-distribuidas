package br.pucrs.tasks;

import br.pucrs.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static br.pucrs.Node.SPACE_REGEX;
import static br.pucrs.SocketService.GROUP;
import static br.pucrs.tasks.FetchTimeTask.*;

public class ReceiverTask extends Thread {

    DatagramSocket socket;
    Node node;

    public ReceiverTask(final DatagramSocket socket, final Node node) {
        this.socket = socket;
        this.node = node;
    }

    @Override
    public void run() {

        DatagramPacket packet = new DatagramPacket(new byte[1024], 0, new byte[1024].length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String response = new String(packet.getData()).trim();
        System.out.println(response);

        // nodos slaves vão receber um ping aos nodos que irão participar da troca de mensagens
        if (response.contains(PING_FROM_MASTER_MASTER)) {
            final Integer port = Integer.valueOf(response.split(SPACE_REGEX)[1]);
            final String message = String.format("%s %d", PING_OK_MESSAGE, this.node.id);
            sendMessage(message, port);
        }

        // nodo master vai receber um ok dos nodos slaves conectados
        if (response.contains(PING_OK_MESSAGE)) {
            System.out.println(response);
        }

        // nodo slave vai receber a mensagem e enviar o id e o time para o nodo master
        if (response.contains(FETCH_TIME_MESSAGE)) {
            final Integer port = Integer.valueOf(response.split(SPACE_REGEX)[1]);
            final String message = String.format("%s %d %s", SEND_TIME_MESSAGE, this.node.id, this.node.time.toString());
            sendMessage(message, port);
        }
    }

    public void sendMessage(final String message, final Integer port) {
        byte[] buffer = message.getBytes();
        final DatagramPacket packet;

        try {
            packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
            this.socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
