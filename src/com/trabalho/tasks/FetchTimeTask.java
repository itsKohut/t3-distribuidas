package com.trabalho.tasks;

import com.trabalho.ClockHandler;
import com.trabalho.Node;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import static com.trabalho.MessageSender.sendMessage;
import static com.trabalho.SocketService.clocks;
import static com.trabalho.tasks.ReceiverTask.FETCH_TIME_MESSAGE;

public class FetchTimeTask {

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

            this.node.connections.forEach((key, value) -> {

                // estrutura inicial de controle do ciclo de uma atualização de tempo
                final ClockHandler clockHandler = new ClockHandler(value.delay, this.node.time, this.node.time);
                clocks.put(key, clockHandler);

                final String message = String.format("%s %d %s", FETCH_TIME_MESSAGE, this.node.port, this.node.time);
                sendMessage(message, value.port, this.socket);
            });
        }
    }
}