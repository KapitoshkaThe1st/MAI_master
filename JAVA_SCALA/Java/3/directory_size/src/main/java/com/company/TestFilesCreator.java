package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class TestFilesCreator {
    String rootDir;
    int maxDepth, branchFactor, nFilesPerDir, maxFileSize;

    public TestFilesCreator(String rootDir, int maxDepth, int branchFactor, int nFilesPerDir, int maxFileSize) {
        this.rootDir = rootDir;
        this.maxDepth = maxDepth;
        this.branchFactor = branchFactor;
        this.nFilesPerDir = nFilesPerDir;
        this.maxFileSize = maxFileSize;
    }

    int totalFileSize = 0;

    static Random rnd = new Random();

    private int writeRandomFile(String path) {
        File f = new File(path);
        int fileSize = rnd.nextInt(maxFileSize);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            byte[] bytes = new byte[fileSize];
            rnd.nextBytes(bytes);
            fos.write(bytes);
        } catch (FileNotFoundException ex) {
            System.err.println("File not found thrown");
        } catch (IOException ex) {
            System.err.println("IO exception thrown");
        }
        return fileSize;
    }

    private int createAux(String dir, int depth) {
        int size = 0;
        if (depth > maxDepth)
            return 0;

        for (int i = 0; i < branchFactor; ++i) {
            String newDir = dir + "/dir" + i;
            File f = new File(newDir);
            f.mkdirs();
            size += createAux(newDir, depth + 1);
        }

        for (int i = 0; i < nFilesPerDir; ++i) {
            String newFile = dir + "/file" + i;
            size += writeRandomFile(newFile);
        }
        return size;
    }

    public void create() {
        totalFileSize = createAux(rootDir, 0);
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }
}
