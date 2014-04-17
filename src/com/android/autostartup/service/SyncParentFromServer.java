package com.android.autostartup.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.autostartup.controller.server.Server;
import com.android.autostartup.dao.ParentDao;
import com.android.autostartup.model.Parent;

public class SyncParentFromServer {

    private static final String TAG = SyncParentFromServer.class.getSimpleName();

    private ParentDao parentDao;

    private Parent[] parentsFromServer;
    private List<Long> newIds = new ArrayList<Long>();
    private List<Long> updatedIds = new ArrayList<Long>();

    public SyncParentFromServer(Context context) {
        parentDao = new ParentDao(context);
    }

    public void updateParentDataFromServer() {

        Server.requestAllParentIds(new Server.GetParentsCallback() {
            @Override
            public void onSuccess(Parent[] parents) {
                parentsFromServer = parents;
                syncData();
            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student ids list failed");
            }
        });

    }

    private void syncData() {
        List<Parent> parentFromLocal = parentDao.getAll();
        if (null == parentFromLocal || parentFromLocal.isEmpty()) {
            Log.i(TAG, "save all ids");
            saveAllParents();
        } else {
            seperateData(parentFromLocal);

            saveParents();
            updateParents();

        }
    }

    private void seperateData(List<Parent> parents) {
        // TODO Refactor
        List<Parent> localParents = parents;
        List<Parent> serverParents = new ArrayList<Parent>();
        for (Parent parent : parentsFromServer) {
            serverParents.add(parent);
        }
        Log.i(TAG, "ServerParentList.size=" + serverParents.size());

        for (int i = serverParents.size() - 1; i > -1; i--) {
            Parent serverObj = serverParents.get(i);
            long serverId = serverObj.id;
            for (int j = localParents.size() - 1; j > -1; j--) {
                Parent localObj = localParents.get(j);

                if (serverId == localObj.id) {
                    if (serverObj.updatedAt != localObj.updatedAt) {
                        updatedIds.add(serverId);
                    }
                    localParents.remove(j);
                    serverParents.remove(i);
                    break;
                }
            }
        }

        Log.i(TAG, "serverParentlist.size=" + serverParents.size());
        for (Parent parent : serverParents) {
            newIds.add(parent.id);
        }
    }

    private void updateParents() {
        if (!updatedIds.isEmpty()) {
            String ids = TextUtils.join(",", updatedIds);
            Log.i(TAG, "update updated ids:" + ids);
            updateParents(ids);
            updatedIds.clear();
        }
    }

    private void saveParents() {
        if (!newIds.isEmpty()) {
            String ids = TextUtils.join(",", newIds);
            Log.i(TAG, "save new ids" + ids);
            saveNewParents(ids);
            newIds.clear();
        }
    }

    private void saveAllParents() {
        Server.requestAllParent(new Server.GetParentsCallback() {
            @Override
            public void onSuccess(Parent[] parents) {
                for (Parent parent : parents) {
                    Log.i(TAG, parent.toString());
                }
                parentDao.loadAndSavePics(parents);
                parentDao.save(parents);
            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student list failed");
            }
        });
    }

    private void saveNewParents(String ids) {
        Server.requestParentsByIds(ids, new Server.GetParentsCallback() {

            @Override
            public void onSuccess(Parent[] parents) {
                parentDao.loadAndSavePics(parents);
                parentDao.save(parents);

            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student list failed");
            }
        });

    }

    private void updateParents(String ids) {
        Server.requestParentsByIds(ids, new Server.GetParentsCallback() {

            @Override
            public void onSuccess(Parent[] parents) {
                parentDao.updateById(parents);

            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student list failed");
            }
        });
    }
}
