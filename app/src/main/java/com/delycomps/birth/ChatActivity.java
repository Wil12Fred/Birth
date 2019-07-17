package com.delycomps.birth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.ModeloLocal.Birth_local;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatActivity extends AppCompatActivity {
    String tokenUI = "", tokenReceptor = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Contacto contacto = extras.getParcelable(Constants.CODE_SEND_CONTACTO);
            if (contacto != null) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                
                tokenUI = currentUser.getUid();
                tokenReceptor = contacto.getTokenFB();

            }
        }
    }
}
