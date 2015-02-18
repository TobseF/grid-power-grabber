package org.tfr.powergrabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static java.lang.System.out;


public class PowerGrabber {

    public static final String CSV_FILE = "values.csv";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat();
    final Scanner scanner = new Scanner(System.in);
    int updateIntervall;
    int readingTimeout;
    boolean loading = false;
    boolean exit = false;
    long received = 0;
    long errors = 0;
    DataWriter dataWriter;
    Date lastReceived;

    public PowerGrabber(int readingTimeout) {
        this.readingTimeout = readingTimeout;
        initDataLoader();
    }

    public PowerGrabber(int updateIntervall, int readingTimeout, DataWriter dataWriter) {
        this.updateIntervall = updateIntervall;
        this.readingTimeout = readingTimeout;
        this.dataWriter = dataWriter;
        initDataLoader();
    }

    public PowerGrabber() {
        System.out.println("Loaded Website Grabber with intervall " + updateIntervall + " seconds");

        initDataLoader();
        readUpdateInterval();
        readReadingTimeout();
        while (!exit) {
            System.out.println("Type Task: start, stop, exit");
            handleUserInput();
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        out.println(new Date() + ": " + message);
    }

    public static void main(String[] args) throws Exception {
        new PowerGrabber();
    }

    public boolean isLoading() {
        return loading;
    }

    private void initDataLoader() {
        new Thread(new DataLoaderProcess(this)).start();
    }

    public void readUpdateInterval() {
        System.out.println("Type update intervall in seconds:");
        boolean correctInput = false;
        while (!correctInput) {
            String input = scanner.nextLine();
            try {
                updateIntervall = Integer.parseInt(input);
                System.out.println("Setting update intervall to " + updateIntervall + " seconds!");
                correctInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input:  " + input);
            }
        }
    }

    public void readReadingTimeout() {
        System.out.println("Type timeout for loading a website seconds:");
        boolean correctInput = false;
        while (!correctInput) {
            String input = scanner.nextLine();
            try {
                readingTimeout = Integer.parseInt(input);
                System.out.println("Setting readingTimeout to " + readingTimeout + " seconds!");
                correctInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input:  " + input);
            }
        }
    }

    private void handleUserInput() {
        String input = scanner.nextLine();
        if (input.equals("start")) {
            start();
        } else if (input.equals("exit")) {
            exit();
        } else if (input.equals("stop")) {
            stop();
        } else {
            System.out.println("Wrong input: '" + input + "'");
        }
    }

    public void start() {
        loading = true;
        System.out.println("Starting DataLogging!");
    }

    public void stop() {
        loading = false;
        System.out.println("stopped!");
    }

    public void exit() {
        exit = true;
        System.out.println("Shutting down!");
    }

    public void readData() {
        try {
            log("reading SR Grid Frequency...");
            String srGridFrequency = getSRGridFrequency();
            log("received: " + srGridFrequency);

            log("reading NR Grid Frequency...");
            String nrGridFrequency = getNRGridFrequency();
            log("received: " + nrGridFrequency);
            received++;
            lastReceived = new Date();
            writeDataToCSV(srGridFrequency, nrGridFrequency);
        } catch (Exception e) {
            errors++;
            log("Couldn't read and write because of: " + e.getMessage());
        }
    }

    private void writeDataToCSV(String srGridFrequency, String nrGridFrequency) throws IOException {
        String newCSVLine = dateFormat.format(new Date()) + ";" + srGridFrequency + ";" + nrGridFrequency + ";";
        log("writing to csv File " + CSV_FILE + ": " + newCSVLine);
        writeValues(newCSVLine);
    }

    public void writeValues(String line) throws IOException {
        FileWriter writer = new FileWriter(CSV_FILE, true);
        writer.append(line).append(System.lineSeparator());
        writer.close();
    }

    public int getUpdateIntervall() {
        return updateIntervall;
    }

    public void setUpdateIntervall(int updateIntervall) {
        this.updateIntervall = updateIntervall;
    }

    public int getReadingTimeout() {
        return readingTimeout;
    }

    public void setReadingTimeout(int readingTimeout) {
        this.readingTimeout = readingTimeout;
    }

    public String getSRGridFrequency() throws Exception {
        String url = "http://www.srldc.org/";
        String selector = "span#ctl00_ContentPlaceHolder1_Label1";
        return getValueFromURL(url, selector);
    }

    public String getNRGridFrequency() throws Exception {
        String url = "http://www.nrldc.org/NRDefault.aspx";
        String selector = "span#ctl00_ContentPlaceHolder1_lblGridFreq";
        return getValueFromURL(url, selector);
    }

    public String getValueFromURL(String url, String selector) throws Exception {
        Document doc = Jsoup.connect(url).timeout(readingTimeout * 1000).get();
        Elements sgrid = doc.select(selector);
        return sgrid.first().text();
    }

    public long getReceived() {
        return received;
    }

    public long getErrors() {
        return errors;
    }

    public Date getLastReceived() {
        return lastReceived;
    }

}
