package com.dataexo.zblog.controller.admin;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.*;
import com.dataexo.zblog.vo.*;
/*import com.paypal.api.payments.Plan;
import com.paypal.base.rest.PayPalRESTException;*/
import com.stripe.model.Payout;
import com.stripe.model.Transfer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is controller class implements to update , delete and add function of each table.
 * For example, there are updateAsset_class , addAsset_class, deleteAsset_class for management of asset_class table.
 * This is rest api class for control the database.
 * Update , Delete and Add functions will be called using ajax with POST method.
 * This class contains also file upload function. The max uploading file size should be determine config file.
 * You can find application-mail.xml file in the project.
 * There is max file size. The default file size is 900K. You can't upload over 900K.
 * <p>
 * Create Time: 6/22
 * Created By: lang
 */


@RestController
public class AdminRestController {

    private static final Logger logger = Logger.getLogger(AdminRestController.class);


    @Value("${address.admin_email}")
    public String admin_email;

    @Value("${address.email}")
    public String contact_email;
    @Value("${aws.access_key_id}")
    public String access_key_id;
    @Value("${aws.secret_access_key}")
    public String secret_access_key;

    @Value("${address.domain}")
    public String domain;

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from user table.
     * It contains findAll,loadUserByUsername,insertUserInfo,resetPassword,loadUserById ...
     */
    @Autowired
    private UserService userService;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private InboxQuestionService inboxQuestionService;
    @Autowired
    private InboxNotifyService inboxNotifyService;
    @Autowired
    private FeeService feeService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_class table.
     * It contains findAll,saveAsset_class,checkExist,loadAsset_class,getAsset_classById ...
     */
    @Autowired
    private Asset_classService asset_classService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_category table.
     * It contains findAll,saveData_category,checkExist,loadData_category,getData_categoryById ...
     */
    @Autowired
    private Data_categoryService data_categoryService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_type table.
     */
    @Autowired
    private Data_typeService data_typeService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_frequency table.
     */
    @Autowired
    private Data_FrequencyService data_frequencyService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from price_model table.
     */
    @Autowired
    private Price_modelService price_modelService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from publisher table.
     */
    @Autowired
    private PublisherService publisherService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from region table.
     */
    @Autowired
    private RegionService regionService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_sets table.
     */
    @Autowired
    private Data_setsService data_setsService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_sets_series table.
     */
    @Autowired
    private Data_sets_seriesService data_sets_seriesService;
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_set_filters table.
     */
    @Autowired
    private Data_set_filtersService data_set_filtersService;
    @Autowired
    private PlanService planService;
    @Autowired
    private Contact_usService contact_usService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ThirdPartyService thirdPartyService;
    /**
     * This bean used for stripe operations.
     */
    @Autowired
    private StripeService stripeService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private Trans_logService trans_logService;

