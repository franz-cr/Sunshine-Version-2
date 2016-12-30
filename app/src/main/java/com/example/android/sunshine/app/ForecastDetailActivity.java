package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ForecastDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the UI to this activity
        setContentView(R.layout.activity_forecast_detail);

        Intent myIntent = getIntent(); //this.getIntent();
        String strForecast = myIntent.getStringExtra("com.example.android.sunshine.app");

        TextView myTextView = (TextView) findViewById(R.id.lblDetailedForecastData);
        myTextView.setText(strForecast);
/*
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(container, new PlaceholderFragment())
                    .commit();
        }
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.forecast_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //FUNCIONES:
        //Handle item selection
        switch (item.getItemId()) {
            case R.id.forecast_detail_menu_item1:
                //Toast variables
                int duration = Toast.LENGTH_LONG;
                String strForecast = "Seleccionó: " + item.getTitle();
                Context context = getApplicationContext();
                //Toast implementation
                Toast toast = Toast.makeText(context, strForecast, duration);
                toast.show();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends android.support.v4.app.Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_forecast_detail, container, false);
            return rootView;
        }
    }
}
