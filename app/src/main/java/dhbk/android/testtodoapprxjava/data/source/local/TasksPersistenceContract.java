package dhbk.android.testtodoapprxjava.data.source.local;

/**
 * Created by phongdth.ky on 8/4/2016.
 */

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the tasks locally.
 */
public class TasksPersistenceContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TasksPersistenceContract() {}

    /** Inner class that defines the table contents {@link TasksDbHelper} */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}
