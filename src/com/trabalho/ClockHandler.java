package com.trabalho;

import java.time.LocalTime;

// classe responsável por manter os dados recebidos de um ciclo de atualização de tempo dos nodos
public final class ClockHandler {

    public Long timeDelay;
    public LocalTime timeServer;
    public LocalTime t1;
    public LocalTime t4;
    public long rtt;
    public long oneTimeDelay;

    public ClockHandler(Long TimeDelay, LocalTime t1, LocalTime t4) {
        this.timeDelay = TimeDelay;
        this.t1 = t1;
        this.t4 = t4;
    }

    @Override
    public String toString() {
        return "timeDelay=" + timeDelay +
                ", timeServer=" + timeServer +
                ", t1=" + t1 +
                ", t4=" + t4 +
                ", rtt=" + rtt +
                ", oneTimeDelay=" + oneTimeDelay;
    }
}
