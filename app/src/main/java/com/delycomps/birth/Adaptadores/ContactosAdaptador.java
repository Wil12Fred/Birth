package com.delycomps.birth.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delycomps.birth.Constants;
import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.R;
import com.delycomps.birth.Utilities.CircularTransformation;
import com.delycomps.birth.Utilities.Utilitarios;
import java.util.ArrayList;
import java.util.List;

public class ContactosAdaptador extends RecyclerView.Adapter<ContactosAdaptador.ViewHolder> {

    private Context context;
    private List<Contacto> listContactos;
    private LayoutInflater layoutInflater;
    private Utilitarios u = new Utilitarios();

    public ContactosAdaptador(List<Contacto> contactoArrayList, Context context, LayoutInflater layoutInflater) {
        this.listContactos = contactoArrayList;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contacto, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url;
        String namesShow;
        if(listContactos.get(position).getName() == null){
            holder.name.setTextColor(Color.parseColor("#737373"));
            holder.daysToBirth.setTextColor(Color.parseColor("#737373"));
            namesShow = listContactos.get(position).getNames();
            url = Constants.DIRECTORY_IMAGES_THUMBS + listContactos.get(position).getIdUser()+"_"+listContactos.get(position).getPhonenumber()+".jpg";
        }else{
            namesShow = listContactos.get(position).getNames() + " " + listContactos.get(position).getSurnames();
            url = Constants.DIRECTORY_IMAGES_THUMBS + listContactos.get(position).getPhonenumber()+".jpg";
        }
        holder.name.setText(namesShow);
        holder.daysToBirth.setText(u.getDdaysBirthday(listContactos.get(position).getBirthday(), false));
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.persona_sin_foto)
                        .error(R.drawable.persona_sin_foto))
                .load(url)
                .apply(new RequestOptions().transforms(new CircularTransformation(context)))
                .into(holder.imageContacto);
    }

    @Override
    public int getItemCount() {
        return listContactos.size();
    }

    public void setFilter(List<Contacto> listContactosmodel) {
        listContactos = new ArrayList<>();
        listContactos.addAll(listContactosmodel);
        notifyDataSetChanged();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, daysToBirth;
        private ImageView imageContacto, showModalContacto;
        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.nameContacto);
            daysToBirth = v.findViewById(R.id.daysToBirth);
            imageContacto = v.findViewById(R.id.imageContacto);
            showModalContacto = v.findViewById(R.id.showModalContacto);
            showModalContacto.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Contacto c = listContactos.get(position);
                switch(v.getId()){
                    case R.id.showModalContacto:

                        Log.d("aaaaaa", "ddddd11 "+v.getPivotY());
                        Log.d("aaaaaa", "ddddd11 "+v.getScaleY());
                        Log.d("aaaaaa", "ddddd11 "+v.getScrollY());
                        Log.d("aaaaaa", "ddddd11 "+v.getY());
                        Log.d("aaaaaa", "ddddd "+v.getRotationY());
                        u.showModalContacto(c, layoutInflater, context, false);
                        break;
                }
            }
        }
    }

}