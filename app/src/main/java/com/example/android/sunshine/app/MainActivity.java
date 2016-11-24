package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //Populate ListView with hardcoded fake data

            //Create an array of Strings with the hardcoded data items
            String[] strFakeDataArray = {
                    "Hoy, lluvia, 23º / 18º",
                    "Mañana, tormentas, 25º/ 18º",
                    "Sábado, chubascos, 25º/ 19º",
                    "Domingo, tormentas, 24º/ 18º",
                    "Lunes, tormentas, 25º/ 17º",
                    "Martes, tormentas, 25º/ 17º"
            };
            //Create a String ArrayList and pass StringArray as a list
            List<String> strLstForecastData = new ArrayList<String>(Arrays.asList(strFakeDataArray));
            //Create ArrayAdapter with Strings
            ArrayAdapter<String> arrAdpForecastData = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    strLstForecastData);
           //Find ListView's ID
            ListView lstVwWeekForecast = (ListView)  rootView.findViewById(R.id.listview_forecast);
            //ListView lstVwWeekForecast = (ListView)container.findViewById(R.id.listview_forecast);
            //Attach ArrayAdapter to ListView control
            lstVwWeekForecast.setAdapter(arrAdpForecastData);

            return rootView;
        }
    }
}
