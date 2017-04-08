package com.gat.repository.datasource;

import com.gat.repository.entity.Book;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public interface MessageDataSource {
    public Observable<List<Message>> getMessageList(String userId);
    public Observable<List<Group>> getGroupList();
    public Observable<List<Group>> loadMoreGroup();
    public Observable<List<Message>> loadMoreMessage();

    public Observable<Boolean> sendMessage(String toUserId, String message);

    public Observable<List<Group>> storeGroupList(List<Group> groupList);
    public Observable<List<Message>> storeMessageList(String groupId, List<Message> messageList);
}
