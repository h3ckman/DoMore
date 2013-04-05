package com.team3.domore;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class Nearby extends FragmentActivity {

	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);

		Intent i = getIntent();
		PlacesList nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).getMap();

		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean enabledWiFi = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!enabledGPS) {
			Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG)
			.show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		map.setMyLocationEnabled(true);
		map.getMyLocation();
		
		TrackGPS gps = new TrackGPS(this);
		
		/**
		 * Display red marker for user location
		 */
		LatLng myLoc = new LatLng(gps.getLatitude(), gps.getLongitude());
		map.addMarker(new MarkerOptions()
		.position(myLoc)
		.title("You Are Here")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 13));
		
		/**
		 * Display blue marker for each place
		 */
		for (Place p : nearPlaces.results) {
			LatLng placeLoc = new LatLng(p.geometry.location.lat, p.geometry.location.lng);	
			map.addMarker(new MarkerOptions()
			.position(placeLoc)
			.title(p.name)
			.snippet(p.vicinity)
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		}
	}
}
