package com.delycomps.birth.Utilities;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delycomps.birth.Constants;
import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.MainActivity;
import com.delycomps.birth.NewPersonaActivity;
import com.delycomps.birth.R;
import com.delycomps.birth.Register3Activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utilitarios {

    public String getsignoChino(String y, boolean configuration) {
        String[] horoscopo_chino = {"Gallo", "Perro", "Chancho", "Rata", "Buey", "Tigre", "Conejo", "Dragon", "Serpiente", "Caballo", "Cabra", "Mono"};
        int year = Integer.parseInt(y);
        if (year != 0) {
            int residuo = (year % 12) - 1;
            if(configuration){
                return "Naciste en el año del "+horoscopo_chino[residuo];
            }else{
                return "Es " + horoscopo_chino[residuo];
            }

        } else {
            return "vacio";
        }
    }

    public String getFormaCleanDate(String birthday, boolean hideYear){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(birthday);
            if(hideYear){
                sdf.applyPattern("dd MMMM");
            }else{
                sdf.applyPattern("dd MMMM yyyy");
            }
            return sdf.format(d);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDdaysBirthday(String birthday, boolean dayofweek) {
        try {
            String str_dayofweek = "";
            Date datec = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
            Calendar b = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            b.setTime(datec);
            b.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            if(c.after(b)){
                b.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
            }
            if(dayofweek){
                String[] nameDays = {"domingo","Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};
                str_dayofweek =", será " + nameDays[b.get(Calendar.DAY_OF_WEEK) - 1];
            }
            Date startDate = b.getTime();
            Date endDate = c.getTime();
            long startTime = startDate.getTime();
            long endTime = endDate.getTime();
            long diffTime = startTime - endTime;
            long diffDays = diffTime / (1000 * 60 * 60 * 24);

            String t = "";
            if (diffDays == 0 || diffDays == 365) {
                t = "Hoy es su cumpleaños, felicitalo.";
            } else if (diffDays == 1) {
                t = "En 1 día"+ str_dayofweek;
            } else {
                t = "En " + diffDays + " días"+ str_dayofweek;
            }
            return t ;
        } catch (ParseException e) {
            e.printStackTrace();
            return birthday;
        }
    }

    public int getAge(String Birthday) {
        int age = 0;
        Date dateOfBirth = null;
        try {
            dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(Birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
//                throw new IllegalArgumentException("Can't be born in the future");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }

    public String getSignoZodiacal(String str_mes, String str_day, boolean configuration) {
        int mes = Integer.parseInt(str_mes);
        int day = Integer.parseInt(str_day);
        String sign = "";
        if (mes == 1) {
            if (day < 20)
                sign = "Capricornio";
            else
                sign = "Acuario";
        } else if (mes == 2) {
            if (day < 19)
                sign = "Acuario";
            else
                sign = "Piscis";
        } else if(mes == 3) {
            if (day < 21)
                sign = "Piscis";
            else
                sign = "Aries";
        } else if (mes == 4) {
            if (day < 20)
                sign = "Aries";
            else
                sign = "Tauro";
        } else if (mes == 5) {
            if (day < 21)
                sign = "Tauro";
            else
                sign = "Geminis";
        } else if( mes == 6) {
            if (day < 21)
                sign = "Geminis";
            else
                sign = "Cancer";
        } else if (mes == 7) {
            if (day < 23)
                sign = "Cancer";
            else
                sign = "Leo";
        } else if( mes == 8) {
            if (day < 23)
                sign = "Leo";
            else
                sign = "Virgo";
        } else if (mes == 9) {
            if (day < 23)
                sign = "Virgo";
            else
                sign = "Libra";
        } else if (mes == 10) {
            if (day < 23)
                sign = "Libra";
            else
                sign = "Escorpio";
        } else if (mes == 11) {
            if (day < 22)
                sign = "Escorpio";
            else
                sign = "Sagitario";
        } else if (mes == 12) {
            if (day < 22)
                sign = "Sagitario";
            else
                sign ="Capricornio";
        }
        if(configuration){
            return "Signo zodiacal: "+sign;
        }else{
            return sign;
        }

    }


    public void showModalContacto(final Contacto c, LayoutInflater lf, final Context cx, boolean isUsuario, int height_screnn, int y, final TextView v){
        AlertDialog.Builder builder = new AlertDialog.Builder(cx);

        View dialogView = lf.inflate(R.layout.modal_contacto, null);

        TextView nameContacto = dialogView.findViewById(R.id.nameContacto);
        LinearLayout container_modal_contacto = dialogView.findViewById(R.id.container_modal_contacto);
        LinearLayout container_modal_contactofalso = dialogView.findViewById(R.id.container_modal_contactofalso);
//        Buttons
        Button enviar_mensaje = dialogView.findViewById(R.id.enviar_mensaje);
        Button contacto_falso_editar = dialogView.findViewById(R.id.contacto_falso_editar);
        Button  contacto_falso_eliminar = dialogView.findViewById(R.id.contacto_falso_eliminar);
        View viewTrans = dialogView.findViewById(R.id.viewTrans);
        
        contacto_falso_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Toast.makeText(cx, "Vamo a liminar", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                
                AlertDialog.Builder builderEliminar = new AlertDialog.Builder(cx);
                builderEliminar.setMessage("¿Desea eliminar a "+ c.getNames()+"?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        contacto_falso_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cx, NewPersonaActivity.class);
                intent.putExtra(Constants.CODE_SEND_CONTACTO, c);
                cx.startActivity(intent);
            }
        });
        enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cx, NewPersonaActivity.class);
                intent.putExtra(Constants.CODE_SEND_CONTACTO, c);
                cx.startActivity(intent);
            }
        });


        if(isUsuario){
            nameContacto.setVisibility(View.GONE);
        }
        if(c.getName() == null){
            nameContacto.setText(c.getNames());
            container_modal_contactofalso.setVisibility(View.VISIBLE);
            container_modal_contacto.setVisibility(View.GONE);
        }else{
            container_modal_contactofalso.setVisibility(View.GONE);
            container_modal_contacto.setVisibility(View.VISIBLE);
            nameContacto.setText(c.getName());
        }

        TextView birthdayContacto = dialogView.findViewById(R.id.birthdayContacto);
        birthdayContacto.setText(getFormaCleanDate(c.getBirthday(), c.getHideYear() == 1));

        TextView signocContacto = dialogView.findViewById(R.id.signocContacto);
        signocContacto.setText(getsignoChino(c.getBirthday().substring(0,4), false));

        TextView daysToBirthContacto = dialogView.findViewById(R.id.daysToBirthContacto);
        daysToBirthContacto.setText(getDdaysBirthday(c.getBirthday(), true));

        TextView yearsContacto = dialogView.findViewById(R.id.yearsContacto);
        if(c.getHideYear() == 0){
            String years = getAge(c.getBirthday()) > 1 ? getAge(c.getBirthday())+" años" : getAge(c.getBirthday()) +"año";
            yearsContacto.setText(years);
        }else{
            yearsContacto.setText("Edad desconocida");
        }
        TextView signozContacto = dialogView.findViewById(R.id.signozContacto);
        signozContacto.setText(getSignoZodiacal(c.getBirthday().substring(5,7), c.getBirthday().substring(8,10), false));

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                v.setTypeface(null, Typeface.NORMAL);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 20;   //x position
        if(height_screnn/2 < y){
            wmlp.y = y;
        }else{
            wmlp.y = y;   //y position
        }
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.show();

        viewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(cx, "Ocultar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  Bitmap getCircleBitmap(Bitmap bitmap) {

        Bitmap output;
        //check if its a rectangular image
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        float r;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
