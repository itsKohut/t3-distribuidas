package com.trabalho.tasks;

import java.net.DatagramSocket;

public class UpdaterThread extends Thread {

    private DatagramSocket socket;

    public UpdaterThread(final DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("update time");
    }
}