package com.ou.repositories;
import com.ou.pojos.Member;
import com.ou.pojos.MemberType;
import com.ou.pojos.Sale;
import com.ou.pojos.SalePercent;
import com.ou.services.MemberService;
import com.ou.utils.DatabaseUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberTypeRepository {
    private final static MemberService MEMBER_SERVICE;
    static {
        MEMBER_SERVICE = new MemberService();
    }
    // lấy danh sách loại thành viên
    public List<MemberType> getMemberTypes(String kw) throws SQLException {
        List<MemberType> memberTypes = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM MemberType mt JOIN Sale s ON mt.sale_id = s.sale_id" +
                    " WHERE memt_name LIKE CONCAT(\"%\", ?, \"%\") ORDER BY memt_total_money ASC";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                MemberType memberType = new MemberType();
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                memberType.setAmountMember(getTotalAmountMember(memberType.getMemtId()));
                Sale sale = new Sale();
                sale.setSaleId(resultSet.getInt("sale_id"));
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                sale.setSalePercent(getSalePercentById(resultSet.getInt("sper_id")));
                memberType.setSale(sale);
                memberTypes.add(memberType);
            }
        }
        return memberTypes;
    }

    // lấy tổng số thành viên của loại thành viên theo mã loại thành viên
    public int getTotalAmountMember(int memtId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(mt.memt_id) as amount_member " +
                    "FROM MemberType mt JOIN Member m ON mt.memt_id = m.memt_id " +
                    "WHERE mt.memt_id = ? " +
                    "GROUP BY mt.memt_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memtId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("amount_member");
            else
                return 0;
        }
    }

    // lấy sale percent theo mã
    public SalePercent getSalePercentById(int sperId) {
        SalePercent salePercent = new SalePercent();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent WHERE sper_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salePercent;
    }

    // lấy tổng sô member type đang hoạt động
    public int getTotalAmountMemberType() throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(memt_id) as total_amount FROM MemberType WHERE memt_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next())
                return resultSet.getInt("total_amount");
            return 0;
        }
    }

    // lấy danh sách các mã giảm giá
    public List<String> getSaleIds() throws SQLException {
        List<String> saleIds = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sale_id FROM Sale WHERE sale_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                saleIds.add(resultSet.getString("sale_id"));
            }
        }
        return saleIds;
    }

    // lấy sale percent by sale id
    public float getSalePercentBySaleId(int saleId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sp.sper_percent FROM Sale s JOIN SalePercent sp ON s.sper_id = sp.sper_id WHERE sale_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, saleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getFloat("sper_percent");
            return 0;
        }
    }

    // thêm một loại thành viên
    public boolean addMemberType(MemberType memberType) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            if(!memberType.getMemtIsActive()) {
                String query = "UPDATE MemberType SET memt_is_active = TRUE WHERE memt_name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, memberType.getMemtName());
                preparedStatement.executeUpdate();
            } else {
                String query = "INSERT INTO MemberType(memt_name, memt_total_money, memt_is_active, sale_id) VALUES(?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, memberType.getMemtName());
                preparedStatement.setBigDecimal(2, memberType.getMemtTotalMoney());
                preparedStatement.setBoolean(3, memberType.getMemtIsActive());
                preparedStatement.setInt(4, memberType.getSale().getSaleId());
                preparedStatement.executeUpdate();
            }
            updateMemberTypeId();
            return true;
        }
    }
    // sửa thông tin một loại thành viên
    public boolean updateMemberType(MemberType memberType) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE MemberType SET memt_name = ?, memt_total_money = ?, sale_id = ? WHERE memt_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, memberType.getMemtName());
            preparedStatement.setBigDecimal(2, memberType.getMemtTotalMoney());
            preparedStatement.setInt(3, memberType.getSale().getSaleId());
            preparedStatement.setInt(4, memberType.getMemtId());
            if(isChangedMemberTypeTotalMoney(memberType)){
                preparedStatement.executeUpdate();
                updateMemberTypeId();
                return true;
            }
            return 1 == preparedStatement.executeUpdate();
        }
    }
    // xóa thông tin một loại thành viên
    public boolean deleteMemberType(MemberType memberType) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE MemberType SET memt_is_active = FALSE WHERE memt_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberType.getMemtId());
            preparedStatement.executeUpdate();
            if(getTotalAmountMember(memberType.getMemtId()) > 0)
                updateMemberTypeId();
            return true;
        }
    }

    // kiểm tra tên loại thành viên có tồn tại
    public boolean isExitsMemberType(String memberTypeName) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM MemberType WHERE memt_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, memberTypeName);
            if(preparedStatement.executeQuery().next())
                return true;
            else
                return false;
        }
    }
    // kiểm tra tên loại thành viên có tồn tại
    public boolean isExitsMemberType(String memberTypeName, int memtId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM MemberType WHERE memt_name = ? AND memt_id != ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, memberTypeName);
            preparedStatement.setInt(2, memtId);
            if(preparedStatement.executeQuery().next())
                return true;
            else
                return false;
        }
    }

    // kiểm tra loại thành viên update info có thay đổi tổng tiền không
    // trả về false nếu k thay đổi, true nếu có thay đổi
    public boolean isChangedMemberTypeTotalMoney(MemberType memberType) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT memt_total_money FROM MemberType WHERE memt_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberType.getMemtId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return !resultSet.getBigDecimal("memt_total_money").equals(memberType.getMemtTotalMoney());
            else
                return false;
        }
    }

    private void updateMemberTypeId() throws SQLException {
        List<MemberType> memberTypes = getMemberTypes();
        List<Member> members = MEMBER_SERVICE.getMembers("");
        int i = 0;
        boolean b = false;
        for (MemberType type : memberTypes){
            for (Member member : members) {
                if (member.getMemTotalPurchase().compareTo(type.getMemtTotalMoney()) >= 0){
                    b = true;
                    MEMBER_SERVICE.updateMemberTypeId(member, type.getMemtId());
                }
                if(!b && i == 0)
                    MEMBER_SERVICE.updateMemberTypeId(member, 0);
            }
            i++;
        }
    }

    // lấy danh sách member type còn hoạt động
    public List<MemberType> getMemberTypes() throws SQLException {
        List<MemberType> memberTypes = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM MemberType mt JOIN Sale s ON mt.sale_id = s.sale_id" +
                    " WHERE memt_is_active = TRUE ORDER BY memt_total_money ASC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                MemberType memberType = new MemberType();
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                memberType.setAmountMember(getTotalAmountMember(memberType.getMemtId()));
                Sale sale = new Sale();
                sale.setSaleId(resultSet.getInt("sale_id"));
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                sale.setSalePercent(getSalePercentById(resultSet.getInt("sper_id")));
                memberType.setSale(sale);
                memberTypes.add(memberType);
            }
        }
        return memberTypes;
    }

    // kiểm tra tổng tiền thanh toán có trùng với loại thành viên khác không
    public boolean isExistTotalMoney(int memtId, BigDecimal totalMoney){
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM MemberType WHERE memt_id != ? AND memt_total_money = ?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setInt(1, memtId);
            p.setBigDecimal(2, totalMoney);
            ResultSet r = p.executeQuery();
            return !r.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
