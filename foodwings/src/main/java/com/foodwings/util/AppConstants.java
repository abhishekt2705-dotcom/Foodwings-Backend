package com.foodwings.util;

import java.math.BigDecimal;

/**
 * Shared application constants.
 */
public final class AppConstants {

    private AppConstants() {
    }

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";

    /** Flat delivery fee applied to every order. */
    public static final BigDecimal DELIVERY_FEE = new BigDecimal("40.00");
}
