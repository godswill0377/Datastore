package com.dataexo.zblog.controller.rest;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.ResultInfo;
import com.dataexo.zblog.util.ResultInfoFactory;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.*;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This controller is for only get the data_sets count from the database.
 */
@RestController
public class Data_setsRestController {

    private static final Logger logger = Logger.getLogger(Data_setsRestController.class);

    @Resource
    private Data_setsService data_setsService;

    @Resource
    private Data_sets_seriesService data_sets_seriesService;  //分页的service

    @Resource
    private UserService userService;

    @Resource
    private FavouriteService favouriteService;

    @Resource
    private Data_set_filtersService filtersService;

    @Resource
    private Data_categoryService data_categoryService;

    @Resource
    private PurchaseService purchaseService;

    @Resource
    private QuestionService questionService;

    @Resource
    private ReviewService reviewService;

    @Resource
    private InboxNotifyService inboxNotifyService;


    /**
     * This is rest api for datasets series pagination.
     * It returns page information such as page size.
     * Using this , it can caculate total page num or current page step
     * @param pager
     * @param session
     * @return
     */
    @RequestMapping("/pager/data_sets_series/load")
    public Pager loadData_sets_seriesPager(Pager<Data_sets_series> pager, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        boolean subscription = false;
        if(userinfo != null){
            if(userinfo.getMembership() != 0){
                long cur= System.currentTimeMillis();
                long exp = Long.parseLong(userinfo.getExpire_date());
                if(cur <= exp){
                    //subscription = true;
                    pager.setIs_sample_available(2);
                }
            }
        }

        data_sets_seriesService.initPage(pager);

        return pager;
    }

    /**
     * This is rest api for datasets series pagination.
     * It returns page information such as page size.
     * Using this , it  caculate total page num or current page step
     * @param pager
     * @return
     */
    //todo
    @RequestMapping("/pager/data_sets/load")
    public Pager loadData_setsPager(Pager<Data_sets> pager){

        String price_ids = "" , assets_id = "" , datatype_id="",region_id="",pub_id="";
        boolean flag= false;
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
            flag=true;
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
        data_setsService.initPage(pager);

        return pager;
    }

