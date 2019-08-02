package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.MyFavourite;
import com.dataexo.zblog.vo.Token;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface FavouriteMapper {


    List<MyFavourite> getFavouriteList(MyFavourite favourite);

    void insertFavourite(MyFavourite favourite);

    MyFavourite getByOne(MyFavourite favourite);

    void deleteFavourite(MyFavourite favourite);

}
