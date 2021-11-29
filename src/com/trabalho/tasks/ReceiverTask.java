package com.trabalho.tasks;

import com.trabalho.ClockHandler;
import com.trabalho.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;

import static com.trabalho.BerkeleyAlgorithm.calculateRTT;
import static com.trabalho.BerkeleyAlgorithm.randomT4Time;
import static com.trabalho.MessageSender.sendMessage;
import static com.trabalho.Node.SPACE_REGEX;
import static com.trabalho.SocketService.clocks;

public class ReceiverTask extends Thread {

    public static final String PING_OK_MESSAGE = "ping_ok";
    public static final String PING_FROM_MASTER_MASTER = "ping_from_master";
    public static final String FETCH_TIME_MESSAGE = "fetch_time";
    public static final String RECEIVE_TIME = "receive_time";
    public static final String ADJUST_TIME = "adjust_time";

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

            // resposta do socket
            final String response = new String(packet.getData()).trim();

            //nodos slaves vão receber um ping do nodo master para dizer que irão participar da troca de mensagens
            if (response.contains(PING_FROM_MASTER_MASTER)) {
                System.out.println(response);
                final Integer port = Integer.valueOf(response.split(SPACE_REGEX)[1]);
                final String message = String.format("%s from node with id %s", PING_OK_MESSAGE, this.node.id);
                sendMessage(message, port, this.socket);
            }

            //nodo master vai receber um ok dos nodos slaves conectados
            if (response.contains(PING_OK_MESSAGE)) {
                System.out.println(response);
            }

            // nodo slave vai receber a mensagem e enviar o id e o time para o nodo master
            if (response.contains(FETCH_TIME_MESSAGE)) {
                System.out.println("Nodo master solicitou o tempo deste nodo");
                final Integer port = Integer.valueOf(response.split(SPACE_REGEX)[1]);
                final String message = String.format("%s %s %s", RECEIVE_TIME, this.node.id, this.node.time.toString());
                sendMessage(message, port, this.socket);
            }

            // SOMENTE NODO MASTER - recebe o tempo atual do nodo escravo
            if (response.contains(RECEIVE_TIME)) {

                final String nodeID = response.split(SPACE_REGEX)[1];
                final String nodeLocalTime = response.split(SPACE_REGEX)[2];

                System.out.println(String.format("Nodo master recebeu o tempo %s do nodo slave com id %s", nodeLocalTime, nodeID));

                ClockHandler clockHandler = clocks.get(nodeID);
                clockHandler.timeServer = LocalTime.parse(nodeLocalTime);

                randomT4Time(clockHandler);
                calculateRTT(clockHandler);

                System.out.println(String.format("NODE ID %s: %s", nodeID, clockHandler));
            }

            // SOMENTE NODOS ESCRAVOS - recebe a atualização de tempo do nodo master
            if (response.contains(ADJUST_TIME)) {
                final Long newTime = Long.valueOf(response.split(SPACE_REGEX)[1]);
                this.node.adjustTime(newTime);
            }
        }
    }
}
