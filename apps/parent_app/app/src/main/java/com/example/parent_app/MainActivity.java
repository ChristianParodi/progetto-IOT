package com.example.parent_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.ListFormatter;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String IP = "172.20.10.2"; // Fede (IP dell'host dell'API)

    private static int childId = -1;
    private TextView tvError;
    private TableLayout history;
    private ImageView iwIcon;
    private TextView childName;
    private TextView isIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iwIcon = findViewById(R.id.image_view_icons);
        tvError = findViewById(R.id.tv_error_scroll);
        history = findViewById(R.id.tl_history);
        childName = findViewById(R.id.tv_name_child);
        isIn = findViewById(R.id.tv_is_in);

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
                                    TextView recordDate = new TextView(this);
                                    TextView recordType = new TextView(this);
                                    TableRow tr = new TableRow(this);

                                    styleTextViews(recordDate, recordType);

                                    try {
                                        // Togliamo i secondi dal datetime
                                        String[] datetime = current.getString("data").split(" ");
                                        datetime[1] = datetime[1].substring(0, 5);
                                        recordDate.setText(datetime[0] + " " + datetime[1]);

                                        String type = current.getString("tipo");
                                        recordType.setText(type);
                                        recordType.setTextColor(type.equalsIgnoreCase("uscita") ? Color.RED : Color.GREEN);

                                    } catch (JSONException e){
                                        tvError.setText(e.toString());
                                    }
                                    tr.addView(recordDate);
                                    tr.addView(recordType);
                                    history.addView(tr);
                                }

                                // Setta l'icona in base all'ultimo elemento (se uscita o ingresso)
                                // Sono gia' automaticamente ordinati per data
                                // mettiamo anche nome e cognome del pargolo
                                JSONObject lastRecord = response.getJSONObject(0);
                                childName.setText(lastRecord.getString("nome") + " " + lastRecord.getString("cognome"));

                                if(lastRecord.getString("tipo").equalsIgnoreCase("ingresso")){
                                    iwIcon.setImageResource(R.drawable.baseline_check_circle_24);
                                    isIn.setTextColor(Color.GREEN);
                                    isIn.setText("Dentro");
                                } else {
                                    iwIcon.setImageResource(R.drawable.baseline_dangerous_24);
                                    isIn.setTextColor(Color.RED);
                                    isIn.setText("Fuori");
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

    private static void styleTextViews(TextView recordDate, TextView recordType) {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        recordDate.setTextSize(16);
        recordDate.setLayoutParams(layoutParams);
        recordDate.setGravity(Gravity.CENTER_HORIZONTAL);
        recordType.setTextSize(16);
        recordType.setLayoutParams(layoutParams);
        recordType.setGravity(Gravity.CENTER_HORIZONTAL);
    }
}