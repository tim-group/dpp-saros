package com.timgroup.saros.proxy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteSomeFile {

    public static void doIt(String contents, String name) {
        try {
            File createTempFile = File.createTempFile(name, "txt");
            PrintWriter writer = new PrintWriter(createTempFile);
            writer.append(contents);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
