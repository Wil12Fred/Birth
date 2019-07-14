package com.delycomps.birth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Register3Activity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progress;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    BottomNavigationView navView;
    Utilitarios u = new Utilitarios();
    RelativeLayout managePhoto;
    EditText showDatePicker, names, surnames;
    TextView check_text;
    CheckBox privateYear;
    boolean showDialogDatePicker = true;
    ImageView containerRegister3,imageUser, showManage, register;
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
                    startActivityForResult(pickPhoto, Constants.CODE_SHOW_GALLERY);//one can be replaced with any action code
                    return true;
                case R.id.show_camara:
                    item.setCheckable(false);
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, Constants.CODE_TAKE_PHOTO);//zero can be replaced with any action code
                    return true;
                case R.id.clear_image:
                    item.setCheckable(false);
                    selectedImage = null;
                    imageUser.setImageDrawable(getDrawable(R.drawable.usuario));
                    navView.getMenu().removeItem(R.id.clear_image);//quitar clear from menu
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        showDatePicker = findViewById(R.id.showDatePicker);
        names = findViewById(R.id.names);
        surnames = findViewById(R.id.surnames);

        check_text = findViewById(R.id.check_text);
        imageUser = findViewById(R.id.imageUser);
        managePhoto = findViewById(R.id.managePhoto);
        navView = findViewById(R.id.menuPhoto);
        navView.getMenu().findItem(R.id.clear_image).setVisible(false);
        navView.getMenu().getItem(0).setCheckable(false);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        containerRegister3 = findViewById(R.id.containerRegister3);

        showManage = findViewById(R.id.showManage);
        register = findViewById(R.id.register);
        privateYear = findViewById(R.id.privateYear);

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
                        inputMethodManager1.hideSoftInputFromWindow(surnames.getWindowToken(), 0);
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
        navView.getMenu().findItem(R.id.clear_image).setVisible(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case Constants.CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImage = getImageUri(this, imageBitmap);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageUser.setImageBitmap(u.getCircleBitmap(bitmap));
                    CropImage.activity(selectedImage)
                            .setFixAspectRatio(true)
                            .start(this);
                    hideManagePhoto();
                }
                break;
            case Constants.CODE_SHOW_GALLERY:
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_text:
                privateYear.setChecked(true);
                break;
            case R.id.showManage:
                InputMethodManager inputMethodManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager1.hideSoftInputFromWindow(surnames.getWindowToken(), 0);
                if (selectedImage != null) {
                    navView.getMenu().clear();
                    navView.inflateMenu(R.menu.menu_manage_photo);
                    navView.getMenu().getItem(0).setCheckable(false);
                }
                managePhoto.setVisibility(View.VISIBLE);
                managePhoto.setAlpha(0.0f);
                managePhoto.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setListener(null);
                break;
            case R.id.showDatePicker:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(surnames.getWindowToken(), 0);
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

                    DatePickerDialog dialog = new DatePickerDialog(Register3Activity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
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
            case R.id.imageUser:
                if (selectedImage == null) {
                    Toast.makeText(this, "image no saleccionada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Si hay imagen seleccionada", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                String str_names = names.getText().toString();
                String str_surnames = surnames.getText().toString();
                String str_showDatePicker = showDatePicker.getText().toString();
                String hideYear = privateYear.isChecked() ? "true" : "false";
                if (str_names.equals("") || str_surnames.equals("") || str_showDatePicker.equals("")) {
                    Toast.makeText(this, "Es necesario que complete toda la información.", Toast.LENGTH_SHORT).show();
                } else {
                    progress = ProgressDialog.show(Register3Activity.this, "Loading", "Espere, por favor.");
                    sendInformation(str_names, str_surnames, str_showDatePicker, hideYear);
                }
                break;
        }
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

    private void sendInformation(final String str_names, final String str_surnames, final String str_showDatePicker, final String hideYear) {
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
        final Birth_local birth_local = new Birth_local(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String numero = currentUser.getPhoneNumber();
        final String tokenUI = currentUser.getUid();

        RequestBody requist_names = RequestBody.create(MediaType.parse("text/plain"), str_names);
        RequestBody requist_surnames = RequestBody.create(MediaType.parse("text/plain"), str_surnames);
        RequestBody requist_birthday = RequestBody.create(MediaType.parse("text/plain"), str_showDatePicker);
        RequestBody requist_hideYear = RequestBody.create(MediaType.parse("text/plain"), hideYear);
        RequestBody requist_phonenumber = RequestBody.create(MediaType.parse("text/plain"), numero);
        RequestBody tokenFB = RequestBody.create(MediaType.parse("text/plain"), tokenUI);

        Call<RegisterResponse> call = birthApi.uploadImage(part, description, requist_names, requist_surnames, requist_birthday, requist_hideYear, requist_phonenumber, tokenFB);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progress.dismiss();
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        birth_local.updateUserActive(response.body().getResult());
                        Intent intent = new Intent(Register3Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Register3Activity.this, "Hubo un error, vuelva a intentarlo111", Toast.LENGTH_SHORT).show();
                        Log.d("respuesta", response.body().getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(Register3Activity.this, "Hubo un error, vuelva a intentarlo.222", Toast.LENGTH_SHORT).show();
                Log.d("respuesta", "error " + t.toString());
            }
        });
    }

}
