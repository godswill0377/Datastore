package com.dataexo.zblog.service;


import com.dataexo.zblog.vo.Vendors;

import java.util.List;

public interface VendorService {

    long insertVendorInfo(Vendors vendor);

    Vendors getVendorIDById(long id);

    void updateVendorInfoById(Vendors vendor);

    List<Vendors> findAll();

    List<Vendors> getAvailableVendors();

    Vendors getVendorByAccountId(String vendorId);

    void deleteVendor(String id);
}
