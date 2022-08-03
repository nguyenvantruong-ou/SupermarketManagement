package com.ou.repositories;

import com.ou.pojos.Staff;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInRepository {
    
    public Staff getAccount(String username, String password) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Staff,Person  WHERE Staff.sta_id = Person.pers_id"
                    + " AND Staff.sta_username = ? "
                    + " AND Staff.sta_password =  ? "
                    + " AND Person.pers_is_active = true";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            
            if(rs.next()){
                Staff staff = new Staff();
                
                staff.setStaUsername(rs.getString("sta_username"));
                staff.setStaPassword(rs.getString("sta_password"));
                staff.setStaIsAdmin(rs.getBoolean("sta_is_admin"));
                staff.setPersId(rs.getInt("sta_id"));
                return  staff;
            }
            else
                return null;
        }
    }

}
