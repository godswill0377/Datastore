package com.dataexo.zblog.controller;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This is main controller of the site. Most of the data process should be done in this controller.
 * For example , the data-sets list , data-sets-series list will be loaded in this conttroller.
 *
 *
 */
@Controller
@RequestMapping("/data_sets")
public class Data_setsController  extends AbstractController {

    private static final Logger logger = Logger.getLogger(Data_setsController.class);

    @Value("${address.domain}")
    private String domain;

    @Value("${address.email}")
    private String contact_email;

    @Value("${strip.pubkey}")
    private String pubkey;

    @Resource
    private Data_categoryService data_categoryService;

    @Resource
    private QuestionService questionService;

    @Resource
    private ReviewService reviewService;

    @Resource
    private FavouriteService favouriteService;

    @Resource
    private Data_setsService data_setsService;  //文章service

    @Resource
    private Data_sets_seriesService data_sets_seriesService;  //文章service

    @Resource
    private UserService userService;

    @Resource
    private VendorService vendorService;

    @Resource
    private Data_set_filtersService filtersService;

    @Autowired
    private PlanService planService;

    @Autowired
    private Sub_manageService sub_manageService;

    /**
     * Load the data_sets table data by pager
     * @param pager
     * @param model
     * @return
     */

    @RequestMapping("/load/data_sets")
    public String loadData_sets(Pager<Data_sets> pager,Model model){

        String price_ids = "" , assets_id = "" , datatype_id="",region_id="",pub_id="";
        if(pager.getPrice_model_ids().length() > 0){
            price_ids = pager.getPrice_model_ids().substring(0,pager.getPrice_model_ids().length() - 1);
        }
        if(pager.getAsset_class_ids().length() > 0){
            assets_id = pager.getAsset_class_ids().substring(0,pager.getAsset_class_ids().length() - 1);
        }
        if(pager.getData_type_ids().length() > 0){
            datatype_id = pager.getData_type_ids().substring(0,pager.getData_type_ids().length() - 1);
        }
        if(pager.getRegion_ids().length() > 0){
            region_id = pager.getRegion_ids().substring(0,pager.getRegion_ids().length() - 1);
        }
        if(pager.getPublisher_ids().length() > 0){
            pub_id = pager.getPublisher_ids().substring(0,pager.getPublisher_ids().length() - 1);
        }

        String[] prar = price_ids.split(",");
        pager.setPrice_model_itr(prar);

        String[] asar = assets_id.split(",");
        pager.setAsset_class_itr(asar);

        String[] dataar = datatype_id.split(",");
        pager.setData_type_itr(dataar);

        String[] regar = region_id.split(",");
        pager.setRegion_itr(regar);

        String[] pubar = pub_id.split(",");
        for(String string : pubar){
            if(string.equals("0")){
                string = "-1";
            }
        }
        pager.setPublisher_itr(pubar);

        List<Data_sets> data_setsList = data_setsService.loadData_sets(pager,null);

        model.addAttribute("data_setsList",data_setsList);
        model.addAttribute("cateid",pager.getCateid());
        model.addAttribute("domain",domain);

        return "subpart/maindata-sets";
    }

    /**
     * Load the data_sets_series table data by pager
     * @param pager
     * @param model
     * @return
     */

    @RequestMapping("/load/data_sets_series")
    public String loadData_sets_series(Pager<Data_sets> pager,Model model,HttpSession session) {

        User userinfo = (User) session.getAttribute("user");
        boolean subscription = false;
        if(userinfo != null){
            if(userinfo.getMembership() != 0){
                long cur= System.currentTimeMillis();
                long exp = Long.parseLong(userinfo.getExpire_date());
                if(cur <= exp){
                    subscription = true;
                    pager.setIs_sample_available(2);
                }
            }
        }

        List<Data_sets_series> data_sets_seriesList = data_sets_seriesService.loadData_sets_series(pager,null);

        if(subscription){
            for(int i = 0 ; i < data_sets_seriesList.size() ; i ++){
                Data_sets_series data_sets_series = data_sets_seriesList.get(i);
                data_sets_series.setIs_sample_available(1);
            }
        }

        model.addAttribute("data_sets_seriesList",data_sets_seriesList);
        model.addAttribute("cateid",pager.getCateid());
        model.addAttribute("domain",domain);
        return "subpart/maindata-sets-series";
    }

    /**
     * Load the series data and show in series list page.
     * @param cateid: this is data category id
     * @param model
     * @return
     */

