package dhbk.android.testtodoapprxjava.data;

import android.support.annotation.Nullable;

import lombok.Getter;

/**
 * Created by phongdth.ky on 8/4/2016.
 */
public class Task {
    /**
     * make getter for this field
     */
    @Getter
    private final String mId;
    @Getter
    private final String mTitle;
    @Getter
    private final String mDescription;
    @Getter
    private final boolean mCompleted;

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param title
     * @param description
     * @param id
     * @param completed
     */
    public Task(@Nullable String title, @Nullable String description, String id, boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }

}
