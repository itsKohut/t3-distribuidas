package com.trabalho;

import com.trabalho.tasks.FetchTimeTask;
import com.trabalho.tasks.ReceiverTask;
import com.trabalho.tasks.UpdaterTimeTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.trabalho.tasks.FetchTimeTask.PING_FROM_MASTER_MASTER;

/**
 * A ideia é esperar todos se conectarem
 * e somente então inicializar o algoritmo de berkeley de fato mandando um multicast do master para os slaves
 * slaves enviarem o tempo deles (tem que ver que tempo dado deve ser enviado)
 * master mandar o horario atualiza para cada um em unicast (tem que ver qual o calculo a ser feito)
 * deve ser em loop a cada x tempo? ou apenas uma execução? deve ter eleição, e se um nodo não responder no inicio?
 */
public final class SocketService {

    public static final String GROUP = "localhost";
    public Node node;
    //public MulticastSocket socket;
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

    public void startConnection() {
        System.out.println("Starting connection with all nodes");
        node.connections.forEach((key, value) -> {
            try {
                sendMessage(PING_FROM_MASTER_MASTER, value.port);
            } catch (IOException e) {
                System.out.println("Não foi possível enviar a mensagem para algum dos nodos");
                System.exit(1);
            }
        });
        System.out.println("All nodes connected");
    }

    public void sendMessage(final String message, final Integer port) throws IOException {
        byte[] buffer = message.getBytes();
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
        this.socket.send(packet);
    }

    private void constructDatagramSocket(final Integer port) throws IOException {
        socket = new DatagramSocket(port);
    }

    public void run() {
        startConnection();
        new ReceiverTask(this.socket, this.node).start();

        new FetchTimeTask(4, this.socket, this.node);;
        new UpdaterTimeTask(4, this.socket);
    }
}
