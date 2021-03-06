package com.vielengames.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;

public interface LoginButtonProvider {
    void init(CreateSessionCallback callback, ViewGroup buttonsContainer);

    void close();

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
}
