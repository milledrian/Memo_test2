package com.example.memo_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {

    private static String TAG = "MemoHelper";
    MemoHelper helper = null;
    private String id_memo;
    private int memo_position;
    private MyListAdapter memo_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetView();

    }

    protected void onResume(){
        super.onResume();
        if(helper == null) {
            helper = new MemoHelper(MainActivity.this);
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        String[] temp_id = {id_memo};
        try {
            Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE where uuid=?", temp_id);
            boolean first = data_set.moveToFirst();
            HashMap<String, String> data = new HashMap<>();

            data.put("id", data_set.getString(0));
            data.put("body", data_set.getString(1));
        }finally{
            db.close();
        }

    }

    public void SetView(){
        if(helper == null){
            helper = new MemoHelper(MainActivity.this);
        }
        final ArrayList<ListItem> Lists = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.i(TAG, "test4: ");

        try{
            Log.i(TAG, "test5: ");
            Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE order by id", null);
            boolean first = data_set.moveToFirst();
            while(first){
                ListItem List = new ListItem();
                Log.i(TAG, "uuid: "+ data_set.getString(0));
                Log.i(TAG, "body: "+ data_set.getString(1));
                List.setUuid(data_set.getString(0));
                List.setBody(data_set.getString(1));
                Lists.add(List);

                first = data_set.moveToNext();
            }
        } finally {
            db.close();
        }

        memo_list = new MyListAdapter(this, Lists, R.layout.list_item);
        ListView listView = (ListView) findViewById(R.id.List);
        listView.setAdapter(memo_list);

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
                Lists.remove(position);
                memo_list.notifyDataSetChanged();
                return true;
            }
        });


        // 新規作成ボタン
        new_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SQLiteDatabase db = helper.getWritableDatabase();
                id_memo = UUID.randomUUID().toString();
                String[] temp_id = {id_memo};
                try {
                    db.execSQL("insert into MEMO_TABLE(uuid) VALUES('"+ id_memo +"')");
                } finally {
                    db.close();
                }
                memo_position = memo_list.getCount();

                Intent intent = new Intent(MainActivity.this, CreatePage.class);
                intent.putExtra("id", id_memo);
                ListItem List = new ListItem();

                Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE where uuid=?",temp_id);

                boolean first = data_set.moveToFirst();

                List.setUuid(data_set.getString(0));
                List.setBody(data_set.getString(1));
                memo_list.add(List);
                startActivity(intent);
            }
        });
    }


}
