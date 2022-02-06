package com.company;

import java.io.File;
import java.io.FileNotFoundException;

public class DirectorySizeCalculator {
    private final File directory;
    int size;

    public DirectorySizeCalculator(String path) throws FileNotFoundException {
        directory = new File(path);

        if(!directory.exists())
            throw new FileNotFoundException(path + "does not exist");

        if(!directory.isDirectory())
            throw new IllegalArgumentException(path + " is not a directory");
    }

    private void walkAux(File directory){
        File[] files = directory.listFiles();
        assert files != null;
        for(File file : files){
            if(file.isFile()){
                size += file.length();
            }
            else
                walkAux(file);
        }
    }

    private void calculate() {
        size = 0;
        walkAux(directory);
    }

    public int getTotalSize() throws FileNotFoundException {
        calculate();
        return size;
    }
}
