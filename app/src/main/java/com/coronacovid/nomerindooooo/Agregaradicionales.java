package com.coronacovid.nomerindooooo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coronacovid.nomerindooooo.modelos.Adicional;
import com.coronacovid.nomerindooooo.modelos.Crema;

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



public class Agregaradicionales extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
TextView cabeceralayoutadicional;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregaradicionales);
        Bundle datos = this.getIntent().getExtras();
        int idproducto = datos.getInt("idproducto");
        String nombreproducto = datos.getString("nombreproducto");
        cabeceralayoutadicional=(TextView)findViewById(R.id.cabeceralayoutadicional);
        cabeceralayoutadicional.setText(nombreproducto);
        new traeradicional().execute("17");
        new traercremas().execute("17");

    }
    private class traercremas extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Agregaradicionales.this);
        HttpURLConnection conne;
        URL url = null;
        ArrayList<Adicional> listadeadicionales = new ArrayList<Adicional>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tCargando Cremas disponibles");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {


            try {
                url = new URL("https://sodapop.pe/sugest/apitraertodaslascremas.php");
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

                        .appendQueryParameter("idproducto", params[0]);

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
                    return ("Connection error");
                }
            } catch (IOException e) {
                e.printStackTrace();

                return e.toString();
            } finally {
                conne.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            ArrayList<Crema> peoplecrema = new ArrayList<>();
            String[] strcrema = {"No Suggestions"};
            ArrayList<String> datalistcrema = new ArrayList<String>();
            Crema mesocrema;
            peoplecrema.clear();
            if (result.equals("no rows")) {
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);
                        mesocrema = new Crema(json_data.getInt("idcrema"), json_data.getString("nombrecrema"),  json_data.getString("estadocrema"));
                        peoplecrema.add(mesocrema);
                    }


                    LinearLayout my_layout = (LinearLayout)findViewById(R.id.my_layout);
                    strcrema = datalistcrema.toArray(new String[datalistcrema.size()]);
                    TextView texto = new TextView(getApplication());
                    texto.setText("        ESCOGE TUS CREMAS        ");
                    texto.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.blue_leftcorner_bkg));
                    texto.setGravity(Gravity.CENTER);

                    //  texto.setLayoutParams(param);
                    texto.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    texto.setTypeface(null, Typeface.BOLD);
                    texto.setShadowLayer(2, 1, 1, R.color.accent);
                    texto.setTextColor(getApplication().getResources().getColor(R.color.colortres));


                    TableRow.LayoutParams textoenlayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    my_layout.addView(texto, textoenlayout);

                    int numerodecrema;



                    for(numerodecrema= 0; numerodecrema < peoplecrema.size(); numerodecrema++) {
                        CheckBox cbc = new CheckBox(getApplication());
                        cbc.setText("   "+peoplecrema.get(numerodecrema).getNombrecrema());
                        cbc.setId(numerodecrema+1);



                        int idcremita=peoplecrema.get(numerodecrema).getIdcrema();
                        String nombrecremita=peoplecrema.get(numerodecrema).getNombrecrema();


                        cbc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView,
                                                         final boolean isChecked) {

                                CharSequence options[];

                                if (isChecked) {
                                  //  realgrabarcrema(nombrecremita,Integer.parseInt(idproductoseleccionado));


                                } else {


                                    //eliminaracrema(idsupremodedetalle,Integer.parseInt(idproductoseleccionado),nombrecremita);
                                }
                            }});



                        my_layout.addView(cbc);
                    }








                } catch (JSONException e) {
                    Log.d("erroro",e.toString());
                }
            }
        }
    }

    private class traeradicional extends AsyncTask<String, String, String> {
        HttpURLConnection conne;
        ProgressDialog pdLoading = new ProgressDialog(Agregaradicionales.this);
        URL url = null;
        ArrayList<Adicional> listadeadicionales = new ArrayList<Adicional>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tCargando Adicionales");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://sodapop.pe/sugest/apitraertodosadicionales.php");
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
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idproducto", params[0]);
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
                    return ("Connection error");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conne.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();
            ArrayList<Adicional> peopleadicional = new ArrayList<>();
            String[] stradicional = {"No Suggestions"};
            ArrayList<String> datalistadicional = new ArrayList<String>();


            Adicional mesoadiconal;
            peopleadicional.clear();
            RecyclerView.Adapter adapteradicional;

            if (result.equals("no rows")) {
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);
                        mesoadiconal = new Adicional(json_data.getInt("idadicional"), json_data.getString("nombreadicional"), json_data.getDouble("precioadicional"), json_data.getString("estadoadicional"));
                        peopleadicional.add(mesoadiconal);
                    }
                    LinearLayout my_layout = (LinearLayout)findViewById(R.id.my_layout);

                    stradicional = datalistadicional.toArray(new String[datalistadicional.size()]);



                    TextView texto = new TextView(getApplication());
                    texto.setText("        AGREGA ALGUN ADICIONAL        ");
                    texto.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.blue_leftcorner_bkg));
                    texto.setGravity(Gravity.CENTER);

                    //  texto.setLayoutParams(param);
                    texto.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    texto.setTypeface(null, Typeface.BOLD);
                    texto.setShadowLayer(2, 1, 1, R.color.accent);
                    texto.setTextColor(getApplication().getResources().getColor(R.color.colortres));

                    TableRow.LayoutParams textoenlayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    my_layout.addView(texto, textoenlayout);












                } catch (JSONException e) {
                    Log.d("erroro",e.toString());
                }
            }
        }

    }

}
