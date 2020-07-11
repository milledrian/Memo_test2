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
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {

    private static String TAG = "MemoHelper";
    MemoHelper helper = null;
    private String id_memo ="";
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
        helper = new MemoHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] temp_id ={id_memo};
        Cursor date;
        if(id_memo!= "") {
            try {
                Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE where uuid=?", temp_id);
                date = db.rawQuery("select ref_uuid,data from DATA_TABLE where ref_uuid=?", temp_id);
                boolean first = data_set.moveToFirst();
                date.moveToFirst();
                ListItem memo_data = new ListItem();

                memo_data.setUuid(data_set.getString(0));
                memo_data.setBody(data_set.getString(1));
                memo_data.setData(date.getString(1));
                memo_list.update(memo_position,memo_data);
            } finally {
                db.close();
            }
        }

    }

    public void SetView(){
        if(helper == null){
            helper = new MemoHelper(MainActivity.this);
        }
        final ArrayList<ListItem> Lists = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();

        try{
            Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE order by id", null);
            Cursor date;
            String[] temp_id;
            boolean first = data_set.moveToFirst();
            while(first){
                ListItem List = new ListItem();
                List.setUuid(data_set.getString(0));
                List.setBody(data_set.getString(1));
                temp_id = new String[]{List.getUuid()};

                date = db.rawQuery("select ref_uuid,data from DATA_TABLE where ref_uuid=?", temp_id);
                date.moveToFirst();

                List.setData(date.getString(1));
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
                id_memo = memo_list.getUUID(position);
                memo_position = position;
                intent.putExtra("id", id_memo);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                id_memo = memo_list.getUUID(position);
                SQLiteDatabase db = helper.getWritableDatabase();
                String[] temp_id = {id_memo};
                db.delete("MEMO_TABLE", "uuid=?", temp_id);
                db.delete("DATA_TABLE", "ref_uuid=?", temp_id);
                Lists.remove(position);
                memo_list.notifyDataSetChanged();
                return true;
            }
        });


        // 新規作成ボタン
        new_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues create_data = new ContentValues();
                id_memo = UUID.randomUUID().toString();
                String[] temp_id = {id_memo};
                memo_position = memo_list.getCount();

                create_data.put("body","");
                create_data.put("uuid",id_memo);
                db.insert("MEMO_TABLE",null,create_data);

                create_data.clear();

                create_data.put("ref_uuid",id_memo);
                create_data.put("data","");
                db.insert("DATA_TABLE",null,create_data);

                ListItem List = new ListItem();

                Cursor data_set = db.rawQuery("select uuid, body from MEMO_TABLE where uuid=?",temp_id);

                boolean first = data_set.moveToFirst();

                List.setUuid(data_set.getString(0));
                List.setBody(data_set.getString(1));
                memo_list.add(List);

                Intent intent = new Intent(MainActivity.this, CreatePage.class);
                intent.putExtra("id", id_memo);
                startActivity(intent);
            }
        });
    }


}
