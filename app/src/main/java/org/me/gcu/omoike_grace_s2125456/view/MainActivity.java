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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load currency list on start
        if (savedInstanceState == null) {


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new CurrencyListFragment(), "CurrencyList")
                    .commit();
        }
    }

    @Override
    public void onCurrencySelected(String code, double rate) {
        onConvert(code, rate); // reuse same conversion logic
    }

    @Override
    public void onConvert(String code, double rate) {
        CurrencyConversionFragment conversionFragment = new CurrencyConversionFragment();
        Bundle args = new Bundle();
        args.putString("currencyCode", code);
        args.putDouble("currencyRate", rate);
        conversionFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, conversionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onShowMap(String code) {
        CurrencyMapFragment mapFragment = new CurrencyMapFragment();
        Bundle args = new Bundle();
        args.putString("currencyCode", code);
        mapFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, mapFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
