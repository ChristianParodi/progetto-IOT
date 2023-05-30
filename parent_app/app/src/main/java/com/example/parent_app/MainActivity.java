package com.example.parent_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
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
    private static final String IP = "192.168.151.187"; // Da controllare ad ogni avvio

    private static int childId = -1;
    private TextView tvError;
    private ScrollView scrollView;
    private ImageView iwIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iwIcon = findViewById(R.id.image_view_icons);
        tvError = findViewById(R.id.tv_error_scroll);
        scrollView = findViewById(R.id.scroll_view);

        if(childId == -1){
            Intent i = new Intent(this, RequestChildDataActivity.class);
            startActivityForResult(i, RequestChildDataActivity.ACTIVITY_ID);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestChildDataActivity.ACTIVITY_ID){
            if(resultCode == RESULT_OK){
                assert data != null;
                childId = data.getIntExtra("child_id", -1);

                RequestQueue queue = Volley.newRequestQueue(this);

                JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, "http://" + IP + ":3000/select/" + childId, null,
                        response -> {
                            try {
                                if(response.length() == 0){
                                    tvError.setText(Html.fromHtml("Il codice inserito non e' valido", 0));
                                    tvError.setText(response.toString());
                                    return;
                                }

                                for(int i = 0; i < response.length(); i++){
                                    JSONObject current = response.getJSONObject(i);
                                    TextView record = new TextView(this);
                                    try {
                                        record.setText(current.getString("data") + "\t" + current.getString("tipo"));
                                    } catch (JSONException e){
                                        tvError.setText(e.toString());
                                    }
//                            scrollView.addView(record);
                                }
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }, error -> tvError.setText(error.toString())
                );
                queue.add(stringRequest);
            }
        }
    }
}