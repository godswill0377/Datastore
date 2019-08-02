package com.dataexo.zblog.service;


import com.dataexo.zblog.vo.Customer_reviews;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ReviewService {

    List<Customer_reviews> loadReviewsByDatasetId(Integer id);
    Double getTotalStars(Integer id);

    void makeHelpful(Integer reviewItemId, int helpfulNum);

    void postReview(Customer_reviews review, HttpSession session);



    List<Customer_reviews> findAll();

    void saveReviews(Customer_reviews reviews);

    /**
     * 分页查询好友列表
     * @param pager
     * @return
     */
    List<Customer_reviews> loadReviews(Pager pager);

    Customer_reviews getReviewsById(Integer id);

    void deleteReviews(Integer id);

    void updateReviews(Customer_reviews reviews);

    void initPage(Pager pager);

    List<Customer_reviews> loadReviews4Vendor(Pager<Customer_reviews> pager);

    int getTotalReview (Pager pager);
}
