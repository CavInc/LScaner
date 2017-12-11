package cav.lscaner.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Map;

import cav.lscaner.R;

//https://habrahabr.ru/post/147546/
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/86-urok-45-spisok-derevo-expandablelistview.html
//http://developer.alexanderklimov.ru/android/views/expandablelistview.php

public class SettingFieldNewActivity extends AppCompatActivity {

    private ExpandableListView mExpandList;

    // названия  (групп)
    String[] groups = new String[] {"База данных", "Товар", "ЕГАИС","Переоценка","Поступление"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_field_new);

        // коллекция для групп
        ArrayList<Map<String, String>> groupData;

        mExpandList = (ExpandableListView) findViewById(R.id.expandableListView);

    }
}
