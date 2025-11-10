package org.me.gcu.omoike_grace_s2125456.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        CurrencyItem item = filteredList.get(position);

        TextView code = convertView.findViewById(R.id.currencyCode);
        TextView rate = convertView.findViewById(R.id.currencyRate);

        code.setText(item.getTargetCurrencyCode());
        rate.setText(String.format("%.4f", item.getExchangeRate()));

        // Colour coding
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

        return convertView;
    }

    // Filter logic
    /*@Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = new ArrayList<>(originalList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    List<CurrencyItem> filtered = new ArrayList<>();
                    for (CurrencyItem item : originalList) {
                        if (item.getTargetCurrencyCode().toLowerCase().contains(query)
                                || (item.getTitle() != null && item.getTitle().toLowerCase().contains(query))) {
                            filtered.add(item);
                        }
                    }
                    results.values = filtered;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<CurrencyItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }*/

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = new ArrayList<>(originalList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    List<CurrencyItem> filtered = new ArrayList<>();

                    for (CurrencyItem item : originalList) {
                        String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";
                        String code = item.getTargetCurrencyCode() != null ? item.getTargetCurrencyCode().toLowerCase() : "";

                        // ✅ Match currency name (Dollar, Yuan, Yen, etc.)
                        if (title.contains(query) || code.contains(query)) {
                            filtered.add(item);
                            continue;
                        }

                        //  Match by derived country
                        if (title.contains("chinese") && query.contains("china")) filtered.add(item);
                        else if (title.contains("japanese") && query.contains("japan")) filtered.add(item);
                        else if (title.contains("british") && (query.contains("uk") || query.contains("united kingdom"))) filtered.add(item);
                        else if (title.contains("american") && (query.contains("usa") || query.contains("united states") || query.contains("america"))) filtered.add(item);
                        else if (title.contains("korean") && query.contains("korea")) filtered.add(item);
                        else if (title.contains("indian") && query.contains("india")) filtered.add(item);
                        else if (title.contains("mexican") && query.contains("mexico")) filtered.add(item);
                        else if (title.contains("brazilian") && query.contains("brazil")) filtered.add(item);
                        else if (title.contains("russian") && query.contains("russia")) filtered.add(item);
                    }

                    results.values = filtered;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<CurrencyItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }




    // Refresh data from MainActivity
    public void updateData(ArrayList<CurrencyItem> newList) {
        originalList.clear();
        originalList.addAll(newList);
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
