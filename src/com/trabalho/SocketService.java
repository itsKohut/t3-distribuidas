package com.trabalho;

import com.trabalho.tasks.ClockDriftTask;
import com.trabalho.tasks.FetchTimeTask;
import com.trabalho.tasks.ReceiverTask;
import com.trabalho.tasks.UpdaterTimeTask;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.trabalho.MessageSender.sendMessage;
import static com.trabalho.tasks.ReceiverTask.PING_FROM_MASTER_MASTER;

public final class SocketService {

    public static final String MASTER = "00";

    public static ConcurrentMap<String, ClockHandler> clocks = new ConcurrentHashMap<>();

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
            new UpdaterTimeTask(10, this.socket, this.node);
        }

        new ClockDriftTask(10, this.node);

    }

    public void startConnection() {
        System.out.println("Starting connection with slaves nodes ...");

        this.node.connections.forEach((key, value) -> {
            final String message = String.format("%s %d", PING_FROM_MASTER_MASTER, this.node.port);
            sendMessage(message, value.port, this.socket);
        });
    }

    private void constructDatagramSocket(final Integer port) throws IOException {
        socket = new DatagramSocket(port);
    }
}
