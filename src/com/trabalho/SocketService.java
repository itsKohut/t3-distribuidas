package com.trabalho;

import com.trabalho.tasks.ClockDriftTask;
import com.trabalho.tasks.FetchTimeTask;
import com.trabalho.tasks.ReceiverTask;
import com.trabalho.tasks.UpdaterTimeTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.trabalho.tasks.FetchTimeTask.PING_FROM_MASTER_MASTER;


public final class SocketService {

    public static final String GROUP = "localhost";
    public static final String MASTER = "00";

    public static ConcurrentMap<String, ClockHandler> clock = new ConcurrentHashMap<>();

    public Node node;
    public DatagramSocket socket;

    public SocketService(final Node node) {

        this.node = node;

        try {
            constructDatagramSocket(node.port);
        } catch (IOException e) {
            System.out.println("Ocorreu ao instanciar o socket do nodo.");
            System.exit(1);
        }
    }

    public void run() {

        new ReceiverTask(this.socket, this.node).start();

        if (this.node.id.equals(MASTER)) {
            startConnection();
            new FetchTimeTask(10, this.socket, this.node);
            new UpdaterTimeTask(10, this.socket);
        }

        new ClockDriftTask(10, this.node);

    }

    public void startConnection() {
        System.out.println("Starting connection with slaves nodes ...");

        Node.connections.forEach((key, value) -> {
            try {
                sendMessage(PING_FROM_MASTER_MASTER, value.port);
            } catch (IOException e) {
                System.out.println("Não foi possível enviar a mensagem para algum dos nodos");
                System.exit(1);
            }
        });


    }

    public void sendMessage(final String message, final Integer port) throws IOException {
        final String finalMessage = String.format("%s %d", message, this.node.port);
        byte[] buffer = finalMessage.getBytes();
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
        this.socket.send(packet);
    }

    private void constructDatagramSocket(final Integer port) throws IOException {
        socket = new DatagramSocket(port);
    }
}
