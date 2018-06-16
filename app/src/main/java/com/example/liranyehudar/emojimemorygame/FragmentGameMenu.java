package com.example.liranyehudar.emojimemorygame;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentGameMenu extends Fragment {

    static final int RESULT_REQUEST = 1;
    static final int MAX_ROWS = 10;
    static final int RESULT_OK = -1;
    private  String[] menuArr = {"Easy - 2x2","Medium - 4x4","Hard - 6x6"};
    private  int []   levelArr= {2,4,6}; //2x2 , 4x4, 6x6 grid size
    private  int []   timer = {30000,45000,60000}; // millsecond for timer
    private  int []   getPointsFromOneMatching = {20,40,60}; //by the level,get point to final result if there is 2 card matching.
    private  String name;
    private  String age;
    private DBHandler db;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    try {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        context = getActivity();
        db = new DBHandler(context);
        name = this.getArguments().getString("name");
        age = this.getArguments().getString("age");
        ListView lstView = view.findViewById(R.id.listView);
        ArrayAdapter<String> adapterArr = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, menuArr);
        lstView.setAdapter(adapterArr);
        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to the game activity
                Intent i = new Intent(context, GameActivity.class);
                i.putExtra("row", levelArr[position]);
                i.putExtra("col", levelArr[position]);
                i.putExtra("name", name);
                i.putExtra("time", timer[position]);
                i.putExtra("point", getPointsFromOneMatching[position]);
                startActivityForResult(i, RESULT_REQUEST);
            }
        });
        return view;
    }
    catch(Exception e){
        Log.e("error",e.getMessage());
        return null;
    }

    }

    // get result from game
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_REQUEST) {
            if (resultCode == RESULT_OK) {
                checkResult(data.getStringExtra("result"));
            }
        }
    }

    public boolean checkResult(String result) {
        int res = Integer.parseInt(result);
        int currentAge = Integer.parseInt(age);
        Cursor data = db.getAllData();
        Cursor cur = db.findMinByResult();
        int minData = cur.getCount();
        int dataCount = data.getCount();
        if (dataCount < MAX_ROWS)
            db.insertData(name, currentAge, res);
        else {
            cur.moveToFirst();
            String s = cur.getString(1);
            int minInTable = Integer.parseInt(cur.getString(1));
            if (res > minInTable) {
                db.updateData(cur.getString(0), name, currentAge, res);
                Toast.makeText(context, "updated" + result, Toast.LENGTH_LONG).show();
            } else
                return false;
        }
        return true;
    }
}