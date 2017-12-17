package cav.lscaner.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.FieldOutFile;
import cav.lscaner.data.models.FileFieldModel;
import cav.lscaner.ui.adapter.CustomExpandListAdapter;
import cav.lscaner.ui.dialogs.SettingFieldDialog;

//https://habrahabr.ru/post/147546/
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/86-urok-45-spisok-derevo-expandablelistview.html
//http://developer.alexanderklimov.ru/android/views/expandablelistview.php
//https://alvinalexander.com/java/jwarehouse/android/core/java/android/widget/SimpleExpandableListAdapter.java.shtml
//https://www.programcreek.com/java-api-examples/index.php?api=android.widget.SimpleExpandableListAdapter

//https://sohabr.net/post/227549/   - а вот тут то что надо
//https://androidexample.com/Custom_Expandable_ListView_Tutorial_-_Android_Example/index.php?view=article_discription&aid=107&aaid=129
//https://ru.androids.help/q10836    -- только поглядеть возможно и нафиг не нужно
//https://www.codeproject.com/Articles/1151814/Android-ExpandablelistView-Tutorial-with-Android-C
//http://abhiandroid.com/ui/expandablelistadapter-example-android.html
// https://stackoverflow.com/questions/5188196/how-to-write-custom-expandablelistadapter - тоже самое

public class SettingFieldNewActivity extends AppCompatActivity {

    private DataManager mDataManager;

    private ExpandableListView mExpandList;

    // названия  (групп)
    String[] groups = new String[] {"База данных", "Товар", "ЕГАИС","Переоценка","Поступление"};

    String[] storeProduct = new String[] {"Штрих-код","Код","Наименование",
            "Остаток","Цена","Цена закупочная","Код ЕГАИС","Артикул"};

    String[] outField = new String[] {"Штрих-код","Код","Кол-во.",
            "Цена","Цена закупочная","Код ЕГАИС","Артикул"};

    int[] storeProductF = new int[]{0,1,2,3,4,5,6,7};

    int[] tovarF = new int[]{0,1,2};
    int[] egaisF = new int[]{0,1,2,5};
    int[] changePriceF = new int[]{0,1,3};
    int[] prihodF = new int[] {0,1,2,4};


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

        mDataManager = DataManager.getInstance();

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

        FileFieldModel storeField = mDataManager.getPreferensManager().getFieldFileModel();
        // заполняем список атрибутов для каждого элемента
        for (int index : storeProductF) {
            m = new HashMap<String, String>();
            m.put("itemText", storeProduct[index]);
            m.put("itemValue", String.valueOf(storeField.get(index)));
            childDataItem.add(m);
        }
        // добавляем в коллекцию коллекций
        childData.add(childDataItem);

        FieldOutFile outFieldFile = mDataManager.getPreferensManager().getFieldOutFile();
        // создаем коллекцию элементов для второй группы
        childDataItem = new ArrayList<Map<String, String>>();
        // заполняем список атрибутов для каждого элемента
        for (int ix:tovarF) {
            m = new HashMap<String, String>();
            m.put("itemText", outField[ix]);
            m.put("itemValue", String.valueOf(outFieldFile.get(ix)));
            childDataItem.add(m);
        }
        childData.add(childDataItem);


        outFieldFile = mDataManager.getPreferensManager().getFieldOutEgaisFile();
        // создаем коллекцию элементов для третьей группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (int item : egaisF) {
            m = new HashMap<String, String>();
            m.put("itemText", outField[item]);
            m.put("itemValue", String.valueOf(outFieldFile.get(item)));
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        outFieldFile = mDataManager.getPreferensManager().getFieldOutChangePriceFile();
        // создаем коллекцию элементов для четвертой группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (int item : changePriceF) {
            m = new HashMap<String, String>();
            m.put("itemText",outField[item]);
            m.put("itemValue", String.valueOf(outFieldFile.get(item)));
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        outFieldFile = mDataManager.getPreferensManager().getFieldOutPrixodFile();
        // создаем коллекцию элементов для пятой группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (int item : prihodF) {
            m = new HashMap<String, String>();
            m.put("itemText", outField[item]);
            m.put("itemValue", String.valueOf(outFieldFile.get(item)));
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        // список атрибутов элементов для чтения
        String childFrom[] = new String[] {"itemText","itemValue"};
        // список ID view-элементов, в которые будет помещены атрибуты элементов
        int childTo[] = new int[] {R.id.expant_list_item_name,R.id.expant_list_item_pos};

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

        adapter.setGroupCallBackListener(mBackListener);


        mExpandList = (ExpandableListView) findViewById(R.id.expandableListView);
        mExpandList.setAdapter(adapter);

    }

    CustomExpandListAdapter.GroupCallBackListener mBackListener = new CustomExpandListAdapter.GroupCallBackListener() {
        @Override
        public void ClickSettingButton(int groupPosition) {
            SettingFieldDialog dialog = new SettingFieldDialog();
            dialog.show(getFragmentManager(),"sfd");
        }
    };
}
