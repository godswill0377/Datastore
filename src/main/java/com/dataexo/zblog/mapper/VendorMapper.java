package com.dataexo.zblog.mapper;


import com.dataexo.zblog.vo.Vendors;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VendorMapper {

    List<Vendors> findAll();

    long insertVendor(Vendors vendor);

    void  updateInfo(Vendors vendor);

    Vendors getVendorIDById(long id);

    void  updateVendorInfoById(Vendors vendor);


    List<Vendors> getAvailableVendors();

    Vendors getVendorByAccountId(String vendorId);

    void deleteVendor(String id);
}
