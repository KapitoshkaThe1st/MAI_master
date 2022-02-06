package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ParallelDirectorySizeCalculator {

    final File directory;
    final int maxThreads;
    int size;


    public ParallelDirectorySizeCalculator(String path, int maxThreads) throws FileNotFoundException {
        directory = new File(path);

        if(!directory.exists())
            throw new FileNotFoundException(path + "does not exist");

        if(!directory.isDirectory())
            throw new IllegalArgumentException(path + " is not a directory");

        this.maxThreads = maxThreads;
    }

    static class WalkerThread extends Thread {
        int size;
        ArrayList<File> directories = new ArrayList<>();

        public void addDirectory(File directory){
            directories.add(directory);
        }

        public int getSize(){
            return size;
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

        @Override
        public void run() {
            for(File directory : directories)
                walkAux(directory);
        }
    }

    private void calculate() {
        size = 0;
        LinkedList<File> directoryQueue = new LinkedList<>();
        directoryQueue.push(directory);
        while(directoryQueue.size() > 0 && directoryQueue.size() < maxThreads){
            File f = directoryQueue.pop();
            File[] files = f.listFiles();
            assert files != null;
            for(File file : files){
                if(file.isFile()){
                    size += file.length();
                }
                else
                    directoryQueue.add(file);
            }
        }

        int nDirectories = directoryQueue.size();
        int nThreads = Math.min(maxThreads, nDirectories);

        WalkerThread[] walkers = new WalkerThread[nThreads];
        int i = 0;
        while(directoryQueue.size() > 0){
            int threadIdx = i % nThreads;
            if(walkers[threadIdx] == null)
                walkers[threadIdx] = new WalkerThread();
            walkers[threadIdx].addDirectory(directoryQueue.pop());
            i++;
        }

        for(WalkerThread walker : walkers)
            walker.start();

        for(WalkerThread walker : walkers){
            try {
                walker.join();
            }
            catch (InterruptedException ex){
                System.out.println("InterruptedException has occurred for some reason");
            }
            size += walker.getSize();
        }
    }

    public int getTotalSize() throws FileNotFoundException {
        calculate();
        return size;
    }
}

