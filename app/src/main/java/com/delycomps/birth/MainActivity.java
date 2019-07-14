package com.delycomps.birth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.ModeloLocal.Birth_local;
import com.delycomps.birth.Utilities.Utilitarios;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        MessagesFragment.OnFragmentInteractionListener {

    private boolean viewIsAtHome;
    Birth_local b = new Birth_local(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        String label_name = "Birlay." +b.getDato("names");
        setTitle(label_name);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflator.inflate(R.layout.custom_imageview, null);
//
//        TextView userConeccted = v.findViewById(R.id.userConneted);
//        setTextUser(userConeccted); //Asignar el nombre del usuario
//        userConeccted.setOnClickListener(this); //mostrar el modal al hacer click
//
//        actionBar.setCustomView(v);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    addFragment(new HomeFragment());
                    viewIsAtHome = true;
                    return true;
                case R.id.chat:
                    addFragment(new MessagesFragment());
                    viewIsAtHome = false;
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };
    public void onBackPressed() {
        if (!viewIsAtHome) { //if the current view is not the News fragment
            BottomNavigationView bottomNavigationView =  findViewById(R.id.nav_view);
            bottomNavigationView.setSelectedItemId(R.id.home); //display the News fragment
        } else {
            moveTaskToBack(true);  //If view is in News fragment, exit application
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private void addFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.userConneted){
            Utilitarios u = new Utilitarios();
            int hideyear= Integer.parseInt(b.getDato("hideYear"));
                    String  phonenumber = b.getDato("phonenumber");
                    String  birthday = b.getDato("birthday");
                    String  surnames = b.getDato("surnames");
                    String  names = b.getDato("names");
            u.showModalContacto(new Contacto(0, hideyear, "",phonenumber,birthday, surnames,names,""), getLayoutInflater(), MainActivity.this, true);
        }
    }
}
