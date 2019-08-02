package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Bucket;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Purchase;

import java.util.List;


public interface BucketService {

    void insertBucket(Bucket bucket);
    Bucket checkExist(Bucket bucket);
    void deleteBucket(Bucket bucket);
    Bucket selectBucket(Bucket bucket);
    List<Bucket> loadBucket(Pager pager);

    void initPage(Pager pager);

    void updateBucketInfo(Bucket bucket);
    void updateBucketInfoById(Bucket bucket);
}
