package org.tfr.powergrabber;

public class DataLoaderProcess implements Runnable {
    private final PowerGrabber powerGrabber;

    public DataLoaderProcess(PowerGrabber powerGrabber) {
        this.powerGrabber = powerGrabber;
    }

    public void run() {
        while (!powerGrabber.exit) {
            while (powerGrabber.loading && !powerGrabber.exit) {
                doReadData();
            }
            PowerGrabber.sleep(1000);
        }
    }

    public void doReadData() {
        boolean receivedData = false;
        while (!receivedData && powerGrabber.loading && !powerGrabber.exit) {
            try {
                powerGrabber.readData();
                receivedData = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            PowerGrabber.sleep(250);
        }
        PowerGrabber.sleep(powerGrabber.updateIntervall);
    }

}
