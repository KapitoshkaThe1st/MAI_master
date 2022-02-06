package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

public class ThreadPoolDirectorySizeCalculator {

    private final File directory;
    private final int maxThreads;
    int size;

    public ThreadPoolDirectorySizeCalculator(String path, int maxThreads) throws FileNotFoundException {
        directory = new File(path);

        if(!directory.exists())
            throw new FileNotFoundException(path + "does not exist");

        if(!directory.isDirectory())
            throw new IllegalArgumentException(path + " is not a directory");

        this.maxThreads = maxThreads;
    }
    private static int walkAux(File directory){
        int size = 0;
        File[] files = directory.listFiles();
        assert files != null;
        for(File file : files){
            if(file.isFile())
                size += file.length();
            else
                size += walkAux(file);
        }

        return size;
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

        ExecutorService es = Executors.newFixedThreadPool(nThreads);

        ArrayList<Future<Integer>> futures = new ArrayList<>();

        for(int i = 0; i < nDirectories; ++i){
            File dir = directoryQueue.pop();
            futures.add(es.submit(() -> walkAux(dir)));
        }

        for(Future<Integer> f : futures){
            try{
                size += f.get();
            }
            catch (Exception ex){
                System.err.println("Exception occurred:\n" + ex.getMessage());
            }
        }
    }

    public int getTotalSize() throws FileNotFoundException {
        calculate();
        return size;
    }
}
