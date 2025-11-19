
package org.me.gcu.omoike_grace_s2125456.view.fragments;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.omoike_grace_s2125456.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CurrencyMapFragment extends Fragment implements OnMapReadyCallback {

    private String currencyCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            currencyCode = getArguments().getString("currencyCode");
    }

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view,
                              @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        //  Add navigation buttons
        Button btnBackToList = view.findViewById(R.id.btnBackToList);
        Button btnBackToConvert = view.findViewById(R.id.btnBackToConvert);

        //Button btnBackToConvert = view.findViewById(R.id.btnBackToConvert);
        if (btnBackToConvert != null) {
            btnBackToConvert.setOnClickListener(v -> {
                String code = currencyCode;
                if (code != null && code.contains("(") && code.contains(")")) {
                    code = code.substring(code.indexOf("(") + 1, code.indexOf(")"));
                }
                // Delegate to activity to open conversion fragment (keeps navigation consistent)
                if (requireActivity() instanceof org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment.OnCurrencySelectedListener) {
                    ((org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment.OnCurrencySelectedListener) requireActivity())
                            .onCurrencySelected(code, 0.0);
                } else {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        // Back to currency list
        btnBackToList.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer,
                            new org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment(),
                            "CurrencyList")
                    .commit();
        });

        // Convert button: explicitly ask the Activity to open the conversion screen
        if (btnBackToConvert != null) {
            btnBackToConvert.setOnClickListener(v -> {
                // parse ISO code from strings like "Euro (EUR)"
                String code = currencyCode;
                if (code != null && code.contains("(") && code.contains(")")) {
                    code = code.substring(code.indexOf("(") + 1, code.indexOf(")"));
                }

                // If the activity implements the listener, delegate to it (so it opens conversion)
                if (requireActivity() instanceof org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment.OnCurrencySelectedListener) {
                    org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment.OnCurrencySelectedListener listener =
                            (org.me.gcu.omoike_grace_s2125456.view.fragments.CurrencyListFragment.OnCurrencySelectedListener) requireActivity();
                    // pass 0.0 for rate if you don't have it here; MainActivity can open conversion UI and fetch/update rate
                    listener.onCurrencySelected(code, 0.0);
                } else {
                    // fallback: pop back stack (existing behavior)
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            String key = requireContext().getPackageManager()
                    .getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString("com.google.android.geo.API_KEY");
            Log.d("MAP_KEY_DEBUG", "Loaded API key: " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String code = currencyCode;
        if (code != null && code.contains("(") && code.contains(")")) {
            code = code.substring(code.indexOf("(") + 1, code.indexOf(")"));
        }

        LatLng location = new LatLng(0, 0);
        String countryName = getCountryNameFromCode(code);

        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> results = geocoder.getFromLocationName(countryName, 1);
            if (results != null && !results.isEmpty()) {
                Address address = results.get(0);
                location = new LatLng(address.getLatitude(), address.getLongitude());
                Log.d("MAP_DEBUG", "Found " + countryName + " → " + location);
            } else {
                Log.d("MAP_DEBUG", "No location found for " + countryName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Currency: " + code));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 4f));
    }

    private String getCountryNameFromCode(String code) {
        try {
            java.util.Currency currency = java.util.Currency.getInstance(code);
            java.util.Locale locale = null;
            for (java.util.Locale l : java.util.Locale.getAvailableLocales()) {
                try {
                    if (java.util.Currency.getInstance(l).equals(currency)) {
                        locale = l;
                        break;
                    }
                } catch (Exception ignored) {}
            }
            if (locale != null) {
                return locale.getDisplayCountry();
            }
        } catch (Exception ignored) {}

        Map<String, String> fallback = new HashMap<>();
        fallback.put("ANG", "Netherlands Antilles");
        fallback.put("XCD", "Saint Lucia");
        fallback.put("XOF", "Senegal");
        fallback.put("XAF", "Cameroon");
        fallback.put("BBD", "Barbados");
        return fallback.getOrDefault(code, code);
    }
}