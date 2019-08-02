package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.BucketMapper;
import com.dataexo.zblog.mapper.PurchaseMapper;
import com.dataexo.zblog.service.BucketService;
import com.dataexo.zblog.service.PurchaseService;
import com.dataexo.zblog.vo.Bucket;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Purchase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class BucketServiceImpl implements BucketService{

    @Resource
    private BucketMapper bucketMapper;

    @Override
    public Bucket selectBucket(Bucket bucket){
       return bucketMapper.selectBucket(bucket);
    }

    @Override
    public void insertBucket(Bucket bucket){
        bucketMapper.insertBucket(bucket);
    }

    @Override
    public Bucket checkExist(Bucket bucket){
        return bucketMapper.checkExist(bucket);
    }

    @Override
    public void deleteBucket(Bucket bucket){
        bucketMapper.deleteBucket(bucket);
    }

    @Override
    public void updateBucketInfo (Bucket bucket){
        bucketMapper.updateBucketInfo (bucket);
    }

    @Override
    public void updateBucketInfoById (Bucket bucket){
        bucketMapper.updateBucketInfoById (bucket);
    }




    @Override
    public void initPage(Pager pager){
        int count =  bucketMapper.initPage( pager);
        pager.setTotalPageNum(count / pager.getLimit() + 1);
        pager.setTotalCount(count);

    }

    @Override
    public List<Bucket> loadBucket (Pager pager) {
        return  bucketMapper.loadBucket(pager);
    }

}
