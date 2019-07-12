package com.delycomps.birth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.delycomps.birth.ModeloLocal.Birth_local;
import com.delycomps.birth.WebService.BirthApi;
import com.delycomps.birth.WebService.NetworkClient;
import com.delycomps.birth.WebService.RegisterResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register2Activity extends AppCompatActivity {
    EditText digite1, digite2, digite3, digite3_1, digite3_2, digite4, prueba1;
    private FirebaseAuth mAuth;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_register2);

        digite1 = findViewById(R.id.digite1);
        digite2 = findViewById(R.id.digite2);
        digite3 = findViewById(R.id.digite3);
        digite3_1 = findViewById(R.id.digite3_1);
        digite3_2 = findViewById(R.id.digite3_2);
        digite4 = findViewById(R.id.digite4);

        digite1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (digite1.getText().toString().length() != 0) digite2.requestFocus();
            }
        });
        digite2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (digite2.getText().toString().length() != 0) digite3.requestFocus();
            }
        });
        digite3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (digite3.getText().toString().length() != 0) digite3_1.requestFocus();
            }
        });
        digite3_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (digite3_1.getText().toString().length() != 0) digite3_2.requestFocus();
            }
        });
        digite3_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (digite3_2.getText().toString().length() != 0) digite4.requestFocus();
            }
        });
        digite4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                InputMethodManager inputMethodManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager1.hideSoftInputFromWindow(digite4.getWindowToken(), 0);
                Intent intent = getIntent();
                String verificationId = intent.getExtras().getString("verificationId");
                String digites = digite1.getText().toString()+digite2.getText().toString()+digite3.getText().toString()+digite3_1.getText().toString()+digite3_2.getText().toString()+digite4.getText().toString();

                if (digites.length() == 6){
                    progress = ProgressDialog.show(Register2Activity.this, "Loading", "Espere, por favor.");
                    verifyPhoneNumberWithCode(verificationId, digites);
                }

            }
        });
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("respuesta", "linea 169"+ task.getResult().getUser().getUid());
                            ConsultaNumber(task.getResult().getUser().getPhoneNumber());
                        } else {
                            progress.dismiss();
                            Log.d("respuesta", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(Register2Activity.this, "Invalid Code.", Toast.LENGTH_SHORT).show();
                                digite1.setText("");digite2.setText("");digite3.setText("");digite4.setText("");digite3_1.setText("");digite3_2.setText("");
                                // [END_EXCLUDE]
                            }else{
                                Toast.makeText(Register2Activity.this, "Hubo un error, hay q corregirlo.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void ConsultaNumber(String number) {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        BirthApi birthApi = retrofit.create(BirthApi.class);

        Call call = birthApi.postConsultaNumber(number);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                progress.dismiss();
                if (response.body() != null) {
                    RegisterResponse res = (RegisterResponse) response.body();
                    if (!res.getSuccess()){
                        Intent intent = new Intent(Register2Activity.this, Register3Activity.class);
                        startActivity(intent);
                    }else{
                        Birth_local birth_local = new Birth_local(Register2Activity.this);
                        birth_local.updateUserActive(((RegisterResponse) response.body()).getResult());
                        Intent intent = new Intent(Register2Activity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                Log.d("respuesta", t.toString());
                Toast.makeText(Register2Activity.this, "Hubo un error, vuelva a intentar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
