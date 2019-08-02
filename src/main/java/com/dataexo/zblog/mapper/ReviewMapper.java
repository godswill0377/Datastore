package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Customer_reviews;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Question_anwsers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ReviewMapper {

    List<Customer_reviews> loadReviewsByDatasetId(Integer id);

    Double getTotalStars(Integer id);

    void makeHelpful(@Param("reviewItemId") Integer reviewItemId, @Param("helpfulNum") int helpfulNum);

    void postReview(Customer_reviews review);

    List<Customer_reviews> findAll();

    void saveReviews(Customer_reviews reviews);

    List<Customer_reviews> loadReviews(Pager pager);

    Customer_reviews getReviewsById(Integer id);

    void deleteReviews(Integer id);

    void updateReviews(Customer_reviews reviews);

    int initPage(Pager pager);

    List<Customer_reviews> loadReviews4Vendor(Pager<Customer_reviews> pager);

    int getTotalReview(Pager pager);
}
