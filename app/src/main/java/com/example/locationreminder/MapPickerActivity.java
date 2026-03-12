package com.example.locationreminder;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        etSearch = findViewById(R.id.etSearch);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        // Search Button Logic
        findViewById(R.id.btnSearch).setOnClickListener(v -> geoLocate());

        // Keyboard "Search" action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                geoLocate();
                return true;
            }
            return false;
        });

        findViewById(R.id.btnConfirmLocation).setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat", selectedLatLng.latitude);
                resultIntent.putExtra("lon", selectedLatLng.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void geoLocate() {
        String searchString = etSearch.getText().toString();
        if (searchString.isEmpty()) return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocationName(searchString, 1);
            if (list.size() > 0) {
                Address address = list.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                selectedLatLng = latLng;
                findViewById(R.id.btnConfirmLocation).setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Ensures names/roads are visible

        // Default to Coimbatore
        LatLng coimbatore = new LatLng(11.0168, 76.9558);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coimbatore, 12f));

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Spot"));
            selectedLatLng = latLng;
            findViewById(R.id.btnConfirmLocation).setVisibility(View.VISIBLE);
        });
    }
}