    /**
     * This is function implement to delete user by id.
     * This will be used in user management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete user in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/vendor/delete", method = RequestMethod.POST)
    public ResultInfo deleteVendorById(String id) {
        ResultInfo resultInfo = null;

        User user = userService.loadUserById(Long.parseLong(id));
        userService.deleteUser(id);

        vendorService.deleteVendor(user.getVendor_id() + "");
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    /**
     * This is function implement to add user in admin panel.
     * This will be used in user management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with user of string data.
     *
     * @param user This param will be used to add user in sql.
     *             It comes from ajax data with POST method.
     * @return When the username or email address is already exist., the resultCode should be fail.
     * because there should be only one username or email address in database.
     * When the username and email address are unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/vendor/add", method = RequestMethod.POST)
    public ResultInfo vendorAdd(User user) {

        ResultInfo resultInfo = null;

        User userInfo = userService.loadUserByUsername(user.getUsername());
        if (userInfo == null) {
            userInfo = userService.loadUserByEmail(user.getEmail());
            if (userInfo == null) {
                String password = Md5Util.pwdDigest(user.getPassword());

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                user.setCreateTime(dateFormat.format(date));
                user.setToken(UUID.randomUUID().toString().toLowerCase());
                user.setPassword(password);

                user.setApiKey(UtilClass.generateAPIKey());

                user.setExpire_date("");

                user.setApiKey(UtilClass.generateAPIKey());

                // insert vendor information

                Vendors vendor = new Vendors();
                vendor.setLegal_name(user.getLegal_name());
                vendor.setAddress(user.getAddress());
                vendor.setCity(user.getCity());
                vendor.setState_province(user.getState_province());
                vendor.setCountry(user.getCountry());
                vendor.setZip_postal(user.getZip_postal());
                vendor.setBusiness_name(user.getBusiness_name());
                vendor.setWebsite_url(user.getWebsite_url());
                vendor.setMobile_num(user.getMobile_num());

                long vendor_id = vendorService.insertVendorInfo(vendor);
                user.setVendor_id(vendor.getId());

                userService.insertUserInfo(user);

                thirdPartyService.addNewsToSystem(user); // add to jforum project

                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            } else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
            }
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
        }

        return resultInfo;
    }

    @RequestMapping(value = "/admin/vendor/update", method = RequestMethod.POST)
    public ResultInfo updateVendor(User user) {

        ResultInfo resultInfo = null;

        User userInfo = userService.loadUserByUsername(user.getUsername());

        if (userInfo == null || userInfo.getUsername().equals(user.getUsername())) {
            userInfo = userService.loadUserByEmail(user.getEmail());
            if (userInfo == null || userInfo.getEmail().equals(user.getEmail())) {

                if (!user.getPassword().equals("")) {
                    String password = Md5Util.pwdDigest(user.getPassword());
                    user.setPassword(password);

                    userService.updatePassword(user);
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                user.setToken(UUID.randomUUID().toString().toLowerCase());

                user.setExpire_date("");

                userService.updateInfo(user);

                Vendors vendor = new Vendors();
                vendor.setId(user.getVendor_id());
                vendor.setLegal_name(user.getLegal_name());
                vendor.setAddress(user.getAddress());
                vendor.setCity(user.getCity());
                vendor.setState_province(user.getState_province());
                vendor.setCountry(user.getCountry());
                vendor.setZip_postal(user.getZip_postal());
                vendor.setBusiness_name(user.getBusiness_name());
                vendor.setWebsite_url(user.getWebsite_url());
                vendor.setMobile_num(user.getMobile_num());

                vendorService.updateVendorInfoById(vendor);

                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            } else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
            }
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
        }

        return resultInfo;
    }

    @RequestMapping(value = "/admin/vendor/activate", method = RequestMethod.POST)
    public ResultInfo activateVendorById(String id, int flag) {
        ResultInfo resultInfo = null;
        boolean act_flag = true;
        if(flag == 1){
            userService.activeAccount(Integer.parseInt(id));
        }
        else{
            userService.deactiveAccount(Integer.parseInt(id));
            act_flag = false;
        }

        User user = userService.loadUserById(Long.parseLong(id));

        String html = UtilClass.activateHtmlTempl(domain, act_flag);
        boolean result = thirdPartyService.sendAwsSes(admin_email, user.getEmail(), "Activation", html);

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to delete user by id.
     * This will be used in user management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete user in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/user/delete", method = RequestMethod.POST)
    public ResultInfo deleteUserById(String id) {
        ResultInfo resultInfo = null;
        userService.deleteUser(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    /**
     * This is function implement to add user in admin panel.
     * This will be used in user management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with user of string data.
     *
     * @param user This param will be used to add user in sql.
     *             It comes from ajax data with POST method.
     * @return When the username or email address is already exist., the resultCode should be fail.
     * because there should be only one username or email address in database.
     * When the username and email address are unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/user/add", method = RequestMethod.POST)
    public ResultInfo userAdd(User user) {

        ResultInfo resultInfo = null;

        User userInfo = userService.loadUserByUsername(user.getUsername());
        if (userInfo == null) {
            userInfo = userService.loadUserByEmail(user.getEmail());
            if (userInfo == null) {
                String password = Md5Util.pwdDigest(user.getPassword());

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                user.setCreateTime(dateFormat.format(date));
                user.setToken(UUID.randomUUID().toString().toLowerCase());
                user.setPassword(password);
                user.setApiKey(UtilClass.generateAPIKey());
                String expire = user.getExpire_date();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date expire_date = format.parse(expire);
                    long exp = expire_date.getTime();
                    user.setExpire_date(exp + "");
                } catch (ParseException e) {
                    e.printStackTrace();

                    logger.error(e);
                }

                user.setApiKey(UtilClass.generateAPIKey());
                userService.insertUserInfo(user);

                thirdPartyService.addNewsToSystem(user); // add to jforum project

                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            } else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
            }
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
        }

        return resultInfo;
    }

    @RequestMapping(value = "/admin/user/update", method = RequestMethod.POST)
    public ResultInfo updateUser(User user) {

        ResultInfo resultInfo = null;

        User userInfo = userService.loadUserByUsername(user.getUsername());

        if (userInfo == null || userInfo.getUsername().equals(user.getUsername())) {
            userInfo = userService.loadUserByEmail(user.getEmail());
            if (userInfo == null || userInfo.getEmail().equals(user.getEmail())) {

                if (!user.getPassword().equals("")) {
                    String password = Md5Util.pwdDigest(user.getPassword());
                    user.setPassword(password);

                    userService.updatePassword(user);
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                user.setToken(UUID.randomUUID().toString().toLowerCase());

                String expire = user.getExpire_date();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date expire_date = format.parse(expire);
                    long exp = expire_date.getTime();
                    user.setExpire_date(exp + "");
                } catch (ParseException e) {
                    e.printStackTrace();

                    logger.error(e);
                }
                user.setVendor_id(-1);
                userService.updateInfo(user);

                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            } else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
            }
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to update asset_class in admin panel.
     * This will be used in asset_class management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with asset_class of string data.
     *
     * @param asset_class This param will be used to update asset_class in sql.
     *                    It comes from ajax data with POST method.
     * @return When asset_class name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/asset_class/update", method = RequestMethod.POST)
    public ResultInfo updateAsset_class(Asset_class asset_class) {

        ResultInfo resultInfo = null;

        Asset_class asset = asset_classService.getAsset_classById(asset_class.getId());
        if (!asset.getName().equals(asset_class.getName())) {
            asset_classService.updateAsset_class(asset_class);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to delete asset_class by id.
     * This will be used in asset_class management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete asset_class in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/asset_class/delete", method = RequestMethod.POST)
    public ResultInfo deleteAsset_class(String id, HttpSession session) {
        ResultInfo resultInfo = null;
        asset_classService.deleteAsset_class(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add asset_class in admin panel.
     * This will be used in asset_class management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with asset_class of string data.
     *
     * @param asset_class This param will be used to add asset_class in sql.
     *                    It comes from ajax data with POST method.
     * @return When asset_class name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/asset_class/add", method = RequestMethod.POST)
    public ResultInfo addAsset_class(Asset_class asset_class, HttpSession session) {

        ResultInfo resultInfo = null;

        Asset_class asset = asset_classService.checkExist(asset_class.getName());
        if (asset == null) {
            asset_classService.saveAsset_class(asset_class);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to update data_category in admin panel.
     * This will be used in data_category management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_category of string data.
     *
     * @param data_category This param will be used to update data_category in sql.
     *                      It comes from ajax data with POST method.
     * @return When data_category name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_category/update", method = RequestMethod.POST)
    public ResultInfo updateData_category(Data_category data_category) {

        ResultInfo resultInfo = null;

        Data_category cat = data_categoryService.checkExist(data_category.getName());

        if (cat == null) {
            data_categoryService.updateData_category(data_category);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


    /**
     * This is function implement to delete data_category by id.
     * This will be used in data_category management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_category in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_category/delete", method = RequestMethod.POST)
    public ResultInfo deleteData_category(String id) {
        ResultInfo resultInfo = null;
        data_categoryService.deleteData_category(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_category in admin panel.
     * This will be used in data_category management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_category of string data.
     *
     * @param data_category This param will be used to add data_category in sql.
     *                      It comes from ajax data with POST method.
     * @return When data_category name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_category/add", method = RequestMethod.POST)
    public ResultInfo addData_category(Data_category data_category, HttpSession session) {

        ResultInfo resultInfo = null;

        Data_category cat = data_categoryService.checkExist(data_category.getName());
        if (cat == null) {
            data_categoryService.saveData_category(data_category);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


    // data_type table

    /**
     * This is function implement to update data_type in admin panel.
     * This will be used in data_type management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_type of string data.
     *
     * @param data_type This param will be used to update data_type in sql.
     *                  It comes from ajax data with POST method.
     * @return When data_type name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_type/update", method = RequestMethod.POST)
    public ResultInfo updateData_type(Data_type data_type) {

        ResultInfo resultInfo = null;

        Data_type data = data_typeService.checkExist(data_type.getName());

        if (data == null) {
            data_typeService.updateData_type(data_type);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


    /**
     * This is function implement to delete data_type by id.
     * This will be used in data_type management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_type in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_type/delete", method = RequestMethod.POST)
    public ResultInfo deleteData_type(String id) {
        ResultInfo resultInfo = null;
        data_typeService.deleteData_type(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_type in admin panel.
     * This will be used in data_type management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_type of string data.
     *
     * @param data_type This param will be used to add data_type in sql.
     *                  It comes from ajax data with POST method.
     * @return When data_type name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_type/add", method = RequestMethod.POST)
    public ResultInfo addData_type(Data_type data_type) {

        ResultInfo resultInfo = null;

        Data_type data = data_typeService.checkExist(data_type.getName());
        if (data == null) {
            data_typeService.saveData_type(data_type);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

// data_update_frequency table

    /**
     * This is function implement to update data_frequency in admin panel.
     * This will be used in data_frequency management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_frequency of string data.
     *
     * @param data_frequency This param will be used to update data_frequency in sql.
     *                       It comes from ajax data with POST method.
     * @return When data_frequency name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_frequency/update", method = RequestMethod.POST)
    public ResultInfo updateData_frequency(Data_frequency data_frequency) {

        ResultInfo resultInfo = null;

        Data_frequency data = data_frequencyService.checkExist(data_frequency.getName());

        if (data == null) {
            data_frequencyService.updateData_frequency(data_frequency);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


    /**
     * This is function implement to delete data_frequency by id.
     * This will be used in data_frequency management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_frequency in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_frequency/delete", method = RequestMethod.POST)
    public ResultInfo deleteData_frequency(String id) {
        ResultInfo resultInfo = null;
        data_frequencyService.deleteData_frequency(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_frequency in admin panel.
     * This will be used in data_frequency management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_frequency of string data.
     *
     * @param data_frequency This param will be used to add data_frequency in sql.
     *                       It comes from ajax data with POST method.
     * @return When data_frequency name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_frequency/add", method = RequestMethod.POST)
    public ResultInfo addData_frequency(Data_frequency data_frequency, HttpSession session) {

        ResultInfo resultInfo = null;

        Data_frequency data = data_frequencyService.checkExist(data_frequency.getName());
        if (data == null) {
            data_frequencyService.saveData_frequency(data_frequency);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


// price_model table

    /**
     * This is function implement to update price_type in admin panel.
     * This will be used in price_type management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with price_type of string data.
     *
     * @param price_model This param will be used to update price_type in sql.
     *                    It comes from ajax data with POST method.
     * @return When price_type name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/price_type/update", method = RequestMethod.POST)
    public ResultInfo updatePrice_Model(Price_model price_model, HttpSession session) {

        ResultInfo resultInfo = null;

        Price_model data = price_modelService.checkExist(price_model.getName());

        if (data == null) {
            price_modelService.updatePrice_model(price_model);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to delete price_type by id.
     * This will be used in price_type management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete price_type in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/price_type/delete", method = RequestMethod.POST)
    public ResultInfo deletePrice_model(String id) {
        ResultInfo resultInfo = null;
        price_modelService.deletePrice_model(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add price_type in admin panel.
     * This will be used in price_type management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with price_type of string data.
     *
     * @param price_model This param will be used to add price_type in sql.
     *                    It comes from ajax data with POST method.
     * @return When price_type name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/price_type/add", method = RequestMethod.POST)
    public ResultInfo addPrice_model(Price_model price_model) {

        ResultInfo resultInfo = null;

        Price_model data = price_modelService.checkExist(price_model.getName());
        if (data == null) {
            price_modelService.savePrice_model(price_model);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

// publisher table

    /**
     * This is function implement to update data_publisher in admin panel.
     * This will be used in data_publisher management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_publisher of string data.
     *
     * @param publisher This param will be used to update data_publisher in sql.
     *                  It comes from ajax data with POST method.
     * @return When data_publisher name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_publisher/update", method = RequestMethod.POST)
    public ResultInfo updateData_publisher(Publisher publisher) {

        ResultInfo resultInfo = null;

        Publisher data = publisherService.checkExist(publisher.getName());

        if (data == null) {
            publisherService.updatePublisher(publisher);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to delete data_publisher by id.
     * This will be used in data_publisher management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_publisher in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_publisher/delete", method = RequestMethod.POST)
    public ResultInfo deletePublisher(String id) {
        ResultInfo resultInfo = null;
        publisherService.deletePublisher(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_publisher in admin panel.
     * This will be used in data_publisher management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_publisher of string data.
     *
     * @param data_publisher This param will be used to add data_publisher in sql.
     *                       It comes from ajax data with POST method.
     * @return When data_publisher name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_publisher/add", method = RequestMethod.POST)
    public ResultInfo addPublisher(Publisher data_publisher) {

        ResultInfo resultInfo = null;

        Publisher data = publisherService.checkExist(data_publisher.getName());
        if (data == null) {
            publisherService.savePublisher(data_publisher);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


// plan table

    /**
     * This is function implement to update data_plan in admin panel.
     * This will also update the amount in stripe dashboard.
     * This will be used in data_plan management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_plan of string data.
     *
     * @param plan This param will be used to update data_plan in sql.
     *             It comes from ajax data with POST method.
     * @return When data_plan name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/plan/update", method = RequestMethod.POST)
    public ResultInfo updatePlan(Plan plan) {

        ResultInfo resultInfo = null;

        Plan data = planService.checkExist(plan);

        if (data == null) {
            String frequency = " ";
            if(plan.getFrequency().contains("30")) {
                frequency = "month";
            }else if(plan.getFrequency().contains("365")){
                frequency = "year";
            }
            if(planService.getPlanByPlanId(plan.getPlan_id()).getReal_price() > 0) {
                if (stripeService.retrivePlan(plan.getPlan_id()) != null) {
                    stripeService.deletePlan(plan.getPlan_id());
                }
            }
            stripeService.createPlan(plan.getPlan_id(), plan.getPlan_name(), frequency, plan.getReal_price());
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
            planService.updatePlan(plan);
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to delete plan by id.
     * This will be used in plan management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete plan in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/plan/delete", method = RequestMethod.POST)
    public ResultInfo deletePlan(String id) {
        ResultInfo resultInfo = null;
        planService.deletePlan(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add plan in admin panel.
     * This will be used in plan management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with plan of string data.
     *
     * @param plan This param will be used to add plan in sql.
     *             It comes from ajax data with POST method.
     * @return When plan name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/plan/add", method = RequestMethod.POST)
    public ResultInfo addPlan(Plan plan) {

        ResultInfo resultInfo = null;

        Plan data = planService.checkExist(plan);
        if (data == null) {
            plan.setVendor_id(-1);
            planService.savePlan(plan);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }


// region table

    /**
     * This is function implement to update data_region in admin panel.
     * This will be used in data_region management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_region of string data.
     *
     * @param region This param will be used to update data_region in sql.
     *               It comes from ajax data with POST method.
     * @return When data_region name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_region/update", method = RequestMethod.POST)
    public ResultInfo updateRegion(Region region) {

        ResultInfo resultInfo = null;

        Region data = regionService.checkExist(region.getName());

        if (data == null) {
            regionService.updateRegion(region);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to delete data_region by id.
     * This will be used in data_region management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_region in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_region/delete", method = RequestMethod.POST)
    public ResultInfo deleteRegion(String id) {
        ResultInfo resultInfo = null;
        regionService.deleteRegion(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_region in admin panel.
     * This will be used in data_region management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_region of string data.
     *
     * @param region This param will be used to add data_region in sql.
     *               It comes from ajax data with POST method.
     * @return When data_region name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_region/add", method = RequestMethod.POST)
    public ResultInfo addRegion(Region region) {

        ResultInfo resultInfo = null;

        Region data = regionService.checkExist(region.getName());
        if (data == null) {
            regionService.saveRegion(region);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }

        return resultInfo;
    }

// data-sets table

    /**
     * This is function implement to update data_set in admin panel.
     * This will be used in data_set management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_set of string data.
     *
     * @param data_sets This param will be used to update data_set in sql.
     *                  It comes from ajax data with POST method.
     * @return When data_set name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_set/update", method = RequestMethod.POST)
    public ResultInfo updateDataSets(Data_sets data_sets) {

        ResultInfo resultInfo = null;
        List<Data_sets> data = data_setsService.checkExist(data_sets);

        if (data == null || data.size() == 0) {
            if (data_sets.getDownload_url_update_at().equals("1")) {
                data_sets.setDownload_url_update_at((System.currentTimeMillis() + 6 * 24 * 3600 * 1000) + "");
            }
            data_setsService.updateData_sets(data_sets);

            if (data_sets.getApi().equals("TIME-SERIES") && data_sets.getHas_series() == 0) {

                data_sets_seriesService.deleteData_sets_seriesByParent(data_sets.getId());
                Data_sets_series series = new Data_sets_series();
                series.setData_set_id(data_sets.getId());
                series.setName(data_sets.getName());
                series.setDescription(data_sets.getDescription());
                series.setCode(data_sets.getCode() + "/" + data_sets.getCode());
                series.setData_update_frequency_id(1);
                series.setIs_sample_available(1);
                series.setSource_url(data_sets.getSource_url());
                series.setEmbed_url_chart(data_sets.getEmbed_url_chart());
                series.setEmbed_url_datagrid(data_sets.getEmbed_url_datagrid());
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                series.setLatest_update_date(format.format(date));

                if (data_sets_seriesService.checkExist(series).size() == 0) {
                    data_sets_seriesService.saveData_sets_series(series);
                }

            }

            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name, code or table_name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to delete data_set by id.
     * This will be used in data_set management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_set in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_set/delete", method = RequestMethod.POST)
    public ResultInfo deleteData_sets(String id) {
        ResultInfo resultInfo = null;
        Data_sets data = data_setsService.getData_setsById(Integer.parseInt(id));

        data_sets_seriesService.deleteDataByCode(data.getCode() + "/");

        data_setsService.deleteData_sets(Integer.parseInt(id));

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_set in admin panel.
     * This will be used in data_set management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_set of string data.
     *
     * @param data_sets This param will be used to add data_set in sql.
     *                  It comes from ajax data with POST method.
     * @return When data_set name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_set/add", method = RequestMethod.POST)
    public ResultInfo addData_Sets(Data_sets data_sets) {

        ResultInfo resultInfo = null;

        List<Data_sets> data = data_setsService.checkExist(data_sets);

        if (data == null || data.size() == 0) {
            //if(data_sets.getDownload_url_update_at().equals("1")){
            data_sets.setDownload_url_update_at((System.currentTimeMillis() + 6 * 24 * 3600 * 1000) + "");
            data_sets.setVendor_id(-1);
            //}
            data_setsService.saveData_sets(data_sets);
            if (data_sets.getApi().equals("TIME-SERIES") && data_sets.getHas_series() == 0) {
                Data_sets_series series = new Data_sets_series();
                series.setData_set_id(data_sets.getId());
                series.setName(data_sets.getName());
                series.setDescription(data_sets.getDescription());
                series.setCode(data_sets.getCode() + "/" + data_sets.getCode());
                series.setData_update_frequency_id(1);
                series.setIs_sample_available(1);
                series.setSource_url(data_sets.getSource_url());
                series.setEmbed_url_chart(data_sets.getEmbed_url_chart());
                series.setEmbed_url_datagrid(data_sets.getEmbed_url_datagrid());
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                series.setLatest_update_date(format.format(date));

                if (data_sets_seriesService.checkExist(series).size() == 0) {
                    data_sets_seriesService.saveData_sets_series(series);
                }

            }
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name, code or table_name already exists!");
        }

        return resultInfo;
    }

    /**
     * This is function implement to upload image to server.
     * This function will be used in data_sets management page of amdin panel.
     * Now the max uploading file size is 900K. You can change this value easily.
     * You can change this value in applicatino-mail.xml.
     * You can see the max filesize in xml file. Now it is 900000.
     * You can change this size.
     *
     * @param request This is HttpServletRequest variable which contains form data.
     * @return The resultCode should be success if there is no exception.
     * But you should make sure the max file size. If the uploading file size is over max size , it will occur error.
     */

