/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>>,SharedPreferences.OnSharedPreferenceChangeListener  {

    private ListView earthquakeListView;
    private TextView earthquakeEmptyList;
    public static final String TAG = EarthquakeActivity.class.getName();
    private static final String JSON_URL = " https://earthquake.usgs.gov/fdsnws/event/1/query";
    private String URL = "";
    private String minMag = "";
    private String orderBy = "";
    SharedPreferences sharedPrefs;
    //?format=geojson&eventtype=earthquake&orderby=time&minmag=5&limit=10
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthquakeAdapter mEarthquakeAdapter;
   // private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

//        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
//        progressBar.setVisibility(View.VISIBLE);

        // Create a fake list of earthquake locations.


        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeEmptyList = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(earthquakeEmptyList);

        mEarthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(mEarthquakeAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake = mEarthquakeAdapter.getItem(position);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(webIntent);
            }
        });


        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        minMag = sharedPrefs.getString(getString(R.string.min_mag_key), getString(R.string.min_mag_default));

        orderBy = sharedPrefs.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_key_default)
        );

//        LoaderManager loaderManager= getLoaderManager();
//        // Create a new {@link ArrayAdapter} of earthquakes
//        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);


        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager= getLoaderManager();
//        // Create a new {@link ArrayAdapter} of earthquakes
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }else{
            View progressBar = findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.GONE);
            earthquakeEmptyList.setText(R.string.no_internet);
        }




        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface

    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {

        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        Uri baseUri = Uri.parse(JSON_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("eventtype","earthquake");
        uriBuilder.appendQueryParameter("orderby",orderBy);
        uriBuilder.appendQueryParameter("minmag",minMag);
        uriBuilder.appendQueryParameter("limit","10");

        URL = uriBuilder.toString();



        return new EarthquakeLoader(this, URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {

        View progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);
        earthquakeEmptyList.setText(R.string.no_earthquakes);

        mEarthquakeAdapter.clear();
        if(data != null && !data.isEmpty()) {
            Log.e("earthquake data", data.toString());
             mEarthquakeAdapter.addAll(data);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default: return false;
        }
        return true;
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mEarthquakeAdapter.clear();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        sharedPrefs = sharedPreferences;

        if(key.equals(getString(R.string.min_mag_key))){
            minMag = sharedPreferences.getString(key, getString(R.string.min_mag_default));
        }else if(key.equals(getString(R.string.order_by_key))){
            orderBy = sharedPreferences.getString(key, getString(R.string.order_by_key_default));
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoaderManager loaderManager= getLoaderManager();
//        // Create a new {@link ArrayAdapter} of earthquakes
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

}
