package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.FavouriteMapper;
import com.dataexo.zblog.mapper.TokenMapper;
import com.dataexo.zblog.service.FavouriteService;
import com.dataexo.zblog.service.TokenService;
import com.dataexo.zblog.vo.MyFavourite;
import com.dataexo.zblog.vo.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class FavouriteServiceImpl implements FavouriteService {

    @Resource
    private FavouriteMapper favouriteMapper;

    @Override
    public List<MyFavourite> getFavouriteList(MyFavourite favourite){
        return favouriteMapper.getFavouriteList(favourite);
    }
    @Override
    public void insertFavourite(MyFavourite favourite){
        favouriteMapper.insertFavourite(favourite);
    }
    @Override
    public MyFavourite getByOne(MyFavourite favourite){
        return favouriteMapper.getByOne(favourite);
    }

    @Override
    public void deleteFavourite(MyFavourite favourite){
        favouriteMapper.deleteFavourite(favourite);
    }

}
