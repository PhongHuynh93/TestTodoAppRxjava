package dhbk.android.testtodoapprxjava.data;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dhbk.android.testtodoapprxjava.data.source.TasksDataSource;
import rx.Observable;

/**
 * Created by phongdth.ky on 8/4/2016.
 */
public class FakeTasksRemoteDataSource implements TasksDataSource {
    private static FakeTasksRemoteDataSource INSTANCE;
    // remote thuc chat la cache 
    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void refreshTasks() {

    }


    @Override
    public Observable<List<Task>> getTasks() {
        Collection<Task> values = TASKS_SERVICE_DATA.values();
        return Observable.from(values).toList();
    }


    @Override
    public void saveTask(Task task) {

    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }
}
