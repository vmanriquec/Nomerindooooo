package com.coronacovid.nomerindooooo.adaptadores;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coronacovid.nomerindooooo.Agregaradicionales;
import com.coronacovid.nomerindooooo.MainActivity;

import com.coronacovid.nomerindooooo.R;
import com.coronacovid.nomerindooooo.modelos.Adicional;
import com.coronacovid.nomerindooooo.modelos.AdicionalRealm;
import com.coronacovid.nomerindooooo.modelos.AdicionalRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.CremaRealm;
import com.coronacovid.nomerindooooo.modelos.CremaRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.DetallepedidoRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.Detallepedidorealm;
import com.coronacovid.nomerindooooo.modelos.PedidoRealmFirebase;
import com.coronacovid.nomerindooooo.modelos.Productoadicional;

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
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class Adaptadorrecibepedidos extends RecyclerView.Adapter<Adaptadorrecibepedidos.AdaptadorViewHolder>  {
    private MainActivity mainContext;
    String foto;
    SharedPreferences prefs;
    String FileName ="myfile";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private List<PedidoRealmFirebase> items;
    public Adaptadorrecibepedidos(ArrayList<PedidoRealmFirebase> items, MainActivity contexto){
        this.mainContext=contexto;
        this.items=items;


    }
    static class AdaptadorViewHolder extends RecyclerView.ViewHolder{
        protected TextView nombre;
        protected TextView direccion,referencias,cuantopaga,vuelto,totalapagar1,idpedido;

        protected TextView telefono;
        protected Button wasap,rechazarpedido,muestrapedido;

        public AdaptadorViewHolder(View v){
            super(v);
            this.nombre=(TextView) v.findViewById(R.id.nombrecliente);
            this.direccion=(TextView) v.findViewById(R.id.direccion);
            this.totalapagar1=(TextView) v.findViewById(R.id.totalapagar1);
            this.telefono=(TextView) v.findViewById(R.id.telefono);
            this.referencias=(TextView) v.findViewById(R.id.referencias);
            this.cuantopaga=(TextView) v.findViewById(R.id.cuantopaga);
            this.vuelto=(TextView) v.findViewById(R.id.vuelto);
            this.idpedido=(TextView) v.findViewById(R.id.idpedido);
            this.wasap=(Button) v.findViewById(R.id.watsapp);
            this.rechazarpedido=(Button) v.findViewById(R.id.rechazarorden);
this.muestrapedido=(Button)v.findViewById(R.id.muestrapedido);
        }
    }
    @Override
    public Adaptadorrecibepedidos.AdaptadorViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tarjetapedidos,viewGroup,false);
        return new Adaptadorrecibepedidos.AdaptadorViewHolder(v);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final Adaptadorrecibepedidos.AdaptadorViewHolder viewHolder, final int position) {
        final PedidoRealmFirebase item = items.get(position);
        viewHolder.itemView.setTag(item);

        viewHolder.vuelto.setText(String.valueOf(item.getVuelto()));
        viewHolder.cuantopaga.setText(String.valueOf(item.getCuantopagaecliente()));
        viewHolder.referencias.setText(String.valueOf(item.getReferencias()));
        viewHolder.idpedido.setText(String.valueOf(item.getIdpedido()));
viewHolder.nombre.setText(String.valueOf(item.getNombreusuario()));
        viewHolder.direccion.setText(item.getDireccionallevar());
        viewHolder.totalapagar1.setText(String.valueOf(item.getTotalpedido()));
        viewHolder.telefono.setText(String.valueOf(item.getTelefono()));


viewHolder.muestrapedido.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final Dialog dialog = new Dialog(mainContext);
        dialog.setTitle("Details");
        dialog.setContentView(R.layout.dialogo);
        RecyclerView detalles=(RecyclerView) dialog.findViewById(R.id.detalle);


        String hh=viewHolder.idpedido.getText().toString();
        Realm pedido = Realm.getDefaultInstance();
        ArrayList<DetallepedidoRealmFirebase> todoslosdetalles = new ArrayList<>();

        RealmResults<Detallepedidorealm> results =
                pedido.where(Detallepedidorealm.class)
                        .equalTo("idpedido", Integer.valueOf(hh))
                        .findAll();
        int w = results.size();
        DetallepedidoRealmFirebase detalleainsertar ;
        for (int i = 0; i < w; i++) {
            int cantidad = results.get(i).getCantidadrealm();
            int idd = results.get(i).getIddetallepedido();
            int idpedido = results.get(i).getIdpedido();
            String nombreproducto = results.get(i).getNombreproductorealm();
            String subtotal = results.get(i).getSubtotal();
            Double precvente = results.get(i).getPrecventarealm();
            int idproductorealm = results.get(i).getIdproductorealm();
            String comentariococina = results.get(i).getComentarioacocina();
            ArrayList<CremaRealmFirebase> todaslascremas = new ArrayList<>();
            RealmResults<CremaRealm> resulcremaao =
                    pedido.where(CremaRealm.class)
                            .equalTo("iddetalle", idd)
                            .findAll();
            int lsrgaa = resulcremaao.size();
            for (int ic = 0; ic < lsrgaa; ic++) {
                RealmResults<CremaRealm> resultsoa =
                        pedido.where(CremaRealm.class)
                                .equalTo("iddetalle", idd)
                                .findAll();
                CremaRealmFirebase dett = new CremaRealmFirebase(ic, resultsoa.get(ic).getNombrecrema().toString(), "1", 1, idproductorealm);
                todaslascremas.add(dett);
            }
            int g=todaslascremas.size();
            String fodat="";
            String foda="";
            for (int u=0;u<g;u++){
                foda=todaslascremas.get(u).getNombrecrema().toString();
                fodat=fodat+foda+"\r\n";



            }
////////////////////////////////////////////adicionales
            ArrayList<AdicionalRealmFirebase> todoslosadicionalesa = new ArrayList<>();
            RealmResults<AdicionalRealm> resulcremaad =
                    pedido.where(AdicionalRealm.class)
                            .equalTo("idadicionaldetalle", idd)
                            .findAll();
            int lsrgaad = resulcremaad.size();
            for (int ica = 0; ica < lsrgaad; ica++) {
                RealmResults<AdicionalRealm> resultsoad =
                        pedido.where(AdicionalRealm.class)
                                .equalTo("idadicionaldetalle", idd)
                                .findAll();
                AdicionalRealmFirebase dettd = new AdicionalRealmFirebase(ica,resultsoad.get(ica).getNombreadicional(),
                        Double.parseDouble(resultsoad.get(ica).getPrecioadicional().toString()),
                        resultsoad.get(ica).getEstadoadicional(),resultsoad.get(ica).getIdproducto(),resultsoad.get(ica).getId(),resultsoad.get(ica).getId());
                todoslosadicionalesa.add(dettd);
            }

            int ga=todoslosadicionalesa.size();
            String fodata="";
            String fodaa="";
            for (int ua=0;ua<ga;ua++){
                fodaa=todoslosadicionalesa.get(ua).getNombreadicional().toString();
                fodata=fodata+fodaa+"\r\n";
            }
            detalleainsertar = new DetallepedidoRealmFirebase(1,1  , cantidad, precvente, Double.parseDouble(subtotal), idpedido, 1, fodata, fodat, comentariococina,1,fodata,comentariococina,nombreproducto,"1");


            ///////////////////////////////////////////////////////





            todoslosdetalles.add(detalleainsertar);


            Adaptadorparaverenviarpedidos adapterproducto = new Adaptadorparaverenviarpedidos(todoslosdetalles,mainContext);
            detalles.setLayoutManager(new GridLayoutManager(mainContext, 1));
            detalles.setAdapter(adapterproducto);


        }






        dialog.show();
    }
});

viewHolder.rechazarpedido.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String op=viewHolder.idpedido.getText().toString();
        new rechazapedido().execute(op);
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

    }
});


        viewHolder.wasap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                try {
                   Intent intent = new Intent(Intent.ACTION_VIEW);
                  intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+viewHolder.telefono.getText().toString()+"&text="+"Hola, puedes comunicarte con migo por este medio"));
                 mainContext.startActivity(intent);
                }catch (Exception e){
                  e.printStackTrace();
                }



            }
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }



    private class rechazapedido extends AsyncTask<String, String, String> {
        HttpURLConnection conne;
//        ProgressDialog pdLoading = new ProgressDialog(mainContext.this);
        URL url = null;
        ArrayList<Adicional> listadeadicionales = new ArrayList<Adicional>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

  //          pdLoading.setMessage("\tCargando Adicionales");
    //        pdLoading.setCancelable(false);
      //      pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://sodapop.pe/sugest/apirechazarpedido.php");
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
                        .appendQueryParameter("idpedido", params[0]);
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
            Log.d("paso",result.toString());
        //    pdLoading.dismiss();

            if (result.equals("no rows")) {
            } else {

            }
        }

    }




}

