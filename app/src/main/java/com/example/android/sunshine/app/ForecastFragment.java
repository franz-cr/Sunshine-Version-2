package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ulrichca on 25/11/2016.
 */

public class ForecastFragment extends android.support.v4.app.Fragment {

    //VARIABLES GLOBALES
    protected ArrayAdapter<String> arrAdpForecastData;

    //FUNCIONES DE LA CLASE
    public ForecastFragment() {
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

        //This command merges the menu items in this xml with the menu items in main options menu.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem fragmentItem) {
        //VARIABLES:
        Context context = this.getActivity().getApplicationContext();
        String strPostalCode = "94043";
        String strForecast;
        int duration = Toast.LENGTH_LONG;

        //FUNCIONES:
        //Handle item selection
        switch (fragmentItem.getItemId()) {
            case R.id.action_refresh:
                //Get location location preference value:
                SharedPreferences prfSunshinePrefs = PreferenceManager.getDefaultSharedPreferences(
                        context);
                String strLocationKey = getString(R.string.edit_text_location_key);
                String strLocationDefault = getString(R.string.edit_text_location_default);
                String strLocationValue = prfSunshinePrefs.getString(strLocationKey, strLocationDefault);
                //Unit Test: Toast showing fetched postal code
                strForecast = "Forecast postal code: " + strLocationValue;
                Toast toast = Toast.makeText(context, strForecast, duration);
                toast.show();
                //Call the OWM REST API using the preferred location and parse the JSON string
                FetchWeatherTask weatherRefreshTask = new FetchWeatherTask();
                weatherRefreshTask.execute(strLocationValue);
                return true;
            case R.id.action_fragment_item:
                //Toast variables
                strForecast = "Seleccionó: " + fragmentItem.getTitle();
                //Toast implementation
                Toast toast2 = Toast.makeText(context, strForecast, duration);
                toast2.show();
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
        //Create ArrayAdapter of Strings
        arrAdpForecastData = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                strLstForecastData);
        //Find ListView's ID
        ListView lstVwWeekForecast = (ListView) rootView.findViewById(R.id.listview_forecast);

        //Attach ArrayAdapter to ListView control
        lstVwWeekForecast.setAdapter(arrAdpForecastData);

        //Added an item click listener to the ListView
        lstVwWeekForecast.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String strForecast = arrAdpForecastData.getItem(position);
                        String strKey = "com.example.android.sunshine.app";

                        Intent detailedForecastIntent = new Intent(getActivity().getApplicationContext(),
                                ForecastDetailActivity.class);
                        //Implement item's click feature here.
                        //Pass clicked day forecast data to new Activity

                        detailedForecastIntent.putExtra(strKey, strForecast);
                        getActivity().startActivity(detailedForecastIntent);
                    }
                }
        );

        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        //VARIABLES GLOBALES DE SUBCLASE
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        final String STR_QUERY_PARAM = "q";
        final String STR_MODE_PARAM = "mode";
        final String STR_UNITS_PARAM = "units";
        final String STR_DAYS_PARAM = "cnt";
        final String STR_APPID_PARAM = "APPID";
        private Uri UriBaseUrl = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily");
        private Integer intForecastDays = 7;
        private String strMode = "json";
        private String strUnits = "metric";
        private String strCount = intForecastDays.toString();
        private String strAppID = "5d7c1591368cffcc1624a4f0e2b5b630";
        private Uri.Builder uriOpenWeatherMap = new Uri.Builder();
        // Will contain the raw JSON response as a string.
        public String strJsonForecast;

        public String tempRoundUpDown (Double dblTemperature, Boolean blnRoundUp) {
            //VARIABLES E INICIALIZACIONES:

            //FUNCIONES:
            if (blnRoundUp)
                dblTemperature = Math.ceil(dblTemperature);
            else
                dblTemperature = Math.floor(dblTemperature);

            return dblTemperature.toString();
        }

        protected String buildForecastDay(int intDayIndex, String strCloudForecast, double dblMaxTemp,
                                          double dblMinTemp) {
            //VARIABLES E INICIALIZACIONES:
            StringBuilder strBldDayForecast = new StringBuilder();
            String strDayForecast = "";

            //PROCESO:

            //Get date in long format
            strBldDayForecast.append(getForecastDayName(intDayIndex));
            strBldDayForecast.append("; ");

            //Load clouds status data
            strBldDayForecast.append(strCloudForecast);
            strBldDayForecast.append("; ");

            //Load day data array with the JSON extracted data
            strBldDayForecast.append(tempRoundUpDown(dblMaxTemp, Boolean.TRUE));
            strBldDayForecast.append("ºC/ ");
            strBldDayForecast.append(tempRoundUpDown(dblMinTemp, Boolean.FALSE));
            strBldDayForecast.append("ºC");

            strDayForecast = strBldDayForecast.toString();

            return strDayForecast;
        }

        protected String getForecastDayName(int intDayIndex) {
            //VARIABLES E INICIALIZACIONES:
            String strForecastDayName = "";
            Calendar calForecastDay = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
            SimpleDateFormat dtFmtShortDayDate = new SimpleDateFormat("EEEE, MMMM dd, yyyy",
                    Locale.getDefault());

            //PROCESO:
            calForecastDay.add(Calendar.DAY_OF_YEAR,intDayIndex);
            strForecastDayName = dtFmtShortDayDate.format(calForecastDay.getTime());

            return strForecastDayName;
        }

        public String[] parseJsonString(Integer intDaysCount) throws JSONException {
            //VARIABLES E INICIALIZACIONES:
            final String LOG_TAG = ForecastFragment.class.getSimpleName();
            String[] strDaysFormattedData = new String[intDaysCount];
            Integer intHumidity, intWeatherID, intClouds, intDegrees;
            Long lngUnixDate;
            Double dblMaxTemp, dblMinTemp, dblDayTemp, dblNightTemp, dblMorningTemp, dblEveningTemp,
                    dblPressure, dblWindSpeed;
            String strWeatherMain, strWeatherDescription, strWeatherIcon;

            //PROCESO:
            //Declare JSON Object and pass string with JSON data
            JSONObject objJsonForecast = new JSONObject(strJsonForecast);
            //Declare JSON data array
            JSONArray arrJsonForecast = objJsonForecast.getJSONArray("list");

            //Parse retrieved days
            for (int i = 0; i < intDaysCount; i++) {
                //Extract into a new JSON object first day forecast data:
                JSONObject objJsonDayForecast = arrJsonForecast.getJSONObject(i);

                //Extract data of selected day
                lngUnixDate = objJsonDayForecast.getLong("dt");
                dblPressure = objJsonDayForecast.getDouble("pressure");
                intHumidity = objJsonDayForecast.getInt("humidity");
                dblWindSpeed = objJsonDayForecast.getDouble("speed");
                intDegrees = objJsonDayForecast.getInt("deg");
                intClouds = objJsonDayForecast.getInt("clouds");

                //Extract into new JSON object day's temperatures data
                JSONObject objJsonDayTemps = objJsonDayForecast.getJSONObject("temp");
                //Extract requested day temps data from temps object
                dblDayTemp = objJsonDayTemps.getDouble("day");
                dblMinTemp = objJsonDayTemps.getDouble("min");
                dblMaxTemp = objJsonDayTemps.getDouble("max");
                dblNightTemp = objJsonDayTemps.getDouble("night");
                dblEveningTemp = objJsonDayTemps.getDouble("eve");
                dblMorningTemp = objJsonDayTemps.getDouble("morn");

                //Extract into a JSON array day's first weather data
                JSONArray arrJsonWeather = objJsonDayForecast.getJSONArray("weather");
                JSONObject objJsonDayWeather = arrJsonWeather.getJSONObject(0);
                intWeatherID = objJsonDayWeather.getInt("id");
                strWeatherMain = objJsonDayWeather.getString("main");
                strWeatherDescription = objJsonDayWeather.getString("description");
                strWeatherIcon = objJsonDayWeather.getString("icon");

                //Load String array with day's forecast data
                strDaysFormattedData[i] = buildForecastDay(i, strWeatherDescription, dblMaxTemp,
                        dblMinTemp);
                }
             return strDaysFormattedData;
        }

        @Override
        protected String[] doInBackground(String... strPostalCode) {
            //TODO: Cambiar tipo del método 'Void' a 'String[]'

            //VARIABLES GLOBALES DE FUNCIÓN
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //PROCESO:
            //Checking that postal code array is not empty (has at least 1 element)
            if (strPostalCode.length == 0)
                return null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                uriOpenWeatherMap = UriBaseUrl.buildUpon()
                    .appendQueryParameter(STR_QUERY_PARAM, strPostalCode[0])
                    .appendQueryParameter(STR_MODE_PARAM, strMode)
                    .appendQueryParameter(STR_UNITS_PARAM, strUnits)
                    .appendQueryParameter(STR_DAYS_PARAM, strCount)
                    .appendQueryParameter(STR_APPID_PARAM, strAppID);
                //Pass built Uri to URL object
                URL urlOpenWeatherMap = new URL(uriOpenWeatherMap.build().toString());

                //Prints out into the log the query URL. Diabled in lesson 2.9.
                //Log.i(LOG_TAG + " - urlOpenWeatherMap", urlOpenWeatherMap.toString());

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

                //Capturing the JSON string to Global string variable
                strJsonForecast = buffer.toString();

                //Prints into the log the returned Json string. Diabled in lesson 2.9.
                //Log.i(LOG_TAG + " - strJsonForecast", strJsonForecast);

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

            //Parse JSON object returned
            if (strJsonForecast.length() > 0) {
                try {
                    return parseJsonString(intForecastDays);
                }
                catch (JSONException e) {
                    Log.e(LOG_TAG, e.toString());
                    /**
                     //Toast exception error:
                     Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                     */
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strResult) {

            if (strResult.length > 0) {
                arrAdpForecastData.clear();
                //For each String in String array, add the string item to the Array Adapter.
                for (String strForecastDayData : strResult) {
                    arrAdpForecastData.add(strForecastDayData);
                }
                //super.onPostExecute(result);
            }
        }
    }
}

