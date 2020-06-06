package com.example.memo_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {

    MemoHelper helper = null;

    private String id_memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetView();

    }

    protected void onResume(){
        super.onResume();
        SetView();
    }

    public void SetView(){
        if(helper == null){
            helper = new MemoHelper(MainActivity.this);
        }
        final ArrayList<HashMap<String, String>> List = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();

        try{
            Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE order by id", null);
            boolean first = data_set.moveToFirst();
            while(first){
                HashMap<String,String> data = new HashMap<>();
                data.put("id",data_set.getString(0));
                data.put("body",data_set.getString(1));
                List.add(data);

                first = data_set.moveToNext();
            }
        } finally {
            db.close();
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                List,
                android.R.layout.simple_list_item_2,
                new String[]{"body","id"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        ListView listView = (ListView) findViewById(R.id.List);
        listView.setAdapter(simpleAdapter);
        Button new_button = (Button) findViewById(R.id.new_memo);

        // メモを閲覧
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, CreatePage.class);
                HashMap<String,String> data = (HashMap<String,String>) parent.getItemAtPosition(position);
                String id_memo = data.get("id");
                intent.putExtra("id", id_memo);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> data = (HashMap<String,String>) parent.getItemAtPosition(position);
                id_memo = data.get("id");
                SQLiteDatabase db = helper.getWritableDatabase();
                try {
                    db.execSQL("DELETE FROM MEMO_TABLE WHERE uuid = '"+ id_memo +"'");
                } finally {
                    db.close();
                }
                List.remove(position);
                simpleAdapter.notifyDataSetChanged();
                return true;
            }
        });


        // 新規作成ボタン
        new_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SQLiteDatabase db = helper.getWritableDatabase();
                String uuid = UUID.randomUUID().toString();
                try {
                    db.execSQL("DELETE FROM MEMO_TABLE WHERE uuid = '"+ uuid +"'");
                } finally {
                    db.close();
                }
                Intent intent = new Intent(MainActivity.this, CreatePage.class);
                intent.putExtra("id", id_memo);
                startActivity(intent);
            }
        });
    }


}
