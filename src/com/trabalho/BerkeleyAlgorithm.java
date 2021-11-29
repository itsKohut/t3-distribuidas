package com.trabalho;

import java.time.LocalTime;


public final class BerkeleyAlgorithm {

    public static final int PLUS_MASTER_NODE = 1;

    // calcula a média do tempo em relação ao tempo do nodo master
    public synchronized static long calculateAverageTime(final LocalTime localTime, final ClockHandler... clockHandlers) {
        long nanoTimeLocal = localTime.toNanoOfDay();
        long serverTimeDifference = 0;
        int quantityNodes = clockHandlers.length + PLUS_MASTER_NODE;
        for (ClockHandler c : clockHandlers) {
            serverTimeDifference += c.timeServer.toNanoOfDay() - nanoTimeLocal;
        }
        return serverTimeDifference / quantityNodes;
    }


    public synchronized static void randomT4Time(ClockHandler clockHandler) {
        long milliseconds = (long) (Math.random() * 1000);
        long millisecondsToNanos = milliseconds * 1000000L;
        long delayToNanos = clockHandler.timeDelay * 1000000L;
        long t4Nano = clockHandler.t4.toNanoOfDay();
        final LocalTime newLocalTime = LocalTime.ofNanoOfDay(t4Nano + delayToNanos + millisecondsToNanos);

        clockHandler.t4 = newLocalTime;
    }

    public synchronized static void calculateRTT(ClockHandler clockHandler) {
        long t4Nanos = clockHandler.t4.toNanoOfDay();
        long t1Nanos = clockHandler.t1.toNanoOfDay();
        long rtt = (t4Nanos - t1Nanos - (clockHandler.timeDelay * 1000000L)) / 1000000L;
        clockHandler.rtt = rtt;
        clockHandler.oneTimeDelay = rtt / 2;
    }
}
