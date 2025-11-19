package org.me.gcu.omoike_grace_s2125456.view;

public interface OnCurrencyActionListener {

    // Place this after the closing brace of CurrencyAdapter
        void onShowMap(String code);
        void onConvert(String code, double rate);
    }


