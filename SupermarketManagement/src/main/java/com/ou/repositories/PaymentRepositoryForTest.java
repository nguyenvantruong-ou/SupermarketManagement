package com.ou.repositories;

import com.ou.pojos.*;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepositoryForTest {
    // Lấy thông tin hóa đơn dựa vào id
    public Bill getBillByCreatedDate(String billCreatedDate) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query ="Select b.bill_id, b.bill_created_date, b.bill_customer_money, b.bill_total_money," +
                    "b.bill_total_sale_money, s.sta_id, m.mem_id " +
                    "FROM Bill b JOIN Staff s ON b.sta_id = b.sta_id " +
                    "JOIN Member m ON b.mem_id = m.mem_id " +
                    "WHERE b.bill_created_date = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, billCreatedDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Bill bill  =new Bill();
                Staff staff  = new Staff();
                staff.setPersId(resultSet.getInt("sta_id"));
                Member member = new Member();
                member.setPersId(resultSet.getInt("mem_id"));
                bill.setBillCreatedDate(resultSet.getTimestamp("bill_created_date"));
                bill.setBillTotalMoney(resultSet.getBigDecimal("bill_total_money"));
                bill.setBillTotalSaleMoney(resultSet.getBigDecimal("bill_total_sale_money"));
                bill.setBillCustomerMoney(resultSet.getBigDecimal("bill_customer_money"));
                bill.setProductBills(getProductBills(resultSet.getInt("bill_id")));
                bill.setStaff(staff);
                bill.setMember(member);
                return bill;
            }
            return null;
        }
    }

    // Lấy thông tin sản phẩm của hóa đơn
    private List<ProductBill> getProductBills(int billId) throws SQLException{
        List<ProductBill> productBills = new ArrayList<>();
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT pu.pru_id, pb.pro_amount " +
                    "FROM Product_Bill pb JOIN Product_Unit pu ON pb.pru_id = pu.pru_id " +
                    "WHERE pb.bill_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ProductBill productBill = new ProductBill();
                ProductUnit productUnit = new ProductUnit();
                productUnit.setPruId(resultSet.getInt("pru_id"));
                productBill.setProductUnit(productUnit);
                productBill.setProAmount(resultSet.getInt("pro_amount"));
                productBills.add(productBill);
            }

        }
        return productBills;
    }
}
