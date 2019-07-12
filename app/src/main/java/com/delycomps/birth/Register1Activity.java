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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register1Activity extends AppCompatActivity {
    CountryCodePicker ccp;
    ProgressDialog progress;

    private FirebaseAuth mAuth;
    EditText phoneNumber;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    int count_preview = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        ccp = findViewById(R.id.ccp);
        Button buttonEnviar = findViewById(R.id.buttonEnviar);
        mAuth = FirebaseAuth.getInstance();
        phoneNumber = findViewById(R.id.phoneNumber);

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int pos = phoneNumber.getText().length();
                if ((s.length() == 3 || s.length() == 7 || s.length() == 11) && (count_preview == 0 || count_preview <= pos)){
                    String new3 = s.toString()+" ";
                    phoneNumber.setText(new3);
                    pos = phoneNumber.getText().length();
                    phoneNumber.setSelection(pos);
                }
                count_preview = pos;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumber.getText().toString().length() == 0) {
                    Toast.makeText(Register1Activity.this, "Ingrese su número de telefono", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.getText().toString().length() < 6) {
                    Toast.makeText(Register1Activity.this, "Ingrese un número de telefono valido", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager inputMethodManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager1.hideSoftInputFromWindow(phoneNumber.getWindowToken(), 0);
                    progress = ProgressDialog.show(Register1Activity.this, "Loading", "Espere, por favor.");
                    sendPhoneNumberVerification("+" + ccp.getSelectedCountryCode() + phoneNumber.getText().toString());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                progress.dismiss();
                Log.d("respuesta", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progress.dismiss();
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("respuesta", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Register1Activity.this, "Hubo un problema, intente mas tarde.", Toast.LENGTH_SHORT).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(Register1Activity.this, "Usted ya excedio el envio de sms permitidos.", Toast.LENGTH_SHORT).show();
                    // The SMS quota for the project has been exceeded
                    // ...
                } else {
                    Toast.makeText(Register1Activity.this, "Hubo un problema, intente mas tarde...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                progress.dismiss();
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("respuesta", "onCodeSent:" + verificationId);

                Intent intent = new Intent(Register1Activity.this, Register2Activity.class);
                intent.putExtra("verificationId", verificationId);

                startActivity(intent);
                // Save verification ID and resending token so we can use them later
            }
        };
    }

    private void sendPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("respuesta", "signInWithCredential:success");

                            // [START_EXCLUDE]
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("respuesta", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signOut() {
        mAuth.signOut();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("respuesta", currentUser != null ? currentUser.getUid() : "nullaso");
    }

}
