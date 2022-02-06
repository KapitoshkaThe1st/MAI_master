package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

public class ForkJoinDirectorySizeCalculator {

    private final File directory;
    int size;

    public ForkJoinDirectorySizeCalculator(String path) throws FileNotFoundException {
        directory = new File(path);

        if(!directory.exists())
            throw new FileNotFoundException(path + "does not exist");

        if(!directory.isDirectory())
            throw new IllegalArgumentException(path + " is not a directory");
    }

    static class WalkTask extends RecursiveTask<Integer>{

        File directory;
        public WalkTask(File directory){
            this.directory = directory;
        }

        @Override
        protected Integer compute() {

            File[] files = directory.listFiles();
            int size = 0;

            List<WalkTask> dividedTasks = new ArrayList<>();
            for(File file : files){
                if (file.isFile())
                    size += file.length();
                else
                    dividedTasks.add(new WalkTask(file));
            }

            return size + ForkJoinTask.invokeAll(dividedTasks)
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        }
    }

    private void calculate() {
        ForkJoinPool fjp = ForkJoinPool.commonPool();
        size = fjp.invoke(new WalkTask(directory)); // waits for completion
    }

    public int getTotalSize() throws FileNotFoundException {
        calculate();
        return size;
    }
}
