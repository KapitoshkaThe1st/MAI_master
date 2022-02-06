package org.mai.dep810.quantity;

import static java.lang.System.*;

import java.math.BigDecimal;

public class Program {

    public static void main(String[] args) {
        // операции над меньшими с большими
        Quantity q1 = new Quantity(new BigDecimal(100), UnitOfMeasure.G);
        Quantity q2 = new Quantity(new BigDecimal(100), UnitOfMeasure.KG);

        out.println(q1 + " + " + q2 + " = " + q1.add(q2));
        out.println(q1 + " - " + q2 + " = " + q1.subtract(q2));

        // операции над большими с меньшими
        Quantity q3 = new Quantity(new BigDecimal(100), UnitOfMeasure.KM);
        Quantity q4 = new Quantity(new BigDecimal(100), UnitOfMeasure.M);

        out.println(q3 + " + " + q4 + " = " + q3.add(q4));
        out.println(q3 + " - " + q4 + " = " + q3.subtract(q4));

        // умножение и деление величин на число
        BigDecimal ratio = new BigDecimal("0.1");

        out.println(q1 + " * " + ratio + " = " + q1.multiply(ratio));
        out.println(q3 + " / " + ratio + " = " + q3.divide(ratio));

        BigDecimal ratio2 = new BigDecimal("0.3");

        out.println(q1 + " * " + ratio2 + " = " + q1.multiply(ratio2));
        out.println(q3 + " / " + ratio2 + " = " + q3.divide(ratio2));

        // Операциями над разными величинами,
        // должны бросать исключение WrongUnitsOfMeasureException
        try {
            out.println(q1.add(q3));
        } catch (Exception ex) {
            out.println("Exception caught: " + ex);
        }

        try {
            out.println(q3.add(q1));
        } catch (Exception ex) {
            out.println("Exception caught: " + ex);
        }

        // деление на ноль, должно брость ArithmeticException
        try {
            out.println(q3.divide(new BigDecimal("0.0")));
        } catch (Exception ex) {
            out.println("Exception caught: " + ex);
        }
    }
}
