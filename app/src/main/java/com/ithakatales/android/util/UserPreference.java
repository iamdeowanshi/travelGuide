package com.ithakatales.android.util;

import com.google.gson.Gson;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.User;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class UserPreference {

    public static final String USER = "_USER" ;

    @Inject PreferenceUtil preferenceUtil;
    @Inject Gson gson;

    public UserPreference() {
        Injector.instance().inject(this);
    }

    public void saveUser(User user) {
        User existingUser = readUser();

        // if no user in preference, save and return
        if (existingUser == null) {
            preferenceUtil.save(USER, user);
            return;
        }

        // if user already in preference, update/merge with new info and save
        existingUser.merge(user);
        preferenceUtil.save(USER, existingUser);
    }

    public User readUser() {
        return (User) preferenceUtil.read(UserPreference.USER, User.class);
    }

    public void removeUser() {
        preferenceUtil.remove(USER);
    }

}