    @RequestMapping(value = "admin/data_set/upload", consumes = "multipart/form-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultInfo uploadFileHandler(HttpServletRequest request) {

        String basePath = UtilClass.getBasePath();

        basePath += "image/upload/";

        ResultInfo resultInfo = null;
        String path = "";
        try {

            Part part = request.getPart("file");
            InputStream stream = part.getInputStream();
            String content = part.getHeader("Content-Disposition");
            int pos = content.indexOf("filename");
            String fileName = content.substring(pos + 10, content.length() - 1);
            pos = fileName.lastIndexOf(".");
            String type = fileName.substring(pos);

            Date now = new Date();
            int month = now.getMonth();

            File temp = new File(basePath + "/" + month);
            if (!temp.exists()) {
                temp.mkdirs();
            }

            path = month + "/" + System.currentTimeMillis() + type;

            OutputStream out = new FileOutputStream(new File(basePath + "/" + path));
            IOUtils.copy(stream, out);
            stream.close();
            out.close();
            resultInfo = ResultInfoFactory.getSuccessResultInfo("image/upload/" + path);
        } catch (IOException e) {
            e.printStackTrace();

            logger.error(e);
            resultInfo = ResultInfoFactory.getErrorResultInfo();
        } catch (ServletException e) {
            e.printStackTrace();

            logger.error(e);
            resultInfo = ResultInfoFactory.getErrorResultInfo();
        }

        return resultInfo;
    }