    /**
     * This is dataset api for get all  datasets columns information
     * @param id
     * @return
     */
    @RequestMapping("/data_sets/get_columns")
    public @ResponseBody
        ResponseEntity<Object> get_columns(String id) {

        Data_sets series = data_setsService.getData_setsById(Integer.parseInt(id));

        try {
                List<String> data = new ArrayList<String>();

                String url = series.getSource_url();
                JSONObject json = null, result = null;

                json = UtilClass.readJsonFromUrl(url);

                result = json.getJSONObject("datatable");
                JSONArray filed_ar = result.getJSONArray("columns");

                for (int j = 0; j < filed_ar.length(); j++) {
                    JSONObject obj = filed_ar.getJSONObject(j);
                    String name = obj.getString("name");
                   // datatype[j] = obj.getString("type");
                    JSONObject column = new JSONObject();
                    column.put("id",name);
                    column.put("name",name);
                    column.put("field",name);
                    data.add(name);

                }
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        } catch (JSONException e) {
            e.printStackTrace();

            logger.error( "Data_setsRestConroller/get_columns: " , e);

        } finally {

        }

        return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * This is rest api for get all dataset by id.
     *
     * @param id
     * @param skip
     * @param pagesize
     * @return
     */
    @RequestMapping("/data_sets/get_alldata/{id}")
    public @ResponseBody
    ResponseEntity<Object>
    get_alldata(@PathVariable String id , String skip , String pagesize) {

        Data_sets series = data_setsService.getData_setsById(Integer.parseInt(id));

        try {
            JSONObject data = new JSONObject();

            String url = series.getSource_url();
            JSONObject json = null, result = null;

            json = UtilClass.readJsonFromUrl(url + "?offset="+skip +"&limit="+pagesize );

            result = json.getJSONObject("datatable");
            JSONArray filed_ar = result.getJSONArray("columns");

            String[] fieldname = new String[filed_ar.length()];
            String[] datatype = new String[filed_ar.length()];

            for (int j = 0; j < filed_ar.length(); j++) {
                JSONObject obj = filed_ar.getJSONObject(j);
                fieldname[j] = obj.getString("name");
                datatype[j] = obj.getString("type");
            }

            JSONArray dataset_ar = result.getJSONArray("data");

            JSONArray datalist = new JSONArray();
            for (int i = 0; i < dataset_ar.length() ; i++) {
                JSONArray ar = dataset_ar.getJSONArray(i);
                String[] record = new String[filed_ar.length()];
                for (int j = 0; j < filed_ar.length(); j++) {
                    record[j] =UtilClass.getDataFromJson( datatype[j] , ar , j);
                }

                JSONObject obj = new JSONObject();
                for (int j = 0; j < filed_ar.length(); j++) {
                    obj.put(fieldname[j],record[j]);
                }
                datalist.put(obj);
//                data.add(record);
            }

            data.put("value",datalist);
            data.put("odata.count",UtilClass.getTotalCount(url + "?offset="+skip +"&limit="+pagesize ));

            return new ResponseEntity<Object>(data.toString(), HttpStatus.OK);

        } catch (JSONException e) {
            e.printStackTrace();

            logger.error( "Data_setsRestConroller/get_alldata: " , e);

        } catch (IOException e) {
            e.printStackTrace();

            logger.error( "Data_setsRestConroller/get_alldata: " , e);

        } finally {

        }
        return new ResponseEntity<Object>(null, HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * This is rest api for integration with zeppelin project.
     * It gets all user's favourite dataset using apikey.
     * apikey is generated from dataexo system.
     * @param apikey
     * @return
     */
    @RequestMapping("/data_sets/myfavourite/{apikey}")
    @ResponseBody
    public  ResponseEntity<Object> getMyFavourite(@PathVariable String apikey ) {
        JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS");

        User user = userService.loadUserByApiKey(apikey);
        if(user == null){
            data.put("array", ar);
            return new ResponseEntity<Object>(data.toString(), headers, HttpStatus.OK);
        }
        MyFavourite favour = new MyFavourite();
        favour.setUserid(user.getId());
        favour.setSeries_id(-1);

        List<MyFavourite> favourList= favouriteService.getFavouriteList(favour);

        try {

            for(int i = 0 ; i < favourList.size() ; i ++){
                JSONObject item = new JSONObject();
                 int id =  Integer.parseInt("" + favourList.get(i).getSeries_id());
                 Data_sets_series series = data_sets_seriesService.getData_sets_seriesById(id);
                 Data_sets data_sets = data_setsService.getData_setsById(series.getData_set_id());
                 item.put("name" , data_sets.getSchema_name() + "." + data_sets.getTable_name());
                 item.put("id" , data_sets.getId());

                 String query = UtilClass.getFilter_query(data_sets, series, filtersService);
                 item.put("query" , query);

                 if(data_sets.getHas_series() == 0){
                     item.put("description" , data_sets.getDescription());
                 }
                 else{
                     item.put("description" , series.getDescription());
                 }

                 ar.put(item);
            }

            data.put("array", ar);

            return new ResponseEntity<Object>(data.toString(), headers, HttpStatus.OK);

        } catch (JSONException e) {
            e.printStackTrace();

            logger.error( "Data_setsRestConroller/getMyFavourite: " , e);

        } finally {

        }

        data.put("array", ar);
        return new ResponseEntity<Object>(data, headers, HttpStatus.OK);
    }


    /*
    This is rest api for integration with zeppelin.
    It returns all public datasets such as free.
    in Zeppelin system , you can see datasets in left side menu
     */
    @RequestMapping("/data_sets/getall")
    @ResponseBody
    public  ResponseEntity<Object> getAllDataSets( ) {
        JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS");

        List<Data_category> catList =  data_categoryService.findAll();
        try {

            for(int i = 0 ; i < catList.size() ; i ++){
                JSONObject item = new JSONObject();
                item.put("catename" , catList.get(i).getName());

                JSONArray itemar = new JSONArray();

                int id =  Integer.parseInt("" + catList.get(i).getId());
                List<Data_sets> data_setsList = data_setsService.loadData_setsByCateid(id);
                item.put("cateid" , id);

                for(int j = 0 ; j < data_setsList.size() ; j ++){
                    Data_sets data_sets = data_setsList.get(j);
                    JSONObject cateitem = new JSONObject();

                    cateitem.put("id" , data_sets.getId());
                    cateitem.put("name" , data_sets.getSchema_name() + "." + data_sets.getTable_name());
                    if(data_sets.getHas_series() == 0){

                        String query = UtilClass.getFilter_query(data_sets, null, filtersService);
                        cateitem.put("query" , query);
                        cateitem.put("description" , data_sets.getDescription());
                    }
                    else{

                        Pager pager = new Pager();
                        pager.setSearch_str("");
                        pager.setParent_code(data_sets.getCode());
                        pager.setIs_free("1");
                        pager.setStart(0);
                        pager.setLimit(1);
                        List<Data_sets_series> seriesList =  data_sets_seriesService.loadData_sets_series(pager , null);
                        if(seriesList.size() > 0){
                            Data_sets_series series = seriesList.get(0);
                            String query = UtilClass.getFilter_query(data_sets, series, filtersService);
                            cateitem.put("query" , query);
                            cateitem.put("description" , series.getDescription());
                        }
                        else{
                            String query = UtilClass.getFilter_query(data_sets, null, filtersService);
                            cateitem.put("query" , query);
                            cateitem.put("description" , data_sets.getDescription());
                        }
                    }

                    itemar.put(cateitem);
                }

                item.put("DatasetsArray" , itemar);

                ar.put(item);
            }

            data.put("array", ar);

            return new ResponseEntity<Object>(data.toString(), headers, HttpStatus.OK);

        } catch (JSONException e) {
            e.printStackTrace();

            logger.error( "Data_setsRestConroller/getAllDataSets: " , e);

        } finally {

        }

        data.put("array", ar);
        return new ResponseEntity<Object>(data, headers, HttpStatus.OK);
    }


    /**
     *
     * @param apikey
     * @return
     */
    @RequestMapping("/data_sets/subscription/{apikey}")
    @ResponseBody
    public  ResponseEntity<Object> getMySubscription(@PathVariable String apikey ) {
        JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS");

        User user = userService.loadUserByApiKey(apikey);
        if(user == null){
            data.put("array", ar);
            return new ResponseEntity<Object>(data.toString(), headers, HttpStatus.OK);
        }

        Pager pager = new Pager();
        pager.setSearch_str("");
        pager.setCateid(""+user.getId());
        pager.setStart(0);

        purchaseService.initPage(pager);
        pager.setLimit(pager.getTotalCount());

        List<Purchase> purchaseList = purchaseService.loadPurchase(pager);
        try {

            for(int i = 0 ; i < purchaseList.size() ; i ++){
                JSONObject item = new JSONObject();
                int id =  Integer.parseInt("" + purchaseList.get(i).getDataset_id());

                Data_sets data_sets = data_setsService.getData_setsById(id);

                item.put("id",data_sets.getId());
                item.put("name" , data_sets.getSchema_name() + "." + data_sets.getTable_name());
                if(data_sets.getHas_series() == 0){

                    String query = UtilClass.getFilter_query(data_sets, null, filtersService);
                    item.put("query" , query);
                    item.put("description" , data_sets.getDescription());
                }
                else{

                    pager = new Pager();
                    pager.setSearch_str("");
                    pager.setParent_code(data_sets.getCode());
                    pager.setIs_free("1");
                    pager.setStart(0);
                    pager.setLimit(1);
                    List<Data_sets_series> seriesList =  data_sets_seriesService.loadData_sets_series(pager , null);
                    if(seriesList.size() > 0){
                        Data_sets_series series = seriesList.get(0);
                        String query = UtilClass.getFilter_query(data_sets, series, filtersService);
                        item.put("query" , query);
                        item.put("description" , series.getDescription());
                    }
                    else{
                        String query = UtilClass.getFilter_query(data_sets, null, filtersService);
                        item.put("query" , query);
                        item.put("description" , data_sets.getDescription());
                    }
                }

                ar.put(item);
            }

            data.put("array", ar);

            return new ResponseEntity<Object>(data.toString(), headers, HttpStatus.OK);

        } catch (JSONException e) {
            e.printStackTrace();

            logger.error( "Data_setsRestConroller/getMySubscription: " , e);

        } finally {

        }

        data.put("array", ar);
        return new ResponseEntity<Object>(data, headers, HttpStatus.OK);
    }









    /**
     * Make a vote
     * @param questionItemId
     * @param votes
     * @param session
     * @return
     */
    @RequestMapping(value = "/data_sets/question/votes/change" ,  method = RequestMethod.POST)
    public ResultInfo makeVote(Integer questionItemId, int votes, HttpSession session)
    {
        ResultInfo resultInfo = null;
        questionService.makeVote(questionItemId,votes);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    /**
     *
     * @param question
     * @param session
     * @return
     */
    @RequestMapping(value = "/data_sets/question/create",  method = RequestMethod.POST)
    public ResultInfo postQuestion(Question_anwsers question,HttpSession session)
    {
        ResultInfo resultInfo = null;
        questionService.postQuestion(question, session);

        User userinfo = (User) session.getAttribute("user");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Inbox_Notify inbox_notify = new Inbox_Notify();
        inbox_notify.setTitle("Question By user " + userinfo.getUsername());
        inbox_notify.setContent(question.getContent());
        inbox_notify.setTo_user_ids(userService.loadUserByVendorId(data_setsService.getData_setsById(question.getDataset_id()).getVendor_id()).getId()+",");
        inbox_notify.setUpdated_at(dateFormat.format(date));
        inboxNotifyService.saveInbox_Notify(inbox_notify);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    @RequestMapping(value="/data_sets/question/checkUser", method = RequestMethod.GET)
    public  ResultInfo checkUser4CreateQuestion(Question_anwsers question,HttpSession session)
    {

        ResultInfo resultInfo = null;

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    /**
     * make helpful
     * @param reviewItemId
     * @param helpfulNum
     * @param session
     * @return
     */
    @RequestMapping(value="/data_sets/review/helpful/change", method = RequestMethod.POST)
    public ResultInfo makeHelpful(Integer reviewItemId,int helpfulNum,HttpSession session)
    {
        ResultInfo resultInfo = null;
        reviewService.makeHelpful(reviewItemId,helpfulNum);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    /**
     *
     * @param review
     * @param session
     * @return
     */
    @RequestMapping(value="/data_sets/review/create", method = RequestMethod.POST)
    public ResultInfo postReview(Customer_reviews review,HttpSession session)
    {
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");

        if(userinfo != null){
            review.setCustomer_id(Integer.parseInt("" + userinfo.getId()));
        }
        else{
            resultInfo = ResultInfoFactory.getErrorResultInfo("not login");
            return resultInfo;

        }
        reviewService.postReview(review, session);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Inbox_Notify inbox_notify = new Inbox_Notify();
        inbox_notify.setTitle("Review By user " + userinfo.getUsername());
        inbox_notify.setContent(review.getContent());
        inbox_notify.setTo_user_ids(userService.loadUserByVendorId(data_setsService.getData_setsById(review.getDataset_id()).getVendor_id()).getId() +",");
        inbox_notify.setUpdated_at(dateFormat.format(date));
        inboxNotifyService.saveInbox_Notify(inbox_notify);

         resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }
}
