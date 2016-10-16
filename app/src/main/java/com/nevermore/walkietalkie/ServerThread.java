package com.nevermore.walkietalkie;

/**
 * Created by TEMP on 16-Oct-16.
 */
public class ServerThread extends Thread {

    boolean running=true;

    public void kill() {
        running = false;
    }

    @Override
    public void run() {

        while(running) {
        }
    }
}
