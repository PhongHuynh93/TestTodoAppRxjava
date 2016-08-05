package dhbk.android.testtodoapprxjava.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import dhbk.android.testtodoapprxjava.data.Task;
import dhbk.android.testtodoapprxjava.data.source.TasksDataSource;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by phongdth.ky on 8/4/2016.
 */
public class TasksLocalDataSource implements TasksDataSource {
    private static TasksLocalDataSource INSTANCE;
    private final BriteDatabase mDatabaseHelper;
    private final Func1<Cursor, Task> mTaskMapperFunction;

    // Prevent direct instantiation.
    public TasksLocalDataSource(Context context) {
        checkNotNull(context);
        TasksDbHelper dbHelper = new TasksDbHelper(context);
        /**
         * create sqlbrite instance
         * query the database {@link TasksDbHelper}
         * A Scheduler is required -> the query can then be run without blocking the main thread
         */
        // TODO: 8/4/2016 declare the database and make a reactive function to save a row to a model
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        mTaskMapperFunction = c -> {
            String itemId = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE));
            String description = c.getString(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed = c.getInt(c.getColumnIndexOrThrow(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            return new Task(title, description, itemId, completed);
        };
    }

    public static TasksLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    /**
     * get task from db
     * @return
     */
    @Override
    public Observable<List<Task>> getTasks() {
        // get 4 columns
        String[] projection = {
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };
        // TODO: 8/4/2016 construct a query -> good construct
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), TasksPersistenceContract.TaskEntry.TABLE_NAME);
        // TODO: 8/4/2016 db này là thư viện ở ngoài có chức năng reactive, method mapToList() return a list of tasks in observable
        return mDatabaseHelper.createQuery(TasksPersistenceContract.TaskEntry.TABLE_NAME, sql).mapToList(mTaskMapperFunction);
    }

    /**
     * save task to local db
     * @param task
     */
    @Override
    public void saveTask(Task task) {
        checkNotNull(task);
        ContentValues values = new ContentValues();
        values.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID, task.getMId());
        values.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE, task.getMTitle());
        values.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION, task.getMDescription());
        values.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED, task.isMCompleted());
        mDatabaseHelper.insert(TasksPersistenceContract.TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void clearCompletedTasks() {
        String selection = TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = {"1"};
        mDatabaseHelper.delete(TasksPersistenceContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }
}
