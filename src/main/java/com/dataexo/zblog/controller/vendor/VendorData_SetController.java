package com.dataexo.zblog.controller.vendor;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.dataexo.zblog.controller.admin.AdminRestController;
import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.service.Data_sets_seriesService;
import com.dataexo.zblog.service.ThirdPartyService;
import com.dataexo.zblog.util.ResultInfo;
import com.dataexo.zblog.util.ResultInfoFactory;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Data_sets_series;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.Vendors;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
public class VendorData_SetController {


    // data-sets table
    private static final Logger logger = Logger.getLogger(VendorData_SetController.class);

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


    @Autowired
    private ThirdPartyService thirdPartyService;

    @Value("${aws.access_key_id}")
    public String access_key_id;

    @Value("${aws.secret_access_key}")
    public String secret_access_key;


    /**
     * This is function implement to update data_set in admin panel.
     * This will be used in data_set management page of admin panel
     * This function is called using ajax.
     * The ajax method should be POST mode with data_set of string data.
     * @param data_sets This param will be used to update data_set in sql.
     *           It comes from ajax data with POST method.
     * @return When data_set name is already exist., the resultCode should be fail.
     *      because there should be only one name in database.
     *      When the name is unique  , it will return success code.
     */

    @RequestMapping(value = "/vendor/data_set/update",method = RequestMethod.POST)
    public ResultInfo updateDataSets(HttpSession session,Data_sets data_sets){

        ResultInfo resultInfo = null;
        List<Data_sets> data = data_setsService.checkExist(data_sets);
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if (data == null || data.size() == 0) {
            if(data_sets.getDownload_url_update_at().equals("1")){
                data_sets.setDownload_url_update_at((System.currentTimeMillis() + 6 * 24 * 3600 * 1000) + "");
            }
            data_sets.setVendor_id(user.getVendor_id());
                System.out.println(user.getVendor_id());
            data_setsService.updateData_sets(data_sets);

            if(data_sets.getApi().equals("TIME-SERIES") && data_sets.getHas_series() == 0){

                data_sets_seriesService.deleteData_sets_seriesByParent(data_sets.getId());
                Data_sets_series series = new Data_sets_series();
                series.setData_set_id(data_sets.getId());
                series.setName(data_sets.getName());
                series.setDescription(data_sets.getDescription());
                series.setCode(data_sets.getCode()+"/"+data_sets.getCode());
                series.setData_update_frequency_id(1);
                series.setIs_sample_available(1);
                series.setSource_url(data_sets.getSource_url());
                series.setEmbed_url_chart(data_sets.getEmbed_url_chart());
                series.setEmbed_url_datagrid(data_sets.getEmbed_url_datagrid());
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                series.setLatest_update_date(format.format(date));

                if(data_sets_seriesService.checkExist(series).size() == 0){
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
     * @param id This param will be used to delete data_set in sql.
     *           It comes from ajax data with POST method.
     * @return The return result is always true.
     */
    @RequestMapping(value = "/vendor/data_set/delete",method = RequestMethod.POST)
    public ResultInfo deleteData_sets(String id){
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
     * @param data_sets This param will be used to add data_set in sql.
     *           It comes from ajax data with POST method.
     * @return When data_set name is already exist., the resultCode should be fail.
     *      because there should be only one name in database.
     *      When the name is unique  , it will return success code.
     */
    @RequestMapping(value = "/vendor/data_set/add",method = RequestMethod.POST)
    public ResultInfo addData_Sets(HttpSession session, Data_sets data_sets){

        ResultInfo resultInfo = null;
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        List<Data_sets> data = data_setsService.checkExist(data_sets);
        if (data == null || data.size() == 0) {
            //if(data_sets.getDownload_url_update_at().equals("1")){
            data_sets.setDownload_url_update_at((System.currentTimeMillis() + 6 * 24 * 3600 * 1000) + "");
            //}
            data_sets.setVendor_id(user.getVendor_id());
            data_setsService.saveData_sets(data_sets);
            if(data_sets.getApi().equals("TIME-SERIES") && data_sets.getHas_series() == 0){
                Data_sets_series series = new Data_sets_series();
                series.setData_set_id(data_sets.getId());
                series.setName(data_sets.getName());
                series.setDescription(data_sets.getDescription());
                series.setCode(data_sets.getCode()+"/"+data_sets.getCode());
                series.setData_update_frequency_id(1);
                series.setIs_sample_available(1);
                series.setSource_url(data_sets.getSource_url());
                series.setEmbed_url_chart(data_sets.getEmbed_url_chart());
                series.setEmbed_url_datagrid(data_sets.getEmbed_url_datagrid());
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                series.setLatest_update_date(format.format(date));

                if(data_sets_seriesService.checkExist(series).size() == 0){
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
     * @param request This is HttpServletRequest variable which contains form data.
     * @return The resultCode should be success if there is no exception.
     *      But you should make sure the max file size. If the uploading file size is over max size , it will occur error.
     */

    @RequestMapping(value = "vendor/data_set/upload", consumes = "multipart/form-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultInfo uploadFileHandler(HttpServletRequest request)
    {


        String basePath = UtilClass.getBasePath();

        basePath += "image/upload/";

        ResultInfo resultInfo = null;
        String path = "";
        try {

            Part part = request.getPart("file");
            InputStream stream = part.getInputStream();
            String content =  part.getHeader("Content-Disposition");
            int pos = content.indexOf("filename");
            String fileName = content.substring(pos + 10, content.length() - 1);
            pos = fileName.lastIndexOf(".");
            String type = fileName.substring(pos);

            Date now = new Date();
            int month = now.getMonth();

            File temp = new File(basePath + "/"+  month);
            if(!temp.exists()) {
                temp.mkdirs();
            }

            path = month + "/"+ System.currentTimeMillis() + type;

            OutputStream out = new FileOutputStream(new File(basePath + "/"+ path));
            IOUtils.copy(stream,out);
            stream.close();
            out.close();
            resultInfo = ResultInfoFactory.getSuccessResultInfo("image/upload/"+path);
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
     * @param bucket_name
     * @param s3_file_key
     * @return
     */
    @RequestMapping(value = "/vendor/data_set/generate_s3Url",method = RequestMethod.POST)
    public ResultInfo Generate_s3Url(String bucket_name, String s3_file_key){

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

        if(credentials==null){
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
        LocalDate today=LocalDate.now();
        LocalDate expireDate=today.plusDays(s3_expire_date);
        request.setExpiration(expireDate.toDate());
        URL geturl=conn.generatePresignedUrl(request);


        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        resultInfo.setObject(geturl.toString());
        return resultInfo;
    }



}
