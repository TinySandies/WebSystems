package com.tinysand.system.services;

import com.tinysand.system.models.User;

import java.sql.SQLException;

public interface ISystemEntrance {
    boolean registration(User user) throws SQLException;
    boolean systemSignIn(User user) throws SQLException;
}
