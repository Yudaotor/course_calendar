package com.example.a2022finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn1, btn2, btn3, btn4, btn5, btn6;
    private ListView lv;
    private MyHelper myHelper = new MyHelper(this);
    private MyAdapter ma = new MyAdapter();
    private String weekNo = "0";
    //Class类信息的存储对象
    private List<Class> list_class = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        init();
    }
    // 初始化
    public void init() {
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.buttonEdit);
        lv = findViewById(R.id.list1);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
    }

    // 查找数据库中的课程信息
    @SuppressLint("Range")
    public void query(String day) {
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cursor = db.query("class", null, "weekNo=?", new String[]{day}, null, null, null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                // 加入list_class中存储
                list_class.add(new Class(day, cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("time")), cursor.getString(cursor.getColumnIndex("room"))));
            }
        }
        db.close();
    }
    // 按照时间排序函数
    public static class TimeSort implements Comparator<Class> {
        @Override
        public int compare(Class aClass, Class t1) {
            return aClass.getTime().compareTo(t1.getTime());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 周一
            case R.id.button1:
                weekNo = "1";
                list_class = new ArrayList<>();
                query("1");
                Collections.sort(list_class, new TimeSort());//按照时间排序
                lv.setAdapter(ma);//显示在listview中
                break;
            // 周二
            case R.id.button2:
                weekNo = "2";
                list_class = new ArrayList<>();
                query("2");
                Collections.sort(list_class, new TimeSort());
                lv.setAdapter(ma);
                break;
            // 周三
            case R.id.button3:
                weekNo = "3";
                list_class = new ArrayList<>();
                query("3");
                Collections.sort(list_class, new TimeSort());
                lv.setAdapter(ma);
                break;
            // 周四
            case R.id.button4:
                weekNo = "4";
                list_class = new ArrayList<>();
                query("4");
                Collections.sort(list_class, new TimeSort());
                lv.setAdapter(ma);
                break;
            // 周五
            case R.id.button5:
                weekNo = "5";
                list_class = new ArrayList<>();
                query("5");
                Collections.sort(list_class, new TimeSort());
                lv.setAdapter(ma);
                break;
            // 修改课程信息
            case R.id.buttonEdit:
                // 判断是否选中了某一天
                if (weekNo == "0"){
                    Toast.makeText(this, "请先选择一天从而进行编辑操作", Toast.LENGTH_LONG).show();
                }
                else{
                    // 因为需要传递的数据时ArrayList所以需要使用Bundle来进行传递
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    Bundle bundle = new Bundle();
                    // 注意，对应的Class类需要进行Parcelable化
                    bundle.putParcelableArrayList("list_class", (ArrayList<? extends Parcelable>) list_class);
                    bundle.putString("weekNo", weekNo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_class.size();
        }

        @Override
        public Object getItem(int i) {
            return list_class.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // 配置listview中的显示效果
            View newView = View.inflate(MainActivity.this, R.layout.activity_class, null);
            TextView textView1 = newView.findViewById(R.id.textViewName);
            TextView textView2 = newView.findViewById(R.id.textViewTime);
            TextView textView3 = newView.findViewById(R.id.textViewRoom);
            textView1.setText(list_class.get(i).getName());
            textView2.setText(list_class.get(i).getTime());
            textView3.setText(list_class.get(i).getRoom());
            return newView;
        }
    }
}