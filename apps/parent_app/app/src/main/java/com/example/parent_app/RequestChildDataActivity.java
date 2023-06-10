package com.example.parent_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class RequestChildDataActivity extends Activity {
    private static final String TAG = "RequestChildDataActivity";
    public static final int ACTIVITY_ID = 1;
    private TextInputEditText tfChildId;
    private Button btnInsertChildId;
    private TextView tvError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_children_data);

        tfChildId = findViewById(R.id.tf_child_id);
        btnInsertChildId = findViewById(R.id.btn_insert_child_id);
        tvError = findViewById(R.id.tv_error);

        btnInsertChildId.setOnClickListener(v -> {
            String child_id = Objects.requireNonNull(tfChildId.getText()).toString();
            if(child_id.equals("")) {
                tvError.setText("Il campo e' obbligatorio");
                return;
            }

            int childId = -1;
            try {
                childId = Integer.parseInt(child_id);
            } catch(NumberFormatException e){
                tvError.setText("Devi inserire un numero maggiore di 0");
                return;
            }

            if(childId < 1){
                tvError.setText("Devi inserire un numero maggiore di 0");
                return;
            }

            Intent res = new Intent();
            res.putExtra("child_id", childId);
            setResult(RESULT_OK, res);
            finish();
        });
    }
}
