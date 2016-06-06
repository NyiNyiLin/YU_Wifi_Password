package com.nyi.yuwifipassword.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "WIFIKEY";

	// Contacts table name
	private static final String TABLE_WIFI_DATA = "Wifi";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_SSID= "ssid";
	private static final String KEY_STATUS = "status";
	private static final String KEY_PASSWORD= "password";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_Customer_TABLE = "CREATE TABLE " + TABLE_WIFI_DATA + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_SSID + " TEXT,"
				+ KEY_STATUS+ " TEXT,"
				+ KEY_PASSWORD + " TEXT"
				+ ")";
		db.execSQL(CREATE_Customer_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_DATA);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */
	/*
		Adding new Customer
	 */
	public void addWifi(Wifi wifi){
		SQLiteDatabase db=this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_SSID, wifi.getSsid());
		values.put(KEY_STATUS, wifi.getStatus());
		values.put(KEY_PASSWORD, wifi.getPassword());

		db.insert(TABLE_WIFI_DATA, null, values);
		db.close(); // Closing database connection

	}

	/*
	 Getting All Customer
	  */
	public ArrayList<Wifi> getAllWifiData() {
		ArrayList<Wifi> wifiDataArrayList = new ArrayList<>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_WIFI_DATA;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
					//Wifi wifi = new Wifi(cursor.getString(1),cursor.getString(2), cursor.getString(3));
					// Adding customer to list
					//wifiDataArrayList.add(wifi);
			} while (cursor.moveToNext());
		}

		// return contact list
		cursor.close();
		return wifiDataArrayList;
	}
/*
	*//*
	Get Jobs Key By Customer ID
	 *//*
	public ArrayList<Job> getJobsByCustomerID(int customer_id){
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Job> JobsList = new ArrayList<>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE__JOBS_CONTACTS;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				if(Integer.parseInt(cursor.getString(0))==customer_id) {
					Job job = new Job(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)),cursor.getString(6),Integer.parseInt(cursor.getString(7)));
					// Adding contact to list
					JobsList.add(job);
				}
			} while (cursor.moveToNext());
		}

		if(JobsList.size()==0){
			JobsList.add(new Job(100,1,1,1,1,1,"1",100));
			return JobsList;
		}
		else return JobsList;

	}*/

	/*
	Deleting single wifi data
	 */
	public void deleteSingleWifiData(Wifi wifi) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIFI_DATA, KEY_SSID + " = ?",
				new String[] { String.valueOf(wifi.getSsid()) });
		db.close();
	}

	/*
	Updating single wifi data
	 */
	public int updateSingleWifiData(Wifi wifi) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SSID, wifi.getSsid());
		values.put(KEY_STATUS, wifi.getStatus());
		values.put(KEY_PASSWORD, wifi.getPassword());

		// updating row
		return db.update(TABLE_WIFI_DATA, values, KEY_SSID + " = ?", new String[]{String.valueOf(wifi.getSsid())});
	}

	/*
	*//*
	Get Customer Name br ID
	 *//*
	public String getCustomerNameByID(int customer_id){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_WIFI_DATA, new String[] { KEY_ID,KEY_NAME,
						KEY_PHONE, KEY_EMAIL,KEY_ADDRESS, KEY_PHOTO }, KEY_ID + "=?",
				new String[] { String.valueOf(customer_id) }, null, null, null,null);
		if (cursor != null)
			cursor.moveToFirst();
		//cursor.getString(0);
		Customer customer = new Customer(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),null);
		return customer.getName();

	}*/
	/*
	//Get Text by Key Name
	public String getTextByKeyName(String key_name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CODE,
						KEY_CUSTOM_CODE,
						KEY_NAME,
						KEY_WORD,
						KEY_ENCRYPT,
						KEY_PASSWORD }, KEY_NAME + "=?",
				new String[] { key_name }, null, null, null,null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor.getString(3);

	}
	*/

}
