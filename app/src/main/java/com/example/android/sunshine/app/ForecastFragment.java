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
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem fragmentItem) {
        //VARIABLES:
        String strPostalCode = "94043";
        String[] _strDayData = new String[6];
        boolean blnJSONsimulate = true;

        //FUNCIONES:
        //Handle item selection
        switch (fragmentItem.getItemId()) {
            case R.id.action_refresh:
                FetchWeatherTask weatherRefreshTask = new FetchWeatherTask();
                weatherRefreshTask.execute(strPostalCode);
/*                if (blnJSONsimulate)
                    _strDayData[0] = getForecastDayName(0);
                    //_strDayData = parseJsonString(simulateJsonString(), 0);
                else {
                    //Implementation for the 'Refresh' button
                    //Passing JSON  string to parsing function while 'strJsonForecast' string
                    //is still available in memory.
                    _strDayData = parseJsonString(weatherRefreshTask.strJsonForecast, 0);
                } */
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

        protected String simulateJsonString () {
            return "{\"city\":{\"id\":5375480,\"name\":\"Mountain View\",\"coord\":{\"lon\":-122.083847,\"lat\":37.386051},\"country\":\"US\",\"population\":0},\"cod\":\"200\",\"message\":0.3816,\"cnt\":7,\"list\":[{\"dt\":1480705200,\"temp\":{\"day\":13.15,\"min\":5.48,\"max\":14.65,\"night\":5.48,\"eve\":12.65,\"morn\":9.49},\"pressure\":997.17,\"humidity\":53,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":3.01,\"deg\":359,\"clouds\":0},{\"dt\":1480791600,\"temp\":{\"day\":11.38,\"min\":0.37,\"max\":16.15,\"night\":3.14,\"eve\":14.74,\"morn\":0.75},\"pressure\":1000.21,\"humidity\":67,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.24,\"deg\":41,\"clouds\":0},{\"dt\":1480878000,\"temp\":{\"day\":10.58,\"min\":-0.12,\"max\":15.4,\"night\":8.21,\"eve\":13.78,\"morn\":0.48},\"pressure\":996.01,\"humidity\":68,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":0.39,\"deg\":24,\"clouds\":0},{\"dt\":1480964400,\"temp\":{\"day\":10.37,\"min\":8.59,\"max\":11.51,\"night\":9.84,\"eve\":11.51,\"morn\":8.59},\"pressure\":1013.19,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":5.41,\"deg\":334,\"clouds\":0},{\"dt\":1481054400,\"temp\":{\"day\":11.18,\"min\":7.23,\"max\":11.87,\"night\":7.23,\"eve\":11.87,\"morn\":8.62},\"pressure\":1014.36,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.21,\"deg\":314,\"clouds\":55,\"rain\":1.43},{\"dt\":1481140800,\"temp\":{\"day\":9.22,\"min\":5.17,\"max\":9.98,\"night\":9.11,\"eve\":9.98,\"morn\":5.17},\"pressure\":1019.41,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":3.26,\"deg\":150,\"clouds\":88,\"rain\":1.72},{\"dt\":1481227200,\"temp\":{\"day\":10.82,\"min\":9.24,\"max\":12.67,\"night\":12.67,\"eve\":12.63,\"morn\":9.24},\"pressure\":1014.78,\"humidity\":0,\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"speed\":8.29,\"deg\":170,\"clouds\":100,\"rain\":27.67}]}";
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

            //Load day data array with the JSON extracted data
            strBldDayForecast.append(tempRoundUpDown(dblMaxTemp, Boolean.TRUE));
            strBldDayForecast.append("º/ ");
            strBldDayForecast.append(tempRoundUpDown(dblMinTemp, Boolean.FALSE));
            strBldDayForecast.append("º");

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

        public String[] parseJsonString(Integer intDaysCount) {
            //VARIABLES E INICIALIZACIONES:
            final String LOG_TAG = ForecastFragment.class.getSimpleName();
            String[] strDaysFormattedData = new String[intDaysCount];
            Integer intHumidity, intWeatherID, intClouds, intDegrees;
            Long lngUnixDate;
            Double dblMaxTemp, dblMinTemp, dblDayTemp, dblNightTemp, dblMorningTemp, dblEveningTemp,
                    dblPressure, dblWindSpeed;
            String strWeatherMain, strWeatherDescription, strWeatherIcon;

            //PROCESO:
            try {
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
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.toString());
                /**        //Toast exception error:
                 Toast.makeText(getApplicationContext(),
                 "Json parsing error: " + e.getMessage(),
                 Toast.LENGTH_LONG).show();
                 */
                strDaysFormattedData[0] = "ERROR";
            }
            return strDaysFormattedData;
        }

        @Override
        protected Void doInBackground(String... strPostalCode) {
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

            //TODO: Delete or comment this sentence once parse string unit test is passed.
            //Command added just for unit testing parse method.
            if (strJsonForecast.length() > 0) {
                parseJsonString(intForecastDays);
                return null;
            }

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

                //Capturing the JSON string to Global string variable
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

            //TODO: Agregar la llamada al método 'parseJsonString' dentro de un 'try/catch'.

            //TODO Ingresar un 'return' con el arreglo de strings proveniente de 'parseJsonString'.

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

