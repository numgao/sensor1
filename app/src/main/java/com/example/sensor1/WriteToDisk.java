package com.example.sensor1;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteToDisk {
    private final static String NEW_LINE_SEPARATOR="\n";
    private ArrayList<String> csvValues;
    private volatile ArrayList<String> rotation;

    private Thread thread;



    public void run() throws IOException {
        CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator("line.separator");
        FileWriter fileWriter = new FileWriter("/storage/emulated/0/ASensorTest/ASensorTest-2023-3-13-6-14-44.csv");
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, formator);

        csvValues.clear();
        synchronized (this) {
            csvValues.addAll(rotation);
        }
        csvPrinter.printRecord(csvValues);

    }

    private void writeToNew() {

        thread = new Thread((Runnable) this);
        thread.start();
    }

    public void setRotation(float[] rotation) {
        if(rotation != null) {
            synchronized (rotation) {
                this.rotation.clear();
                for (int i = 0; i < 3; i++) {
                    this.rotation.add(String.valueOf(rotation[i]));
                }
            }
        }
    }

    }
