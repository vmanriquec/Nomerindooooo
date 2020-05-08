package com.coronacovid.nomerindooooo.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coronacovid.nomerindooooo.Listaproductos;
import com.coronacovid.nomerindooooo.Subirproductos;
import com.coronacovid.nomerindooooo.modelos.Detallepedido;
import com.coronacovid.nomerindooooo.modelos.Productos;
import com.coronacovid.nomerindooooo.R;
import com.coronacovid.nomerindooooo.modelos.Detallepedido;
import com.coronacovid.nomerindooooo.modelos.Productos;
import com.squareup.picasso.Picasso;

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
import jp.wasabeef.picasso.transformations.CropSquareTransformation;



public class Adaptadormaestraproducto extends RecyclerView.Adapter<Adaptadormaestraproducto.AdaptadorViewHolder> {
    private Context mainContext;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    String foto;
    SharedPreferences prefs;
    String FileName ="myfile";
    private List<Productos> items;
    ArrayList<Detallepedido> detallepedido=new ArrayList<>();
    Detallepedido objdetallepedido;
    Realm realm = Realm.getDefaultInstance();
    public Adaptadormaestraproducto(List<Productos> items, Context contexto){
        this.mainContext=contexto;
        this.items=items;
        prefs = mainContext.getSharedPreferences(FileName, Context.MODE_PRIVATE);

          String idalmacenactiv = prefs.getString("idalmacenactivo", "");


    }
    static class AdaptadorViewHolder extends RecyclerView.ViewHolder{
        protected TextView productonombre;
        protected TextView idproducto;
        protected TextView productoprecio;
        protected TextView productoingredientes,stockp;
        protected ImageView productoimagen;
        protected TextView inventario;
    protected  ImageView getProductoimagen;
        protected Button editar,eliminar;
        ;

        public AdaptadorViewHolder(View v){
            super(v);
            this.productonombre=(TextView) v.findViewById(R.id.nombreproductoproducto);
            this.productoprecio=(TextView) v.findViewById(R.id.precioproducto);
            this.idproducto=(TextView) v.findViewById(R.id.idproductoproducto);
            this.inventario=(TextView) v.findViewById(R.id.inventarioproducto);
            this.productoingredientes=(TextView) v.findViewById(R.id.ingredientesproductos);
            this.productoimagen=(ImageView) v.findViewById(R.id.imagenproductos);
this.eliminar=(Button)v.findViewById(R.id.eliminar);


        }
    }
    @Override
    public Adaptadormaestraproducto.AdaptadorViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tarjetamuestraproducto,viewGroup,false);
        return new Adaptadormaestraproducto.AdaptadorViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final Adaptadormaestraproducto.AdaptadorViewHolder viewHolder, final int position) {
        final Productos item = items.get(position);
        viewHolder.itemView.setTag(item);
        viewHolder.productonombre.setText(item.getNombreproducto());
        viewHolder.productoingredientes.setText(item.getIngredientes());
        viewHolder.productoprecio.setText("S/. "+ String.valueOf(item.getPrecventa()));
        viewHolder.idproducto.setText(String.valueOf(item.getIdproducto()));
if (item.getEstadoproducto().equals("")){

            viewHolder.inventario.setText("0");

        }else{

            viewHolder.inventario.setText(String.valueOf(item.getEstadoproducto()));
        }


        //viewHolder.michek.setVisibility(View.GONE);
        //viewHolder.cantidadpedida.setVisibility(View.GONE);

/*asignar imagen desde url*/
        foto=item.getDescripcion().toString();

        Picasso.get().load(foto).transform(new CropSquareTransformation()).resize(200, 200)
                .into( viewHolder.productoimagen);

        viewHolder.productoimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast ImageToast = new Toast(mainContext.getApplicationContext());
                LinearLayout toastLayout = new LinearLayout(mainContext.getApplicationContext());
                toastLayout.setOrientation(LinearLayout.VERTICAL);

                ImageView image = new ImageView(mainContext.getApplicationContext());
                TextView text = new TextView(mainContext.getApplicationContext());
                foto=item.getDescripcion().toString();
                Picasso.get().load("image_URL").into(viewHolder.productoimagen);
                Picasso.get().load(foto).transform(new CropSquareTransformation())
                        .resize(350, 350)
                        .into( image);
                text.setText(item.getIngredientes());
                text.setTextColor(Color.RED);
                text.setBackgroundColor(Color.WHITE);
                text.setGravity(12);
                toastLayout.addView(image);
                toastLayout.addView(text);
                ImageToast.setView(toastLayout);
                ImageToast.setGravity (Gravity.TOP | Gravity.LEFT, 40, 40);
                ImageToast.setDuration(Toast.LENGTH_LONG);
                ImageToast.show();


                ImageToast.getView().setPadding( 20, 100, 20, 20);



            }
        });



        /*si esta check activo para aumentar cantidad*/



        //prefs = mainContext.getApplicationContext().getSharedPreferences(FileName, Context.MODE_PRIVATE);
        //String idalmacenactiv = prefs.getString("idalmacenactivosf", "1");
        //int i= Integer.parseInt(idalmacenactiv);

viewHolder.eliminar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
String oo=String.valueOf(item.getIdproducto());
        new eliminarproducto().execute(oo);
    }
});


    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    private class eliminarproducto extends AsyncTask<String, String, String> {
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
                url = new URL("https://sodapop.pe/sugest/apieliminarproducto.php");
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

                        .appendQueryParameter("idproducto", params[0])
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

        }

        }

    }



