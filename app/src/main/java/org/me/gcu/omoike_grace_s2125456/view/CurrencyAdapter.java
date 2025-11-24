package org.me.gcu.omoike_grace_s2125456.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.me.gcu.omoike_grace_s2125456.R;
import org.me.gcu.omoike_grace_s2125456.model.CurrencyItem;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private final ArrayList<CurrencyItem> originalList; // all currencies
    private ArrayList<CurrencyItem> filteredList;       // visible items

    public CurrencyAdapter(Context context, ArrayList<CurrencyItem> currencies) {
        this.context = context;
        this.originalList = currencies;
        this.filteredList = new ArrayList<>(currencies);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // -------------------------
    //  COUNTRY EXTRACTION LOGIC
    // -------------------------
    private String extractCountry(String fullName) {
        if (fullName == null) return "";

        // Remove (XXX)
        int idx = fullName.lastIndexOf("(");
        if (idx > 0) {
            fullName = fullName.substring(0, idx).trim();
        }

        // Remove last word (currency name)
        String[] parts = fullName.split(" ");
        if (parts.length > 1) {
            StringBuilder country = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                country.append(parts[i]).append(" ");
            }
            return country.toString().trim();
        }

        return fullName.trim();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        CurrencyItem item = filteredList.get(position);

        TextView code = convertView.findViewById(R.id.currencyCode);
        TextView rate = convertView.findViewById(R.id.currencyRate);
        Button btnMap = convertView.findViewById(R.id.btnMap);
        Button btnConvert = convertView.findViewById(R.id.btnConvert);

        code.setText(item.getTargetCurrencyCode());
        rate.setText(String.format("%.2f", item.getExchangeRate()));

        // --- Colour coding ---
        double rateValue = item.getExchangeRate();
        convertView.setBackgroundColor(Color.WHITE);
        if (rateValue < 1.0) {
            convertView.setBackgroundColor(Color.parseColor("#E0F7FA"));  // light blue
        } else if (rateValue < 5.0) {
            convertView.setBackgroundColor(Color.parseColor("#FFF9C4"));  // light yellow
        } else if (rateValue < 10.0) {
            convertView.setBackgroundColor(Color.parseColor("#FFE0B2"));  // light orange
        } else {
            convertView.setBackgroundColor(Color.parseColor("#FFCDD2"));  // light red
        }

        // --- Button actions ---
        /*btnMap.setOnClickListener(v -> {
            if (context instanceof OnCurrencyActionListener) {
                ((OnCurrencyActionListener) context)
                        .onShowMap(item.getTargetCurrencyCode());
            }
        });*/
        btnMap.setOnClickListener(v -> {
            if (context instanceof OnCurrencyActionListener) {
                ((OnCurrencyActionListener) context)
                        .onShowMap(
                                item.getTargetCurrencyCode(),
                                item.getExchangeRate()      // ⬅ pass rate as well
                        );
            }
        });


        btnConvert.setOnClickListener(v -> {
            if (context instanceof OnCurrencyActionListener) {
                ((OnCurrencyActionListener) context)
                        .onConvert(item.getTargetCurrencyCode(), item.getExchangeRate());
            }
        });

        return convertView;
    }

    // -------------------------
    //        FILTERING
    // -------------------------
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<CurrencyItem> tempList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    tempList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (CurrencyItem item : originalList) {

                        String name = item.getCurrencyName() != null ? item.getCurrencyName().toLowerCase() : "";
                        String code = item.getTargetCurrencyCode() != null ? item.getTargetCurrencyCode().toLowerCase() : "";
                        String country = extractCountry(item.getCurrencyName()).toLowerCase();

                        if (name.contains(filterPattern)
                                || code.contains(filterPattern)
                                || country.contains(filterPattern)) {
                            tempList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = tempList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<CurrencyItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    // Refresh data from ViewModel
    public void updateData(ArrayList<CurrencyItem> newList) {
        originalList.clear();
        originalList.addAll(newList);
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