    /**
     * Generate S3 download url
     *
     * @param bucket_name
     * @param s3_file_key
     * @return
     */
    @RequestMapping(value = "/admin/data_set/generate_s3Url", method = RequestMethod.POST)
    public ResultInfo Generate_s3Url(String bucket_name, String s3_file_key) {

        ResultInfo resultInfo = null;

        String accessKey = access_key_id;
        String secretKey = secret_access_key;

        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
//			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
//				+ "Please make sure that your credentials file is at the correct "
//				+ "location (/Users/USERNAME/.aws/credentials), and is in valid format.", e);

            logger.error(e);
        }

        if (credentials == null) {
            credentials = new BasicAWSCredentials(accessKey, secretKey);
        }

        final AmazonS3 conn = AmazonS3ClientBuilder.standard()
                //.enableAccelerateMode()
                //.withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        //GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("jianghua_liang_adpromising", "hello-spring-mysql.war", HttpMethod.GET);
        //request.setSSEAlgorithm(SSEAlgorithm.getDefault());
        //GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("jianghua_liang_adpromising", "business-data/anonymous2/issue_view.csv", HttpMethod.GET);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket_name, s3_file_key, HttpMethod.GET);

        int s3_expire_date = 7;
        LocalDate today = LocalDate.now();
        LocalDate expireDate = today.plusDays(s3_expire_date);
        request.setExpiration(expireDate.toDate());
        URL geturl = conn.generatePresignedUrl(request);


        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        resultInfo.setObject(geturl.toString());
        return resultInfo;
    }


