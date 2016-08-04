package dhbk.android.testtodoapprxjava.data.source;


import java.util.List;

import dhbk.android.testtodoapprxjava.data.Task;
import rx.Observable;

/**
 * Main entry point for accessing tasks data.
 * <p>
 */
public interface TasksDataSource {
    void refreshTasks();

    Observable<List<Task>> getTasks();

    void saveTask(Task task);
}
