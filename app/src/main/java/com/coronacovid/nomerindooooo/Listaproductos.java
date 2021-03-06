package com.coronacovid.nomerindooooo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.coronacovid.nomerindooooo.adaptadores.Adaptadormaestraproducto;
import com.coronacovid.nomerindooooo.modelos.Productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Listaproductos extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

Button todos,nuevo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maestraproductos);
        todos=(Button)findViewById(R.id.todos);
nuevo=(Button)findViewById(R.id.nuevoproductito);

        MultiAutoCompleteTextView myMultiAutoCompleteTextView
                = (MultiAutoCompleteTextView)findViewById(
                R.id.multiAutoCompleteTextView);
       RecyclerView recycler = (RecyclerView) findViewById(R.id.recymaestraproductos);

        int numberOfColumns = 6;
        recycler.setHasFixedSize(true);
        lManager = new LinearLayoutManager(Listaproductos.this);
        recycler.setLayoutManager(lManager);

        myMultiAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selected = (String) parent.getItemAtPosition(position);


                new traerproductospornombre().execute(selected);
            }
        });



        todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new traerproductosporidalmacenidfamilia().execute("1");
            }
        });
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ListSong = new Intent(getApplicationContext(), Subirproductos.class);
                Listaproductos.this.startActivity(ListSong);
            }
        });






        new llenarautocomplete().execute("1");

        new traerproductosporidalmacenidfamilia().execute("1");

    }
    private class traerproductospornombre extends AsyncTask<String, String, String> {

        HttpURLConnection conne;
        URL url = null;
        ArrayList<Productos> listaalmaceno = new ArrayList<Productos>();
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recymaestraproductos);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("https://sodapop.pe/sugest/apitraerproductospornombredeappsolounalmacen.php");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                conne = (HttpURLConnection) url.openConnection();
                conne.setReadTimeout(READ_TIMEOUT);
                conne.setConnectTimeout(CONNECTION_TIMEOUT);
                conne.setRequestMethod("POST");
                conne.setDoInput(true);
                conne.setDoOutput(true);

                // Append parameters to URL



                Uri.Builder builder = new Uri.Builder()

                        .appendQueryParameter("nombreproducto", params[0])
                        ;

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conne.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conne.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }
            try {
                int response_code = conne.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conne.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    return (

                            result.toString()


                    );

                } else {
                    return("Connection error");
                }
            } catch (IOException e) {
                e.printStackTrace()                ;

                return e.toString();
            } finally {
                conne.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            ArrayList<Productos> people=new ArrayList<>();
           String[] strArrData = {"No Suggestions"};

            people.clear();


            ArrayList<String> dataList = new ArrayList<String>();
            Productos meso;
            if(result.equals("no rows")) {
                Toast.makeText(Listaproductos.this,"no existen datos a mostrar",Toast.LENGTH_LONG).show();

            }else{
                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);
                        meso = new Productos(json_data.getInt("idproducto"), json_data.getString("nombreproducto"), json_data.getString("estadoproducto"), json_data.getString("ingredientes"),json_data.getDouble(("precventa")),json_data.getString("descripcion"));
                        people.add(meso);
                    }
                    strArrData = dataList.toArray(new String[dataList.size()]);
                    recycler.removeAllViews();
                    recycler.setAdapter(null);
                    adapter = new Adaptadormaestraproducto(people,Listaproductos.this.getApplicationContext());
                    recycler.setLayoutManager(new GridLayoutManager(Listaproductos.this.getApplicationContext(), 1));
                    recycler.setAdapter(adapter);
                } catch (JSONException e) {

                }
            }
        }
    }

    private class llenarautocomplete extends AsyncTask<String, String, String> {
        ArrayList<Productos> people=new ArrayList<>();
        private String[] strArrData = {"No Suggestions"};

        HttpURLConnection conne;
        URL url = null;
        ArrayList<Productos> listaalmaceno = new ArrayList<Productos>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("https://sodapop.pe/sugest/apitraertodoslosproductosporalmacen.php");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                conne = (HttpURLConnection) url.openConnection();
                conne.setReadTimeout(READ_TIMEOUT);
                conne.setConnectTimeout(CONNECTION_TIMEOUT);
                conne.setRequestMethod("POST");
                conne.setDoInput(true);
                conne.setDoOutput(true);

                // Append parameters to URL



                Uri.Builder builder = new Uri.Builder()

                        .appendQueryParameter("idalmacen", params[0])
                        ;

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conne.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conne.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }
            try {
                int response_code = conne.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conne.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    return (

                            result.toString()


                    );

                } else {
                    return("Connection error");
                }
            } catch (IOException e) {
                e.printStackTrace()                ;

                return e.toString();
            } finally {
                conne.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            ArrayList<String> mylist = new ArrayList<String>();
            people.clear();


            ArrayList<String> dataList = new ArrayList<String>();
            Productos meso;
            if(result.equals("no rows")) {
                Toast.makeText(Listaproductos.this.getApplicationContext(),"no existen datos a mostrar",Toast.LENGTH_LONG).show();

            }else{

                try {


                    JSONArray jArray = new JSONArray(result);


                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);

                        meso = new Productos(json_data.getInt("idproducto"), json_data.getString("nombreproducto"), json_data.getString("estadoproducto"), json_data.getString("ingredientes"),json_data.getDouble(("precventa")),json_data.getString("descripcion"));
                        people.add(meso);

                        mylist.add(json_data.getString("nombreproducto"));

                    }
                    strArrData = dataList.toArray(new String[dataList.size()]);


                    MultiAutoCompleteTextView myMultiAutoCompleteTextView
                            = (MultiAutoCompleteTextView)findViewById(
                            R.id.multiAutoCompleteTextView);
                    myMultiAutoCompleteTextView.setAdapter(
                            new ArrayAdapter<String>(Listaproductos.this.getApplicationContext(),android.R.layout.simple_dropdown_item_1line,mylist));
                    myMultiAutoCompleteTextView.setTokenizer(
                            new MultiAutoCompleteTextView.CommaTokenizer());



                } catch (JSONException e) {

                }

            }

        }

    }

    private class traerproductosporidalmacenidfamilia extends AsyncTask<String, String, String> {
        ArrayList<Productos> people=new ArrayList<>();
        private String[] strArrData = {"No Suggestions"};

        HttpURLConnection conne;
        URL url = null;
        ArrayList<Productos> listaalmaceno = new ArrayList<Productos>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("https://sodapop.pe/sugest/apitraerproductosmaestra.php");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                conne = (HttpURLConnection) url.openConnection();
                conne.setReadTimeout(READ_TIMEOUT);
                conne.setConnectTimeout(CONNECTION_TIMEOUT);
                conne.setRequestMethod("POST");
                conne.setDoInput(true);
                conne.setDoOutput(true);

                // Append parameters to URL



                Uri.Builder builder = new Uri.Builder()

                        .appendQueryParameter("idalmacen", params[0])
                        ;

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conne.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conne.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }
            try {
                int response_code = conne.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conne.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    return (

                            result.toString()


                    );

                } else {
                    return("Connection error");
                }
            } catch (IOException e) {
                e.printStackTrace()                ;

                return e.toString();
            } finally {
                conne.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {

            people.clear();
            RecyclerView recycler = (RecyclerView) findViewById(R.id.recymaestraproductos);


            ArrayList<String> dataList = new ArrayList<String>();
            Productos meso;
            if(result.equals("no rows")) {
                Toast.makeText(Listaproductos.this.getApplicationContext(),"no existen datos a mostrar",Toast.LENGTH_LONG).show();

            }else{

                try {


                    JSONArray jArray = new JSONArray(result);


                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);

                        meso = new Productos(json_data.getInt("idproducto"), json_data.getString("nombreproducto"), json_data.getString("estadoproducto"), json_data.getString("ingredientes"),json_data.getDouble(("precventa")),json_data.getString("descripcion"));
                        people.add(meso);

                    }
                    strArrData = dataList.toArray(new String[dataList.size()]);


                    adapter = new Adaptadormaestraproducto(people,Listaproductos.this.getApplicationContext());
                    recycler.setLayoutManager(new GridLayoutManager(Listaproductos.this.getApplicationContext(), 1));

                    recycler.setAdapter(adapter);


                } catch (JSONException e) {

                }

            }

        }

    }
}
