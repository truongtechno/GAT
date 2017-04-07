package com.gat.repository.impl;

import com.gat.repository.MessageRepository;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MessageRepositoryImpl implements MessageRepository {
    private final Lazy<MessageDataSource> networkDataSource;
    private final Lazy<MessageDataSource> localDataSource;


    public MessageRepositoryImpl(Lazy<MessageDataSource> networkDataSource, Lazy<MessageDataSource> localDataSource) {
        this.networkDataSource = networkDataSource;
        this.localDataSource = localDataSource;
    }
    @Override
    public Observable<List<Message>> getMessageList(String groupId) {
        return Observable.defer(() -> networkDataSource.get().getMessageList(groupId));
    }

    @Override
    public Observable<List<Group>> getGroupList() {
        return Observable.defer(() -> networkDataSource.get().getGroupList())
                .flatMap(list -> localDataSource.get().storeGroupList(list));
    }

    @Override
    public Observable<Boolean> sendMessage(String groupId, String message) {
        return Observable.defer(() -> networkDataSource.get().sendMessage(groupId, message));
    }
}