    @RequestMapping("/cateid/{cateid}/details/{dataset_id}")
    public String viewDatasetSeries(@PathVariable String cateid
            ,@PathVariable String dataset_id,String searchQuestion,Model model,HttpSession session){

        baseRequest(session,model);
        Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(dataset_id));
        int datasets_count = data_sets_seriesService.getData_sets_count(data_sets.getId());

        if(data_sets.getPrice_model_id() == 1){

            model.addAttribute("is_free","1");
        }
        else{

            model.addAttribute("is_free","0");
        }

        //for Subscribe To button in front end
        int vendor_id = (int)data_sets.getVendor_id();
        List<Plan> plansByVendorId = planService.getPlanByVendorId(vendor_id);
        String flagMembership = "0";
        for(Plan plan: plansByVendorId){
            if(plan.getReal_price() > 0.00){
                flagMembership = "1";
                break;
            }
        }
        model.addAttribute("vendorName", vendorService.getVendorIDById(vendor_id).getLegal_name());
        model.addAttribute("flagMembership", flagMembership);
        Pager<Data_sets_series> pager = new Pager<Data_sets_series>();
        pager.setSearch_str("");
        pager.setParent_code(data_sets.getCode());
        pager.setStart(1);
        pager.setLimit(10000);

        if(data_sets.getEmbed_url_datagrid() == null ){
            data_sets.setEmbed_url_datagrid("");
        }
        if(data_sets.getEmbed_url_chart() == null ){
            data_sets.setEmbed_url_chart("");
        }

        List<Data_sets_series> data_sets_seriesList = data_sets_seriesService.loadData_sets_series(pager,null);

        for(Plan plan : plansByVendorId){
            String price = "N/A";
            if(plan.getPlan_id().contains("single") && plan.getPlan_id().contains("month")){
                if(plan.getReal_price() > 0){
                    price = "$" +plan.getReal_price();
                }
                model.addAttribute("single_monthly", price);
                }
            if(plan.getPlan_id().contains("single") && plan.getPlan_id().contains("year")){
                if(plan.getReal_price() > 0){
                    price = "$" +plan.getReal_price();
                }
                model.addAttribute("single_yearly", price);

            }
            if(plan.getPlan_id().contains("enterprise") && plan.getPlan_id().contains("month")){
                if(plan.getReal_price() > 0){
                    price = "$" +plan.getReal_price();
                }
                model.addAttribute("enterprise_monthly", price);
            }
            if(plan.getPlan_id().contains("enterprise") && plan.getPlan_id().contains("year")){
                if(plan.getReal_price() > 0){
                    price = "$" +plan.getReal_price();
                }
                model.addAttribute("enterprise_yearly", price);

            }
        }

        model.addAttribute("data_sets_seriesList",data_sets_seriesList);

        model.addAttribute("chart_days",Static.chart_days);
        model.addAttribute("base_url",domain);
        model.addAttribute("cateid",cateid);
        model.addAttribute("datasets_count",datasets_count);
        model.addAttribute("data_sets",data_sets);
        model.addAttribute("parent_code",data_sets.getCode());
        model.addAttribute("domain",domain);
        model.addAttribute("searchQuestion",searchQuestion);

        /////// get question list
        getQuestionList(dataset_id, searchQuestion, model);

        // get reviews list.
        getReviewList(dataset_id, model);

