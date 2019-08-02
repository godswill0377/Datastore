package com.dataexo.zblog.service.impl;


import com.dataexo.zblog.mapper.VendorMapper;
import com.dataexo.zblog.service.VendorService;
import com.dataexo.zblog.vo.Vendors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {



    @Resource
    VendorMapper vendorMapper;


    @Override
    public  long insertVendorInfo(Vendors vendor) {
          long vendor_id=vendorMapper.insertVendor(vendor);
          return vendor_id;
    }


    public void updateInfo(Vendors vendor)
    {
        vendorMapper.updateInfo(vendor);
    }

   public Vendors getVendorIDById(long id)
   {
       return vendorMapper.getVendorIDById(id);
   }

   public void  updateVendorInfoById(Vendors vendor)
   {
       vendorMapper.updateVendorInfoById(vendor);
   }

   public List<Vendors> findAll()
   {
       return vendorMapper.findAll();
   }

    public List<Vendors> getAvailableVendors()
    {
        return vendorMapper.getAvailableVendors();
    }

    public Vendors getVendorByAccountId(String vendorId){
        return vendorMapper.getVendorByAccountId(vendorId);
    }

    public void deleteVendor(String id){

        vendorMapper.deleteVendor(id);
    }



}
