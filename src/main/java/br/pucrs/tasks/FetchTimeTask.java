package br.pucrs.tasks;

import br.pucrs.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import static br.pucrs.SocketService.GROUP;

/**
 * Tasker responsavel por ficar escutando as mensagens que chegam e reagir a elas
 */
public class FetchTimeTask {

    public static final String PING_OK_MESSAGE = "ping_ok";
    public static final String PING_FROM_MASTER_MASTER = "ping_from_master";
    public static final String FETCH_TIME_MESSAGE = "fetch_time";
    public static final String SEND_TIME_MESSAGE = "send_time";


    Timer timer;

    public FetchTimeTask(final Integer seconds, final DatagramSocket socket, final Node node) {
        timer = new Timer();
        timer.schedule(new FetchTimeExecuter(socket, node), 1000, seconds * 1000);
    }

    class FetchTimeExecuter extends TimerTask {

        DatagramSocket socket;
        Node node;

        public FetchTimeExecuter(final DatagramSocket socket, final Node node) {
            this.socket = socket;
            this.node = node;
        }

        // envio de mensagem
        public void run() {
            System.out.println("fetch time");
//            node.connections.forEach((key, value) -> {
//                try {
//                    sendMessage(FETCH_TIME_MESSAGE, value.port);
//                } catch (Exception e) {
//                    System.out.println("Não foi possível enviar a mensagem para algum dos nodos");
//                    System.exit(1);
//                }
//            });
//            System.out.println("All nodes connected");
        }

        public void sendMessage(final String message, final Integer port) throws IOException {
            byte[] buffer = message.getBytes();
            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(GROUP), port);
            this.socket.send(packet);
        }
    }
}
