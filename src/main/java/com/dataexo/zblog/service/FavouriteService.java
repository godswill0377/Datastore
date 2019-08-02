package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.MyFavourite;
import com.dataexo.zblog.vo.Token;

import java.util.List;

/**
 * This class is for token management service and this will be used when the user is forgotten their password.
 * @Date 2017/6/20
 * version 1.0
 */

public interface FavouriteService {
    List<MyFavourite> getFavouriteList(MyFavourite favourite);
    void insertFavourite(MyFavourite favourite);
    MyFavourite getByOne(MyFavourite favourite);

     void deleteFavourite(MyFavourite favourite);
}
