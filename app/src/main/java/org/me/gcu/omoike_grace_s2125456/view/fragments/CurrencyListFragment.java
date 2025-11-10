package org.me.gcu.omoike_grace_s2125456.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.omoike_grace_s2125456.R;
import org.me.gcu.omoike_grace_s2125456.model.CurrencyItem;
import org.me.gcu.omoike_grace_s2125456.view.CurrencyAdapter;
import org.me.gcu.omoike_grace_s2125456.viewmodel.CurrencyViewModel;

import java.util.ArrayList;

public class CurrencyListFragment extends Fragment {

    private ArrayList<CurrencyItem> currencyList = new ArrayList<>();
    private CurrencyAdapter adapter;
    private CurrencyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currency_list, container, false);

        ListView listView = view.findViewById(R.id.listView);
        Button startButton = view.findViewById(R.id.startButton);
        SearchView searchView = view.findViewById(R.id.searchView);

        adapter = new CurrencyAdapter(requireContext(), currencyList);
        listView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        // Observe ViewModel
        viewModel.getCurrencyData().observe(getViewLifecycleOwner(), parsedCurrencies -> {
            currencyList.clear();
            currencyList.addAll(parsedCurrencies);
            adapter.updateData(parsedCurrencies);

            // Update top 3 summary cards
            for (CurrencyItem item : parsedCurrencies) {
                String code = item.getTargetCurrencyCode();
                if (code == null) continue;

                if (code.contains("(USD)"))
                    ((TextView) view.findViewById(R.id.usdRate)).setText(String.format("%.4f", item.getExchangeRate()));
                else if (code.contains("(EUR)"))
                    ((TextView) view.findViewById(R.id.eurRate)).setText(String.format("%.4f", item.getExchangeRate()));
                else if (code.contains("(JPY)"))
                    ((TextView) view.findViewById(R.id.jpyRate)).setText(String.format("%.2f", item.getExchangeRate()));
            }
        });

        startButton.setOnClickListener(v -> viewModel.loadCurrencies());

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

        // Handle ListView item clicks → go to conversion screen
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            CurrencyItem selectedCurrency = (CurrencyItem) adapter.getItem(position);

            // Notify MainActivity to open the conversion fragment
            if (getActivity() instanceof OnCurrencySelectedListener) {
                ((OnCurrencySelectedListener) getActivity())
                        .onCurrencySelected(selectedCurrency.getTargetCurrencyCode(), selectedCurrency.getExchangeRate());
            }
        });

        return view;
    }

    // Interface for communicating with MainActivity
    public interface OnCurrencySelectedListener {
        void onCurrencySelected(String code, double rate);
    }
}
