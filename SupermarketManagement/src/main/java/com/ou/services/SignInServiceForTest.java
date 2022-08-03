package com.ou.services;

import com.ou.repositories.SignInRepositoryForTest;

import java.sql.SQLException;
import java.util.List;

public class SignInServiceForTest {
    private static SignInRepositoryForTest signInRepositoryForTest;

    static {
        signInRepositoryForTest = new SignInRepositoryForTest();
    }

    static List<String> getStaff() throws SQLException {
        return signInRepositoryForTest.getStaff();
    }

    public Integer getIdByUsername (String username) {
        if (!username.isEmpty()) {
            try {
                return signInRepositoryForTest.getIdbyUsername(username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
