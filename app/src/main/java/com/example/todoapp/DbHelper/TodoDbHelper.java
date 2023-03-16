//package com.example.todoapp.DbHelper;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.text.TextUtils;
//import android.util.Log;
//
//import java.io.ByteArrayOutputStream;
//
//public class TodoDbHelper extends SQLiteOpenHelper {
//
//    private static final String TAG = "ToDoDBHelper";
//    private static final String DATABASE_NAME = "todo_database";
//    private static final int DATABASE_VERSION = 2;
//    private static final String TABLE_USER = "user";
//    private static final String KEY_ID_USER = "id";
//    private static final String KEY_NAME = "name";
//    private static final String KEY_EMAIL = "email";
//
//    private static final String KEY_PASSWORD = "password";
//
//    private static final String TABLE_IMAGE = "todo_images";
//    private static final String KEY_ID_IMAGE = "image_id";
//    private static final String KEY_IMAGE_BLOB = "image";
//
//    private static final String TABLE_TEMP = "temporary_name";
//
//    Context context;
//
//    public TodoDbHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        this.context = context;
//    }
//
//
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//
//        db.execSQL("CREATE TABLE " + TABLE_IMAGE + "("
//                +KEY_ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                +KEY_IMAGE_BLOB + " BLOB " + ")");
//
//
//        db.execSQL("CREATE TABLE " + TABLE_USER + "("
//                + KEY_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + KEY_NAME + " VARCHAR CHECK(length(" + KEY_NAME + ") <= 50) NOT NULL,"
//                + KEY_EMAIL + " VARCHAR UNIQUE NOT NULL," + KEY_PASSWORD + " VARCHAR NOT NULL,"
//                + KEY_ID_IMAGE + " VARCHAR" + ",FOREIGN KEY" + "(" + KEY_ID_IMAGE + ") REFERENCES " + TABLE_IMAGE + "(" + KEY_ID_IMAGE + ")" + ")");
//    }
//
//    //todo - create new table for image, add the id in contacts table. do all this in migration
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
////        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
////        onCreate(db);
//        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
//
//        switch (oldVersion) {
//            case 1:
//                upgradeSchemaToVersion2(db);
//        }
//
//
//    }
//
//    @Override
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//        Log.e(TAG, "DownGrading table from " + oldVersion + " to " + newVersion);
//
//        switch (oldVersion) {
//            case 2:
//                downgradeSchemaToVersion1(db);
//        }
//    }
//
//    private void downgradeSchemaToVersion1(SQLiteDatabase db) {
//
//        Log.d(TAG, "downgradeSchemaToVersion1: ");
//        db.execSQL("CREATE TABLE " + TABLE_TEMP + "(" + KEY_ID_CONTACT + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " VARCHAR CHECK(length(" + KEY_NAME + ") <= 50) NOT NULL," + KEY_PHONE_NO + " VARCHAR CHECK(length(" + KEY_PHONE_NO + ") <= 10) NOT NULL" + ")");
//
//        db.execSQL("INSERT INTO " + TABLE_TEMP + " (" + KEY_ID_CONTACT + "," + KEY_NAME + "," + KEY_PHONE_NO + ") SELECT " + KEY_ID_CONTACT + "," + KEY_NAME + "," + KEY_PHONE_NO + " FROM " + TABLE_CONTACT);
//
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_IMAGE);
//
//        db.execSQL("ALTER TABLE " + TABLE_TEMP + " RENAME TO " + TABLE_CONTACT);
//
//
//    }
//
//    private void upgradeSchemaToVersion2(SQLiteDatabase db) {
//        //create table to store images
//        //Add a foreign key col in contact table
//        Log.d(TAG, "upgradeSchemaToVersion2: ");
//        db.execSQL("CREATE TABLE " + TABLE_CONTACT_IMAGE + "(" + KEY_ID_IMAGES +
//                " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_IMAGE + " BLOB " + ")");
//        db.execSQL("ALTER TABLE " + TABLE_CONTACT
//                + " ADD COLUMN " + KEY_ID_IMAGES
//                + " INTEGER REFERENCES " + TABLE_CONTACT_IMAGE
//                + "(" + KEY_ID_IMAGES + ")");
//    }
//
//
////    //todo - consume the returned values, create the model and update the list
////    public long addContact(ContactModel contact) {
////        long val = -1;
////        try {
////            SQLiteDatabase db = this.getWritableDatabase();
////
////            contact.img_id=-1;
////            if(contact.img!=null)
////            {
////                contact.img_id=setImage(contact.img);
////            }
////
////            ContentValues values = new ContentValues();
////            values.put(KEY_NAME, contact.name);
////            values.put(KEY_PHONE_NO, contact.phone_no);
////            if(contact.img_id>=1)
////            {
////                values.put(KEY_ID_IMAGES,contact.img_id);
////            }
////
////
////
////            val = db.insert(TABLE_CONTACT, null, values);
////        } catch (Exception e) {
////            Log.d(TAG, "addContact: " + e);
////        }
////        return val;
////
////    }
//
//
////    //todo use thread to fetch, close the cursor, use try catch
////    public ArrayList<ContactModel> fetchContacts() {
////        ArrayList<ContactModel> arrContacts = new ArrayList<>();
////
////
////                SQLiteDatabase db = MyDBHelper.this.getReadableDatabase();
////
////                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACT, null);
////
////
////                while (cursor.moveToNext()) {
////                    ContactModel model = new ContactModel();
////                    model.id = cursor.getInt(0);
////                    model.name = cursor.getString(1);
////                    model.phone_no = cursor.getString(2);
////                    model.img_id=cursor.getInt(3);
////                    if(model.img_id!=0)
////                    {
////                        model.img=getImage(model.img_id);
////                    }
////
////                    arrContacts.add(model);
////                }
////                cursor.close();
////
////
////
////        return arrContacts;
////    }
//
////    public int updateContact(ContactModel contact) {
////
////        try {
////            SQLiteDatabase database = this.getWritableDatabase();
////
////            ContentValues cv = new ContentValues();
////            cv.put(KEY_NAME, contact.name);
////            cv.put(KEY_PHONE_NO, contact.phone_no);
////            if(contact.img_id!=0 &&contact.img_id!=0)
////            {
////                cv.put(KEY_ID_IMAGES,contact.img_id);
////            }
////            return database.update(TABLE_CONTACT, cv, KEY_ID_CONTACT + " = " + contact.id, null);
////        } catch (Exception e) {
////            Log.d(TAG, "updateContact: " + e);
////
////        }
////        return -1;
////
////    }
//
//
//    public int deleteContact(long id) {
//
//        try {
//            SQLiteDatabase database = this.getWritableDatabase();
//            return database.delete(TABLE_CONTACT, KEY_ID_CONTACT + " = ?", new String[]{String.valueOf(id)});
//        } catch (Exception e) {
//            Log.d(TAG, "deleteContact: " + e);
//        }
//        return -1;
//    }
////
////    public ContactModel getContact(long id) {
////        try {
////
////            SQLiteDatabase db = this.getReadableDatabase();
////
////            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACT + " WHERE " + KEY_ID_CONTACT + " = ?", new String[]{String.valueOf(id)});
////
////            ContactModel model = new ContactModel();
////            cursor.moveToNext();
////            model.id = cursor.getInt(0);
////            model.name = cursor.getString(1);
////            model.phone_no = cursor.getString(2);
////            model.img_id=cursor.getInt(3);
////            Log.d("tttt", "getContact: " + model);
////            cursor.close();
////            return model;
////        } catch (Exception e) {
////            Log.d(TAG, "getContact: " + e);
////        }
////        return null;
////
////
////    }
//
//
//    public long setImage(Bitmap imageBitmap) {
//        long val = -1;
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//
//            ContentValues values = new ContentValues();
//
//            values.put(KEY_IMAGE, byteArray);
//
//            val = db.insert(TABLE_CONTACT_IMAGE, null, values);
//        } catch (Exception e) {
//            Log.d(TAG, "addContact: " + e);
//        }
//        return val;
//
//    }
//
//    public Bitmap getImage(long id) {
//        if (id <= 0)
//            return null;
//        SQLiteDatabase db = TodoDbHelper.this.getReadableDatabase();
//
//
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACT_IMAGE + " WHERE " + KEY_ID_IMAGES + " = ?", new String[]{String.valueOf(id)});
//        cursor.moveToNext();
//
//
//        byte[] byteArray = cursor.getBlob(1);
//
//        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        cursor.close();
//        return bm;
//    }
//
//    //to be used in content provider
//    public Cursor getCursorForAllContacts() {
//        SQLiteDatabase db = TodoDbHelper.this.getWritableDatabase();
//        Cursor cursor = db.query(TABLE_CONTACT, new String[]{KEY_ID_CONTACT, KEY_NAME, KEY_PHONE_NO}, null, null, null, null, null);
//        return cursor;
//    }
//
//    //to be used in content provider
//    public Cursor getCursorForName(String name) {
//        SQLiteDatabase db = TodoDbHelper.this.getWritableDatabase();
//        Cursor cursor = db.query(TABLE_CONTACT, new String[]{KEY_ID_CONTACT, KEY_PHONE_NO}, KEY_NAME + " LIKE '%" + name + "%'", null, null, null, null);
//        return cursor;
//    }
//
//    public Cursor getCount() {
//        SQLiteDatabase db = TodoDbHelper.this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT  COUNT(*) FROM " + TABLE_CONTACT, null);
//        return cursor;
//    }
//}
//
//
//
//
//
