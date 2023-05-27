package com.example.parent_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String IP = "192.168.154.111"; // Da controllare ad ogni avvio
    private Button btnCall;
    private TextView tvNome;
    private TextView tvCognome;
    private TextView tvTipo;
    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCall = findViewById(R.id.btnCall);
        tvNome = findViewById(R.id.tvNome);
        tvCognome = findViewById(R.id.tvCognome);
        tvTipo = findViewById(R.id.tvTipo);
        tvData = findViewById(R.id.tvData);

        btnCall.setOnClickListener(v -> {
            RequestQueue queue = Volley.newRequestQueue(this);

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, "http://" + IP + ":3000/select/1", null,
                    response -> {
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject current = response.getJSONObject(i);
                                tvNome.setText(Html.fromHtml("<b>Nome:</b> " + current.getString("nome") + "\n", 0));
                                tvCognome.setText(Html.fromHtml("<b>Cognome:</b> " + current.getString("cognome") + "\n", 0));
                                tvTipo.setText(Html.fromHtml("<b>Tipo:</b> " + current.getString("tipo") + "\n", 0));
                                tvData.setText(Html.fromHtml("<b>Data:</b> " + current.getString("data") + "\n", 0));
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }, error -> tvNome.setText(error.toString())
            );
            queue.add(stringRequest);
        });
    }
}