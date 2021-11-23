package com.trabalho.tasks;

import java.net.DatagramSocket;

public class ListenerThread extends Thread {

    private DatagramSocket socket;

    public ListenerThread(final DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("fetch time");
    }
}