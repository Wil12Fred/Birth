package com.delycomps.birth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.delycomps.birth.Adaptadores.ContactosAdaptador;
import com.delycomps.birth.Entidades.Contacto;
import com.delycomps.birth.ModeloLocal.Birth_local;
import com.delycomps.birth.WebService.BirthApi;
import com.delycomps.birth.WebService.NetworkClient;
import com.delycomps.birth.WebService.RegisterResponse;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    ProgressDialog progress;
    RecyclerView rv_contactos;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        List<Contacto> cc =  b.getContactos();
        if(b.getContactos() != null){
            Log.d("auxiliarr", b.getDato("codUpdate"));
            startAdapter(b.getContactos());
//            progress = ProgressDialog.show(getActivity(), "Loading", "Espere, por favor.");
//            getNumber();
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
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            if(data.getBooleanExtra("register_new_person", false)){
                Birth_local b = new Birth_local(getActivity());
                startAdapter(b.getContactos());
                Toast.makeText(getActivity(), "Tenemos q actualizar", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 998 && resultCode == RESULT_OK && data != null) {
            if(data.getBooleanExtra("finish", false)){
                getActivity().finish();
                System.exit(0);
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
                startActivityForResult(intent, 999);
                break;
            case R.id.action_configuracion:
                Intent intent1 = new Intent(getActivity(), ConfiguracionActivity.class);
                startActivityForResult(intent1,998);
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
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
                    } else {
                        //don't do anything with this contact because we've already found this number
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
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        BirthApi birthApi = retrofit.create(BirthApi.class);
        Birth_local b = new Birth_local(getContext());
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
                    setContactoSQLite(contactosVerified);
                    startAdapter(contactosVerified);
                }else{
                    Toast.makeText(getContext(), "No hay contactos por mostrar.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), "Hubo un error, vuelva a intentar", Toast.LENGTH_SHORT).show();
                Log.d("respuesta", t.toString());
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
        rv_contactos.setAdapter(new ContactosAdaptador(lc, getContext(), this.getLayoutInflater()));
    }
}
