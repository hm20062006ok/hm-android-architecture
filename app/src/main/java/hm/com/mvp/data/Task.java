package hm.com.mvp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class Task {


    private final String mId;
    private final String mDescription;
    private final boolean mCompleted;
    private final String mTitle;

    public Task(@Nullable String title, @Nullable String description) {
        this(title, description, UUID.randomUUID().toString(), false);
    }

    public Task(@Nullable String title, @Nullable String description, @NonNull String id) {
        this(title, description, id, false);
    }

    public Task(@NonNull String title, @NonNull String description, boolean completed){
        this(title, description, UUID.randomUUID().toString(), completed);
    }

    public Task(@NonNull String title, @NonNull String description,
                @NonNull String id, @NonNull boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }
    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }


    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        }else{
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive(){return !mCompleted;}

    public boolean isEmpty(){
        return Strings.isNullOrEmpty(mTitle) && Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task  = (Task)obj;
        return Objects.equal(mId,task.mId) &&
                Objects.equal(mTitle, task.mTitle) &&
                Objects.equal(mDescription, task.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Task with Title " + mTitle;
    }
}
