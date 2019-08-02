package com.dataexo.zblog.service.impl;


import com.dataexo.zblog.mapper.ReviewMapper;
import com.dataexo.zblog.service.ReviewService;
import com.dataexo.zblog.vo.Customer_reviews;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public List<Customer_reviews> loadReviewsByDatasetId(Integer id){
        return reviewMapper.loadReviewsByDatasetId(id);
    }

    @Override
    public Double getTotalStars(Integer id){
        return reviewMapper.getTotalStars(id);
    }

    @Override
    public void makeHelpful(Integer reviewItemId, int helpfulNum) {
        reviewMapper.makeHelpful(reviewItemId,helpfulNum);
    }

    @Override
    public void postReview(Customer_reviews review, HttpSession session) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        review.setUpdated_date(dateFormat.format(date));
        review.setHelpful_num(0);
        User userinfo = (User) session.getAttribute("user");
        if(userinfo != null){
            review.setCustomer_id((int) userinfo.getId());
            review.setCustomer_name(userinfo.getBusiness_name());
        }
        reviewMapper.postReview(review);
    }






    @Override
    public List<Customer_reviews> findAll() {
        return reviewMapper.findAll();
    }

    @Override
    public void saveReviews(Customer_reviews reviews) {
        reviewMapper.saveReviews(reviews);
    }

    @Override
    public List<Customer_reviews> loadReviews(Pager pager) {
        pager.setStart(pager.getStart());
        return reviewMapper.loadReviews(pager);
    }

    @Override
    public Customer_reviews getReviewsById(Integer id) {
        return reviewMapper.getReviewsById(id);
    }

    @Override
    public void deleteReviews(Integer id) {
        reviewMapper.deleteReviews(id);
    }

    @Override
    public void updateReviews(Customer_reviews reviews) {
        reviewMapper.updateReviews(reviews);
    }

    @Override
    public void initPage(Pager pager) {
        int count = reviewMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public List<Customer_reviews> loadReviews4Vendor(Pager<Customer_reviews> pager) {
        return reviewMapper.loadReviews4Vendor(pager);
    }

    @Override
    public int getTotalReview(Pager pager) {
        return reviewMapper.getTotalReview(pager);
    }

}
