package com.trabalho.tasks;

import com.trabalho.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import static com.trabalho.SocketService.GROUP;

public class FetchTimeTask {

    public static final String PING_OK_MESSAGE = "ping_ok";
    public static final String PING_FROM_MASTER_MASTER = "ping_from_master";
    public static final String FETCH_TIME_MESSAGE = "fetch_time";
    public static final String RECEIVE_TIME = "receive_time";

    Timer timer;

    public FetchTimeTask(final Integer seconds, final DatagramSocket socket, final Node node) {
        timer = new Timer();
        timer.schedule(new FetchTimeExecuter(socket, node), 4000, seconds * 1000);
    }

    class FetchTimeExecuter extends TimerTask {

        DatagramSocket socket;
        Node node;

        public FetchTimeExecuter(final DatagramSocket socket, final Node node) {
            this.socket = socket;
            this.node = node;
        }

        //envio unicast para todos os nodos slaves solicitando a hora deles
        public void run() {

            Node.connections.forEach((key, value) -> {
                try {
                    sendMessage(FETCH_TIME_MESSAGE, value.port);
                } catch (Exception e) {
                    System.out.println("Não foi possível enviar a mensagem para algum dos nodos");
                    System.exit(1);
                }
            });
        }

        //envio de mensagem para os nodos filhos requisitando a hora deles
        public void sendMessage(final String message, final Integer port) throws IOException {
            final String finalMessage = String.format("%s %d", message, this.node.port);
            byte[] buffer = finalMessage.getBytes();
            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
            this.socket.send(packet);
        }
    }
}