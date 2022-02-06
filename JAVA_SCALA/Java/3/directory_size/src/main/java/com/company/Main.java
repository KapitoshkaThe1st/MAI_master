package com.company;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String path = "/home/alex/programming/quotes_monitor";

        try {
            ParallelDirectorySizeCalculator dsc = new ParallelDirectorySizeCalculator(path, 16);
            System.out.println("Directory '" + path + "' size: " + dsc.getTotalSize());
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
