// java
package org.me.gcu.omoike_grace_s2125456.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
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
import java.util.Date;

public class CurrencyListFragment extends Fragment {

    private static final String KEY_LIST_STATE = "key_list_state";
    private static final String KEY_LAST_UPDATED = "key_last_updated";
    private static final String KEY_SELECTED_CODE = "key_selected_code";

    private ArrayList<CurrencyItem> currencyList = new ArrayList<>();
    private CurrencyAdapter adapter;
    private CurrencyViewModel viewModel;

    private ListView listView;
    private Handler handler;
    private final long REFRESH_INTERVAL_MS = 60_000L;
    private TextView lastUpdated;
    private Runnable refreshTask;
    private Parcelable listViewState;
    private String pendingSelectedCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currency_list, container, false);

        listView = view.findViewById(R.id.listView);
        SearchView searchView = view.findViewById(R.id.searchView);
        lastUpdated = view.findViewById(R.id.lastUpdatedText);

        adapter = new CurrencyAdapter(requireContext(), currencyList);
        listView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        // Summary cards - same IDs must exist in both layouts
        View usdCard = view.findViewById(R.id.usdCard);
        View eurCard = view.findViewById(R.id.eurCard);
        View jpyCard = view.findViewById(R.id.jpyCard);

        usdCard.setOnClickListener(v -> openConversion("USD"));
        eurCard.setOnClickListener(v -> openConversion("EUR"));
        jpyCard.setOnClickListener(v -> openConversion("JPY"));

        // Observe LiveData — will re-deliver current data after rotation
        viewModel.getCurrencyData().observe(getViewLifecycleOwner(), parsedCurrencies -> {
            currencyList.clear();
            currencyList.addAll(parsedCurrencies);
            adapter.updateData(parsedCurrencies);

            // Update top 3 summary cards (IDs must be consistent across layouts)
            for (CurrencyItem item : parsedCurrencies) {
                String code = item.getTargetCurrencyCode();
                if (code == null) continue;
                if (code.contains("(USD)"))
                    ((TextView) view.findViewById(R.id.usdRate)).setText(String.format("%.2f", item.getExchangeRate()));
                else if (code.contains("(EUR)"))
                    ((TextView) view.findViewById(R.id.eurRate)).setText(String.format("%.2f", item.getExchangeRate()));
                else if (code.contains("(JPY)"))
                    ((TextView) view.findViewById(R.id.jpyRate)).setText(String.format("%.2f", item.getExchangeRate()));
            }

            // restore saved list state after adapter has data
            if (listViewState != null) {
                listView.post(() -> {
                    listView.onRestoreInstanceState(listViewState);
                    listViewState = null;
                });
            }

            // If a card selection was requested before restore, notify activity
            if (pendingSelectedCode != null) {
                if (getActivity() instanceof OnCurrencySelectedListener) {
                    ((OnCurrencySelectedListener) getActivity()).onCurrencySelected(pendingSelectedCode, findRateForCode(pendingSelectedCode));
                }
                pendingSelectedCode = null;
            }
        });

        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { adapter.getFilter().filter(query); return false; }
            @Override public boolean onQueryTextChange(String newText) { adapter.getFilter().filter(newText); return false; }
        });

        // Item click → conversion
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            CurrencyItem selectedCurrency = (CurrencyItem) adapter.getItem(position);
            if (getActivity() instanceof OnCurrencySelectedListener) {
                ((OnCurrencySelectedListener) getActivity())
                        .onCurrencySelected(selectedCurrency.getTargetCurrencyCode(), selectedCurrency.getExchangeRate());
            }
        });

        // Handler + Runnable (guarded)
        handler = new Handler(Looper.getMainLooper());
        refreshTask = new Runnable() {
            @Override public void run() {
                if (!isAdded() || getView() == null) return;
                // Only load if needed — ViewModel keeps data across rotations
                viewModel.loadCurrencies();
                if (lastUpdated != null) {
                    String time = java.text.DateFormat.getTimeInstance().format(new Date());
                    lastUpdated.setText("Last updated: " + time);
                }
                Context ctx = getContext();
                if (ctx != null) Toast.makeText(ctx, "Exchange rates automatically refreshed", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        };

        // Initial load only if ViewModel has no data
        if (viewModel.getCurrencyData().getValue() == null || viewModel.getCurrencyData().getValue().isEmpty()) {
            viewModel.loadCurrencies();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null && refreshTask != null) {
            handler.removeCallbacks(refreshTask);
            handler.postDelayed(refreshTask, 10_000L);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && refreshTask != null) handler.removeCallbacks(refreshTask);
    }

    @Override
    public void onDestroyView() {
        if (handler != null && refreshTask != null) handler.removeCallbacks(refreshTask);
        lastUpdated = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save ListView scroll state
        if (listView != null) {
            Parcelable state = listView.onSaveInstanceState();
            outState.putParcelable(KEY_LIST_STATE, state);
        }
        // save last updated text
        if (lastUpdated != null) {
            outState.putString(KEY_LAST_UPDATED, lastUpdated.getText().toString());
        }
        // optional: save selected code if needed
        // outState.putString(KEY_SELECTED_CODE, currentlySelectedCode);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) return;
        // restore list state later (after adapter update)
        listViewState = savedInstanceState.getParcelable(KEY_LIST_STATE);
        String last = savedInstanceState.getString(KEY_LAST_UPDATED);
        if (last != null && lastUpdated != null) lastUpdated.setText(last);
        // pendingSelectedCode = savedInstanceState.getString(KEY_SELECTED_CODE); // if you saved selection
    }

    // Helper to find rate by code
    private double findRateForCode(String code) {
        for (CurrencyItem item : currencyList) {
            if (item.getTargetCurrencyCode() != null && item.getTargetCurrencyCode().contains(code)) {
                return item.getExchangeRate();
            }
        }
        return 0.0;
    }

    private void openConversion(String code) {
        double rate = findRateForCode(code);
        if (!isAdded()) {
            // Fragment not attached yet — remember to notify later inside observer
            pendingSelectedCode = code;
            return;
        }
        if (getActivity() instanceof OnCurrencySelectedListener) {
            ((OnCurrencySelectedListener) getActivity()).onCurrencySelected(code, rate);
        }
    }

    public interface OnCurrencySelectedListener {
        void onCurrencySelected(String code, double rate);
    }
}