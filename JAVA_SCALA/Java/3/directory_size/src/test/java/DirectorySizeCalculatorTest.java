import com.company.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.io.FileUtils;

class DirectorySizeCalculatorTest {
    static String testDir = "/home/alex/dirs";
    static TestFilesCreator tfc;

    @BeforeAll
    static void setUpOnce(){
        tfc = new TestFilesCreator(testDir, 5, 5, 10, 128);
        tfc.create();
    }

    @AfterAll
    static void cleanUp(){
        try {
            FileUtils.deleteDirectory(new File(testDir));
        }
        catch (IOException ex){
            System.err.println("Clean-up IO error");
        }
    }

    int nTimes = 10;
    <V> V measureTime(Callable<V> c){
        long start = System.currentTimeMillis();
        V result = null;
        for(int i = 0; i < nTimes; ++i) {
            try {
                result = c.call();
            } catch (Exception ex) {
                fail("An exception occurred:\n" + ex.getMessage());
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (double)(end - start) / nTimes + " ms");
        return result;
    }

    @Test
    void testDirectorySizeCalculator() {
        try{
            DirectorySizeCalculator dsc = new DirectorySizeCalculator(testDir);
            System.out.println("DirectorySizeCalculator: ");
            int realSize = tfc.getTotalFileSize();
            int calculatedSize = measureTime(dsc::getTotalSize);
            System.out.println("Real size: " + realSize);
            System.out.println("Calculated size: " + calculatedSize);
            assertEquals(calculatedSize, realSize);
        }
        catch (FileNotFoundException ex){
            fail("File not found");
        }
    }

    @Test
    void testParallelDirectorySizeCalculator() {
        try{
            ParallelDirectorySizeCalculator pdsc = new ParallelDirectorySizeCalculator(testDir, 16);
            System.out.println("ParallelDirectorySizeCalculator: ");
            int realSize = tfc.getTotalFileSize();
            int calculatedSize = measureTime(pdsc::getTotalSize);
            System.out.println("Real size: " + realSize);
            System.out.println("Calculated size: " + calculatedSize);
            assertEquals(calculatedSize, realSize);
        }
        catch (FileNotFoundException ex){
            fail("File not found");
        }
    }

    @Test
    void testThreadPoolDirectorySizeCalculator() {
        try{
            ThreadPoolDirectorySizeCalculator tpdsc = new ThreadPoolDirectorySizeCalculator(testDir, 16);
            System.out.println("ThreadPoolDirectorySizeCalculator: ");
            int realSize = tfc.getTotalFileSize();
            int calculatedSize = measureTime(tpdsc::getTotalSize);
            System.out.println("Real size: " + realSize);
            System.out.println("Calculated size: " + calculatedSize);
            assertEquals(calculatedSize, realSize);
        }
        catch (FileNotFoundException ex){
            fail("File not found");
        }
    }

    @Test
    void testForkJoinPoolDirectorySizeCalculator() {
        try{
            ForkJoinDirectorySizeCalculator fjdsc = new ForkJoinDirectorySizeCalculator(testDir);
            System.out.println("ForkJoinPoolDirectorySizeCalculator: ");
            int realSize = tfc.getTotalFileSize();
            int calculatedSize = measureTime(fjdsc::getTotalSize);
            System.out.println("Real size: " + realSize);
            System.out.println("Calculated size: " + calculatedSize);
            assertEquals(calculatedSize, realSize);
        }
        catch (FileNotFoundException ex){
            fail("File not found");
        }
    }
}