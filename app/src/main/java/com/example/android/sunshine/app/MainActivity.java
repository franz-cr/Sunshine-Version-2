package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    //VARIABLES GLOBALES
    protected String[] strForecastDataMain = {"Item 1", "Item 2", "Item 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This code adds the fragment to the current activity where is declared in.
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_about) {
            //Toast variables
            int duration = Toast.LENGTH_LONG;
            String strForecast = "Seleccionó: " + item.getTitle();
            Context context = getApplicationContext();
            //Toast implementation
            Toast toast = Toast.makeText(context, strForecast, duration);
            toast.show();

/*            //Declaration and implementation of new Activity
            Intent detailedForecast = new Intent(this, ForecastDetailActivity.class);
            this.startActivity(detailedForecast);
*/
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}

