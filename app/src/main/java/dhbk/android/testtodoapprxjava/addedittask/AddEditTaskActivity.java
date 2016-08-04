package dhbk.android.testtodoapprxjava.addedittask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dhbk.android.testtodoapprxjava.R;


public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);
    }
}
