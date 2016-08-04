package dhbk.android.testtodoapprxjava.task;

/**
 * Created by phongdth.ky on 8/4/2016.
 */

import dhbk.android.testtodoapprxjava.BasePresenter;
import dhbk.android.testtodoapprxjava.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TasksContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean b);
    }

    interface Presenter extends BasePresenter {
        void loadTasks(boolean forceUpdate);
    }
}
