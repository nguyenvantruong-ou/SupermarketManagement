package com.ou.services;

import com.ou.pojos.Manufacturer;
import com.ou.repositories.ManufacturerRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ManufacturerService {
    private final static ManufacturerRepository MANUFACTURER_REPOSITORY;

    static {
        MANUFACTURER_REPOSITORY = new ManufacturerRepository();
    }

    // Lấy danh sách các nhà sản xuất
    public List<Manufacturer> getManufacturers(String kw) throws SQLException {
        return MANUFACTURER_REPOSITORY.getManufacturers(kw);
    }

    // Lấy số lượng nhà sản xuất
    public int getManufacturerAmount() throws SQLException {
        return MANUFACTURER_REPOSITORY.getManufacturerAmount();
    }

    // Thêm nhà sản xuất
    public boolean addManufacturer(Manufacturer manufacturer) throws SQLException {
        if (manufacturer == null ||
                manufacturer.getManName() == null || manufacturer.getManName().trim().isEmpty() ||
                manufacturer.getManName().trim().length() >= 200)
            return false;
        if (MANUFACTURER_REPOSITORY.isExistManufacturer(manufacturer)) {
            Manufacturer manufacturerAdd = MANUFACTURER_REPOSITORY.getManufacturers(manufacturer.getManName()
                    .trim()).get(0);
            if (!manufacturerAdd.getManIsActive())
                return MANUFACTURER_REPOSITORY.addManufacturer(manufacturerAdd);
            return false;
        }
        return MANUFACTURER_REPOSITORY.addManufacturer(manufacturer);
    }

    // Chỉnh sửa thông tin nhà sản xuất
    public boolean updateManufacturer(Manufacturer manufacturer) throws SQLException {
        if (manufacturer == null ||
                manufacturer.getManId() == null ||
                manufacturer.getManName() == null || manufacturer.getManName().trim().isEmpty() ||
                manufacturer.getManName().trim().length() >= 200)
            return false;
        if (!MANUFACTURER_REPOSITORY.isExistManufacturer(manufacturer.getManId()))
            return false;
        if (MANUFACTURER_REPOSITORY.isExistManufacturer(manufacturer)) {
            Manufacturer existMan = MANUFACTURER_REPOSITORY.getManufacturerByName(manufacturer.getManName());
            if (!Objects.equals(existMan.getManId(), manufacturer.getManId()))
                return false;
        }
        return MANUFACTURER_REPOSITORY.updateManufacturer(manufacturer);
    }

    // Xóa nhà sản xuất
    public boolean deleteManufacturer(Manufacturer manufacturer) throws SQLException {
        if (manufacturer == null || manufacturer.getManId() == null)
            return false;
        if (!MANUFACTURER_REPOSITORY.isExistManufacturer(manufacturer.getManId()))
            return false;
        return MANUFACTURER_REPOSITORY.deleteManufacturer(manufacturer);
    }

    // Lấy tất cả các nhà sản xuất còn hoạt động
    public List<Manufacturer> getAllActiveManufacturer() throws SQLException {
        return MANUFACTURER_REPOSITORY.getAllActiveManufacturer();
    }

    // Láy thông tin của manufacturer dựa vào tên nhà sản xuất
    public Manufacturer getManufacturerByName(String manName) throws SQLException {
        if (manName == null)
            return null;
        return MANUFACTURER_REPOSITORY.getManufacturerByName(manName);
    }
}
