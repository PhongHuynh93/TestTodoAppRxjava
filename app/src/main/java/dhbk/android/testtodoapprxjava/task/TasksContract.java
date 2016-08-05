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

        void showAddTask();

        void setLoadingIndicator(boolean b);

        void showLoadingTasksError();

        void showTasks(List<Task> tasks);

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showNoTasks();

        void showSuccessfullySavedMessage();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showTaskDetailsUi(String mId);
    }

    interface Presenter extends BasePresenter {
        void loadTasks(boolean forceUpdate);

        void result(int requestCode, int resultCode);

        void addNewTask();

        void openTaskDetails(Task clickedTask);

        void completeTask(Task completedTask);

        void activateTask(Task activatedTask);
    }
}
