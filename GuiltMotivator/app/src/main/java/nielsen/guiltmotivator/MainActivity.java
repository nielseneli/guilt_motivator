package nielsen.guiltmotivator;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import static java.security.AccessController.getContext;

/** The main activity, its got 2 whole fragments.
 *  */
public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    public static final String SAVED_COLOR = "saved_color";
    public static final String SAVED_TONE = "saved_tone";
    public static final String SAVED_NAME = "saved_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.fragmentcontainer) != null) {

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

            int defaultValue = getResources().getColor(R.color.white);
            int background = sharedPref.getInt(SAVED_COLOR, defaultValue);

            getWindow().getDecorView().setBackgroundColor(background);

            if (savedInstanceState != null) {
                return;
            }

            Fragment defaultFragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //set the default to be the list view
            fragmentTransaction.add(R.id.fragmentcontainer, defaultFragment);
            fragmentTransaction.commit();
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Fragment settingsFragment = new SettingsFragment();

            fragmentTransaction.replace(R.id.fragmentcontainer, settingsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (id == R.id.home) {
            Fragment defaultFragment = new HomeFragment();

            fragmentTransaction.replace(R.id.fragmentcontainer, defaultFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager manager;                                            //initializes manager as FragmentManager
        FragmentTransaction transaction;                                    //initializes transaction as FragmentTransaction

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        transaction.replace(R.id.fragmentcontainer, fragment);
        transaction.addToBackStack(null).commit();
    }

    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch(v.getId()) {
            case R.id.polite:
                    if (v.getId() == R.id.polite) {
                        Toast.makeText(this, "Jolly good", Toast.LENGTH_SHORT).show();
                        editor.putString(MainActivity.SAVED_TONE, "polite");
                        editor.apply();
                    }

            case R.id.profane:
                    if (v.getId() == R.id.profane) {
                        Toast.makeText(this, "Fuck you", Toast.LENGTH_SHORT).show();
                        editor.putString(MainActivity.SAVED_TONE, "profane");
                        editor.apply();
                    }

        }

    }

    public void onMainFragmentInteraction(Uri uri){

    }

    public void onSettingsFragmentInteraction(Uri uri){

    }

    public void onStop() {
        super.onStop();
    }


}