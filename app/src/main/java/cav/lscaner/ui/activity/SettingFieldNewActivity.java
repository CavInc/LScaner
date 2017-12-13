package cav.lscaner.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cav.lscaner.R;
import cav.lscaner.ui.adapter.CustomExpandListAdapter;

//https://habrahabr.ru/post/147546/
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/86-urok-45-spisok-derevo-expandablelistview.html
//http://developer.alexanderklimov.ru/android/views/expandablelistview.php

public class SettingFieldNewActivity extends AppCompatActivity {

    private ExpandableListView mExpandList;

    // названия  (групп)
    String[] groups = new String[] {"База данных", "Товар", "ЕГАИС","Переоценка","Поступление"};

    String[] storeProduct = new String[] {"Штрих-код","Код","Наименование",
            "Остаток","Цена","Цена закупочная","Код ЕГАИС","Артикул"};
    String[] tovar = new String[]{"Штрих-код","Код","Кол-во."};
    String[] egais = new String[] {"Штрих-код","Код","Кол-во.","Код ЕГАИС"};
    String[] changePrice = new String[]{"Штрих-код","Код","Цена"};
    String[] prihod = new String[] {"Штрих-код","Код","Кол-во.","Цена закупочная"};

    // коллекция для групп
    ArrayList<Map<String, String>> groupData;

    // коллекция для элементов одной группы
    ArrayList<Map<String, String>> childDataItem;

    // общая коллекция для коллекций элементов
    ArrayList<ArrayList<Map<String, String>>> childData;

    // список атрибутов группы или элемента
    Map<String, String> m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_field_new);

        // заполняем коллекцию групп из массива с названиями групп
        groupData = new ArrayList<Map<String, String>>();
        for (String group : groups) {
            // заполняем список атрибутов для каждой группы
            m = new HashMap<String, String>();
            m.put("groupName", group); // имя раздела
            groupData.add(m);
        }
        // список атрибутов групп для чтения
        String groupFrom[] = new String[] {"groupName"};
        // список ID view-элементов, в которые будет помещены атрибуты групп
        int groupTo[] = new int[] {R.id.elg_name};


        // создаем коллекцию для коллекций элементов
        childData = new ArrayList<ArrayList<Map<String, String>>>();

        // создаем коллекцию элементов для первой группы
        childDataItem = new ArrayList<Map<String, String>>();
        // заполняем список атрибутов для каждого элемента
        for (String storeP : storeProduct) {
            m = new HashMap<String, String>();
            m.put("itemText", storeP);
            childDataItem.add(m);
        }
        // добавляем в коллекцию коллекций
        childData.add(childDataItem);

        // создаем коллекцию элементов для второй группы
        childDataItem = new ArrayList<Map<String, String>>();
        // заполняем список атрибутов для каждого элемента
        for (String item : tovar) {
            m = new HashMap<String, String>();
            m.put("itemText", item);
            childDataItem.add(m);
        }
        childData.add(childDataItem);


        // создаем коллекцию элементов для третьей группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (String item : egais) {
            m = new HashMap<String, String>();
            m.put("itemText", item);
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        // создаем коллекцию элементов для четвертой группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (String item : changePrice) {
            m = new HashMap<String, String>();
            m.put("itemText", item);
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        // создаем коллекцию элементов для пятой группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (String item : prihod) {
            m = new HashMap<String, String>();
            m.put("itemText", item);
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        // список атрибутов элементов для чтения
        String childFrom[] = new String[] {"itemText"};
        // список ID view-элементов, в которые будет помещены атрибуты элементов
        int childTo[] = new int[] {R.id.expant_list_item_name};


        /*
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                R.layout.expant_list_group_item,
                groupFrom,
                groupTo,
                childData,
                R.layout.expand_list_item,
                childFrom,
                childTo);
        */

        CustomExpandListAdapter adapter = new CustomExpandListAdapter(
                this,
                groupData,
                R.layout.expant_list_group_item,
                groupFrom,
                groupTo,
                childData,
                R.layout.expand_list_item,
                childFrom,
                childTo);

        mExpandList = (ExpandableListView) findViewById(R.id.expandableListView);
        mExpandList.setAdapter(adapter);

    }
}
