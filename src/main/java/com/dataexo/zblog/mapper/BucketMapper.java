package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Bucket;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Purchase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BucketMapper {


    void insertBucket(Bucket bucket);

    void deleteBucket(Bucket bucket);

    Bucket checkExist(Bucket bucket);

    Bucket selectBucket(Bucket bucket);

    int initPage(Pager pager);

    List<Bucket> loadBucket(Pager pager);

    void updateBucketInfo(Bucket bucket);

    void updateBucketInfoById(Bucket bucket);
}
