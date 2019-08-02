package com.dataexo.zblog.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Pager<T> implements Serializable {

    /**

     * 默认每页显示数

     */
    public static final int PAGE_SIZE = 10;

    /**

     * 默认页数

     */
    public static final int PAGE_NUM = 1;

    /**

     * 页数

     */
    private int page;

    /**

     * 每页显示数

     */
    private int limit = PAGE_SIZE;

    /**

     * 总页数

     */
    private int totalPageNum;

    /**

     * 记录总数

     */
    private int totalCount;

    /**

     * 分页信息

     */
    private List<T> rows = new ArrayList<T>();

    /**

     * 分页计算起始值

     */
    private int start;

    private String diagram_update_time;
    /**
     * 分页计算结束值  暂时没用
     */
    private int endIndex;

    private int vendor_flag;

    private String parent_code;
    private String asset_class_ids;
    private String data_type_ids;
    private String region_ids;
    private String publisher_ids;
    // for vendor dashboard
    private Integer dataset_id;
    private Integer vendor_id;
    private Integer user_id;
    private String order_by;

    public String getCateid() {
        return cateid;
    }

    public void setCateid(String cateid) {
        this.cateid = cateid;
    }

    private String cateid;
    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    private String is_free;
    public int getIs_sample_available() {
        return is_sample_available;
    }

    public void setIs_sample_available(int is_sample_available) {
        this.is_sample_available = is_sample_available;
    }

    private int is_sample_available;
    public void setPageNum(int pageNum) {
        if(pageNum <= 0){
            pageNum = PAGE_NUM;
        }
        if(pageNum > totalPageNum){
            pageNum = totalPageNum;
        }
        // 分页开始值 关键

        if(pageNum == 0){
            start = 0;
        }else{
            start = (pageNum - 1) * limit;
        }
        this.page = pageNum;
    }

    public int getStart() {
        // 分页开始值 关键

        if (page == 0) {
            start = 0;
        } else {
            start = (page - 1) * limit;
        }
        return start;
    }

    public void setPageSize(int pageSize) {
        if(pageSize <= 0){
            pageSize = PAGE_SIZE;
        }
        // 计算最大页数

        int pageCount = totalCount / pageSize;
        if(totalCount % pageSize == 0){
            totalPageNum = pageCount;
        }else{
            totalPageNum = pageCount + 1;
        }
        this.limit = pageSize;
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        if(totalCount > 0){
            if(page <= 0){
                page = PAGE_NUM;
            }
            // 计算最大页数

            int pageCount = totalCount / PAGE_SIZE;
            if(totalCount % PAGE_SIZE == 0){
                totalPageNum = pageCount;
            }else{
                totalPageNum = pageCount + 1;
            }
        }else{
            totalPageNum = 0;
        }

        if(page > totalPageNum){
            page = totalPageNum;
        }
    }

    public String getSearch_str() {
        return search_str;
    }

    public void setSearch_str(String search_str) {
        this.search_str = search_str;
    }
    private String search_str;


    public String getPrice_model_ids() {
        return price_model_ids;
    }

    public void setPrice_model_ids(String price_model_ids) {

        this.price_model_ids = price_model_ids;
    }

    public String getAsset_class_ids() {
        return asset_class_ids;
    }

    public void setAsset_class_ids(String asset_class_ids) {
        this.asset_class_ids = asset_class_ids;
    }

    public String getData_type_ids() {
        return data_type_ids;
    }

    public void setData_type_ids(String data_type_ids) {
        this.data_type_ids = data_type_ids;
    }

    public String getRegion_ids() {
        return region_ids;
    }

    public void setRegion_ids(String region_ids) {
        this.region_ids = region_ids;
    }

    public String getPublisher_ids() {
        return publisher_ids;
    }

    public void setPublisher_ids(String publisher_ids) {
        this.publisher_ids = publisher_ids;
    }

    private String price_model_ids;

    public String getParent_code() {
        return parent_code;
    }

    public void setParent_code(String parent_code) {
        this.parent_code = parent_code;
    }


    public String[] getPrice_model_itr() {
        return price_model_itr;
    }

    public void setPrice_model_itr(String[] price_model_itr) {
        this.price_model_itr = price_model_itr;
    }

    public String[] getAsset_class_itr() {
        return asset_class_itr;
    }

    public void setAsset_class_itr(String[] asset_class_itr) {
        this.asset_class_itr = asset_class_itr;
    }

    public String[] getData_type_itr() {
        return data_type_itr;
    }

    public void setData_type_itr(String[] data_type_itr) {
        this.data_type_itr = data_type_itr;
    }

    public String[] getRegion_itr() {
        return region_itr;
    }

    public void setRegion_itr(String[] region_itr) {
        this.region_itr = region_itr;
    }

    public String[] getPublisher_itr() {
        return publisher_itr;
    }

    public void setPublisher_itr(String[] publisher_itr) {
        this.publisher_itr = publisher_itr;
    }

    private String[] price_model_itr;
    private String[] asset_class_itr;
    private String[] data_type_itr;
    private String[] region_itr;
    private String[] publisher_itr;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setStart(int start) {
        this.start = start;
    }


    public String getDiagram_update_time() {
        return diagram_update_time;
    }

    public void setDiagram_update_time(String diagram_update_time) {
        this.diagram_update_time = diagram_update_time;
    }

    public int getVendor_flag() {
        return vendor_flag;
    }

    public void setVendor_flag(int vendor_flag) {
        this.vendor_flag = vendor_flag;
    }




    public Integer getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(Integer vendor_id) {
        this.vendor_id = vendor_id;
    }


    public Integer getDataset_id() {
        return dataset_id;
    }

    public void setDataset_id(Integer dataset_id) {
        this.dataset_id = dataset_id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    private Integer questionId;


    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    // for transaction log from date and to date search functionality
    private String dateTo;

    private String dateFrom;

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    //1 for email marketing 2 for coupon marketing
    private int couponOrEmail;

    public int getCouponOrEmail() {
        return couponOrEmail;
    }

    public void setCouponOrEmail(int couponOrEmail) {
        this.couponOrEmail = couponOrEmail;
    }

    private int trans_type, status;

    public int getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(int trans_type) {
        this.trans_type = trans_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int coupon_id;

    public int getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(int coupon_id) {
        this.coupon_id = coupon_id;
    }
}