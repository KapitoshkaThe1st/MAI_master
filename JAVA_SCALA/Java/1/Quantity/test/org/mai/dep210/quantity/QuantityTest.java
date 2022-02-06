package org.mai.dep210.quantity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mai.dep810.quantity.Quantity;
import org.mai.dep810.quantity.UnitOfMeasure;
import org.mai.dep810.quantity.WrongUnitsOfMeasureException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by Asus on 9/24/2018.
 */
public class QuantityTest {

    Quantity q1, q2, q3, q4;
    BigDecimal ratio, ratio2;

    @Before
    public void setUp() throws Exception {
        q1 = new Quantity(new BigDecimal(100), UnitOfMeasure.G);
        q2 = new Quantity(new BigDecimal(100), UnitOfMeasure.KG);
        q3 = new Quantity(new BigDecimal(100), UnitOfMeasure.KM);
        q4 = new Quantity(new BigDecimal(100), UnitOfMeasure.M);

        ratio = new BigDecimal("0.1");
        ratio2 = new BigDecimal("0.3");
    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void add() throws Exception {
        assertEquals(q1.add(q2), new Quantity(new BigDecimal("100100"), UnitOfMeasure.G));
        assertEquals(q3.add(q4), new Quantity(new BigDecimal("100.1"), UnitOfMeasure.KM));
    }

    @Test
    public void subtract() throws Exception {
        assertEquals(q1.subtract(q2), new Quantity(new BigDecimal("-99900"), UnitOfMeasure.G));
        assertEquals(q3.subtract(q4), new Quantity(new BigDecimal("99.9"), UnitOfMeasure.KM));
    }

    @Test
    public void multiply() throws Exception {
        assertEquals(q1.multiply(ratio), new Quantity(new BigDecimal("10.0"), UnitOfMeasure.G));
        assertEquals(q3.multiply(ratio2), new Quantity(new BigDecimal("30.0"), UnitOfMeasure.KM));
    }

    @Test
    public void divide() throws Exception {
        assertEquals(q1.divide(ratio), new Quantity(new BigDecimal("1000.0"), UnitOfMeasure.G));
        assertEquals(q3.divide(ratio2), new Quantity(new BigDecimal("333.33333333333333333333"), UnitOfMeasure.KM));
    }

    @Test(expected = WrongUnitsOfMeasureException.class)
    public void differentMeasuresOperations() throws Exception {
        q1.add(q3);
        q1.subtract(q3);
    }

    @Test(expected = ArithmeticException.class)
    public void divisionByZero() throws Exception {
        q3.divide(new BigDecimal(0.0));
    }
}