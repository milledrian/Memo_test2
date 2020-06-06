package com.example.memo_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

public class CreatePage extends AppCompatActivity implements View.OnClickListener{

    MemoHelper helper = null;
    String id ="";
    boolean Flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);

        if(helper == null){
            helper = new MemoHelper(CreatePage.this);
        }
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            Cursor data_set = db.rawQuery("select body from MEMO_TABLE where uuid = '"+ id +"'", null);
            boolean first = data_set.moveToFirst();
            while (first) {
                String dispBody = data_set.getString(0);
                EditText body = (EditText)findViewById(R.id.text_body);
                body.setText(dispBody, TextView.BufferType.NORMAL);
                first = data_set.moveToNext();
            }
        } finally {
            db.close();
        }

        findViewById(R.id.return_button).setOnClickListener(this);
        findViewById(R.id.save_button).setOnClickListener(this);


    }
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.return_button:
                finish();
                break;
            case R.id.save_button:
                EditText body = (EditText)findViewById(R.id.text_body);
                String data_body = body.getText().toString();

                SQLiteDatabase db = helper.getWritableDatabase();
                try{
                    db.execSQL("update MEMO_TABLE set body = '"+ data_body +"' where uuid = '"+id+"'");
                } finally{
                    db.close();
                }
                finish();
                break;
        }
    }
}
