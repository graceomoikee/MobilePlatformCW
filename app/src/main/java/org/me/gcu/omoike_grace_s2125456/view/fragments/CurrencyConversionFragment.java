package org.me.gcu.omoike_grace_s2125456.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.omoike_grace_s2125456.R;

public class CurrencyConversionFragment extends Fragment {

    private String selectedCode;
    private double selectedRate;
    private boolean isReversed = false;
    private boolean isSwapped = false;
    private EditText gbpInput;
    private TextView resultText;
    private TextView titleText;
    private TextView rateInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currency_conversion, container, false);

        // --- Get arguments passed from MainActivity ---
        if (getArguments() != null) {
            selectedCode = getArguments().getString("currencyCode");
            selectedRate = getArguments().getDouble("currencyRate");
        }

        // --- Bind UI ---
        titleText = view.findViewById(R.id.titleText);
        rateInfo = view.findViewById(R.id.rateInfo);
        gbpInput = view.findViewById(R.id.gbpInput);
        resultText = view.findViewById(R.id.resultText);


        Button btnSwap = view.findViewById(R.id.btnSwap);
        Button btnEquals = view.findViewById(R.id.btnEquals);
        Button backButton = view.findViewById(R.id.backButton);
        Button mapButton = view.findViewById(R.id.mapButton);

        // --- Display initial conversion info ---
        titleText.setText("Convert GBP → " + selectedCode);
        rateInfo.setText(String.format("1 GBP = %.2f %s", selectedRate, selectedCode));

        // --- Keyboard behaviour ---
        gbpInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        gbpInput.requestFocus();

        // --- Swap conversion direction ---
        btnSwap.setOnClickListener(v -> {
            isReversed = !isReversed;

            // Update labels
            if (isReversed) {
                titleText.setText("Convert " + selectedCode + " → GBP");
                rateInfo.setText(String.format("1 %s = %.2f GBP", selectedCode, 1 / selectedRate));
            } else {
                titleText.setText("Convert GBP → " + selectedCode);
                rateInfo.setText(String.format("1 GBP = %.2f %s", selectedRate, selectedCode));
            }

            // Automatically re-run conversion if there’s input
            String input = gbpInput.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    double result;

                    if (isReversed) {
                        result = value / selectedRate;
                        resultText.setText(String.format("%.2f %s = %.2f GBP", value, selectedCode, result));
                    } else {
                        result = value * selectedRate;
                        resultText.setText(String.format("%.2f GBP = %.2f %s", value, result, selectedCode));
                    }
                } catch (NumberFormatException e) {
                    resultText.setText("Invalid input - please enter numbers only");
                }
            } else {
                resultText.setText("Enter amount");
            }
        });

        // --- Perform conversion ---
       /* btnEquals.setOnClickListener(v -> {
            String input = gbpInput.getText().toString().trim();
            if (input.isEmpty()) {
                resultText.setText("Enter amount");
                return;
            }

            try {
                double value = Double.parseDouble(input);
                double result;

                if (isReversed) {
                    result = value / selectedRate;
                    resultText.setText(String.format("%.2f %s = %.2f GBP", value, selectedCode, result));
                } else {
                    result = value * selectedRate;
                    resultText.setText(String.format("%.2f GBP = %.2f %s", value, result, selectedCode));
                }

            } catch (NumberFormatException e) {
                resultText.setText("Invalid input");
            }
        });*/
        btnEquals.setOnClickListener(v -> {
            try {
                String input = gbpInput.getText().toString().trim();
                if (input.isEmpty()) {
                    resultText.setText("Please enter an amount");
                    return;
                }

                double value = Double.parseDouble(input);
                double result;

                if (isSwapped) {
                    // Converting FROM target currency TO GBP
                    result = value / selectedRate;
                    resultText.setText(String.format("%.2f %s = %.2f GBP", value, selectedCode, result));
                } else {
                    // Converting FROM GBP TO target currency
                    result = value * selectedRate;
                    resultText.setText(String.format("%.2f GBP = %.2f %s", value, result, selectedCode));
                }

            } catch (NumberFormatException e) {
                resultText.setText("Invalid input");
            }
        });


        // --- Back button (return to list) ---
        //backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        //Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Always show the list fragment explicitly so rotation/back stack/order can't leave the Map visible.
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment(), "CurrencyList")
                    .commit();
        });
        // --- Map button (opens map screen) ---
        mapButton.setOnClickListener(v -> {
            CurrencyMapFragment mapFragment = new CurrencyMapFragment();

            Bundle args = new Bundle();
            args.putString("currencyCode", selectedCode); // pass selected currency code
            args.putDouble("currencyRate", selectedRate);
            mapFragment.setArguments(args);

            android.util.Log.d("MAP_DEBUG", "Passing currencyCode = " + selectedCode);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, mapFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // ---  Restore input and result after rotation ---
        if (savedInstanceState != null) {
            String savedInput = savedInstanceState.getString("inputAmount", "");
            String savedResult = savedInstanceState.getString("resultText", "");

            gbpInput.setText(savedInput);
            resultText.setText(savedResult);
        }

        return view;
    }

    // --- Save state so rotation keeps data ---
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (gbpInput != null) {
            outState.putString("inputAmount", gbpInput.getText().toString());
        }

        if (resultText != null) {
            outState.putString("resultText", resultText.getText().toString());
        }
    }

}