// data-series table

    /**
     * This is function implement to update data_sets_series in admin panel.
     * This will be used in data_sets_series management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_sets_series of string data.
     *
     * @param data_sets_series This param will be used to update data_sets_series in sql.
     *                         It comes from ajax data with POST method.
     * @return When data_sets_series name or code is already exist., the resultCode should be fail.
     * because there should be only one name or code in database.
     * When the name and code are unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_series/update", method = RequestMethod.POST)
    public ResultInfo updateDataSets(Data_sets_series data_sets_series) {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data_sets_series.setLatest_update_date(format.format(date));

        ResultInfo resultInfo = null;

        Data_sets data_sets = new Data_sets();
        data_sets.setName("");
        data_sets.setId(0);

        String code = data_sets_series.getCode();
        int pos = code.indexOf("/");
        code = code.substring(0, pos);

        data_sets.setCode(code);
        List<Data_sets> parentdata = data_setsService.checkExist(data_sets);

        if (parentdata == null || parentdata.size() == 0) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The parent code doesn't exist! " + code + " doesn't exist in data_sets table. Please make sure the data_sets code.");
        } else {

            List<Data_sets_series> data = data_sets_seriesService.checkExist(data_sets_series);

            if (data == null || data.size() == 0) {
                data_sets_seriesService.updateData_sets_series(data_sets_series);
                resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
            } else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("The name or code already exists!");
            }
        }
        return resultInfo;
    }

    /**
     * This is function implement to delete data_sets_series by id.
     * This will be used in data_sets_series management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_sets_series in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_series/delete", method = RequestMethod.POST)
    public ResultInfo deleteData_series(String id) {
        ResultInfo resultInfo = null;

        data_sets_seriesService.deleteData_sets_series(Integer.parseInt(id));

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_sets_series in admin panel.
     * This will be used in data_sets_series management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_sets_series of string data.
     *
     * @param data_sets_series This param will be used to add data_sets_series in sql.
     *                         It comes from ajax data with POST method.
     * @return When data_sets_series name or code is already exist., the resultCode should be fail.
     * because there should be only one name and code in database.
     * When the name and code are unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_series/add", method = RequestMethod.POST)
    public ResultInfo addData_Series(Data_sets_series data_sets_series) {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data_sets_series.setLatest_update_date(format.format(date));

        ResultInfo resultInfo = null;

        Data_sets data_sets = new Data_sets();
        data_sets.setName("");
        data_sets.setId(0);

        String code = data_sets_series.getCode();
        int pos = code.indexOf("/");
        if (pos < 0) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The CODE is incorrect. You should input like this. 'EOD/V1'!");
        } else {
            code = code.substring(0, pos);

            data_sets.setCode(code);
            List<Data_sets> parentdata = data_setsService.checkExist(data_sets);

            if (parentdata == null || parentdata.size() == 0) {
                resultInfo = ResultInfoFactory.getErrorResultInfo("The parent code doesn't exist! " + code + " doesn't exist in data_sets table. Please make sure the data_sets code.");
            } else {
                data_sets_series.setId(0);
                List<Data_sets_series> data = data_sets_seriesService.checkExist(data_sets_series);

                if (data == null || data.size() == 0) {
                    data_sets_seriesService.saveData_sets_series(data_sets_series);
                    resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
                } else {
                    resultInfo = ResultInfoFactory.getErrorResultInfo("The name or code already exists!");
                }
            }
        }
        return resultInfo;
    }


// data_set_filter table

    /**
     * This is function implement to update data_set_filter in admin panel.
     * This will be used in data_set_filter management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_set_filter of string data.
     *
     * @param filters This param will be used to update data_set_filter in sql.
     *                It comes from ajax data with POST method.
     * @return When data_set_filter name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/admin/data_set_filter/update", method = RequestMethod.POST)
    public ResultInfo updateData_set_filter(Data_set_filters filters) {

        ResultInfo resultInfo = null;

        data_set_filtersService.updateFilter(filters);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
        return resultInfo;
    }

    /**
     * This is function implement to delete data_set_filters by id.
     * This will be used in data_set_filters management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete data_set_filters in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/data_set_filter/delete", method = RequestMethod.POST)
    public ResultInfo deleteData_set_filters(String id) {
        ResultInfo resultInfo = null;
        data_set_filtersService.deleteFilter(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * This is function implement to add data_set_filters in admin panel.
     * This will be used in data_set_filters management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_set_filters of string data.
     *
     * @param filters This param will be used to add data_set_filters in sql.
     *                It comes from ajax data with POST method.
     * @return When data_set_filters name is already exist., the resultCode should be fail.
     * because there should be only one name in database.
     * When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/admin/data_set_filter/add", method = RequestMethod.POST)
    public ResultInfo addData_set_filters(Data_set_filters filters) {

        ResultInfo resultInfo = null;

        data_set_filtersService.saveFilter(filters);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Add Success!");

        return resultInfo;
    }


// contact_us table

    /**
     * This is function implement to delete contact_us by id.
     * This will be used in contact_us management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete contact_us in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/contact_us/delete", method = RequestMethod.POST)
    public ResultInfo deleteContact_us(String id) {
        ResultInfo resultInfo = null;
        contact_usService.deleteContact_us(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    /**
     * This is function implement to delete question by id.
     * This will be used in question management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete user in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/question/delete", method = RequestMethod.POST)
    public ResultInfo deleteQuestionById(String id) {
        ResultInfo resultInfo = null;
        questionService.deleteQuestion(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/admin/question/update", method = RequestMethod.POST)
    public ResultInfo updateQuestion(Question_anwsers question_anwsers) {

        ResultInfo resultInfo = null;
        question_anwsers.setDeleted_date(null);

        questionService.updateQuestion(question_anwsers);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();

        return resultInfo;
    }


    /**
     * This is function implement to delete question by id.
     * This will be used in review management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with id of string data.
     *
     * @param id This param will be used to delete user in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/admin/reviews/delete", method = RequestMethod.POST)
    public ResultInfo deleteReviewsById(String id) {
        ResultInfo resultInfo = null;
        reviewService.deleteReviews(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/admin/reviews/update", method = RequestMethod.POST)
    public ResultInfo updateReviews(Customer_reviews reviews) {

        ResultInfo resultInfo = null;

        reviews.setDeleted_date(null);
        reviewService.updateReviews(reviews);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }


    ///////////////
    @RequestMapping("/admin/dataset/question/answer/update")
    public ResultInfo updateAnswer(Question_anwsers answer) {
        ResultInfo resultInfo = null;

        questionService.updateAnswer(answer);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    @RequestMapping(value = "/admin/dataset/question/answer/delete/{id}", method = RequestMethod.POST)
    public ResultInfo deleteAnswerById(String id) {
        ResultInfo resultInfo = null;
        questionService.deleteQuestion(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    /**
     * @param answer
     * @param session
     * @return
     */
    @RequestMapping("/admin/dataset/question/answer/create")
    public ResultInfo postQuestion(Question_anwsers answer, HttpSession session) {
        ResultInfo resultInfo = null;
        answer.setAnswer_by("Administrator");
        questionService.createAnswer(answer);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    /////////// Inbox Question Functions Section ///////////////////////


    /////// question for inbox
    @RequestMapping("/admin/inbox/question/create")
    public ResultInfo createInboxQuestion(Question_anwsers question, HttpSession session) {
        ResultInfo resultInfo = null;
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if (vendors != null) {
            question.setQuestion_by_userid((int) vendors.getId());
        }

        inboxQuestionService.createQuestion(question);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/admin/inbox/question/delete", method = RequestMethod.POST)
    public ResultInfo deleteInboxQuestionById(Integer id) {
        ResultInfo resultInfo = null;
        inboxQuestionService.deleteQuestion("" + id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    @RequestMapping("/admin/inbox/question/answer/update")
    public ResultInfo updateInboxAnswer(Question_anwsers answer, HttpSession session) {
        ResultInfo resultInfo = null;


        inboxQuestionService.updateAnswer(answer);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/admin/inbox/question/answer/delete/{id}", method = RequestMethod.GET)
    public ResultInfo deleteInboxAnswerById(@PathVariable Integer id) {
        ResultInfo resultInfo = null;
        inboxQuestionService.deleteAnswer(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping("/admin/inbox/question/answer/create")
    public ResultInfo createInboxAnswer(Question_anwsers question, HttpSession session) {
        ResultInfo resultInfo = null;
        question.setType(2);

        Question_anwsers question_tmp = inboxQuestionService.getRowById(question.getParent_id());

        /////////////// send email to user ///////////////
        User user = userService.loadUserByVendorId(question_tmp.getQuestion_by_userid());

        String html = UtilClass.HtmlToUser(question.getContent());
        boolean result = thirdPartyService.sendAwsSes(admin_email, user.getEmail(), "Answer", html);

        //////////////////////////////
        inboxQuestionService.createAnswer(question);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    //////////////////////////

    @RequestMapping(value = "/admin/inbox/notification/update", method = RequestMethod.POST)
    public ResultInfo updateNotification(Inbox_Notify inbox_notify) {

        ResultInfo resultInfo = null;

        Inbox_Notify inbox_notify11 = inboxNotifyService.getInbox_NotifyById(inbox_notify.getId());

        inbox_notify11.setTitle(inbox_notify.getTitle());
        inbox_notify11.setContent(inbox_notify.getContent());
        inbox_notify11.setTo_user_ids(inbox_notify.getTo_user_ids());

        inboxNotifyService.updateInbox_Notify(inbox_notify11);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/admin/inbox/notification/delete", method = RequestMethod.POST)
    public ResultInfo deleteNotification(String id) {
        ResultInfo resultInfo = null;
        inboxNotifyService.deleteInbox_Notify(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/admin/inbox/notification/add", method = RequestMethod.POST)
    public ResultInfo addNotification(Inbox_Notify inbox_notify) {

        ResultInfo resultInfo = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        inbox_notify.setUpdated_at(dateFormat.format(date));

        if (!inbox_notify.getTo_user_ids().equals("")) {
            inbox_notify.setTo_user_ids(inbox_notify.getTo_user_ids() + ",");

            String[] ids = inbox_notify.getTo_user_ids().split(",");

            ///////// send notification to all users.
            for (int i = 0; i < ids.length; i++) {
                if (!"".equals(ids[i])) {
                    User user = userService.loadUserById(Integer.parseInt(ids[i] + ""));

                    if (user != null) {
                        String html = UtilClass.HtmlToUser(inbox_notify.getContent());
                        thirdPartyService.sendAwsSes(admin_email, user.getEmail(), inbox_notify.getTitle(), html);
                    }
                }
            }
            inboxNotifyService.saveInbox_Notify(inbox_notify);
        }

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }

    @RequestMapping(value = "/admin/fee/update", method = RequestMethod.POST)
    public ResultInfo updateFee(Fee fee) {

        ResultInfo resultInfo = null;
        feeService.updateFee(fee);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }


    @RequestMapping(value = "/admin/withdraw/cancel/{id}",method = RequestMethod.POST)
    public ResultInfo cancelWithdraw(HttpSession session, @PathVariable int id, String description) {

        ResultInfo resultInfo = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();

        Withdraw withdraw = withdrawService.getById(id);

        // status 2 for cancelled by admin or failed
        withdraw.setStatus(4);
        withdraw.setDescription(description +" Cancelled at "+dateFormat.format(date));
        withdrawService.updateWithdraw(withdraw);

        Trans_log trans_log = new Trans_log();
        trans_log.setVendor_id(withdraw.getVendor_id());
        trans_log.setAmount(withdraw.getWithdraw_amount());
        trans_log.setDescription(description);
        trans_log.setDate(dateFormat.format(date));
        trans_log.setTrans_type(1);
        trans_log.setStatus(0);
        trans_logService.saveTrans(trans_log);
        userService.updateBalance(withdraw.getVendor_id(), (withdraw.getWithdraw_amount()));


        Inbox_Notify inbox_notify = new Inbox_Notify();
        inbox_notify.setTitle("Withdraw Disapproved");
        inbox_notify.setContent(description);
        inbox_notify.setTo_user_ids(userService.loadUserByVendorId(withdraw.getVendor_id()).getId() + ",");
        inbox_notify.setUpdated_at(dateFormat.format(date));
        inboxNotifyService.saveInbox_Notify(inbox_notify);
        thirdPartyService.sendAwsSes(contact_email, userService.loadUserByVendorId(withdraw.getVendor_id()).getEmail(), "Withdraw Request Disapproved", description);

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }

    @RequestMapping(value = "/admin/withdraw/approve/{id}",method = RequestMethod.POST)
    public ResultInfo approveWithdraw(HttpSession session, @PathVariable int id) {


        ResultInfo resultInfo = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();

        Withdraw withdraw = withdrawService.getById(id);
        Vendors vendor = vendorService.getVendorIDById(withdraw.getVendor_id());

        Payout payout = stripeService.transferToAccount(vendor.getStripe_acc_id(), withdraw.getWithdraw_amount());
        if(payout.getCurrency().equals("error")){
            resultInfo = ResultInfoFactory.getErrorResultInfo(payout.getObject());
            return resultInfo;
        }

        // status 3 for approved by admin
        withdraw.setStatus(3);
        
        withdraw.setDescription("Withdraw Approved By admin at "+dateFormat.format(date));
        withdrawService.updateWithdraw(withdraw);

        Trans_log trans_log = new Trans_log();
        trans_log.setVendor_id(withdraw.getVendor_id());
        trans_log.setAmount(withdraw.getWithdraw_amount());
        trans_log.setDescription("Withdraw Approved By admin");
        trans_log.setDate(dateFormat.format(date));
        trans_log.setTrans_type(0);
        trans_log.setStatus(1);
        trans_logService.saveTrans(trans_log);

        Inbox_Notify inbox_notify = new Inbox_Notify();
        inbox_notify.setTitle("Withdraw Approved");
        inbox_notify.setContent("Withdraw Request of $" + withdraw.getWithdraw_amount() + " has been Approved");
        inbox_notify.setTo_user_ids(userService.loadUserByVendorId(withdraw.getVendor_id()).getId() + ",");
        inbox_notify.setUpdated_at(dateFormat.format(date));
        inboxNotifyService.saveInbox_Notify(inbox_notify);

        thirdPartyService.sendAwsSes(contact_email, userService.loadUserByVendorId(withdraw.getVendor_id()).getEmail(), "Withdraw Request Approved", withdraw.getDescription() +" " + withdraw.getWithdraw_amount());
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }


}
