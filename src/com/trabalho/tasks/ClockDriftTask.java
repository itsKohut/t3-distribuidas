package com.trabalho.tasks;

import com.trabalho.Node;

import java.util.Timer;
import java.util.TimerTask;

public class ClockDriftTask {

    Timer timer;

    public ClockDriftTask(final Integer seconds, final Node node) {
        timer = new Timer();
        timer.schedule(new ClockDriftExecuter(node), 10000, seconds * 1000);
    }

    class ClockDriftExecuter extends TimerTask {

        Node node;

        public ClockDriftExecuter(final Node node) {
            this.node = node;
        }

        public void run() {
//            this.node.delay +=  this.node.delay;
        }
    }
}