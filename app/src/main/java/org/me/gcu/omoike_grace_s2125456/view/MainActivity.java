package org.me.gcu.omoike_grace_s2125456.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.omoike_grace_s2125456.R;
import org.me.gcu.omoike_grace_s2125456.model.CurrencyItem;
import org.me.gcu.omoike_grace_s2125456.viewmodel.CurrencyViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ArrayList<CurrencyItem> currencyList = new ArrayList<>();
    private CurrencyAdapter adapter;
    private ListView listView;
    private ViewSwitcher viewSwitcher;
    private double selectedRate = 0.0;
    private String selectedCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewSwitcher = findViewById(R.id.viewSwitcher);
        startButton = findViewById(R.id.startButton);
        listView = findViewById(R.id.listView);
        adapter = new CurrencyAdapter(this, currencyList);
        listView.setAdapter(adapter);

        //Initializse ViewModel
        CurrencyViewModel viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        //Observe LiveData for updates
        viewModel.getCurrencyData().observe(this, parsedCurrencies -> {
            currencyList.clear();
            currencyList.addAll(parsedCurrencies);
            adapter.updateData(parsedCurrencies);

            //Update top 3 summary rates
            for (CurrencyItem item : parsedCurrencies) {
                String code = item.getTargetCurrencyCode();
                if (code == null) continue;

                if (code.contains("(USD)"))
                    ((TextView) findViewById(R.id.usdRate)).setText(String.format("%.4f", item.getExchangeRate()));
                else if (code.contains("(EUR)"))
                    ((TextView) findViewById(R.id.eurRate)).setText(String.format("%.4f", item.getExchangeRate()));
                else if (code.contains("(JPY)"))
                    ((TextView) findViewById(R.id.jpyRate)).setText(String.format("%.2f", item.getExchangeRate()));
            }
        });

        //Fetch data when button clicked
        startButton.setOnClickListener(v -> viewModel.loadCurrencies());

        //ListView item click → open conversion screen
        listView.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyItem selectedCurrency = (CurrencyItem) adapter.getItem(position);
            selectedCode = selectedCurrency.getTargetCurrencyCode();
            selectedRate = selectedCurrency.getExchangeRate();

            TextView titleText = findViewById(R.id.titleText);
            TextView rateInfo = findViewById(R.id.rateInfo);
            titleText.setText("Convert GBP → " + selectedCode);
            rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

            viewSwitcher.showNext();
        });

        //Clickable summary cards
        setupSummaryCards();

        //Conversion controls
        setupConversionUI();

        //Search bar filter
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    //  Helper method for clickable top-3 summary cards
    private void setupSummaryCards() {
        LinearLayout usdCard = findViewById(R.id.usdCard);
        LinearLayout eurCard = findViewById(R.id.eurCard);
        LinearLayout jpyCard = findViewById(R.id.jpyCard);

        View.OnClickListener summaryClickListener = v -> {
            String code = "";
            double rate = 0.0;

            if (v.getId() == R.id.usdCard) code = "(USD)";
            else if (v.getId() == R.id.eurCard) code = "(EUR)";
            else if (v.getId() == R.id.jpyCard) code = "(JPY)";

            for (CurrencyItem item : currencyList) {
                if (item.getTargetCurrencyCode() != null && item.getTargetCurrencyCode().contains(code)) {
                    selectedRate = item.getExchangeRate();
                    selectedCode = item.getTargetCurrencyCode(); // Full text"
                    break;
                }
            }

            TextView titleText = findViewById(R.id.titleText);
            TextView rateInfo = findViewById(R.id.rateInfo);
            titleText.setText("Convert GBP → " + selectedCode);
            rateInfo.setText(String.format("1 GBP = %.4f %s", selectedRate, selectedCode));

            viewSwitcher.showNext();
        };

        usdCard.setOnClickListener(summaryClickListener);
        eurCard.setOnClickListener(summaryClickListener);
        jpyCard.setOnClickListener(summaryClickListener);
    }

    // Helper method for conversion UI
    private void setupConversionUI() {
        Button convertButton = findViewById(R.id.convertButton);
        Button reverseButton = findViewById(R.id.reverseButton);
        Button backButton = findViewById(R.id.backButton);
        EditText gbpInput = findViewById(R.id.gbpInput);
        TextView resultText = findViewById(R.id.resultText);

        // GBP to selected currency
        convertButton.setOnClickListener(v -> {
            String input = gbpInput.getText().toString();
            if (!input.isEmpty()) {
                double gbpValue = Double.parseDouble(input);
                double result = gbpValue * selectedRate;
                resultText.setText(String.format("%.2f GBP = %.2f %s", gbpValue, result, selectedCode));
            } else {
                resultText.setText("Please enter an amount.");
            }
        });

        // selected currency to GBP
        reverseButton.setOnClickListener(v -> {
            String input = gbpInput.getText().toString();
            if (!input.isEmpty()) {
                double foreignValue = Double.parseDouble(input);
                double result = foreignValue / selectedRate;
                resultText.setText(String.format("%.2f %s = %.2f GBP", foreignValue, selectedCode, result));
            } else {
                resultText.setText("Please enter an amount.");
            }
        });

        backButton.setOnClickListener(v -> viewSwitcher.showPrevious());
    }
}
