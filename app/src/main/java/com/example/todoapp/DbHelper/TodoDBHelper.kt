package com.example.todoapp.DbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.todoapp.constants.Constants
import com.example.todoapp.models.Category
import com.example.todoapp.models.Task
import com.example.todoapp.models.User
import java.io.*
import java.util.*

class TodoDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, TodoDBHelper.DATABASE_VERSION) {


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

        private const val TABLE_TASKS = "todo_tasks"
        private const val KEY_TASK_TITLE = "task_title"
        private const val KEY_TASK_DESCRIPTION = "task_desc"
        private const val KEY_TASK_PRIORITY = "task_priority"
        private const val KEY_TASK_STATUS = "task_status"
        private const val KEY_TASK_IS_REMINDER_SET = "task_is_reminder_set"
        private const val KEY_TASK_REMIND_TIME = "task_remind_time"
        private const val KEY_TASK_USER_ID = "user_id"
        private const val KEY_TASK_IMAGE_ID = "image_id"


        private const val TABLE_TASK_CATEGORY_MAPPING = "todo_task_category_mapping"
        private const val KEY_ID_CATEGORY = "category_id"
        private const val KEY_ID_TASK = "task_id"


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
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_IMAGE_BLOB + " BLOB " + ")"
        )


        db!!.execSQL(
            "CREATE TABLE " + TABLE_USER + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_NAME + " VARCHAR CHECK(length(" + KEY_NAME + ") <= 50) NOT NULL,"
                    + KEY_EMAIL + " VARCHAR UNIQUE NOT NULL," + KEY_PASSWORD + " VARCHAR NOT NULL,"
                    + KEY_ID_IMAGE + " INTEGER,"
                    + "FOREIGN KEY" + "(" + KEY_ID_IMAGE + ") REFERENCES " + TABLE_IMAGE + "(" + KEY_ID_IMAGE + ")" + ")"
        )


        db!!.execSQL(
            "CREATE TABLE  $TABLE_CATEGORY ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_CATEGORY_NAME VARCHAR CHECK(length( $KEY_CATEGORY_NAME ) <= 50) NOT NULL,"
                    + "$KEY_CATEGORY_DESCRIPTION VARCHAR ,"
                    + "$KEY_CATEGORY_ICON_ID INTEGER,"
                    + "$KEY_CATEGORY_USER_ID INTEGER,"
                    + "FOREIGN KEY($KEY_CATEGORY_ICON_ID  ) REFERENCES $TABLE_IMAGE($KEY_ID_IMAGE ),"
                    + "FOREIGN KEY($KEY_CATEGORY_USER_ID  ) REFERENCES $TABLE_USER($KEY_ID_USER ) ON DELETE CASCADE)"

        )

        db!!.execSQL(
            "CREATE TABLE  $TABLE_TASKS ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_TASK_TITLE VARCHAR CHECK(length( $KEY_TASK_TITLE ) <= 100) NOT NULL,"
                    + "$KEY_TASK_DESCRIPTION VARCHAR ,"
                    + "$KEY_TASK_PRIORITY VARCHAR NOT NULL,"
                    + "$KEY_TASK_IS_REMINDER_SET INTEGER NOT NULL,"
                    + "$KEY_TASK_REMIND_TIME BLOB,"
                    + "$KEY_TASK_STATUS VARCHAR NOT NULL,"
                    + "$KEY_TASK_IMAGE_ID INTEGER,"
                    + "$KEY_TASK_USER_ID INTEGER NOT NULL,"
                    + "FOREIGN KEY($KEY_TASK_IMAGE_ID  ) REFERENCES $TABLE_IMAGE($KEY_ID_IMAGE ),"
                    + "FOREIGN KEY($KEY_TASK_USER_ID  ) REFERENCES $TABLE_USER($KEY_ID_USER ) ON DELETE CASCADE)"

        )

        db!!.execSQL(
            "CREATE TABLE  $TABLE_TASK_CATEGORY_MAPPING ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_ID_TASK INTEGER NOT NULL,"
                    + "$KEY_ID_CATEGORY INTEGER NOT NULL,"
                    + "FOREIGN KEY($KEY_ID_TASK  ) REFERENCES $TABLE_TASKS($KEY_ID ) ON DELETE CASCADE,"
                    + "FOREIGN KEY($KEY_ID_CATEGORY  ) REFERENCES $TABLE_CATEGORY($KEY_ID) ON DELETE CASCADE)"

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
        var imageId: Long = -1L
        if (image_bitmap != null) {
            imageId = insertImage(image_bitmap!!)
            if (imageId.equals(-1)) {
                return -1 //insert image failed..return
            }

        }
        values.put(KEY_ID_IMAGE, imageId)


        try {
            insertedRowId = db.insert(TABLE_USER, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return insertedRowId
    }

    fun insertImage(imageBitmap: Bitmap): Long {
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
        return insertedRowID
    }

    /**
     * return id of inserted image else -1 in case of failure
     */
    fun getImage(imageId: Long): Bitmap? {
        val db = readableDatabase
        var imageBitmap: Bitmap? = null
        try {
            val imageCursor = db.rawQuery(
                "SELECT * FROM $TABLE_IMAGE WHERE $KEY_ID=$imageId ", null
            )

            imageCursor.moveToNext()
            val imageBlob = imageCursor.getBlob(1)

            imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return imageBitmap
    }

    /**
     * return id of user if successfully logged in else -1 otherwise
     */
    fun login(email: String, password: String): Long {

        val db: SQLiteDatabase = this.readableDatabase
        var loggedInUserId = -1L

        val cursor = db.rawQuery(
            "SELECT $KEY_ID FROM $TABLE_USER WHERE $KEY_EMAIL = '${email.lowercase()}' AND $KEY_PASSWORD = '$password'",
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

    /**
     * return instance of user with given userID else null in case of failure
     */
    fun getUser(userId: Long): User? {

        var user: User? = null
        val db: SQLiteDatabase = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $KEY_ID = $userId ", null
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
                user.image_bitmap = getImage(imageId)
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        return user

    }

    /**
     * return id of newly created category else -1 in case of failure
     */
    fun createCategory(
        name: String,
        description: String,
        imageBitmap: Bitmap?,
        userId: Long
    ): Long {
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
            values.put(KEY_CATEGORY_ICON_ID, imageId)
        }

        try {
            insertedRowId = db.insert(TABLE_CATEGORY, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insertedRowId
    }

    /**
     * return id of newly created task else -1 in case of failure
     */
    fun createTask(
        title: String,
        description: String?,
        priority: String,
        isReminderSet: Boolean,
        remindTime: Calendar?,
        status: String,
        taskImage: Bitmap?,
        userId: Long
    ): Long {
        var insertedRowId: Long = -1
        val db = this.writableDatabase


        val values = ContentValues()
        values.put(KEY_TASK_TITLE, title)
        values.put(KEY_TASK_DESCRIPTION, description)
        values.put(KEY_TASK_PRIORITY, priority)
        values.put(KEY_TASK_IS_REMINDER_SET, if (isReminderSet) 1 else 0)
        values.put(KEY_TASK_STATUS, status)
        values.put(KEY_TASK_USER_ID, userId)

        var imageId: Long = -1
        if (taskImage != null) {
            imageId = insertImage(taskImage!!)
            if (imageId == -1L) {
                return -1L //insert image failed..return
            }
        }

        if (isReminderSet && remindTime != null) {
            try {
                val outputStream = ByteArrayOutputStream()
                val oos = ObjectOutputStream(outputStream)
                oos.writeObject(remindTime)
                val calendarAsBytes = outputStream.toByteArray()
                values.put(KEY_TASK_REMIND_TIME, calendarAsBytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }

        values.put(KEY_TASK_IMAGE_ID, imageId)

        try {
            insertedRowId = db.insert(TABLE_TASKS, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insertedRowId
    }


    /**
     * return instance of Task with given taskId else null in case of failure
     */
    fun getTask(taskId: Long): Task? {

        var task: Task? = null
        val db: SQLiteDatabase = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $KEY_ID = $taskId ", null)

        try {

            cursor.moveToNext()

            val taskTitle = cursor.getString(1)
            val taskDescription = cursor.getString(2)
            val taskPriority = Constants.Priority from cursor.getString(3)
            val taskIsReminderSet = cursor.getInt(4)
            val taskRemindDateTimeBlob = cursor.getBlob(5)
            val taskStatus = Constants.Status from cursor.getString(6)
            val taskImageId = cursor.getLong(7)
            val taskUserId = cursor.getLong(8)

            var remindDateTime: Calendar? = null

            if (taskIsReminderSet == 1) {

                try {
                    val inoutStream = ByteArrayInputStream(taskRemindDateTimeBlob)
                    val ois = ObjectInputStream(inoutStream)
                    remindDateTime = ois.readObject() as Calendar
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }


            val categoriesList=fetchAllCategoriesOfTask(taskId)

            task = Task(
                taskId,
                taskTitle,
                taskDescription,
                taskPriority!!,
                categoriesList,
                taskIsReminderSet == 1,
                remindDateTime,
                taskStatus!!,
                taskImageId,
                null,
                taskUserId
            )

            cursor.close()

            if (taskImageId != -1L) {
                task.imageBitmap = getImage(taskImageId)
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return task

    }

    fun fetchAllTasksOfUser(userId: Long): ArrayList<Task> {

        val tasksList = ArrayList<Task>()
        var task: Task? = null
        val db: SQLiteDatabase = this.readableDatabase

        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $KEY_TASK_USER_ID = $userId ", null)

        try {

            while (cursor.moveToNext()) {
                val taskId = cursor.getLong(0)
                val taskTitle = cursor.getString(1)
                val taskDescription = cursor.getString(2)
                val taskPriority = Constants.Priority from cursor.getString(3)
                val taskIsReminderSet = cursor.getInt(4)
                val taskRemindDateTimeBlob = cursor.getBlob(5)
                val taskStatus = Constants.Status from cursor.getString(6)
                val taskImageId = cursor.getLong(7)
                val taskUserId = cursor.getLong(8)
                var imageBitmap: Bitmap? = null
                var remindDateTime: Calendar? = null


                if (taskIsReminderSet == 1) {

                    try {
                        val inoutStream = ByteArrayInputStream(taskRemindDateTimeBlob)
                        val ois = ObjectInputStream(inoutStream)
                        remindDateTime = ois.readObject() as Calendar
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }

                val categoriesList=fetchAllCategoriesOfTask(taskId)

                task = Task(
                    taskId,
                    taskTitle,
                    taskDescription,
                    taskPriority!!,
                    categoriesList,
                    taskIsReminderSet == 1,
                    remindDateTime,
                    taskStatus!!,
                    taskImageId,
                    null,
                    taskUserId
                )
                if (taskImageId != -1L) {
                    task.imageBitmap = getImage(taskImageId)
                }
                tasksList.add(task)

            }

            cursor.close()


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return tasksList

    }

    fun fetchAllCategoriesOfUser(userId: Long): ArrayList<Category> {

        val categoriesList = ArrayList<Category>()
        var category: Category? = null
        val db: SQLiteDatabase = this.readableDatabase

        val cursor =
            db.rawQuery(
                "SELECT * FROM $TABLE_CATEGORY WHERE $KEY_CATEGORY_USER_ID = $userId ",
                null
            )

        try {

            while (cursor.moveToNext()) {
                val categoryId = cursor.getLong(0)
                val categoryName = cursor.getString(1)
                val categoryDescription = cursor.getString(2)
                val categoryIconId = cursor.getLong(3)


                category = Category(
                    id = categoryId,
                    name = categoryName,
                    description = categoryDescription,
                    iconId = categoryIconId,
                    iconBitmap = null,
                    userId=userId
                )

                if (categoryIconId != -1L) {
                    category.iconBitmap = getImage(categoryIconId)
                }
                categoriesList.add(category)

            }

            cursor.close()


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return categoriesList

    }

    fun fetchAllCategoriesOfTask(taskId: Long): ArrayList<Category> {

        val categoriesList = ArrayList<Category>()
        var category: Category? = null
        val db: SQLiteDatabase = this.readableDatabase

        val cursor =
            db.rawQuery(
                "SELECT * FROM $TABLE_TASK_CATEGORY_MAPPING WHERE $KEY_ID_TASK = $taskId ",
                null
            )
        try {

            while (cursor.moveToNext()) {
                val categoryId = cursor.getLong(0)
                val categoryName = cursor.getString(1)
                val categoryDescription = cursor.getString(2)
                val categoryIconId = cursor.getLong(3)
                val categoryUserId = cursor.getLong(4)


                category = Category(
                    id = categoryId,
                    name = categoryName,
                    description = categoryDescription,
                    iconId = categoryIconId,
                    iconBitmap = null,
                    userId=categoryUserId
                )

                if (categoryIconId != -1L) {
                    category.iconBitmap = getImage(categoryIconId)
                }
                categoriesList.add(category)
            }

            cursor.close()


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return categoriesList

    }

    fun createTaskCategoryMapping(taskId: Long, categoryId: Long): Long {
        var insertedRowId: Long = -1
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_ID_TASK, taskId)
        values.put(KEY_ID_CATEGORY, categoryId)


        try {
            insertedRowId = db.insert(TABLE_TASK_CATEGORY_MAPPING, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return insertedRowId
    }

    fun deleteTask(taskId: Long): Int {
        try {
            val database = this.writableDatabase
            return database.delete(TABLE_TASKS, "$KEY_ID = ?", arrayOf(taskId.toString()))

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return -1
    }

    fun updateTask(task: Task): Int {

        try {
            val database = this.writableDatabase
            val cv = ContentValues()
            cv.put(KEY_TASK_TITLE, task.title)
            cv.put(KEY_TASK_DESCRIPTION, task.description)
            cv.put(KEY_TASK_STATUS, task.status.value)
            cv.put(KEY_TASK_PRIORITY, task.priority.priority)
            cv.put(KEY_TASK_IS_REMINDER_SET, task.isReminderSet)

            if (task.isReminderSet && task.reminderTime != null) {
                try {
                    val outputStream = ByteArrayOutputStream()
                    val oos = ObjectOutputStream(outputStream)
                    oos.writeObject(task.reminderTime)
                    val calendarAsBytes = outputStream.toByteArray()
                    cv.put(KEY_TASK_REMIND_TIME, calendarAsBytes)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            var imageId = -1L
            if (task.imageId == -1L && task.imageBitmap != null) {

                imageId = insertImage(task.imageBitmap!!)
                if (imageId == -1L)
                    return 0 // image update failed

            } else if (task.imageId != -1L) {
                if (deleteImage(imageId = task.imageId) != 0)
                    imageId = -1
                if (task.imageBitmap != null) {
                    imageId = insertImage(task.imageBitmap!!)
                    if (imageId == -1L)
                        return 0 // image update failed
                }

            }
            cv.put(KEY_ID_IMAGE, imageId)
            return database.update(TABLE_TASKS, cv, KEY_ID + " = " + task.id, null)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 0

    }

    private fun deleteImage(imageId: Long): Int {
        try {
            val database = this.writableDatabase
            return database.delete(TABLE_IMAGE, "$KEY_ID = ?", arrayOf(imageId.toString()))

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 0
    }


}