package com.example.a2022finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnAdd, btnDelete, btnZ, btnSave, btnBack;
    private EditText etName, etTime, etRoom;
    private ListView list2;
    private List<Class> list_class = new ArrayList<>();
    private MyAdapter ma = new MyAdapter();
    private MyHelper myHelper = new MyHelper(this);
    private String name, time, room, weekNo;
    private int chosenItem = -1;// 选中的课程序号
    private int showItem;
    private String changedIndex = "0";//0代表先前没有进行操作
    private Class changedItem, changedItem2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }
    //初始化
    public void init() {
        btnAdd = findViewById(R.id.buttonAdd);
        btnDelete = findViewById(R.id.buttonDelete);
        btnZ = findViewById(R.id.buttonZ);
        btnSave = findViewById(R.id.buttonSave);
        btnBack = findViewById(R.id.buttonBack);
        list2 = findViewById(R.id.list2);
        //设置listview的点击实践，实现点击课程记录下它对应的序号
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenItem = i;
                showItem = chosenItem + 1;
                Toast.makeText(EditActivity.this, "你已选中第" + showItem + "项", Toast.LENGTH_SHORT).show();
            }
        });
        btnAdd.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnZ.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        etName = findViewById(R.id.editTextName);
        etTime = findViewById(R.id.editTextTime);
        etRoom = findViewById(R.id.editTextRoom);
        //接受来自MainActivity中的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        list_class = bundle.getParcelableArrayList("list_class");
        weekNo = bundle.getString("weekNo");
        //展示在list2中
        list2.setAdapter(ma);
    }

    //插入方法
    private boolean insert(String name, String time, String room) {
        changedIndex = "insert";//记录本次操作，便于撤销
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weekNo", weekNo);
        values.put("name", name);
        values.put("time", time);
        values.put("room", room);
        long id = db.insert("class", null, values);
        db.close();
        Class classNew = new Class(weekNo, name, time, room);
        //需要同时对list_class中的进行修改，不然notifyDataSetChanged方法不会生效，就不能实现动态刷新
        list_class.add(classNew);
        Collections.sort(list_class, new MainActivity.TimeSort());//时间排序
        changedItem = classNew;//记录下插入的课程信息，便于撤销时删除
        ma.notifyDataSetChanged();//刷新listview
        return id > 0;
    }

    //用于撤销中对应删除的反操作
    private boolean insert(Class item) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weekNo", weekNo);
        values.put("name", item.getName());
        values.put("time", item.getTime());
        values.put("room", item.getRoom());
        long id = db.insert("class", null, values);
        db.close();
        list_class.add(item);
        Collections.sort(list_class, new MainActivity.TimeSort());
        ma.notifyDataSetChanged();
        changedIndex = "0";//重置操作信息，避免多次撤销
        return id > 0;
    }

    //删除操作
    private boolean delete(int chosenItem) {
        changedIndex = "delete";//记录操作
        Class item = (Class) list2.getItemAtPosition(chosenItem);
        changedItem = item;//记录删除的课程信息
        SQLiteDatabase db = myHelper.getWritableDatabase();
        long id = db.delete("class", "name = ? and weekNo = ?", new String[]{item.getName(), weekNo});
        db.close();
        //动态刷新
        list_class.remove(item);
        ma.notifyDataSetChanged();
        return id > 0;
    }
    //用于撤销中的新增的反操作
    private boolean delete(Class item) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        long id = db.delete("class", "name = ? and weekNo = ?", new String[]{item.getName(), weekNo});
        db.close();
        list_class.remove(item);
        ma.notifyDataSetChanged();
        changedIndex = "0";
        return id > 0;
    }

    //保存操作，即update
    private boolean save(String name, String time, String room) {
        changedIndex = "save";//记录操作
        Class item = (Class) list2.getItemAtPosition(chosenItem);
        changedItem = item;//记录修改的原课程信息
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Class item2 = new Class();//记录更新后的课程信息
        if (!name.isEmpty()){//对于为空的字段判断为不进行修改，保持原状
            values.put("name", name);
            item2.setName(name);
        }else{
            item2.setName(item.getName());
        }
        if (!time.isEmpty()){
            values.put("time", time);
            item2.setTime(time);
        }else{
            item2.setTime(item.getTime());
        }
        if (!room.isEmpty()){
            values.put("room", room);
            item2.setRoom(room);
        }else{
            item2.setRoom(item.getRoom());
        }
        //通过周几和课程名称来确定一门课程
        int updateCount = db.update("class", values, "name = ? and time = ?", new String[]{item.getName(), item.getTime()});
        db.close();
        //动态刷新
        list_class.remove(item);
        list_class.add(item2);
        Collections.sort(list_class, new MainActivity.TimeSort());
        changedItem2 = item2;
        ma.notifyDataSetChanged();
        return updateCount > 0;
    }

    //用于撤销操作中保存操作的反操作
    private boolean save(Class item, Class item2) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(item.getName() != item2.getName()){
            values.put("name", item.getName());
        }
        if(item.getTime() != item2.getTime()){
            values.put("time", item.getTime());
        }
        if(item.getRoom() != item2.getRoom()){
            values.put("room", item.getRoom());
        }
        int updateCount = db.update("class", values, "name = ? and time = ?", new String[]{item2.getName(), item2.getTime()});
        db.close();
        //动态刷新
        list_class.remove(item2);
        list_class.add(item);
        Collections.sort(list_class, new MainActivity.TimeSort());
        ma.notifyDataSetChanged();
        changedIndex = "0";//还原操作标记
        return updateCount > 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                if(list_class.size() >= 5){
                    Toast.makeText(EditActivity.this, "新建失败，课程最多一天5门", Toast.LENGTH_SHORT).show();
                    break;
                }
                name = etName.getText().toString().trim();
                time = etTime.getText().toString().trim();
                room = etRoom.getText().toString().trim();
                if(name.isEmpty() || time.isEmpty() || room.isEmpty()){//判断是否为空，避免插入空信息的课程
                    Toast.makeText(EditActivity.this, "课程名称，时间，教室不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (insert(name, time, room)) {
                    Toast.makeText(EditActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonDelete:
                if (chosenItem == -1){//判断是否选中了课程
                    Toast.makeText(EditActivity.this, "请先选择一节课", Toast.LENGTH_SHORT).show();
                }else{
                    if (delete(chosenItem)) {
                        changedIndex = "-1"; // 重置，防止误删
                        Toast.makeText(EditActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.buttonZ:
                if(changedIndex == "0"){//判断是否进行过操作
                    Toast.makeText(EditActivity.this, "没有需要撤回的操作", Toast.LENGTH_SHORT).show();
                }else if(changedIndex == "insert"){
                    if(delete(changedItem)){
                        Toast.makeText(EditActivity.this, "撤回新增成功", Toast.LENGTH_SHORT).show();
                    }
                }else if(changedIndex == "delete"){
                    if(insert(changedItem)){
                        Toast.makeText(EditActivity.this, "撤回删除成功", Toast.LENGTH_SHORT).show();
                    }

                }else if(changedIndex == "save"){
                    if(save(changedItem, changedItem2)){
                        Toast.makeText(EditActivity.this, "撤回保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.buttonSave:
                if (chosenItem == -1){//判断是否选中课
                    Toast.makeText(EditActivity.this, "请先选择一节课", Toast.LENGTH_SHORT).show();
                }else{
                    name = etName.getText().toString().trim();
                    time = etTime.getText().toString().trim();
                    room = etRoom.getText().toString().trim();
                    if (save(name, time, room)) {
                        Toast.makeText(EditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.buttonBack:
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
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
            View newView = View.inflate(EditActivity.this, R.layout.activity_class, null);
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