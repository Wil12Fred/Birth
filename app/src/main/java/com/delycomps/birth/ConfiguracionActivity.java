package com.delycomps.birth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delycomps.birth.ModeloLocal.Birth_local;
import com.delycomps.birth.Utilities.CircularTransformation;
import com.delycomps.birth.Utilities.Utilitarios;
import com.delycomps.birth.WebService.BirthApi;
import com.delycomps.birth.WebService.NetworkClient;
import com.delycomps.birth.WebService.RegisterResponse;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConfiguracionActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progress;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    View item1;
    BottomNavigationView navView;
    Utilitarios u = new Utilitarios();
    RelativeLayout managePhoto;
    EditText showDatePicker, names, surnames;
    TextView check_text, phonenumberConf, birthdayConf, yearConf, signozConf, signocConf, daysToBirthConf;
    CheckBox privateYear;
    boolean showDialogDatePicker = true;
    ImageView containerRegister3, imageUser, showManage;
    Uri selectedImage = null;
    Button buttonEliminar;

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Birth_local b = new Birth_local(ConfiguracionActivity.this);
                    progress = ProgressDialog.show(ConfiguracionActivity.this, "Loading", "Espere, por favor.");
                    b.deleteAllContactos();
                    b.updateDato("names", "");
                    b.updateDato("surnames", "");
                    b.updateDato("birthday", "");
                    b.updateDato("hideYear", "");
                    b.updateDato("horoscopo", "");
                    b.updateDato("codUpdate", "");
                    b.updateDato("register", "false");
                    deleteUsuario(b.getDato("phonenumber"));
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(ConfiguracionActivity.this, "noo", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
        setContentView(R.layout.activity_configuracion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showDatePicker = findViewById(R.id.showDatePickerConf);
        names = findViewById(R.id.namesConf);
        surnames = findViewById(R.id.surnamesConf);
        phonenumberConf = findViewById(R.id.phonenumberConf);
        check_text = findViewById(R.id.check_textConf);
        imageUser = findViewById(R.id.imageUserConf);
        managePhoto = findViewById(R.id.managePhotoConf);
        navView = findViewById(R.id.menuPhotoConf);
        navView.getMenu().getItem(0).setCheckable(false);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        item1 = findViewById(R.id.clear_image);
        containerRegister3 = findViewById(R.id.ContainerConf);
        showManage = findViewById(R.id.showManageConf);
        privateYear = findViewById(R.id.privateYearConf);

        buttonEliminar = findViewById(R.id.buttonEliminar);

        birthdayConf = findViewById(R.id.birthdayConf);
        yearConf = findViewById(R.id.yearConf);
        signozConf = findViewById(R.id.signozConf);
        signocConf = findViewById(R.id.signocConf);
        daysToBirthConf = findViewById(R.id.daysToBirthConf);

        //SETEO DE FORMULARIOS CON BDLOCAL
        Birth_local b = new Birth_local(ConfiguracionActivity.this);
        String birthdayx = b.getDato("birthday");
        birthdayConf.setText(u.getFormaCleanDate(birthdayx, b.getDato("hideYear").equals("1")));
        yearConf.setText("" + u.getAge(birthdayx) + " años");
        signozConf.setText(u.getSignoZodiacal(birthdayx.substring(5, 7), birthdayx.substring(8, 10)));
        signocConf.setText(u.getsignoChino(birthdayx.substring(0, 4)));
        daysToBirthConf.setText(u.getDdaysBirthday(birthdayx, true));

        Glide.with(this)
                .load(Constants.DIRECTORY_IMAGES_THUMBS)
                .apply(new RequestOptions().transforms(new CircularTransformation(this)))
                .into(imageUser);
        //FIN SETEO

        names.setText(b.getDato("names"));
        surnames.setText(b.getDato("surnames"));
        showDatePicker.setText(birthdayx);
        phonenumberConf.setText(b.getDato("phonenumber"));
        privateYear.setChecked(b.getDato("hideYear").equals("1"));

        buttonEliminar.setOnClickListener(this);
        showManage.setOnClickListener(this);
        showDatePicker.setOnClickListener(this);
        imageUser.setOnClickListener(this);
        check_text.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_textConf:
                privateYear.setChecked(true);
                break;
            case R.id.showManageConf:
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
            case R.id.showDatePickerConf:
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

                    DatePickerDialog dialog = new DatePickerDialog(ConfiguracionActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
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
            case R.id.imageUserConf:
                if (selectedImage == null) {
                    Toast.makeText(this, "image no saleccionada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Si hay imagen seleccionada", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonEliminar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Desea eliminar la cuenta definitivamente?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
//            case R.id.registerPersona:
//                String str_names = names.getText().toString();
//                String str_showDatePicker = showDatePicker.getText().toString();
//                String hideYear = privateYear.isChecked() ? "true" : "false";
//                if (str_names.equals("") || str_showDatePicker.equals("")) {
//                    Toast.makeText(this, "Es necesario que complete toda la información.", Toast.LENGTH_SHORT).show();
//                } else {
//                    progress = ProgressDialog.show(ConfiguracionActivity.this, "Loading", "Espere, por favor.");
//                    sendInformation(str_names, str_showDatePicker, hideYear);
//                }
//                break;
        }
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
                    }
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

    private void deleteUsuario(String number) {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        BirthApi birthApi = retrofit.create(BirthApi.class);

        Call call = birthApi.deleteUsuario(number);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                progress.dismiss();
                if (response.body() != null) {
                    Birth_local b = new Birth_local(ConfiguracionActivity.this);
                    b.updateDato("phonenumber", "");

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("finish", true);
                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();

                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                Log.d("respuesta", t.toString());
                Toast.makeText(ConfiguracionActivity.this, "Hubo un error, vuelva a intentar11111", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
