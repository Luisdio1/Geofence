package gr.hua.dit.android.geofenceapp;

//Contract class with the information about the database
public class DbContract {

    public static String DB_NAME= "POINTS_DB";
    public static int DB_VERSION = 1;
    public static String TABLE_NAME = "POINTS";
    public static String FIELD_1 = "LAT";
    public static String FIELD_2 = "LON";
    public static String FIELD_3 = "ACTION";
    public static String FIELD_4 = "TIMESTAMP";
    public static  String CREATE_TABLE = "CREATE TABLE "+
            TABLE_NAME+
            " (" +
            FIELD_1+" REAL, "+FIELD_2+"  REAL, "+FIELD_3+" TEXT,"+FIELD_4+" INTEGER);";
    public static String AUTHORITY = "gr.hua.dit.android.geofenceapp" ;
    public static String PATH = DbContract.TABLE_NAME;
}
