package org.me.gcu.omoike_grace_s2125456.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import org.me.gcu.omoike_grace_s2125456.R;
import org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment;
import org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyConversionFragment;
import org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyMapFragment;

public class MainActivity extends AppCompatActivity
        implements CurrencyListFragment.OnCurrencySelectedListener, OnCurrencyActionListener {

    private static final int CONTAINER_ID = R.id.fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Always load the list fragment first for BOTH orientations
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(CONTAINER_ID, new CurrencyListFragment(), "CurrencyList")
                    .commit();
        }
    }

    @Override
    public void onCurrencySelected(String code, double rate) {
        onConvert(code, rate);
    }

    @Override
    public void onConvert(String code, double rate) {
        CurrencyConversionFragment conversionFragment = new CurrencyConversionFragment();
        Bundle args = new Bundle();
        args.putString("currencyCode", code);
        args.putDouble("currencyRate", rate);
        conversionFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(CONTAINER_ID, conversionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onShowMap(String code, double rate) {
        CurrencyMapFragment mapFragment = new CurrencyMapFragment();
        Bundle args = new Bundle();
        args.putString("currencyCode", code);
        args.putDouble("currencyRate", rate);

        mapFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(CONTAINER_ID, mapFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
