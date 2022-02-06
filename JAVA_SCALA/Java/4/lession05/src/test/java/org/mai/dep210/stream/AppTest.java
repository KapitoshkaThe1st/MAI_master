package org.mai.dep210.stream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mai.dep210.stream.iris.Iris;
import org.mai.dep210.stream.iris.IrisDataSetHelper;
import org.mai.dep210.stream.iris.Petal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigorous Test :-)
     */

    IrisDataSetHelper helper = null;

    ArrayList<Iris> readDataSet(String path){
        ArrayList<Iris> irises = null;
        try{
            irises = Files.lines(Paths.get(path))
                    .map(Iris::parse)
                    .collect(Collectors.toCollection(ArrayList<Iris>::new));
        }
        catch (IOException ex){
            System.err.println("Could not open file " + path);
            System.exit(1);
        }

        return irises;
    }

    void initDataSetWithTrunc(){
        helper = new IrisDataSetHelper(readDataSet("iris_trunc.data"));
    }

    void initDataSet(){
        helper = new IrisDataSetHelper(readDataSet("iris.data"));
    }

    double eps = 0.0001;
    private boolean approxEqualDouble(Double a, Double b){
        return Math.abs(a - b) < eps;
    }

    public void testAvgError()
    {
        IrisDataSetHelper emptyHelper = new IrisDataSetHelper(new ArrayList<>());
        Double avg = emptyHelper.getAverage(Iris::getPetalWidth);
        assertEquals(0.0, avg);
    }

    public void testAvg(){
        initDataSetWithTrunc();

        Double avg = helper.getAverage(Iris::getPetalWidth);
        assertTrue(approxEqualDouble(avg, 1.175));
    }

    public void testFilter(){
        initDataSetWithTrunc();

        Iris[] expectedValues = Stream.of("5.1,3.5,1.4,0.2,Iris-setosa",
        "4.7,3.2,1.3,0.2,Iris-setosa",
        "7.0,3.2,4.7,1.4,Iris-versicolor",
        "6.4,3.2,4.5,1.5,Iris-versicolor",
        "6.3,3.3,6.0,2.5,Iris-virginica").map(Iris::parse).toArray(Iris[]::new);

        List<Iris> filtered = helper.filter(x -> x.getSepalWidth() > 3.1);
        assertThat(filtered, hasItems(expectedValues));
        assertEquals(expectedValues.length, filtered.size());
    }

    public void testFilteredAvgError(){
        initDataSetWithTrunc();
        Double filteredAvg = helper.getAverageWithFilter(x -> x.getSepalWidth() > 100.0, Iris::getSepalWidth);

        assertEquals(0.0, filteredAvg);
    }

    public void testFilteredAvg(){
        initDataSetWithTrunc();

        Double filteredAvg = helper.getAverageWithFilter(x -> x.getSepalWidth() > 3.1, Iris::getSepalWidth);
        assertTrue(approxEqualDouble(filteredAvg, 3.28));
    }

    public void testGroupBy(){
        initDataSet();

        Map<Petal, List<Iris>> groups = helper.groupBy(Iris::classifyByPetal);
        assertEquals(groups.size(), Petal.values().length);
    }

    public void testGroupByMax(){
        initDataSet();

        Map<String, Optional<Iris>> groupsMaxSepalWidth = helper.maxFromGroupedBy(Iris::getSpecies, Iris::getSepalWidth);

        assertEquals(groupsMaxSepalWidth.size(), Petal.values().length);

        assertTrue(approxEqualDouble(groupsMaxSepalWidth.get("Iris-setosa").get().getSepalWidth(), 4.4));
        assertTrue(approxEqualDouble(groupsMaxSepalWidth.get("Iris-versicolor").get().getSepalWidth(), 3.4));
        assertTrue(approxEqualDouble(groupsMaxSepalWidth.get("Iris-virginica").get().getSepalWidth(), 3.8));
    }
}
