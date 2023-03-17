package com.example.todoapp.DbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.todoapp.models.User
import java.io.ByteArrayOutputStream

class TodoDBHelper(context: Context) :
    SQLiteOpenHelper(context, TodoDBHelper.DATABASE_NAME, null, TodoDBHelper.DATABASE_VERSION) {

//    private var mDbHelper: TodoDBHelper?

//    init {
//        mDbHelper = TodoDBHelper(context)
//    }

    companion object {
        private const val TAG = "ToDoDBHelper"
        private const val DATABASE_NAME = "todo_database"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USER = "user"

        private const val KEY_ID = "_id"
        private const val KEY_ID_USER = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"

        private const val KEY_PASSWORD = "password"

        private const val TABLE_IMAGE = "todo_images"
        private const val KEY_ID_IMAGE = "image_id"
        private const val KEY_IMAGE_BLOB = "image"

        private const val TABLE_CATEGORY = "todo_categories"
        private const val KEY_CATEGORY_NAME = "category_name"
        private const val KEY_CATEGORY_DESCRIPTION = "category_desc"
        private const val KEY_CATEGORY_ICON_ID = "category_icon_id"
        private const val KEY_CATEGORY_USER_ID = "category_user_id"

//        private const val TABLE_TASKS = "todo_tasks"
//        private const val KEY_TASKS_TITLE= "task_titile"
//        private const val KEY_TASKS_NAME= "task_name"
//        private const val KEY_TASKS_NAME= "task_name"


        private const val TABLE_TEMP = "temporary_name"

        @Volatile
        private lateinit var instance: TodoDBHelper

        fun getInstance(context: Context): TodoDBHelper {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = TodoDBHelper(context)
                }
                return instance!!
            }
        }

    }


    override fun onCreate(db: SQLiteDatabase?) {

        db!!.execSQL(
            "CREATE TABLE " + TABLE_IMAGE + "("
                    + KEY_ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_IMAGE_BLOB + " BLOB " + ")"
        )


        db!!.execSQL(
            "CREATE TABLE " + TABLE_USER + "("
                    + KEY_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_NAME + " VARCHAR CHECK(length(" + KEY_NAME + ") <= 50) NOT NULL,"
                    + KEY_EMAIL + " VARCHAR UNIQUE NOT NULL," + KEY_PASSWORD + " VARCHAR NOT NULL,"
                    + KEY_ID_IMAGE + " INTEGER" + ",FOREIGN KEY" + "(" + KEY_ID_IMAGE + ") REFERENCES " + TABLE_IMAGE + "(" + KEY_ID_IMAGE + ")" + ")"
        )


        db!!.execSQL(
            "CREATE TABLE  $TABLE_CATEGORY ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_CATEGORY_NAME VARCHAR CHECK(length( $KEY_CATEGORY_NAME ) <= 50) NOT NULL,"
                    + "$KEY_CATEGORY_DESCRIPTION VARCHAR ,"
                    + "$KEY_CATEGORY_ICON_ID INTEGER,"
                    + "$KEY_CATEGORY_USER_ID INTEGER,"
                    + "FOREIGN KEY($KEY_CATEGORY_ICON_ID  ) REFERENCES $TABLE_IMAGE($KEY_ID_IMAGE ),"
                    + "FOREIGN KEY($KEY_CATEGORY_USER_ID  ) REFERENCES $TABLE_IMAGE($KEY_ID_IMAGE ))"

        )

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun createUser(name: String, email: String, password: String, image_bitmap: Bitmap?): Long {
        var insertedRowId: Long = -1
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_EMAIL, email.lowercase())
        values.put(KEY_PASSWORD, password)

        if (image_bitmap != null) {
            val imageId = insertImage(image_bitmap!!)
            if (imageId.equals(-1)) {
                return -1 //insert image failed..return
            }
            try {

                values.put(KEY_ID_IMAGE, imageId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            insertedRowId = db.insert(TABLE_USER, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            db.close()
        }
        return insertedRowId
    }

    private fun insertImage(imageBitmap: Bitmap): Long {
        var insertedRowID: Long = -1
        val db = this.writableDatabase
        try {

            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val values = ContentValues()
            values.put(KEY_IMAGE_BLOB, byteArray)
            insertedRowID = db.insert(TABLE_IMAGE, null, values)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            db.close()
        }
        return insertedRowID

    }

    /**
     * return id of user if successfully logged in else -1 otherwise
     */
    fun login(email: String, password: String): Long {

        val db: SQLiteDatabase = this.readableDatabase
        var loggedInUserId = -1L

        val cursor = db.rawQuery(
            "SELECT $KEY_ID_USER FROM $TABLE_USER WHERE $KEY_EMAIL = '${email.lowercase()}' AND $KEY_PASSWORD = '$password'",
            null
        )

        try {

            cursor.moveToNext()
            loggedInUserId = cursor.getLong(0)
            cursor.close()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
//        finally {
//            db.close()
//        }
        return loggedInUserId

    }

    fun getUser(userId: Long): User? {

        var user: User? = null
        val db: SQLiteDatabase = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $KEY_ID_USER = $userId ", null
        )

        try {

            cursor.moveToNext()

            val loggedInUserId = cursor.getLong(0)
            val name = cursor.getString(1)
            val email = cursor.getString(2)
            val imageId = cursor.getLong(4)
            user = User(loggedInUserId, name, email, imageId, null)
            cursor.close()
            if (imageId != -1L) {
                try {
                    cursor.close()

                    val imageCursor = db.rawQuery(
                        "SELECT * FROM $TABLE_IMAGE WHERE $KEY_ID_IMAGE=$imageId ", null
                    )

                    imageCursor.moveToNext()
                    val imageBlob = imageCursor.getBlob(1)
                    imageCursor.close()

                    val imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.size)
                    user.image_bitmap = imageBitmap


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

//        finally {
//            db.close()
//        }
        return user

    }

    fun createCategory( name:String,description:String,imageBitmap:Bitmap?, userId:Long): Long {
        var insertedRowId: Long = -1
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_CATEGORY_NAME, name)
        values.put(KEY_CATEGORY_DESCRIPTION, description)
        values.put(KEY_CATEGORY_USER_ID, userId)

        if (imageBitmap != null) {
            val imageId = insertImage(imageBitmap!!)
            if (imageId.equals(-1)) {
                return -1 //insert image failed..return
            }
            try {

                values.put(KEY_CATEGORY_ICON_ID, imageId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            insertedRowId = db.insert(TABLE_CATEGORY, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        finally {
//            db.close()
//        }
        return insertedRowId
    }

}