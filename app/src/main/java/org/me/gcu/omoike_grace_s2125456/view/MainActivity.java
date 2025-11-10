package org.me.gcu.omoike_grace_s2125456.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import org.me.gcu.omoike_grace_s2125456.R;
import org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment;
import org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyConversionFragment;

public class MainActivity extends AppCompatActivity implements CurrencyListFragment.OnCurrencySelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the first fragment (Currency List)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new CurrencyListFragment())
                    .commit();
        }
    }

    // This method is called from CurrencyListFragment when a currency is tapped
    @Override
    public void onCurrencySelected(String code, double rate) {
        CurrencyConversionFragment conversionFragment = new CurrencyConversionFragment();

        Bundle args = new Bundle();
        args.putString("currencyCode", code);
        args.putDouble("currencyRate", rate);
        conversionFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, conversionFragment);
        transaction.addToBackStack(null); // allows back navigation
        transaction.commit();
    }
}
