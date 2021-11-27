package com.trabalho.tasks;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class UpdaterTimeTask {

    Timer timer;

    public UpdaterTimeTask(final Integer seconds, final DatagramSocket socket) {
        timer = new Timer();
        timer.schedule(new UpdaterTimeExecuter(socket), 3000, seconds * 1000);
    }

    class UpdaterTimeExecuter extends TimerTask {

        DatagramSocket socket;

        public UpdaterTimeExecuter(final DatagramSocket socket) {
            this.socket = socket;
        }

        // todo deve chamar o algortimo de berkeleys chamando uma collection global que tem os tempos dos nodos slaves.

        public void run() {
            System.out.println("update time");
        }
    }
}