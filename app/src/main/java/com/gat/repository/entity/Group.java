package com.gat.repository.entity;

import com.gat.common.util.Strings;
import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ducbtsn on 4/1/17.
 */

@AutoValue
public abstract class Group {
    public abstract String groupId();
    public abstract String lastMessage();
    public abstract Long timeStamp();
    public abstract Boolean isRead();
    public abstract List<String> users();
    public abstract String userName();
    public abstract String userImage();

    public static Group instance(Group group) {
        return new AutoValue_Group.Builder()
                .groupId(group.groupId())
                .lastMessage(group.lastMessage())
                .users(group.users())
                .isRead(group.isRead())
                .timeStamp(group.timeStamp())
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Group.Builder()
                .groupId(Strings.EMPTY)
                .lastMessage(Strings.EMPTY)
                .timeStamp(new Date().getTime())
                .isRead(false)
                .userName(Strings.EMPTY)
                .userImage(Strings.EMPTY)
                .users(new ArrayList<String>());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder groupId(String groupId);
        public abstract Builder lastMessage(String message);
        public abstract Builder timeStamp(Long timeStamp);
        public abstract Builder isRead(Boolean isRead);
        public abstract Builder users(List<String> users);
        public abstract Builder userName(String userName);
        public abstract Builder userImage(String userImage);
        public abstract Group build();
    }

}
