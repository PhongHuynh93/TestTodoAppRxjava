package dhbk.android.testtodoapprxjava.task;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by huynhducthanhphong on 8/4/16.
 */
public class TasksFilterType {

    @IntDef({ALL_TASKS, ACTIVE_TASKS, COMPLETED_TASKS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TASK_TYPE {}

    public static final int ALL_TASKS = 0;
    public static final int ACTIVE_TASKS = 1;
    public static final int COMPLETED_TASKS = 2;
}
