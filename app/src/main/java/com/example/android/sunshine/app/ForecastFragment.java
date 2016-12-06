package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ulrichca on 25/11/2016.
 */

public class ForecastFragment extends android.support.v4.app.Fragment {

    //VARIABLES GLOBALES
    private ArrayAdapter<String> arrAdpForecastData;

    //FUNCIONES DE LA CLASE
    public ForecastFragment() {
    }

    public void parseJsonString(String strJsonForecast) {
        //VARIABLES:
        final String LOG_TAG = ForecastFragment.class.getSimpleName();
        Double dblMaxTemp, dblMinTemp, dlbDayTemp;
        Integer intDayIndex = 0;

        //FUNCIONES:
        try {
            //Declare JSON Object and pass string with JSON data
            JSONObject objJsonForecast = new JSONObject(strJsonForecast);
            //Declare JSON data array
            JSONArray arrJsonForecast = objJsonForecast.getJSONArray("list");
            //Extract into a new JSON object first day forecast data:
            JSONObject objJsonDayForecast = arrJsonForecast.getJSONObject(intDayIndex);
            //Extract into new JSON object day temperatures
            JSONObject objJsonDayTemps = objJsonDayForecast.getJSONObject("temp");
            //Extract into strings day's temp data
            dblMinTemp = objJsonDayTemps.getDouble("min");
            dlbDayTemp = objJsonDayTemps.getDouble("day");
            dblMaxTemp = objJsonDayTemps.getDouble("max");

            //return dblMaxTemp;
            //
            //ListView lstVwWeekForecast = (ListView) R.id.listview_forecast;
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.toString());
 /**        //Toast exception error:
            Toast.makeText(getApplicationContext(),
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
                    */
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Calls the parent method
        super.onCreate(savedInstanceState);

        //This settings will create call backs to the 'onCreateOptionsMenu' and
        //'onOptionsItemsSelected' method for the fragment after executing the
        //methods in the main activity.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem fragmentItem) {
        //VARIABLES:
        String strPostalCode = "94043";
        //Handle item selection
        switch (fragmentItem.getItemId()) {
            case R.id.action_refresh:
                //Implementation for the 'Refresh' button
                FetchWeatherTask weatherRefreshTask = new FetchWeatherTask();
                weatherRefreshTask.execute(strPostalCode);
                parseJsonString(weatherRefreshTask.strJsonForecast);
                return true;
            default:
                return super.onOptionsItemSelected(fragmentItem);
        }
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
        arrAdpForecastData = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                strLstForecastData);
        //Find ListView's ID
        ListView lstVwWeekForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        //ListView lstVwWeekForecast = (ListView)container.findViewById(R.id.listview_forecast);
        //Attach ArrayAdapter to ListView control
        lstVwWeekForecast.setAdapter(arrAdpForecastData);


        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        //VARIABLES GLOBALES DE CLASE
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private final String STR_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String STR_QUERY_PARAM = "q";
        final String STR_MODE_PARAM = "mode";
        final String STR_UNITS_PARAM = "units";
        final String STR_DAYS_PARAM = "cnt";
        final String STR_APPID_PARAM = "APPID";
        private Uri UriBaseUrl = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily");
        private String strMode = "json";
        private String strUnits = "metric";
        private String strCount = "7";
        private String strAppID = "5d7c1591368cffcc1624a4f0e2b5b630";
        private Uri.Builder uriOpenWeatherMap = new Uri.Builder();
        public String strJsonForecast;

        @Override
        protected Void doInBackground(String... strPostalCode) {

            //VARIABLES GLOBALES DE FUNCIÓN
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                // Initial approach: manual hardcoded URL
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" +
                //        strPostalCode[0] + "&mode=json&units=metric&cnt=7";
                //String appKey = "&APPID=5d7c1591368cffcc1624a4f0e2b5b630";
                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //Pass hardcoded string Url to URL object
                //URL urlOpenWeatherMap = new URL(baseUrl.concat(appKey));

                // Best approach: using Uri and Uri.Builder classes to build the URL.
                uriOpenWeatherMap = UriBaseUrl.buildUpon();
                uriOpenWeatherMap.appendQueryParameter(STR_QUERY_PARAM, strPostalCode[0]);
                uriOpenWeatherMap.appendQueryParameter(STR_MODE_PARAM, strMode);
                uriOpenWeatherMap.appendQueryParameter(STR_UNITS_PARAM, strUnits);
                uriOpenWeatherMap.appendQueryParameter(STR_DAYS_PARAM, strCount);
                uriOpenWeatherMap.appendQueryParameter(STR_APPID_PARAM, strAppID);

                uriOpenWeatherMap = UriBaseUrl.buildUpon()
                    .appendQueryParameter(STR_QUERY_PARAM, strPostalCode[0])
                    .appendQueryParameter(STR_MODE_PARAM, strMode)
                    .appendQueryParameter(STR_UNITS_PARAM, strUnits)
                    .appendQueryParameter(STR_DAYS_PARAM, strCount)
                    .appendQueryParameter(STR_APPID_PARAM, strAppID);
                //Pass built Uri to URL object
                URL urlOpenWeatherMap = new URL(uriOpenWeatherMap.build().toString());


                //Prints out into the log the query URL
                Log.i(LOG_TAG + " - urlOpenWeatherMap", urlOpenWeatherMap.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) urlOpenWeatherMap.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //Capturing the JSON string
                forecastJsonStr = buffer.toString();

                //Passing the JSON string to Global string variable
                strJsonForecast = buffer.toString();

                //Prints into the log the returned Json string
                Log.i(LOG_TAG + " - strJsonForecast", strJsonForecast);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }

    /*
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=5d7c1591368cffcc1624a4f0e2b5b630
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=5d7c1591368cffcc1624a4f0e2b5b630");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("ForecastFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ForecastFragment", "Error closing stream", e);
                }
            }
        }
    */
}

