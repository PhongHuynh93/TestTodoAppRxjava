package dhbk.android.testtodoapprxjava.task;

import android.support.annotation.NonNull;

import java.util.List;

import dhbk.android.testtodoapprxjava.data.Task;
import dhbk.android.testtodoapprxjava.data.source.TasksRepository;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by phongdth.ky on 8/4/2016.
 */
public class TasksPresenter implements TasksContract.Presenter{
    private final TasksRepository mTasksRepository;
    private final TasksContract.View mTasksView;
    // this class hold all of your Subscriptions
    /**
     * @see <a href="http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/"></a>
     */
    private final CompositeSubscription mSubscriptions;
    private boolean mFirstLoad = true;

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
        Subscription subscription = mTasksRepository
                .getTasks()
                .flatMap(new Func1<List<Task>, Observable<Task>>() {
                    @Override
                    public Observable<Task> call(List<Task> tasks) {
                        return Observable.from(tasks);
                    }
                })
                .filter(new Func1<Task, Boolean>() {
                    @Override
                    public Boolean call(Task task) {
                        switch (mCurrentFiltering) {
                            case ACTIVE_TASKS:
                                return task.isActive();
                            case COMPLETED_TASKS:
                                return task.isCompleted();
                            case ALL_TASKS:
                            default:
                                return true;
                        }
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onCompleted() {
                        mTasksView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTasksView.showLoadingTasksError();
                    }

                    @Override
                    public void onNext(List<Task> tasks) {
                        processTasks(tasks);
                    }
                });
        mSubscriptions.add(subscription);
    }

}
