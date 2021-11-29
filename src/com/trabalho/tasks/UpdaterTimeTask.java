package com.trabalho.tasks;

import com.trabalho.Node;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import static com.trabalho.BerkeleyAlgorithm.calculateAverageTime;
import static com.trabalho.MessageSender.sendMessage;
import static com.trabalho.SocketService.clocks;
import static com.trabalho.tasks.ReceiverTask.ADJUST_TIME;

public class UpdaterTimeTask {

    public static final String SLAVE_ONE = "01";
    public static final String SLAVE_TWO = "02";
    public static final String SLAVE_THREE = "03";

    Timer timer;

    public UpdaterTimeTask(final Integer seconds, final DatagramSocket socket, final Node node) {
        timer = new Timer();
        timer.schedule(new UpdaterTimeExecuter(socket, node), 8000, seconds * 1000);
    }

    class UpdaterTimeExecuter extends TimerTask {
        DatagramSocket socket;
        Node node;

        public UpdaterTimeExecuter(final DatagramSocket socket, final Node node) {
            this.socket = socket;
            this.node = node;
        }

        public void run() {

            final Long averarageTimeInNanos = calculateAverageTime(this.node.getTime(), clocks.get(SLAVE_ONE), clocks.get(SLAVE_TWO), clocks.get(SLAVE_THREE));
            this.node.adjustTime(this.node.getTime(), averarageTimeInNanos);

            // percorre cada conexão do nodo master
            this.node.connections.forEach((key, value) -> {

                // com a chave busca a collection de clocks a fim de buscar o valor do one time delay
                final Long oneTimeDelay = clocks.get(key).oneTimeDelay;
                // soma o tempo do nodo master que havia sido atualizado com o tempo médio com o one time delay
                long newTimeInNanos = this.node.time.toNanoOfDay() + oneTimeDelay;

                // monta a mensagem de atualização do tempo no nodo slave
                final String message = String.format("%s %s", ADJUST_TIME, newTimeInNanos);

                //envia a mensagem
                sendMessage(message, value.port, this.socket);
            });

            // limpa para a proxima iteração de atualização de tempo
            clocks.forEach((key, value) -> {
                clocks.remove(key);
            });

        }
    }
}