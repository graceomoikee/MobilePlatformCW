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
        TextView titleText = view.findViewById(R.id.titleText);
        TextView rateInfo = view.findViewById(R.id.rateInfo);
        EditText gbpInput = view.findViewById(R.id.gbpInput);
        TextView resultText = view.findViewById(R.id.resultText);
        Button btnSwap = view.findViewById(R.id.btnSwap);
        Button btnEquals = view.findViewById(R.id.btnEquals);
        Button backButton = view.findViewById(R.id.backButton);
        Button mapButton = view.findViewById(R.id.mapButton);

        // --- Display initial conversion info ---
        titleText.setText("Convert GBP → " + selectedCode);
        rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

        // --- Keyboard behaviour ---
        gbpInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        gbpInput.requestFocus();

        // --- Swap conversion direction ---
        btnSwap.setOnClickListener(v -> {
            isReversed = !isReversed;
            if (isReversed) {
                titleText.setText("Convert " + selectedCode + " → GBP");
                rateInfo.setText(String.format("1 %s = %.4f GBP", selectedCode, 1 / selectedRate));
                resultText.setText("Mode: " + selectedCode + " → GBP");
            } else {
                titleText.setText("Convert GBP → " + selectedCode);
                rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));
                resultText.setText("Mode: GBP → " + selectedCode);
            }
        });

        // --- Perform conversion ---
        btnEquals.setOnClickListener(v -> {
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
        });

        // --- Back button (return to list) ---
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());


        // --- Map button (opens map screen) ---
        mapButton.setOnClickListener(v -> {
            CurrencyMapFragment mapFragment = new CurrencyMapFragment();

            Bundle args = new Bundle();
            args.putString("currencyCode", selectedCode); // pass selected currency code
            mapFragment.setArguments(args);

            android.util.Log.d("MAP_DEBUG", "Passing currencyCode = " + selectedCode);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, mapFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }
}