        data_setsService.increaseVisitingNum(dataset_id);
        if( "TABLES".equals(data_sets.getApi())){
            if(data_sets.getHas_series() == 1){
                return "blog/datasets_tables";
            }
            else{
                if(datasets_count == 1){
                    Data_sets_series data_sets_series = null;
                    for(Data_sets_series data_sets_seriess : data_sets_seriesList){
                        if(data_sets_seriess.getData_set_id() == data_sets.getId()){
                            data_sets_series = data_sets_seriess;
                        }
                    }
                    model.addAttribute("data_sets_series", data_sets_series);
                    // set filter query for detail of zeppelin
                    if(data_sets.getSchema_name() == "" && data_sets.getTable_name() == ""){
                        model.addAttribute("filter_query","");
                    }else{
                        String condition = "";
                        if((data_sets_series.getFilter_id() == null ||  data_sets_series.getFilter_id() == "" || data_sets_series.getFilter_id() == ",")
                                && (data_sets_series.getFilter_condition() == null || data_sets_series.getFilter_condition() == "") )
                        {
                            condition = "";
                        }
                        else{

                            if(data_sets_series.getFilter_id() == null ||  data_sets_series.getFilter_id() == "" || data_sets_series.getFilter_id() == ","){
                                condition = data_sets_series.getFilter_condition();
                                if(condition.indexOf("where") < 0){
                                    condition = "where " + condition;
                                }
                            }
                            else{
                                String[] ids = data_sets_series.getFilter_id().split(",");
                                for(int i = 0 ; i < ids.length && !ids[i].equals(""); i ++){
                                    Data_set_filters filterItem = filtersService.getFilterById(Integer.parseInt(ids[i]));

                                    if(i != 0){
                                        condition += " and ";
                                    }
                                    condition += filterItem.getColumn_name() + " " + filterItem.getComparator() + " " + filterItem.getFilter_value();

                                }
                                condition = "where " + condition;
                            }
                        }
                        condition = "select * from " + data_sets.getSchema_name() + "." + data_sets.getTable_name() + " " + condition;
                        model.addAttribute("filter_query", condition);

                        model.addAttribute("notename", data_sets.getSchema_name() + "_" + data_sets.getTable_name());
                    }

                }
                else{
                    if(data_sets.getSchema_name() == "" && data_sets.getTable_name() == ""){
                        model.addAttribute("filter_query","");
                    }else{
                        String condition = "select * from " + data_sets.getSchema_name() + "." + data_sets.getTable_name() ;
                        model.addAttribute("filter_query", condition);

                    }
                }
                return "blog/datasets_tables_grid";
            }
        }

        // increase visiting nums

