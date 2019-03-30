package cav.lscaner.utils;

public interface ConstantManager{

    String SELECTED_FILE = "SELECTED_FILE";
    String DELIMETER_STORE = "DELIMETER_STORE";
    String DELIMETER_SCANER = "DELIMETER_SCANER";
    String SCALE_PREFIX = "SCALE_PREFIX";
    String SCALE_SIZE = "SCALE_SIZE";
    String STORE_FILE_NAME = "STORE_FILE_NAME";
    String SELECTED_FILE_NAME = "SELECTED_FILE_NAME";
    String DEMO_VERSION = "DEMO_VERSION";
    String REGISTRY_NUMBER = "REGISTRY_NUMBER";
    String CODE_FILE = "CODE_FILE";
    String SELECTED_FILE_TYPE = "SELECTED_FILE_TYPE";

    int FILE_TYPE_PRODUCT = 0;
    int FILE_TYPE_EGAIS = 1;
    int FILE_TYPE_PRIHOD = 2;
    int FILE_TYPE_CHANGE_PRICE = 3;
    int FILE_TYPE_ALCOMARK = 4;

    int RET_OK = 200;

    int RET_ERROR = 201;
    int RET_NO_FIELD_MANY = 202;
    int RET_NO_SD = 203;

    String PREFIX_FILE_TOVAR = "{ТОВАР}";
    String PREFIX_FILE_EGAIS = "{ЕГАИС}";
    String PREFIX_FILE_CHANGEPRICE = "{Переоценка}";
    String PREFIX_FILE_PRIHOD = "{Поступление}";
    String PREFIX_FILE_ALCOMARK = "{АКЛОМАРК}";

    int GD = 0;
    int LS = 1;
    int FL = 2;
}