package com.delycomps.birth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import com.delycomps.birth.Adaptadores.ContactosAdaptador;
import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.ModeloLocal.Birth_local;
import com.delycomps.birth.WebService.BirthApi;
import com.delycomps.birth.WebService.NetworkClient;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener{

    private OnFragmentInteractionListener mListener;

    ProgressDialog progress;
    RecyclerView rv_contactos;
    ContactosAdaptador contactosAdaptador;
    List<Contacto> contactoLista;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        rv_contactos = v.findViewById(R.id.rvContactos);
        rv_contactos.setHasFixedSize(true);

        // use a linear layout manager
        rv_contactos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_contactos.addItemDecoration(new DividerItemDecoration(rv_contactos.getContext(), DividerItemDecoration.VERTICAL));
        Birth_local b = new Birth_local(getActivity());
        if(b.getContactos() != null){
            contactoLista = b.getContactos();
//            startAdapter(b.getContactos());
            progress = ProgressDialog.show(getActivity(), "Loading", "Espere, por favor.");
            getNumber();
        }else{
            progress = ProgressDialog.show(getActivity(), "Loading", "Espere, por favor.");
            getNumber();
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CODE_NEW_PERSON && resultCode == RESULT_OK && data != null) {
            if(data.getBooleanExtra("register_new_person", false)){
                Birth_local b = new Birth_local(getActivity());
                startAdapter(b.getContactos());
            }
        }
        if (requestCode == Constants.CODE_CONFIGURATION && resultCode == RESULT_OK && data != null) {
            if(data.getBooleanExtra("finish", false)){
                Objects.requireNonNull(getActivity()).finish();
                System.exit(0);
            }else if(data.getBooleanExtra("change_config", false)){
                Birth_local b = new Birth_local(getActivity());
                String label_name = "Birlay." + b.getDato("names");
                getActivity().setTitle(label_name);
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.chat_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_searchP);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Buscar");
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_add_contacto:
                Intent intent = new Intent(getActivity(), NewPersonaActivity.class);
                startActivityForResult(intent, Constants.CODE_NEW_PERSON);
                break;
            case R.id.action_configuracion:
                Intent intent1 = new Intent(getActivity(), ConfiguracionActivity.class);
                startActivityForResult(intent1,Constants.CODE_CONFIGURATION);
                break;
        }
        return false;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactosAdaptador.setFilter(filter(contactoLista, newText));
        return false;
    }
    private List<Contacto> filter(List<Contacto> models, String query) {
        query = query.toLowerCase();
        final List<Contacto> filteredModelList = new ArrayList<>();
        for (Contacto model : models) {
            final String text = model.getNames().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void getNumber() {
        List<String> phonenumbers = new ArrayList<>();
        List<Contacto> contactoList = new ArrayList<>();
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions PERMISOOOOOOOOOOOOS
        }

        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        contactoList.add(new Contacto(cursor.getString(indexOfDisplayName), cursor.getString(indexOfDisplayNumber).replace(" ","")));
                        phonenumbers.add(cursor.getString(indexOfDisplayNumber).replace(" ",""));
                    }
                }
            } finally {
                cursor.close();
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(phonenumbers);
        consultarContactos(json, contactoList);
    }
    private void consultarContactos(final String list_phonenumbers, final List<Contacto> list_contactos) {
        Log.d("jsss", list_phonenumbers);
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        BirthApi birthApi = retrofit.create(BirthApi.class);
        Birth_local b = new Birth_local(getActivity());
        Log.d("respuesta", b.getDato("phonenumber"));
        Call<List<Contacto>> call = birthApi.verifyContactos(list_phonenumbers, b.getDato("phonenumber"));
        call.enqueue(new Callback<List<Contacto>>() {
            @Override
            public void onResponse(Call<List<Contacto>> call, Response<List<Contacto>> response) {
                progress.dismiss();
                if (response.body() != null) {
                    List<Contacto>  contactosVerified = response.body();
//                    Collections.sort(contactosVerified, new Comparator<Contacto>() {
//                        public int compare(Contacto v1, Contacto v2) {
//                            return v1.getName().compareTo(v2.getName());
//                        }
//                    });
                    //Insertar el nombre de contacto local al Contacto
                    for (int i = 0; i < contactosVerified.size(); i++) {
                        for (int j = 0; j < list_contactos.size(); j++) {
                            if (contactosVerified.get(i).getPhonenumber().equals(list_contactos.get(j).getPhonenumber())){
                                contactosVerified.get(i).setName(list_contactos.get(j).getName());
                            }
                        }
                    }
                    contactoLista = contactosVerified;
                    startAdapter(contactosVerified);
                    setContactoSQLite(contactosVerified);
                }else{
                    Toast.makeText(getContext(), "No hay contactos por mostrar.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), "Sin conexión a internet, vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setContactoSQLite(List<Contacto> lc){
        Birth_local b = new Birth_local(getActivity());
        b.deleteAllContactos();
        for (Contacto c: lc) {
            b.setContacto(c, false);
        }
    }
    private void startAdapter(List<Contacto> lc){
        contactosAdaptador = new ContactosAdaptador(lc, getContext(), this.getLayoutInflater());
        rv_contactos.setAdapter(contactosAdaptador);
    }
}
