package org.me.gcu.omoike_grace_s2125456.view.fragments;

import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

public class CurrencyMapFragment extends Fragment implements OnMapReadyCallback {

    private String currencyCode;

    private static final Map<String, LatLng> countryMap = new HashMap<>();
    static {
        countryMap.put("USD", new LatLng(38.9, -77.0));   // Washington DC
        countryMap.put("EUR", new LatLng(50.1, 8.6));     // Frankfurt
        countryMap.put("JPY", new LatLng(35.7, 139.7));   // Tokyo
        countryMap.put("CNY", new LatLng(39.9, 116.4));   // Beijing
        countryMap.put("INR", new LatLng(28.6, 77.2));    // New Delhi
        countryMap.put("BRL", new LatLng(-15.8, -47.9));  // Brasília
        countryMap.put("GBP", new LatLng(51.5, -0.1));    // London
    }

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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(0, 0); // default
        String code = currencyCode != null ? currencyCode : "";
        android.util.Log.d("MAP_DEBUG", "Currency code passed: " + currencyCode);

        for (Map.Entry<String, LatLng> entry : countryMap.entrySet()) {
            if (code.contains(entry.getKey())) {  // allow partial match like "(GBP)"
                location = entry.getValue();
                break;
            }
        }

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Currency: " + code));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 4f));
    }

}
