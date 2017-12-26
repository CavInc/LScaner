package cav.lscaner.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

    private static final String TAG = "SFA";
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

    private CustomExpandListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_field_new);

        mDataManager = DataManager.getInstance();

        mExpandList = (ExpandableListView) findViewById(R.id.expandableListView);

        updateUI();
        setupToolBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    public void setupToolBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    private void updateUI() {
        storeProductF = mDataManager.getPreferensManager().getFieldFileActive();
        tovarF = mDataManager.getPreferensManager().getFieldOutActive();
        egaisF = mDataManager.getPreferensManager().getFieldEGAISActive();
        changePriceF = mDataManager.getPreferensManager().getFieldChangePriceActive();
        prihodF = mDataManager.getPreferensManager().getFieldPrihoxActive();

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

        if (adapter == null) {
            adapter = new CustomExpandListAdapter(
                    this,
                    groupData,
                    R.layout.expant_list_group_item,
                    groupFrom,
                    groupTo,
                    childData,
                    R.layout.expand_list_item,
                    childFrom,
                    childTo);
            mExpandList.setAdapter(adapter);
            adapter.setGroupCallBackListener(mBackListener);
        }else {
            adapter.setChildData(childData);
            adapter.notifyDataSetChanged();
        }

    }

    CustomExpandListAdapter.GroupCallBackListener mBackListener = new CustomExpandListAdapter.GroupCallBackListener() {
        @Override
        public void ClickSettingButton(int groupPosition) {
            Log.d(TAG,"GP - "+groupPosition);
            SettingFieldDialog dialog = SettingFieldDialog.newInstance(groupPosition);
            dialog.setSettingFieldDialogListener(mFieldDialogListener);
            dialog.show(getFragmentManager(),"sfd");
        }
    };

    // получили данные с настройки полей
    SettingFieldDialog.SettingFieldDialogListener mFieldDialogListener = new SettingFieldDialog.SettingFieldDialogListener() {
        @Override
        public void onNegativeButton() {

        }

        @Override
        public void onPostitiveButton() {
            updateUI();
        }
    };


    // сохраняем данные в preferens
    public void saveData(){
        int ic = adapter.getGroupCount();
        for (int i=0;i<ic;i++){
            Log.d(TAG, String.valueOf(adapter.getChildrenCount(i)));
            // база данных
            if (((HashMap)adapter.getGroup(i)).get("groupName").equals(groups[0])) {
                saveStoreProduct(i);
            }
            // сканированные товара
            if (((HashMap)adapter.getGroup(i)).get("groupName").equals(groups[1])) {
                saveTovar(i,0);
            }
            // егаис
            if (((HashMap)adapter.getGroup(i)).get("groupName").equals(groups[2])) {
                saveTovar(i,1);
            }
            // переоценка
            if (((HashMap)adapter.getGroup(i)).get("groupName").equals(groups[3])) {
                saveTovar(i,2);
            }
            // приход
            if (((HashMap)adapter.getGroup(i)).get("groupName").equals(groups[4])) {
                saveTovar(i,3);
            }
        }
    }

    /*
        String[] storeProduct = new String[] {"Штрих-код","Код","Наименование",
            "Остаток","Цена","Цена закупочная","Код ЕГАИС","Артикул"};
    */

    private void saveStoreProduct(int groupID) {
        FileFieldModel field = new FileFieldModel();
        int ic = adapter.getChildrenCount(groupID);
        for (int i=0;i<ic;i++){
            HashMap lx = (HashMap) adapter.getChild(groupID, i);
            // штрикод
            if (lx.get("itemText").equals(storeProduct[0])) {
                field.setBar(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // код - артикул
            if (lx.get("itemText").equals(storeProduct[1])) {
                field.setArticul(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // наименование
            if (lx.get("itemText").equals(storeProduct[2])) {
                field.setName(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // остаток
            if (lx.get("itemText").equals(storeProduct[3])) {
                field.setOstatok(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // цена
            if (lx.get("itemText").equals(storeProduct[4])) {
                field.setPrice(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // цена зак
            if (lx.get("itemText").equals(storeProduct[5])) {
                field.setBasePrice(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // егаис
            if (lx.get("itemText").equals(storeProduct[6])) {
                field.setEGAIS(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // артикул - codtv
            if (lx.get("itemText").equals(storeProduct[7])) {

            }
        }
        mDataManager.getPreferensManager().setFieldFileModel(field);
    }

    /*
        String[] outField = new String[] {"Штрих-код","Код","Кол-во.",
            "Цена","Цена закупочная","Код ЕГАИС","Артикул"};
     */

    private void saveTovar(int groupId,int mode){
        FieldOutFile field = new FieldOutFile();
        int ic = adapter.getChildrenCount(groupId);
        for (int i=0;i<ic;i++) {
            HashMap lx = (HashMap) adapter.getChild(groupId, i);
            // штрикод
            if (lx.get("itemText").equals(outField[0])) {
                field.setBarcode(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // код - артикул
            if (lx.get("itemText").equals(outField[1])) {
                field.setArticul(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // кол-во.
            if (lx.get("itemText").equals(outField[2])) {
                field.setQuantity(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // цена
            if (lx.get("itemText").equals(outField[3])) {
                field.setPrice(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // цена зак
            if (lx.get("itemText").equals(outField[4])) {
                field.setBasePrice(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // егаис
            if (lx.get("itemText").equals(outField[5])) {
                field.setEGAIS(Integer.parseInt(lx.get("itemValue").toString()));
            }
            // артикул - codtv
            if (lx.get("itemText").equals(outField[6])) {
                field.setCodeTV(Integer.parseInt(lx.get("itemValue").toString()));
            }

        }
        switch (mode) {
            case (0):
                mDataManager.getPreferensManager().setFieldOutFile(field);
                break;
            case (1):
                mDataManager.getPreferensManager().setFieldOutEgaisFile(field);
                break;
            case (2):
                mDataManager.getPreferensManager().setFieldOutChangePriceFile(field);
                break;
            case (3):
                mDataManager.getPreferensManager().setFieldOutPrixodFile(field);
                break;
        }
    }

}
