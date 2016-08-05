package dhbk.android.testtodoapprxjava.taskdetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dhbk.android.testtodoapprxjava.R;


public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail_act);
    }
}