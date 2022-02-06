package org.mai.dep810.quantity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;

public class Quantity {
    private final BigDecimal value;
    private final UnitOfMeasure measure;

    private static final int maxDivisionScale = 20;

    public Quantity(BigDecimal value, UnitOfMeasure measure) {
        this.value = value;
        this.measure = measure;
    }

    public BigDecimal getValue() {
        return value;
    }

    public UnitOfMeasure getMeasure() {
        return measure;
    }

    private static UnitOfMeasure getMeasureBase(UnitOfMeasure measure){
        UnitOfMeasure baseMeasure = measure.getBase();
        return baseMeasure == null ? measure : baseMeasure;
    }

    private Quantity performSimilarMeasureCheckedBinaryOperation(Quantity other, BinaryOperator<BigDecimal> operator){
        UnitOfMeasure otherMeasure = other.getMeasure();

        UnitOfMeasure thisBaseMeasure = getMeasureBase(measure);
        UnitOfMeasure otherBaseMeasure = getMeasureBase(otherMeasure);

        if(thisBaseMeasure != otherBaseMeasure)
            throw new WrongUnitsOfMeasureException(
                    "Could not perform operation on quantities of different units of measure: "
                            + measure + ", " + otherMeasure);

        BigDecimal otherToThisCoef = otherMeasure.getCoeff().divide(measure.getCoeff(), maxDivisionScale, RoundingMode.HALF_DOWN);
        return new Quantity(operator.apply(value, other.value.multiply(otherToThisCoef)).stripTrailingZeros(), measure);
    }

    public Quantity add(Quantity other) {
        return performSimilarMeasureCheckedBinaryOperation(other, BigDecimal::add);
    }

    public Quantity subtract(Quantity other) {
        return performSimilarMeasureCheckedBinaryOperation(other, BigDecimal::subtract);
    }

    public Quantity multiply(BigDecimal ratio) {
        return new Quantity(value.multiply(ratio), measure);
    }

    public Quantity divide(BigDecimal ratio) {
        if(ratio.equals(BigDecimal.ZERO))
            throw new ArithmeticException("ratio must not be zero");

        return new Quantity(value.divide(ratio, maxDivisionScale, RoundingMode.HALF_DOWN).stripTrailingZeros(), measure);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) {
            return true;
        }
        if(!(o instanceof Quantity)) {
            return false;
        }
        Quantity q = (Quantity) o;

        return value.compareTo(q.value) == 0 && measure.equals(q.measure);
    }

    public String toString() {
        return value + " " + measure;
    }
}
