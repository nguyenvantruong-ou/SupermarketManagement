package com.ou.repositories;

import com.ou.pojos.*;
import com.ou.services.MemberService;
import com.ou.services.StaffService;
import com.ou.utils.DatabaseUtils;
import com.ou.utils.PersonType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillRepository {
    private final static StaffService STAFF_SERVICE;
    private final static MemberService MEMBER_SERVICE;


    static {
        STAFF_SERVICE = new StaffService();
        MEMBER_SERVICE = new MemberService();
    }

    // Lấy danh sách hóa đơn do nhân viên tạo dựa vào tên nhân viên
    public List<Bill> getBills(String name, PersonType personType, List<String> dates) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT b.bill_id, b.bill_created_date, b.bill_customer_money," +
                    "b.bill_total_money, b.bill_total_sale_money, s.sta_id, m.mem_id " +
                    "FROM Bill b JOIN Staff s ON b.sta_id = s.sta_id " +
                    "LEFT JOIN Member m ON b.mem_id = m.mem_id ";
            if (name == null || name.isEmpty())
                name = "";
            else
                name = name.trim();

            if (personType == null)
                personType = PersonType.STAFF;

            if (personType == PersonType.STAFF)
                query += "JOIN Person p1 ON s.sta_id = p1.pers_id ";

            if (personType == PersonType.MEMBER)
                query += "JOIN Person p1 ON m.mem_id = p1.pers_id ";

            query +="WHERE (p1.pers_first_name LIKE CONCAT(\"%\", ? , \"%\") " +
                    "OR p1.pers_last_name LIKE CONCAT(\"%\", ? , \"%\")) ";

            if (dates != null && dates.size() == 1)
                query += "AND DATE(b.bill_created_date) = ?";

            if (dates != null && dates.size() == 2)
                query += "AND DATE(b.bill_created_date) BETWEEN ? AND ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name.trim());
            preparedStatement.setString(2, name.trim());
            if (dates != null && dates.size() >= 1)
                preparedStatement.setString(3, dates.get(0));
            if (dates != null && dates.size() == 2)
                preparedStatement.setString(4, dates.get(1));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Bill bill = new Bill();
                int billId = resultSet.getInt("bill_id");
                int staId = resultSet.getInt("sta_id");
                Integer memId = resultSet.getInt("mem_id");
                bill.setBillId(billId);
                bill.setBillCreatedDate(resultSet.getTimestamp("bill_created_date"));
                bill.setBillCustomerMoney(resultSet.getBigDecimal("bill_customer_money"));
                bill.setBillTotalSaleMoney(resultSet.getBigDecimal("bill_total_sale_money"));
                bill.setBillTotalMoney(resultSet.getBigDecimal("bill_total_money"));
                bill.setStaff(STAFF_SERVICE.getStaffById(staId));

                Member member = MEMBER_SERVICE.getMemberById(memId);
                if (member != null)
                    bill.setMember(member);

                bills.add(bill);
            }
        }
        return bills;
    }

    // Lấy số lượng hóa đơn
    public int getBillAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as bill_amount FROM Bill";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("bill_amount");
        }
        return 0;
    }

    // Lấy danh sách sản phẩm tương ứng của hóa đơn
    public List<ProductBill> getProductBillsByBillId(int billId) throws SQLException {
        List<ProductBill> productBills = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT p.pro_id, p.pro_name, pb.pro_amount, c.cat_name, m.man_name, " +
                    "pu.pro_price, u.uni_name " +
                    "FROM Product_Bill pb JOIN Product_Unit pu ON pb.pru_id = pu.pru_id " +
                    "JOIN Product p ON pu.pro_id = p.pro_id " +
                    "JOIN Bill b ON pb.bill_id = b.bill_id " +
                    "JOIN Unit u ON pu.uni_id = u.uni_id " +
                    "JOIN Category c ON p.cat_id = c.cat_id " +
                    "JOIN Manufacturer m ON p.man_id = m.man_id " +
                    "WHERE b.bill_id = ? " +
                    "ORDER BY p.pro_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ProductUnit productUnit = new ProductUnit();
                Product product = new Product();
                Unit unit = new Unit();
                Category category = new Category();
                Manufacturer manufacturer = new Manufacturer();
                ProductBill productBill = new ProductBill();
                product.setProId(resultSet.getInt("pro_id"));
                product.setProName(resultSet.getString("pro_name"));
                category.setCatName(resultSet.getString("cat_name"));
                manufacturer.setManName(resultSet.getString("man_name"));
                unit.setUniName(resultSet.getString("uni_name"));
                product.setCategory(category);
                product.setManufacturer(manufacturer);
                productUnit.setProduct(product);
                productUnit.setUnit(unit);
                productUnit.setProPrice(resultSet.getBigDecimal("pro_price"));
                productBill.setProductUnit(productUnit);
                productBill.setProAmount(resultSet.getInt("pro_amount"));
                productBills.add(productBill);
            }
        }
        return productBills;
    }

    // Lấy thông tin ngày tạo hóa đơn
    public Date getCreatedDateBill(int billId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT bill_created_date FROM Bill WHERE bill_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getDate("bill_created_date");
        }
        return null;
    }
}
