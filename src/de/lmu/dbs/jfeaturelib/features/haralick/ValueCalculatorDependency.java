package de.lmu.dbs.jfeaturelib.features.haralick;

import java.awt.image.BufferedImage;

@Deprecated
public abstract class ValueCalculatorDependency extends Calculator {

    protected ValueCalculator value_calc;

    public ValueCalculatorDependency(ValueCalculator value_calc) {
        super(value_calc);
        this.value_calc = value_calc;
    }

    public ValueCalculatorDependency(BufferedImage image) {
        this(new ValueCalculator(image));
    }

    public ValueCalculator getValueCalculator() {
        return this.value_calc;
    }
}
