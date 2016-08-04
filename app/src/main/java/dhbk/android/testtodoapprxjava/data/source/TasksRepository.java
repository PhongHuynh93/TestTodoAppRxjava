package dhbk.android.testtodoapprxjava.data.source;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dhbk.android.testtodoapprxjava.data.Task;
import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by phongdth.ky on 8/4/2016.
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE;
    // 2 data sources
    private final TasksDataSource mTasksRemoteDataSource;
    private final TasksDataSource mTasksLocalDataSource;
    // indicate that the cache must be fill with new datas
    private boolean mCacheIsDirty = false;
    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Task> mCachedTasks;

    // Prevent direct instantiation.
    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * set state of cache (= true is need update with new data)
     */
    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }


    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * @return Observable contains list of tasks
     */
    @Override
    public Observable<List<Task>> getTasks() {
        // Respond immediately with cache if available and not dirty
        if (mCachedTasks != null && !mCacheIsDirty) {
            return Observable.from(mCachedTasks.values()).toList();
        } else if (mCachedTasks == null) {
            // create a new cache
            mCachedTasks = new LinkedHashMap<>();
        }

        // get start from remote db
        Observable<List<Task>> remoteTasks = mTasksRemoteDataSource
                .getTasks()
                // phát thay vì 1 list task 1 lần thì phát từng task 1
                .flatMap(Observable::from)
                // save từng task vào local db va cache (key là ID của value)
                .doOnNext(task -> {
                    mTasksLocalDataSource.saveTask(task);
                    mCachedTasks.put(task.getMId(), task);
                })
                /**
                 * toList( ) — collect all items from an Observable and emit them as a single List
                 * sau khi save từng task thì ta gom lại thành 1 cục và chạy den onCOmplete
                 */
                .toList()
                /**
                 * cache giờ đã chứa giá trị mới
                 */
                .doOnCompleted(() -> mCacheIsDirty = false);
        /**
         * nếu cache cũ thì ta lấy thì remote db
         */
        if (mCacheIsDirty) {
            return remoteTasks;
        }
        // nếu cache cũ nhưng empty thì lấy từ db
        else {
            // Query the local storage if available. If not, query the network.
            Observable<List<Task>> localTasks = mTasksLocalDataSource.getTasks();
            /**
             * Concat
             emit the emissions from two or more Observables without interleaving them
             */
            /**
             * First
             emit only the first item (or the first item that meets some condition) emitted by an Observable
             */
            // TODO: 8/4/2016 concat là nối 2 cái lại, rất đúng trong TH này, tức là có thể db chưa có task mà remote có rồi hay ngược lại
            // ta muốn phát có có trước
            return Observable.concat(localTasks, remoteTasks).first();
        }
    }

    @Override
    public void saveTask(Task task) {

    }
}
