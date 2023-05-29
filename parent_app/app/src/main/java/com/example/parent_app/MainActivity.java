package com.example.parent_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private static final String IP = "192.168.1.7"; // Da controllare ad ogni avvio

    private static int childId = -1;
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

        if(childId == -1){
            Intent i = new Intent(this, RequestChildDataActivity.class);
            startActivityForResult(i, RequestChildDataActivity.ACTIVITY_ID);
        }

        btnCall.setOnClickListener(v -> {
            RequestQueue queue = Volley.newRequestQueue(this);

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, "http://" + IP + ":3000/select/" + childId, null,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestChildDataActivity.ACTIVITY_ID){
            if(resultCode == RESULT_OK){
                assert data != null;
                childId = data.getIntExtra("child_id", -1);
            }
        }
    }
}