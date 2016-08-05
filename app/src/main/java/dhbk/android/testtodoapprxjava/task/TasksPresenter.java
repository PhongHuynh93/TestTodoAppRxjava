package dhbk.android.testtodoapprxjava.task;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.List;

import dhbk.android.testtodoapprxjava.addedittask.AddEditTaskActivity;
import dhbk.android.testtodoapprxjava.data.Task;
import dhbk.android.testtodoapprxjava.data.source.TasksDataSource;
import dhbk.android.testtodoapprxjava.data.source.TasksRepository;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by phongdth.ky on 8/4/2016.
 */
public class TasksPresenter implements TasksContract.Presenter{
    private final TasksRepository mTasksRepository;
    private final TasksContract.View mTasksView;
    @Getter @Setter
    private int mCurrentFiltering = TasksFilterType.ALL_TASKS;
    // this class hold all of your Subscriptions
    /**
     * @see <a href="http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/"></a>
     */
    private final CompositeSubscription mSubscriptions;
    private boolean mFirstLoad = true;
    private int mFiltering;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mSubscriptions = new CompositeSubscription();
        mTasksView.setPresenter(this);
    }

    /**
     * when subscribe to the presenter, load task immediately
     */
    @Override
    public void subscribe() {
        loadTasks(false);
    }


    /**
     * Unsubscribes any subscriptions that are currently part of this CompositeSubscription
     * and remove them from the CompositeSubscription so that the CompositeSubscription is empty
     * and able to manage new subscriptions.
     */
    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    /**
     * load tasks , always load on first call
     * @param forceUpdate indicate to load the db or not
     */
    @Override
    public void loadTasks(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        // show indicate or not
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }

        // update the state of cache in reposition
        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        /**
         * unsubcribe old lists and subscribe new ones.
         */
        unsubscribe();
        Subscription subscription =
                // get the list of tasks from all sources
                mTasksRepository.getTasks()
                // change the a list of task to emit each task.
                .flatMap(Observable::from)
                        /**
                         * emit only those items from an Observable that pass a predicate test
                         * ứng với current filter mà ta đang mở hiện tại trong screen, ta chỉ lấy task tương ứng thôi
                         */
                .filter(task -> {
                    switch (mCurrentFiltering) {
                        case TasksFilterType.ACTIVE_TASKS:
                            return task.isActive();
                        case TasksFilterType.COMPLETED_TASKS:
                            return task.isCompleted();
                        case TasksFilterType.ALL_TASKS:
                        default:
                            return true;
                    }
                })
                        /**
                         * convert an Observable into another object or data structure
                         * sau khi lọc ra được 1 dãy list thì ta gom lại thành 1 list duy nhất
                         */
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                        /**
                         * nhờ hàm toList() ta có kết quả là 1 dãy task
                         */
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onCompleted() {
                        // sau khi xong thì tắt load
                        mTasksView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTasksView.showLoadingTasksError();
                    }

                    @Override
                    public void onNext(List<Task> tasks) {
                        // sau khi lấy được 1 dãy task thì ta xử lý nó
                        processTasks(tasks);
                    }
                });
        mSubscriptions.add(subscription);
    }

    /**
     * process tasks from database
     * @param tasks
     */
    private void processTasks(List<Task> tasks) {
        // if we dont have any task compare to filter type,
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mTasksView.showTasks(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    /**
     * show label above lists depend on state of tasks
     */
    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case TasksFilterType.ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case TasksFilterType.COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    /**
     * when we dont have any task in the category
     */
    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case TasksFilterType.ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case TasksFilterType.COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    /**
     * return from add todo list
     * @param requestCode
     * @param resultCode
     */
    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    /**
     * show detail list
     * @param requestedTask
     */
    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksView.showTaskDetailsUi(requestedTask.getMId());
    }

    @Override
    public void completeTask(Task completedTask) {

    }

    @Override
    public void activateTask(Task activatedTask) {

    }

    /**
     * clear the tasks which has complete states
     * so refresh the reposition
     */
    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();  // clear in db
        mTasksView.showCompletedTasksCleared(); // show a snackbar
        // load new tasks
        loadTasks(false, false);
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#COMPLETED_TASKS}, or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */
    @Override
    public void setFiltering(int requestType) {
        mCurrentFiltering = requestType;
    }
}
