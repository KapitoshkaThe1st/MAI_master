package org.mai.dep810.quantity;

import java.math.BigDecimal;

public enum UnitOfMeasure {
    KG(BigDecimal.ONE, null),
    G(new BigDecimal("0.001"), KG),
    T(new BigDecimal("1000.000"), KG),

    M(BigDecimal.ONE, null),
    KM(new BigDecimal("1000.000"), M),
    CM(new BigDecimal("0.01"), M)
    ;

    private BigDecimal coeff;
    private UnitOfMeasure base;

    UnitOfMeasure(BigDecimal coeff, UnitOfMeasure base) {
        this.coeff = coeff;
        this.base = base;
    }

    public BigDecimal getCoeff() {
        return coeff;
    }

    public UnitOfMeasure getBase() {
        return base;
    }
}