        return "dataset/data_series";
    }

    private void getReviewList(@PathVariable String dataset_id, Model model) {
        List<Customer_reviews> reviewList =reviewService.loadReviewsByDatasetId(Integer.parseInt(dataset_id));

        Double totalStars = reviewService.getTotalStars(Integer.parseInt(dataset_id));

        for (int i = 0 ; i < reviewList.size() ; i ++){
            Customer_reviews item = reviewList.get(i);

            String dateStr = item.getUpdated_date();
            DateFormat format = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);
            try {
                Date date = format.parse(dateStr);
                format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                dateStr = format.format(date).toString();
                item.setUpdated_date(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            reviewList.set(i, item);
        }

        model.addAttribute("reviewList",reviewList);
        model.addAttribute("totalstars" , totalStars);
    }

    private void getQuestionList(@PathVariable String dataset_id, String searchQuestion, Model model) {
        Question_anwsers question = new Question_anwsers();
        question.setDataset_id(Integer.parseInt(dataset_id));
        question.setContent(searchQuestion);
        List<Question_anwsers> question_list = questionService.loadQuestionByDatasetId(question);

        for(int i = 0 ; i < question_list.size() ; i ++){
            Question_anwsers item = question_list.get(i);
            List<Question_anwsers> answerList = questionService.loadAnswerList(item.getId());

            if(answerList.size() == 0){
                Question_anwsers anwsers = new Question_anwsers();
                anwsers.setContent("No Answer");
                answerList.add(anwsers);
            }
            else{
                for(int j = 0 ; j < answerList.size() ; j ++) {
                    Question_anwsers answer_item = answerList.get(j);
                    String dateStr = answer_item.getUpdated_date();
                    DateFormat format = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);
                    try {
                        Date date = format.parse(dateStr);
                        format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                        dateStr = format.format(date).toString();
                        answer_item.setUpdated_date(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    answerList.set(j, answer_item);
                }

            }
            item.setAnswers_num(answerList.size() - 1);
            item.setAnwsers_list(answerList);

            question_list.set(i, item);
        }

        model.addAttribute("questionList",question_list);
    }

    /**
     * This function is for chart table page.
     * @param cateid: this is data category id
     * @param model
     * @return
     */

    @RequestMapping("/cateid/{cateid}/ddetails/{seriesid}")
    public String loadData_sets_seri_detail(@PathVariable String cateid
            , @PathVariable String seriesid
            ,HttpServletResponse response
            ,Model model
            ,HttpSession session){

        logger.debug("dataset view for chart and grid");

        User userinfo = (User) session.getAttribute("user");
        Data_sets_series data_sets_series = data_sets_seriesService.getData_sets_seriesById(Integer.parseInt(seriesid));
        if(baseRequest(session,model)){
            if(userinfo.getMembership() != 0 || data_sets_series.getIs_sample_available() == 1){
                if(userinfo.getMembership() != 0 && data_sets_series.getIs_sample_available() != 1){
                    long expire = Long.parseLong(userinfo.getExpire_date());
                    long cur = System.currentTimeMillis();
                    if(cur > expire){
                        model.addAttribute("same_available","0");
                    }
                    else{
                        model.addAttribute("same_available","1");
                    }
                }
                else{
                    model.addAttribute("same_available","1");
                }
            }
            else{
                model.addAttribute("same_available","0");
            }
        }

        String codeid = data_sets_series.getCode();
        String[] ar = codeid.split("/");
        if(ar.length > 0){
            codeid = ar[0];
        }

        Data_sets data_sets = data_setsService.getData_setsById(data_sets_series.getData_set_id());

        int datasets_count = data_sets_seriesService.getData_sets_count(data_sets_series.getData_set_id());
        String update_date = data_sets_series.getLatest_update_date();

        try {
            SimpleDateFormat  sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=sdf.parse(update_date);
            sdf= new SimpleDateFormat("dd MMM yyyy");
            update_date = sdf.format(date);
            update_date = "on " +update_date;
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error( e);
        }

        MyFavourite favourite = new MyFavourite();
        if(data_sets_series != null)
            favourite.setSeries_id(data_sets_series.getId());
        if(userinfo != null)
            favourite.setUserid(userinfo.getId());

        MyFavourite favour_flag = favouriteService.getByOne(favourite);
        if(favour_flag == null){
            model.addAttribute("favourite","fa fa-heart-o");
        }
        else{
            model.addAttribute("favourite","fa fa-heart");
        }
        if(data_sets.getPrice_model_id() == 1){
            model.addAttribute("is_free","1");
        }
        else{
            model.addAttribute("is_free","0");
        }

        // source_type=1:  this is chart or datagrid based on web service .
        // source_type=2:  this is chart or datagrid based on iframe .
        if(data_sets_series.getEmbed_url_chart().equals("")){
            model.addAttribute("source_type","1");
        }
        else{
            model.addAttribute("source_type","2");
        }

        // set filter query for detail of zeppelin
        if(data_sets.getSchema_name() == "" && data_sets.getTable_name() == ""){
            model.addAttribute("filter_query","");
        }
        else{
            String condition = "";
            if((data_sets_series.getFilter_id() == null ||  data_sets_series.getFilter_id() == "" || data_sets_series.getFilter_id() == ",")
                    && (data_sets_series.getFilter_condition() == null || data_sets_series.getFilter_condition() == "") )
            {
               condition = "";
            }
            else{

                if(data_sets_series.getFilter_id() == null ||  data_sets_series.getFilter_id() == "" || data_sets_series.getFilter_id() == ","){
                    condition = data_sets_series.getFilter_condition();
                    if(condition.indexOf("where") < 0){
                        condition = "where " + condition;
                    }
                }
                else{
                    String[] ids = data_sets_series.getFilter_id().split(",");
                    for(int i = 0 ; i < ids.length && !ids[i].equals(""); i ++){
                        Data_set_filters filterItem = filtersService.getFilterById(Integer.parseInt(ids[i]));

                        if(i != 0){
                            condition += " and ";
                        }
                        condition += filterItem.getColumn_name() + " " + filterItem.getComparator() + " " + filterItem.getFilter_value();

                    }
                    condition = "where " + condition;
                }
            }
            condition = "select * from " + data_sets.getSchema_name() + "." + data_sets.getTable_name() + " " + condition;
            model.addAttribute("filter_query", condition);

            model.addAttribute("notename", data_sets.getSchema_name() + "_" + data_sets.getTable_name());
        }


        // This is for sample data available check code part.
        // If you are a subscription user and it doesn't expire , you can be available to see the data set series.
        // But if not , you can't be available.

        model.addAttribute("cateid",cateid);
        model.addAttribute("base_url",domain);
        model.addAttribute("datasets_count",datasets_count);
        model.addAttribute("update_date",update_date);
        model.addAttribute("data_sets_series",data_sets_series);
        model.addAttribute("data_sets",data_sets);
        model.addAttribute("domain",domain);
        model.addAttribute("userinfo",userinfo);
        model.addAttribute("parent_code",codeid);

        response.addHeader("P3P","CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");

         return "dataset/view/timeseries";
    }

    /**
     * This function is for data_sets_series document page.
     * @param cateid: This is the data category id
     * @param codeid : This is the data CODE
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("/cateid/{cateid}/details/{codeid}/documentation")
    public String loadData_sets_document(@PathVariable String cateid,@PathVariable String codeid,Model model,HttpSession session,Pager<Data_category> pager){

        logger.debug("dataset document page view");

        baseRequest(session,model);
        Data_sets data_sets = data_setsService.getData_setsByCode(codeid);
        int datasets_count = data_sets_seriesService.getData_sets_count(data_sets.getId());

        if(data_sets.getPrice_model_id() == 1){
            model.addAttribute("is_free","1");
        }
        else{
            model.addAttribute("is_free","0");
        }

        pager.setSearch_str("");
        pager.setParent_code(codeid);
        pager.setIs_sample_available(2);
        pager.setStart(0);
        pager.setLimit(3);

        List<Data_sets_series> data_sets_seriesList = data_sets_seriesService.loadData_sets_series(pager,null);

        for(int i = 0 ; i < data_sets_seriesList.size(); i ++){
            SimpleDateFormat  sdf=new SimpleDateFormat("yyyy-MM-dd");
            Data_sets_series series = data_sets_seriesList.get(i);
            Date date= null;
            try {
                date = sdf.parse(series.getLatest_update_date());

            } catch (ParseException e) {
                e.printStackTrace();
                logger.error( e);
            }

            sdf= new SimpleDateFormat("dd MMM yyyy");
            if(date != null)
                series.setLatest_update_date(sdf.format(date));
            data_sets_seriesList.set(i,series);
        }


        if(data_sets.getPrice_model_id() == 1){
            model.addAttribute("is_free","1");
        }
        else{
            model.addAttribute("is_free","0");
        }

        if(datasets_count >= 3)
            model.addAttribute("datasets_count",datasets_count - 3);
        else{
            model.addAttribute("datasets_count",datasets_count );
        }

        model.addAttribute("data_sets_seriesList",data_sets_seriesList);
        model.addAttribute("data_sets",data_sets);
        model.addAttribute("base_url", domain);
        model.addAttribute("cateid",cateid);
        model.addAttribute("datasets_count",datasets_count);
        model.addAttribute("parent_code",codeid);
        model.addAttribute("domain",domain);


        return "dataset/data_docu";
    }

    @RequestMapping("/cateid/{cateid}/details/{codeid}/purchase")
    public String purchaseDatasets(@PathVariable String cateid, @PathVariable String codeid, Model model, HttpSession session) throws ParseException {

        logger.debug("dataset purchase page view");

        baseRequest(session,model);

        Data_sets data_sets = data_setsService.getData_setsByCode(codeid);

        User userinfo = (User) session.getAttribute("user");

        if(userinfo!= null){
            List<Sub_manage> sub_manages = sub_manageService.getSubscriptionByUserId(userinfo.getId());
            for(Sub_manage sub_manage : sub_manages){
                if(sub_manage.getVendor_id() == data_sets.getVendor_id()){
                    model.addAttribute("balance", sub_manage.getBalance());
                    model.addAttribute("membership", sub_manage.getId());

                    if(sub_manage.getStatus() == 1){
                        model.addAttribute("expired","0");
                    }else{
                        model.addAttribute("expired","1");
                        model.addAttribute("balance", "0");
                    }
                    break;
                }else{
                    model.addAttribute("balance", "0");
                    model.addAttribute("membership", "0");
                    model.addAttribute("expired","1");
                }


            }
        }

        if(data_sets.getPrice_model_id() == 1){
            model.addAttribute("is_free","1");
        }
        else{
            model.addAttribute("is_free","0");
        }

        model.addAttribute("contact_email",contact_email);

        model.addAttribute("cateid",cateid);

        model.addAttribute("data_sets",data_sets);
        model.addAttribute("base_url", domain);
        model.addAttribute("parent_code",codeid);
        model.addAttribute("domain",domain);
        model.addAttribute("stripe_pubkey",pubkey);

        return "dataset/data_purchase";
    }


    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String showQuestionList(String dataset_id,String searchQuestion, Model model) {
        /////// get question list

        if(searchQuestion == null){
            searchQuestion = "";
        }

        try {

            searchQuestion = URLDecoder.decode(searchQuestion, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getQuestionList(dataset_id, searchQuestion,model);

        return "subpart/question-list :: question-list";
    }

    @RequestMapping(value = "/reviews", method = RequestMethod.GET)
    public String showReviewList(String dataset_id, Model model) {

        // get reviews list.
        getReviewList(dataset_id, model);

        return "subpart/review-list :: review-list";
    }

}
