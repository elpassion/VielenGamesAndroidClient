package com.elpassion.vielengames;

import android.app.Application;
import android.content.Context;

import com.elpassion.vielengames.data.VielenGamesModel;

import javax.inject.Inject;

import dagger.ObjectGraph;

public final class VielenGamesApp extends Application {

    @Inject
    VielenGamesModel model;
    @Inject
    SessionUpdatesHandler handler;

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        initObjectGraph();
        inject(this);
    }

    private void initObjectGraph() {
        objectGraph = ObjectGraph.create(Modules.get(this));
    }

    public static void inject(Context context, Object object) {
        VielenGamesApp app = (VielenGamesApp) context.getApplicationContext();
        app.inject(object);
    }

    private void inject(Object object) {
        objectGraph.inject(object);
    }
}
