package com.gat.feature.message.item;

import android.support.annotation.IntDef;
import com.gat.common.adapter.Item;
import com.google.auto.value.AutoValue;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rey on 2/15/2017.
 */
@AutoValue
public abstract class LoadingMessage implements Item{

    @IntDef({Message.DEFAULT, Message.LOADING, Message.ERROR, Message.EMPTY, Message.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Message {
        int DEFAULT     = 0;
        int LOADING     = 1;
        int ERROR       = 2;
        int EMPTY       = 3;
        int COMPLETED   = 4;
    }

    public static LoadingMessage instance(@Message int message, boolean fullHeight){
        return new AutoValue_LoadingMessage(message, fullHeight);
    }

    @Message
    public abstract int message();
    public abstract boolean fullHeight();

}
