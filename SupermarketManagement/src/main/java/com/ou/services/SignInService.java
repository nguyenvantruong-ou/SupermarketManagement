package com.ou.services;

import com.ou.pojos.Staff;
import com.ou.repositories.SignInRepository;
import com.ou.utils.MD5Utils;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInService {
    private final static SignInRepository SIGN_IN_REPOSITORY;

    static {
        SIGN_IN_REPOSITORY = new SignInRepository();
    }
    

    // Lay thong tin tai khoan
    public Staff getAccountMD5(String username, String password) throws SQLException, NoSuchAlgorithmException{
        try {
            password = MD5(password);
        } catch (Exception ex) {
            Logger.getLogger(Staff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SIGN_IN_REPOSITORY.getAccount(username, password);
    }

    // Lấy mã MD5 ---- Phục vụ viết testcase
    public String MD5(String text){
        return MD5Utils.getMd5(text);
    }
}
