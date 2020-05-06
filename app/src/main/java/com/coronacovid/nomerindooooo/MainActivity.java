package com.coronacovid.nomerindooooo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.coronacovid.nomerindooooo.adaptadores.Adaptadorparaverenviarpedidos;
import com.coronacovid.nomerindooooo.adaptadores.Adaptadorrecibepedidos;
import com.coronacovid.nomerindooooo.modelos.AdicionalRealm;
import com.coronacovid.nomerindooooo.modelos.AdicionalRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.CremaRealm;
import com.coronacovid.nomerindooooo.modelos.CremaRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.CrudadicionalRealm;
import com.coronacovid.nomerindooooo.modelos.CrudcremaRealm;
import com.coronacovid.nomerindooooo.modelos.Crudetallepedido;
import com.coronacovid.nomerindooooo.modelos.Crudpedido;
import com.coronacovid.nomerindooooo.modelos.DetallepedidoRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.Detallepedidorealm;
import com.coronacovid.nomerindooooo.modelos.PedidoRealm;
import com.coronacovid.nomerindooooo.modelos.PedidoRealmFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public String idfirebase="",cremasentexto="";
    Context context;
    RecyclerView detalles;
    public ArrayList<DetallepedidoRealmFirebase> todoslosdetalles = new ArrayList<>();
    public ArrayList<PedidoRealmFirebase> todoslospedidos = new ArrayList<>();
    public ArrayList<CremaRealmFirebase> todaslascremas = new ArrayList<>();
    public ArrayList<AdicionalRealmFirebase> todoslosadicionales = new ArrayList<>();
    ArrayList<PedidoRealmFirebase> pb = new ArrayList<PedidoRealmFirebase>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs;
        String FileName = "myfile";

        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("pedido.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        eliminartododerealm();

        final RecyclerView pedidos= findViewById(R.id.recycler);

        pedidos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("PEDIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<DetallepedidoRealmFirebase>dp=new ArrayList<DetallepedidoRealmFirebase>();
                ArrayList<CremaRealmFirebase>cf=new ArrayList<CremaRealmFirebase>();
                ArrayList<AdicionalRealmFirebase>ad=new ArrayList<AdicionalRealmFirebase>();
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    eliminartododerealm();
                    PedidoRealmFirebase pedidoRealmFirebase=snapshot.getValue(PedidoRealmFirebase.class);
                    pb.add(pedidoRealmFirebase);


                }
                new traerpedidos().execute("1");
                int g=pb.size();

                String idfirebase="";
//el getidfirebase
                for (int u=0;u<g;u++){
                    idfirebase=pb.get(u).getIdfirebase().toString();
                    new traerdetallesdepedidofirebase().execute(idfirebase);
                  }

                MediaPlayer mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.voz);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.start();


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Button refresca=(Button) findViewById(R.id.refresca);
        refresca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Listaproductos.class);
                startActivityForResult(intent, 0);
            }
        });


    }

    private void eliminartododerealm() {
        Realm pedido = Realm.getDefaultInstance();
        pedido.beginTransaction();
        pedido.deleteAll();
        pedido.commitTransaction();

    }



    class traeradicionales extends AsyncTask<String, String, String> {
        HttpURLConnection conne;
        URL url = null;
        ArrayList<DetallepedidoRealmFirebase> listaalmaceno = new ArrayList<DetallepedidoRealmFirebase>();
        ProgressDialog pdLoading = new ProgressDialog(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // pdLoading.setMessage("\tCargando cremas :)");
            //pdLoading.setCancelable(false);
            //pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://sodapop.pe/sugest/traeradicionalesdetallefirebase.php");
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

                        .appendQueryParameter("iddetalle", params[0]);

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


            AdicionalRealmFirebase mesoproducto;
            String[] strArrDatausuario = {"No Suggestions"};
            ArrayList<String> dataListusuario = new ArrayList<String>();
            if (result.equals("no rows")) {
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);
                       // mesoproducto = new AdicionalRealmFirebase(json_data.getInt("idadicional"),json_data.getString("nombreadicional")
                         //       ,json_data.getDouble("precioadicional"),json_data.getString("estadoadicional"), json_data.getInt("idproducto"),1,json_data.getInt("iddetalle"));

                        realmgrabaradicional(json_data.getString("nombreadicional"),json_data.getDouble("precioadicional"),json_data.getInt("idadicional")
                                , json_data.getInt("idproducto"),json_data.getInt("iddetalle"),json_data.getInt("iddetalle"));

                    }

                } catch (JSONException e) {
                    Log.d("erroro",e.toString());
                }
            }

        }
    }
    class traercremas extends AsyncTask<String, String, String> {
        HttpURLConnection conne;
        URL url = null;
        ArrayList<DetallepedidoRealmFirebase> listaalmaceno = new ArrayList<DetallepedidoRealmFirebase>();
        ProgressDialog pdLoading = new ProgressDialog(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pdLoading.setMessage("\tCargando cremas :)");
           //pdLoading.setCancelable(false);
           // pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://sodapop.pe/sugest/traercremasdedetallesfirebase.php");
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

                        .appendQueryParameter("iddetalle", params[0]);

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


            CremaRealmFirebase mesoproducto;
            String[] strArrDatausuario = {"No Suggestions"};
            ArrayList<String> dataListusuario = new ArrayList<String>();
            if (result.equals("no rows")) {
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);
                        mesoproducto = new CremaRealmFirebase(json_data.getInt("idcremadetalle"),json_data.getString("nombrecrema")
                                ,json_data.getString("estadocrema"),json_data.getInt("iddetalle"),json_data.getInt("idproducto"));

                        realgrabarcrema(json_data.getString("nombrecrema"),json_data.getInt("idproducto"),json_data.getInt("iddetalle"),json_data.getInt("iddetalle"));
                        todaslascremas.add(mesoproducto);

                    }

                } catch (JSONException e) {
                    Log.d("erroro",e.toString());
                }
            }

        }
    }

    private class traerdetallesdepedidofirebase extends AsyncTask<String, String, String> {
        HttpURLConnection conne;
        URL url = null;
        ArrayList<DetallepedidoRealmFirebase> listaalmaceno = new ArrayList<DetallepedidoRealmFirebase>();


       // ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

//           pdLoading.setMessage("\tCargando detalles :)");
  //          pdLoading.setCancelable(false);
    //        pdLoading.show();


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://sodapop.pe/sugest/traerdetallesporidfirebase.php");
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

                        .appendQueryParameter("idfirebase", params[0]);

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




            DetallepedidoRealmFirebase mesoproducto;
            String[] strArrDatausuario = {"No Suggestions"};
            ArrayList<String> dataListusuario = new ArrayList<String>();
            if (result.equals("no rows")) {
            } else {

                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.optJSONObject(i);
                        mesoproducto = new DetallepedidoRealmFirebase(json_data.getInt("iddetallepedido"), json_data.getInt("idproducto"), json_data.getInt("cantidad")
                                , json_data.getDouble("precventa"), json_data.getDouble("subtotal"), json_data.getInt("idpedido"), json_data.getInt("idalmacen")
                                , json_data.getString("adicionales"), json_data.getString("cremas"), json_data.getString("comentario"), json_data.getInt("ojo"),
                                json_data.getString("imagenreal"), json_data.getString("comentarioacocina"), json_data.getString("nombreproductorealm"), "1");
                        todoslosdetalles.add(mesoproducto);
                        realmgrbarenbasedatosdetallepedido(json_data.getInt("idproducto"), json_data.getInt("cantidad"), json_data.getString("nombreproductorealm")
                                , json_data.getDouble("precventa"), json_data.getInt("idpedido"), json_data.getString("subtotal"), json_data.getString("comentarioacocina"), json_data.getInt("iddetallepedido"));
                    }

                    int g = todoslosdetalles.size();
                    for (int u = 0; u < g; u++) {
                        int ty = todoslosdetalles.get(u).getIddetallepedido();
                        String yh = String.valueOf(ty);
                        new traercremas().execute(yh);
                        new traeradicionales().execute(yh);


                    }
                 //   new traerpedidos().execute("1");
      //              pdLoading.dismiss();



                } catch (JSONException e) {
                    Log.d("erroro", e.toString());
                }



            }
        }
    }

    public  static void realmgrbarenbasedatosdetallepedido(final int idproducto, final int cantidad, final String nombre, final Double precio, final int idpedido, final String subtotal, final String comentariococina, final int detallepedido) {
        Realm pedido = Realm.getDefaultInstance();
        pedido.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm pedido) {
                int index = Crudetallepedido.calculateIndex();

                Detallepedidorealm realmDetallepedidorealm = pedido.createObject(Detallepedidorealm.class, index);
                realmDetallepedidorealm.setIdproductorealm(idproducto);
                realmDetallepedidorealm.setCantidadrealm(cantidad);
                realmDetallepedidorealm.setNombreproductorealm(nombre);
                realmDetallepedidorealm.setPrecventarealm(precio);
                realmDetallepedidorealm.setSubtotal(subtotal);
                realmDetallepedidorealm.setComentarioacocina(comentariococina);
                realmDetallepedidorealm.setIdpedido(idpedido);

                realmDetallepedidorealm.setIddetallepedido(detallepedido);

            }

        });


    }

    public  static void realmgrabaradicional(final String nombreadicional, final Double precioadicional
            ,final int idadicional,final int idproducto, final int idadicionaldetalle, final int iddetalle) {

        Realm pedido = Realm.getDefaultInstance();

        pedido.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm pedido) {
                int index = CrudadicionalRealm.calculateIndex();
                AdicionalRealm AdicionalRealm = pedido.createObject(AdicionalRealm.class, index);
                //AdicionalRealm.setId(iddetalle);
                AdicionalRealm.setId(idadicional);
                AdicionalRealm.setIdproducto(idproducto);
                AdicionalRealm.setNombreadicional(nombreadicional);
                AdicionalRealm.setPrecioadicional(precioadicional);
                AdicionalRealm.setIdadicionaldetalle(idadicionaldetalle);
                AdicionalRealm.setEstadoadicional("1");
            }
        });


    }

    public  static void realgrabarcrema(final String nombrecrema, final int idproducto, final int idcremadetalle, final int iddetalle) {
        Realm pedido = Realm.getDefaultInstance();
        pedido.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm pedido) {

                int index = CrudcremaRealm.calculateIndex();
                CremaRealm CremaRealm = pedido.createObject(CremaRealm.class, index);
                CremaRealm.setEstadocrema("1");
                CremaRealm.setIdproducto(idproducto);
                CremaRealm.setNombrecrema(nombrecrema);
                CremaRealm.setIdcrema(iddetalle);
                CremaRealm.setIddetalle(iddetalle);

            }
        });


    }



    public  static void realgrabarpedido(
            final int idpedido, final int idcliente, final
    int idmesa, final Double totalpedido,final String estadopedido,
            final String fechapedido,final int idusuario, final int idalmacen,
            final String idfacebook, final String descripcionpedido,
            final String llevar, final String direccionallevar) {
                Realm pedido = Realm.getDefaultInstance();
        pedido.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm pedido) {
                int index = Crudpedido.calculateIndex();
                PedidoRealm realmDetallepedidorealm = pedido.createObject(PedidoRealm.class, index);
                realmDetallepedidorealm.setIdpedido(idpedido);
                realmDetallepedidorealm.setIdcliente(idcliente);
                realmDetallepedidorealm.setIdmesa(idmesa);
                realmDetallepedidorealm.setTotalpedido(totalpedido);
                realmDetallepedidorealm.setEstadopedido(estadopedido);
                realmDetallepedidorealm.setFechapedido(fechapedido);
                realmDetallepedidorealm.setIdalmacen(idalmacen);
                realmDetallepedidorealm.setIdfacebook(idfacebook);
                realmDetallepedidorealm.setIdusuario(idusuario);
                realmDetallepedidorealm.setIdalmacen(idalmacen);
                realmDetallepedidorealm.setDescripcionpedido(descripcionpedido);
               // realmDetallepedidorealm.setLlevar(llevar);
              //  realmDetallepedidorealm.setd(direccionallevar);

                //realmDetallepedidorealm.setIdpedido(index);

            }

        });


    }


    public class traerpedidos extends AsyncTask<String, String, String> {

        HttpURLConnection conne;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("https://sodapop.pe/sugest/traertodoslospedidosdelivery.php");
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
                        .appendQueryParameter("nombre", params[0]);
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
                    return (result.toString());

                } else {
                    return("Connection error");
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
            Log.d("waaaaaaa",result);
            //if(result.equals("no rows")) {
            //}else{

            //}
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                 PedidoRealmFirebase   pedidofirebase = new PedidoRealmFirebase
                         (json_data.getInt("idpedido"),
                                 json_data.getInt("idcliente"),
                                 json_data.getInt("idmesa")
                                 , json_data.getDouble("totalpedido")
                                 ,json_data.getString("estadopedido"),
                                 json_data.getString("fechapedido")
                                 ,json_data.getInt("idusuario"),
                                 json_data.getInt("idalmacen"),
                                 json_data.getString("idfacebook"),
                                 json_data.getString("observaciones")
                         ,json_data.getString("llevar")
                                 ,json_data.getString("direccionllevar"),
                                 json_data.getString("idfirebase"),
                                 json_data.getString("monbredescuento")
                                 ,json_data.getString("montodescuento")
                                 ,json_data.getString("nombrecosto"),
                                 json_data.getString("montocosto"),
                                 json_data.getString("longitud")
                        ,json_data.getString("latitud"),
                                 json_data.getString("pagocliente"),
                                 json_data.getString("vuelto")
                         ,json_data.getString("telefono"),
                                 json_data.getString("refrencias"),
                                 json_data.getString("nombreusuariof"),
                                 json_data.getString("idfacebook"));
                    todoslospedidos.add(pedidofirebase);


                }



                int ere =todoslospedidos.size();

for(int a=0;a<ere;a++){

    realgrabarpedido(
            todoslospedidos.get(a).getIdpedido(),
            todoslospedidos.get(a).getIdcliente(),
            todoslospedidos.get(a).getIdmesa()
    ,todoslospedidos.get(a).getTotalpedido(),
            todoslospedidos.get(a).getEstadopedido(),
            todoslospedidos.get(a).getFechapedido()
    ,todoslospedidos.get(a).getIdusuario(),
            todoslospedidos.get(a).getIdalmacen()
            ,todoslospedidos.get(a).getIdfacebook()
            ,todoslospedidos.get(a).getDescripcionpedido(),
            todoslospedidos.get(a).getLlevar(),
            todoslospedidos.get(a).getDireccionallevar());
}
                final RecyclerView pedidos= findViewById(R.id.recycler);

                Adaptadorrecibepedidos adaptador = new Adaptadorrecibepedidos( todoslospedidos,MainActivity.this);
                pedidos.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();

} catch (JSONException e) {


                Log.d("erororor",e.toString());
            }


        }

    }





}


