package com.ou.services;

import com.ou.pojos.Unit;
import com.ou.repositories.UnitRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class UnitService {
    private final static UnitRepository UNIT_REPOSITORY;

    static {
        UNIT_REPOSITORY = new UnitRepository();
    }


    // Lấy danh sách các đơn vị
    public List<Unit> getUnits(String kw) throws SQLException {
        return UNIT_REPOSITORY.getUnits(kw);
    }

    // Lấy số lượng đơn vị
    public int getUnitAmount() throws SQLException {
        return UNIT_REPOSITORY.getUnitAmount();
    }

    // Thêm đơn vị
    public boolean addUnit(Unit unit) throws SQLException {
        if (unit == null ||
                unit.getUniName() == null || unit.getUniName().trim().isEmpty() ||
                unit.getUniName().trim().length() >= 100)
            return false;
        if (UNIT_REPOSITORY.isExistUnit(unit)) {
            Unit unitAdd = UNIT_REPOSITORY.getUnits(unit.getUniName().trim()).get(0);
            if (!unitAdd.getUniIsActive())
                return UNIT_REPOSITORY.addUnit(unitAdd);
            return false;
        }
        return UNIT_REPOSITORY.addUnit(unit);
    }

    // Chỉnh sửa thông tin đơn vị
    public boolean updateUnit(Unit unit) throws SQLException {
        if (unit == null ||
                unit.getUniId() == null ||
                unit.getUniName() == null || unit.getUniName().trim().isEmpty() ||
                unit.getUniName().trim().length() >= 100)
            return false;
        if (!UNIT_REPOSITORY.isExistUnit(unit.getUniId()))
            return false;
        if (UNIT_REPOSITORY.isExistUnit(unit)) {
            Unit existUnit = UNIT_REPOSITORY.getUnitByName(unit.getUniName());
            if (!Objects.equals(existUnit.getUniId(), unit.getUniId()))
                return false;
        }
        return UNIT_REPOSITORY.updateUnit(unit);
    }

    // Xóa đơn vị
    public boolean deleteUnit(Unit unit) throws SQLException {
        if (unit == null || unit.getUniId() == null)
            return false;
        if (!UNIT_REPOSITORY.isExistUnit(unit.getUniId()))
            return false;
        return UNIT_REPOSITORY.deleteUnit(unit);
    }

    // Lấy tất cả những đơn vị còn hoạt động
    public List<Unit> getAllActiveUnit() throws SQLException {
        return UNIT_REPOSITORY.getAllActiveUnit();
    }

    //Lấy thông tin của unit dựa vào tên
    public Unit getUnitByName(String uniName) throws SQLException {
        if (uniName == null)
            return null;
        return UNIT_REPOSITORY.getUnitByName(uniName);
    }
}
