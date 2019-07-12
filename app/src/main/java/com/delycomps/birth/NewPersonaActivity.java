package com.delycomps.birth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.ModeloLocal.Birth_local;
import com.delycomps.birth.Utilities.Utilitarios;
import com.delycomps.birth.WebService.BirthApi;
import com.delycomps.birth.WebService.NetworkClient;
import com.delycomps.birth.WebService.RegisterResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class NewPersonaActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progress;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    View item1;
    BottomNavigationView navView;

    RelativeLayout managePhoto;
    EditText showDatePicker, names;
    TextView check_text;
    CheckBox privateYear;
    boolean showDialogDatePicker = true;
    ImageView containerRegister3, imageUser, showManage, register;
    Uri selectedImage = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.show_galeria:
                    item.setCheckable(false);
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                    return true;
                case R.id.show_camara:
                    item.setCheckable(false);
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                    return true;
                case R.id.clear_image:
                    item.setCheckable(false);
                    selectedImage = null;
                    imageUser.setImageDrawable(getDrawable(R.drawable.usuario));
                    item1 = findViewById(R.id.clear_image);
                    item1.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_persona);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showDatePicker = findViewById(R.id.showDatePickerPersona);
        names = findViewById(R.id.namesPersona);

        check_text = findViewById(R.id.check_textPersona);
        imageUser = findViewById(R.id.imageUserPersona);
        managePhoto = findViewById(R.id.managePhotoPersona);
        navView = findViewById(R.id.menuPhotoPersona);
        navView.getMenu().getItem(0).setCheckable(false);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        item1 = findViewById(R.id.clear_image);
        containerRegister3 = findViewById(R.id.containerNewPersona);

        showManage = findViewById(R.id.showManagePersona);
        register = findViewById(R.id.registerPersona);
        privateYear = findViewById(R.id.privateYearPersona);

        showManage.setOnClickListener(this);
        showDatePicker.setOnClickListener(this);
        imageUser.setOnClickListener(this);
        check_text.setOnClickListener(this);
        register.setOnClickListener(this);
        containerRegister3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() != R.id.showManage) {
                    managePhoto.setAlpha(1.0f);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        hideManagePhoto();
                        InputMethodManager inputMethodManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager1.hideSoftInputFromWindow(names.getWindowToken(), 0);

                        return true;
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        return true;
                    }
                }
                return true;
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                showDialogDatePicker = true;
                month = month + 1;
                String str_month = (month < 10 ? "0" + month : Integer.toString(month));
                String str_day = (dayOfMonth < 10 ? "0" + dayOfMonth : Integer.toString(dayOfMonth));
                String text = "" + year + "-" + str_month + "-" + str_day;
                showDatePicker.setText(text);
            }
        };

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {

                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImage = getImageUri(this, imageBitmap);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }Utilitarios u = new Utilitarios();
                    imageUser.setImageBitmap(u.getCircleBitmap(bitmap));
                    CropImage.activity(selectedImage)
                            .setFixAspectRatio(true)
                            .start(this);
                    hideManagePhoto();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    CropImage.activity(selectedImage)
                            .setFixAspectRatio(true)
                            .start(this);
                    hideManagePhoto();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
                if (resultCode == RESULT_OK) {
                    selectedImage = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        Utilitarios u = new Utilitarios();
                        imageUser.setImageBitmap(u.getCircleBitmap(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.d("respuesta", result.getError().toString());
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_textPersona:
                privateYear.setChecked(true);
                break;
            case R.id.showManagePersona:
                InputMethodManager inputMethodManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager1.hideSoftInputFromWindow(names.getWindowToken(), 0);
                if (selectedImage != null) {
                    item1.setVisibility(View.VISIBLE);
                }
                managePhoto.setVisibility(View.VISIBLE);
                managePhoto.setAlpha(0.0f);
                managePhoto.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setListener(null);
                break;
            case R.id.showDatePickerPersona:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(names.getWindowToken(), 0);
                if (showDialogDatePicker) {
                    showDialogDatePicker = false;
                    int year, month, day;
                    if (showDatePicker.getText().toString().equals("")) {
                        Calendar cal = Calendar.getInstance();
                        year = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH);
                        day = cal.get(Calendar.DAY_OF_MONTH);
                    } else {
                        String date_selected = showDatePicker.getText().toString();
                        year = Integer.parseInt(date_selected.substring(0, 4));
                        month = Integer.parseInt(date_selected.substring(5, 7)) - 1;
                        day = java.lang.Integer.parseInt(date_selected.substring(8, 10));
                    }

                    DatePickerDialog dialog = new DatePickerDialog(NewPersonaActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
                    dialog.setOnCancelListener(new DatePickerDialog.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showDialogDatePicker = true;
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                break;
            case R.id.imageUserPersona:
                if (selectedImage == null) {
                    Toast.makeText(this, "image no saleccionada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Si hay imagen seleccionada", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.registerPersona:
                String str_names = names.getText().toString();
                String str_showDatePicker = showDatePicker.getText().toString();
                String hideYear = privateYear.isChecked() ? "true" : "false";
                if (str_names.equals("") || str_showDatePicker.equals("")) {
                    Toast.makeText(this, "Es necesario que complete toda la información.", Toast.LENGTH_SHORT).show();
                } else {
                    progress = ProgressDialog.show(NewPersonaActivity.this, "Loading", "Espere, por favor.");
                    sendInformation(str_names, str_showDatePicker, hideYear);
                }
                break;
        }
    }

    private void sendInformation(final String str_names, final String str_showDatePicker, final String hideYear) {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        BirthApi birthApi = retrofit.create(BirthApi.class);

        MultipartBody.Part part = null;
        RequestBody description = null;
        if (selectedImage != null) {
            File fileSelected = new File(selectedImage.getPath());
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("*/*"), fileSelected);
            // Create MultipartBody.Part using file request-body,file name and part name
            part = MultipartBody.Part.createFormData("file", fileSelected.getName(), fileReqBody);
            //Create request body with text description and text media type
            description = RequestBody.create(MediaType.parse("text/plain"), fileSelected.getName());
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String numero = currentUser.getPhoneNumber();

        RequestBody requist_names = RequestBody.create(MediaType.parse("text/plain"), str_names);
        RequestBody requist_birthday = RequestBody.create(MediaType.parse("text/plain"), str_showDatePicker);
        RequestBody requist_hideYear = RequestBody.create(MediaType.parse("text/plain"), hideYear);
        RequestBody requist_phonenumber = RequestBody.create(MediaType.parse("text/plain"), numero);


        Call<RegisterResponse> call = birthApi.registerContactoFalso(part, description, requist_names, requist_birthday, requist_hideYear, requist_phonenumber);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progress.dismiss();
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        Birth_local b = new Birth_local(NewPersonaActivity.this);
                        b.setContacto(response.body().getResult(), true);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("register_new_person", true);
                        setResult(Activity.RESULT_OK, resultIntent);

                        finish();
                        Toast.makeText(NewPersonaActivity.this, "Contacto registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewPersonaActivity.this, "Hubo un error, vuelva a intentarlo111", Toast.LENGTH_SHORT).show();
                        Log.d("respuesta", response.body().getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(NewPersonaActivity.this, "Hubo un error, vuelva a intentarlo.222", Toast.LENGTH_SHORT).show();
                Log.d("respuesta", "AQUI SI ENTRA1");
            }
        });
    }

    private void hideManagePhoto() {
        managePhoto.animate()
                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        managePhoto.setVisibility(View.GONE);
                    }
                });
    }
}


