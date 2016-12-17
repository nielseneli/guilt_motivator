package nielsen.guiltmotivator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;


/** The main activity, its got 3 whole fragments.
 *  */


public class MainActivity extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener{

    public static final String SAVED_TONE = "saved_tone";
    public static final String SAVED_NAME = "saved_name";
    public static final String SAVED_PRONOUNS = "saved_pronouns";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EmailService.updateActivity(this);
        Intent mServiceIntent = new Intent(this, EmailService.class);
        this.startService(mServiceIntent);

        // we got a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // woo! navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.fragmentcontainer) != null) {

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
    public void onBackPressed() {
        // if the drawer is open, pressing back closes it
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //noinspection SimplifiableIfStatement
        // using the navigation drawer to switch fragments
        if (id == R.id.nav_settings) {
            Fragment settingsFragment = new SettingsFragment();
            fragmentTransaction.replace(R.id.fragmentcontainer, settingsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (id == R.id.nav_home) {
            Fragment defaultFragment = new HomeFragment();
            fragmentTransaction.replace(R.id.fragmentcontainer, defaultFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        // whoa! navigation drawer to opening a site!
        if (id == R.id.nav_help) {
            Uri uri = Uri.parse("https://github.com/nielsenlouise/guilt_motivator/blob/master/README.md");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager manager;                    //initializes manager as FragmentManager
        FragmentTransaction transaction;            //initializes transaction as FragmentTransaction
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentcontainer, fragment);
        transaction.addToBackStack(null).commit();
    }

    public void onRadioButtonClicked(View v) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // all the radio buttons
        switch(v.getId()) {
            // if tone, make a snarky comment/toast and put in sharedPrefs
            case R.id.politeButtonContact:
                if (v.getId() == R.id.politeButtonContact) {
                    Toast.makeText(this, "Jolly good", Toast.LENGTH_SHORT).show();
//                    editor.putString(MainActivity.SAVED_TONE, "polite");
//                    editor.apply();
                }

            case R.id.rudeButtonContact:
                if (v.getId() == R.id.rudeButtonContact) {
                    Toast.makeText(this, "You fool", Toast.LENGTH_SHORT).show();
//                    editor.putString(MainActivity.SAVED_TONE, "rude");
//                    editor.apply();
                }

            case R.id.profaneButtonContact:
                if (v.getId() == R.id.profaneButtonContact) {
                    Toast.makeText(this, "Fuck you", Toast.LENGTH_SHORT).show();
//                    editor.putString(MainActivity.SAVED_TONE, "profane");
//                    editor.apply();
                }

            // if pronouns, just put in sharedPrefs
            case R.id.he:
                if (v.getId() == R.id.he) {
                    editor.putString(MainActivity.SAVED_PRONOUNS, "he");
                    editor.apply();
                }

            case R.id.she:
                if (v.getId() == R.id.she) {
                    editor.putString(MainActivity.SAVED_PRONOUNS, "she");
                    editor.apply();
                }

            case R.id.they:
                if (v.getId() == R.id.they) {
                    editor.putString(MainActivity.SAVED_PRONOUNS, "they");
                    editor.apply();
                }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    public void onSettingsFragmentInteraction(Uri uri){
    }

    public void onStop() {
        super.onStop();
    }

}