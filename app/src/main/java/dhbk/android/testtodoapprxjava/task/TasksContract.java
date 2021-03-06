package dhbk.android.testtodoapprxjava.task;

/**
 * Created by phongdth.ky on 8/4/2016.
 */

import java.util.List;

import dhbk.android.testtodoapprxjava.BasePresenter;
import dhbk.android.testtodoapprxjava.BaseView;
import dhbk.android.testtodoapprxjava.data.Task;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TasksContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean b);

        void showLoadingTasksError();

        void showTasks(List<Task> tasks);

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showNoTasks();
    }

    interface Presenter extends BasePresenter {
        void loadTasks(boolean forceUpdate);
    }
}
