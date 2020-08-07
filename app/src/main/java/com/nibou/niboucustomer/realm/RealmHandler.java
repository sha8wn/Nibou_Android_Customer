package com.nibou.niboucustomer.realm;

import com.nibou.niboucustomer.models.RealmChatModel;
import io.realm.*;

public class RealmHandler {
    private Realm realm;

    public RealmHandler() {
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return realm;
    }

    public void insertMessageInRealm(final RealmChatModel chatModel) {
        try {
            realm.executeTransaction(realm -> realm.insertOrUpdate(chatModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMessageInRealm(final String messageId) {
        try {
            realm.executeTransaction(realm -> realm.where(RealmChatModel.class).equalTo("messageId", messageId).findAll().deleteAllFromRealm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllMessageInRealm() {
        try {
            realm.executeTransaction(realm -> realm.deleteAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RealmChatModel getMessageInRealm(String messageId) {
        try {
            realm.beginTransaction();
            RealmChatModel models = realm.where(RealmChatModel.class).equalTo("messageId", messageId).findFirst();
            realm.commitTransaction();
            return models;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
