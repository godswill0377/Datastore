package com.dataexo.zblog.util;


import com.dataexo.zblog.controller.MainController;
import com.dataexo.zblog.service.Data_set_filtersService;
import com.dataexo.zblog.vo.*;
import com.dataexo.zblog.vo.auth.Email;
import com.dataexo.zblog.vo.auth.MailgunClient;
import com.dataexo.zblog.vo.resources.instances.MessageInstance;
import com.google.api.client.util.Value;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Resource;
import javax.json.Json;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Member;
import java.net.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class UtilClass {

    private static final Logger logger = Logger.getLogger(UtilClass.class);


    private static String readAll(BufferedReader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        String line;

     /*    char[] chars = new char[8192];
        for(int len; (len = rd.read(chars)) > 0;) {
            // process chars.
            sb.append(chars);
        }*/
       while ((line = rd.readLine()) != null)
        {
            sb.append(line);
        }
        return sb.toString();
    }

    public static String getTotalCount(String url) throws IOException {
        URL obj = new URL(url);
        URLConnection conn = obj.openConnection();

//get all headers
        Map<String, List<String>> map = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                if (entry != null && entry.getKey() != null && entry.getKey().equals("X-total-count")) {
                    return entry.getValue().get(0);
                }
        }
        return "0";
    }
    public static JSONObject readJsonFromUrl(String url) {
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            rd.close();

            JSONObject json = new JSONObject(jsonText);
            return json;

        } catch (IOException e) {

            logger.error( e);
            e.printStackTrace();
        } finally {
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
                logger.error( e);
                e.printStackTrace();
            }
        }
        JSONObject temp = new JSONObject();
        return temp;
    }
    public static String readJsonTextFromUrl(String url) {
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            rd.close();

          return jsonText;

        } catch (IOException e) {
            logger.error( e);
            e.printStackTrace();
        } finally {
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
                logger.error( e);
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(date);
    }

    public static String timeToString(long time , String formatStr) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(formatStr);

        return format.format(date);
    }

    public static JSONObject build_dataseries_json(JSONObject src, Data_sets_series series) {
        JSONObject result = new JSONObject();
        JSONObject sub_result = new JSONObject();

        try {
           src = src.getJSONObject("dataset");
            sub_result.put("id", src.getString("id"));
            sub_result.put("dataset_code", series.getCode());
            sub_result.put("name", series.getName());
            sub_result.put("description", series.getDescription());
            sub_result.put("newest_available_date", src.getString("newest_available_date"));
            sub_result.put("oldest_available_date", src.getString("oldest_available_date"));

            JSONArray colums = src.getJSONArray("column_names");
            JSONArray columns_new = new JSONArray();
            columns_new.put("Date");
            for (int j = 2; j < colums.length(); j++) {
                columns_new.put(colums.getString(j));
            }

            sub_result.put("frequency", series.getData_update_frequency_text());

            JSONArray data = new JSONArray();

            JSONArray srcdata = src.getJSONArray("data");

            JSONArray newest = new JSONArray();
            for (int i = 0; i < srcdata.length(); i++) {
                JSONArray obj = srcdata.getJSONArray(i);
                JSONArray list = new JSONArray();

                long time = 0;
                String text = obj.get(0).toString();
                char ch = text.charAt(0);
                if( ch >= '0' && ch <= '9'){
                    time = obj.getLong(0);
                }
                else{
                    time = obj.getLong(1);
                }
                list.put(convertTime(time));

                for (int j = 2; j < colums.length(); j++) {

                    String temp = obj.get(j).toString();
                    if(temp.equals("null")){
                        list.put(0);
                    }
                    else {
                        int len = temp.length();
                        if(temp.charAt(len-1) == 'k' || temp.charAt(len-1) == 'K'){
                            temp = temp.substring(0,len - 1);
                            list.put(Double.parseDouble(temp) * 1000);
                        }
                        else {
                            list.put(obj.getDouble(j));
                       }
                    }
                }

                data.put(list);

                if (i == srcdata.length() - 1) {
                    newest = list;
                }
            }

            sub_result.put("column_names", columns_new);
            sub_result.put("newest_values", newest);

            sub_result.put("data", data);
            result.put("status","success");
            result.put("dataset", sub_result);
        } catch (JSONException e) {
            logger.error( e);
            e.printStackTrace();
            result.put("status","fail");
            result.put("error",e.getMessage());
        }
        return result;
    }

    public static String readURL(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }

    public static MessageInstance sendMail(String from, String to, String body, String subject, String domain, String apikey) {
        MailgunClient client = new MailgunClient(domain, apikey);

        Email email = new Email(to, from, body, subject);
        try {
            MessageInstance msg = client.sendSimpleEmail(email);
            return msg;
        } catch (Exception e) {
            logger.error( e);
            e.printStackTrace();
        }
        return null;
    }

    public static String membershipText1(int type) {
        if(type == 0){
            return "Free";
        }

        //membership type is odd then membership is single else enterprise
        if(type % 2 == 1){
            return  "Single";
        }
        return "Enterprise";
    }

    public static String membershipText2(int type, Double price) {
        String result = "";
        if(type ==0){
            result = "$0 per month/year";
        }else {
            switch (type % 4) {
                case 1:
                    result = "$" + price + "  per month";
                    break;
                case 2:
                    result = "$" + price + "  per month";
                    break;
                case 3:
                    result = "$" + price + "  per year";
                    break;
                case 0:
                    result = "$" + price + "  per year";
                    break;
            }
        }
        return result;
    }

    public static String getExpireDate(int mode) {
        long current =  System.currentTimeMillis();

        if(mode == 0){
            current = 0;
        }else {
            switch (mode % 4) {
                case 1:
                    long val = 60 * 60 * 24 * 30;
                    current += (val * 1000);

                    break;
                case 2:
                    val = 60 * 60 * 24 * 30;
                    current += (val * 1000);
                    break;
                case 3:
                    val = 60 * 60 * 24 * 365;
                    current += (val * 1000);
                    break;
                case 0:
                    val = 60 * 60 * 24 * 365;
                    current += (val * 1000);
                    break;
            }
        }
        return ""+current;
    }

    public static String getExpiryDate (int mode){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String current = dateFormat.format(date);
        String expiry_date = "";
        if(mode == 0){
            expiry_date = current;
        }else {
            switch (mode % 4) {
                case 1:
                    expiry_date = dateFormat.format(DateUtils.addMonths(date, 1));
                    break;
                case 2:
                    expiry_date = dateFormat.format(DateUtils.addMonths(date, 1));
                    break;
                case 3:
                    expiry_date = dateFormat.format(DateUtils.addYears(date, 1));
                    break;
                case 0:
                    expiry_date = dateFormat.format(DateUtils.addYears(date, 1));
                    break;
            }
        }
        return expiry_date;
    }

    public static double getBalance(int mode,Membership membership) {
        double value = 0;
        switch (mode){
            case 0:
                value = 0;
                break;
            case 1:
                value = membership.getM_single_value();
                break;
            case 2:
                value = membership.getM_enterprise_value();
                break;
            case 3:
                value = membership.getY_single_value();
                break;
            case 4:
                value = membership.getY_enterprise_value();
                break;
        }
        return value;
    }


    public static String getDataFromJson(String type , JSONArray ar , int j){
        String result = "";


        if (type.equals("Date") || type.equals("DATE")
                || type.equals("TIMESTAMP") || type.equals("timestamp") || type.equals("TimeStamp")) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                Date date = new Date(ar.getLong(j));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                result = format.format(date);
            }
        }
        else{
            result= ar.get(j).toString();
        }

        if(result.equals("null")){
            result = "";
        }

      /*  if (type.equals("String") || type.equals("VARCHAR") ||  type.equals("STRING") ||  type.equals("Varchar")) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                result= ar.getString(j);
            }
        } else if (type.indexOf("BigDecimal") >= 0 || type.indexOf("Decimal") >= 0
                || type.indexOf("BIGDECIMAL") >= 0 || type.indexOf("DECIMAL") >= 0
                ) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                result = Double.toString(ar.getDouble(j));
            }
        } else if (type.equals("Integer") || type.equals("INTEGER")) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                result = Integer.toString(ar.getInt(j));
            }
        } else if (type.equals("Date") || type.equals("DATE")) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                Date date = new Date(ar.getLong(j));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                result = format.format(date);
            }
        }

        else if (type.equals("boolean") || type.equals("BOOLEAN")  || type.equals("Boolean")) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                result = Boolean.toString(ar.getBoolean(j));

                if(result.equals("1")){
                    result = "True";
                }
                else  if(result.equals("0")){
                    result = "False";
                }
            }
        }

        else if (type.equals("long") || type.equals("Long") || type.equals("LONG") || type.equals("BIGINT")  || type.equals("Bigint")
                || type.equals("bigint")
                ) {
            if (ar.get(j).toString().indexOf("null") < 0) {
                result = Long.toString(ar.getLong(j));
            }
        }

        else if (type.equals("TIMESTAMP") || type.equals("timestamp") || type.equals("TimeStamp") ) {
            if (ar.get(j).toString().indexOf("null") < 0) {

                Date date = new Date(ar.getLong(j));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                result = format.format(date);
            }
        }

*/
        return result;
    }

    private static final Map<String, String> fileExtensionMap;

    static {
        fileExtensionMap = new HashMap<String, String>();
        // MS Office
        fileExtensionMap.put("doc", "application/msword");
        fileExtensionMap.put("dot", "application/msword");
        fileExtensionMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileExtensionMap.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        fileExtensionMap.put("docm", "application/vnd.ms-word.document.macroEnabled.12");
        fileExtensionMap.put("dotm", "application/vnd.ms-word.template.macroEnabled.12");
        fileExtensionMap.put("xls", "application/vnd.ms-excel");
        fileExtensionMap.put("xlt", "application/vnd.ms-excel");
        fileExtensionMap.put("xla", "application/vnd.ms-excel");
        fileExtensionMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileExtensionMap.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        fileExtensionMap.put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
        fileExtensionMap.put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
        fileExtensionMap.put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        fileExtensionMap.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        fileExtensionMap.put("ppt", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pot", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pps", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("ppa", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        fileExtensionMap.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        fileExtensionMap.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        fileExtensionMap.put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
        fileExtensionMap.put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        fileExtensionMap.put("potm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        fileExtensionMap.put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        // Open Office
        fileExtensionMap.put("odt", "application/vnd.oasis.opendocument.text");
        fileExtensionMap.put("ott", "application/vnd.oasis.opendocument.text-template");
        fileExtensionMap.put("oth", "application/vnd.oasis.opendocument.text-web");
        fileExtensionMap.put("odm", "application/vnd.oasis.opendocument.text-master");
        fileExtensionMap.put("odg", "application/vnd.oasis.opendocument.graphics");
        fileExtensionMap.put("otg", "application/vnd.oasis.opendocument.graphics-template");
        fileExtensionMap.put("odp", "application/vnd.oasis.opendocument.presentation");
        fileExtensionMap.put("otp", "application/vnd.oasis.opendocument.presentation-template");
        fileExtensionMap.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        fileExtensionMap.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
        fileExtensionMap.put("odc", "application/vnd.oasis.opendocument.chart");
        fileExtensionMap.put("odf", "application/vnd.oasis.opendocument.formula");
        fileExtensionMap.put("odb", "application/vnd.oasis.opendocument.database");
        fileExtensionMap.put("odi", "application/vnd.oasis.opendocument.image");
        fileExtensionMap.put("oxt", "application/vnd.openofficeorg.extension");
        // Other
        fileExtensionMap.put("txt", "text/plain");
        fileExtensionMap.put("rtf", "application/rtf");
        fileExtensionMap.put("pdf", "application/pdf");
    }
    public static String getMimeType(String fileName) {
        // 1. first use java's built-in utils
        FileNameMap mimeTypes = URLConnection.getFileNameMap();
        String contentType = mimeTypes.getContentTypeFor(fileName);

        // 2. nothing found -> lookup our in extension map to find types like ".doc" or ".docx"
        if (contentType == null) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());;
            contentType = fileExtensionMap.get(extension);
        }
        return contentType;
    }



    private static String USER_AGENT = "Mozilla/5.0";
    // HTTP GET request
    public static String sendGet(String url, String serverMode) throws Exception {


        URL obj = new URL(url);

        BufferedReader in = null;
        if(serverMode.equals("https")){
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

        }
        else{
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    // HTTP POST request
    public static String sendPost(String url , String param, String serverMode) throws Exception {


        URL obj = new URL(url);
        BufferedReader in = null;
        if(serverMode.equals("https")){
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setConnectTimeout(4000);

            String urlParameters =param;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

        }
        else{
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setConnectTimeout(4000);

            String urlParameters =param;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        }

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result

        return response.toString();


    }

    public static String getFilter_query(Data_sets data_sets , Data_sets_series data_sets_series , Data_set_filtersService filtersService){
        if(data_sets.getSchema_name() == "" && data_sets.getTable_name() == ""){
            return "";
        }
        else{
            String condition = "";
            if(data_sets_series == null){
                condition = "select * from " + data_sets.getSchema_name() + "." + data_sets.getTable_name();
                return condition;
            }
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
           return condition;
        }
    }

    public static String generateAPIKey(){
        String apiKey = UUID.randomUUID().toString();
        apiKey += System.currentTimeMillis();
        return apiKey;
    }



    public static String generateToken(){
        String tokenVal = UUID.randomUUID().toString().toLowerCase();
        return tokenVal;
    }



    public static String checkoutHtmlString(int mode){
        String html = "";

        if(mode == 1) {
            html = "<html>" +
                    "<head>" +
                    "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                    "<meta name='format-detection' content='telephone=no' /> " +
                    "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                    "<style type='text/css'>" +
                    "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                    "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                    "table{border-collapse:collapse;}" +
                    "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                    "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                    "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                    "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                    "" +
                    ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                    ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                    "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                    "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                    "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                    "" +
                    "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                    "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                    "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                    "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                    ".flexibleImage{height:auto;}" +
                    ".linkRemoveBorder{border-bottom:0 !important;}" +
                    "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                    "" +
                    "body, #bodyTable{background-color:#E1E1E1;}" +
                    "#emailHeader{background-color:#E1E1E1;}" +
                    "#emailBody{background-color:#FFFFFF;}" +
                    "#emailFooter{background-color:#E1E1E1;}" +
                    ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                    ".emailButton{background-color:#205478; border-collapse:separate;}" +
                    ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                    ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                    ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                    ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                    ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                    ".imageContentText {margin-top: 10px;line-height:0;}" +
                    ".imageContentText a {line-height:0;}" +
                    "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                    "" +
                    "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                    "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                    "" +
                    ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                    ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                    "" +
                    "" +
                    "@media only screen and (max-width: 480px){" +
                    "body{width:100% !important; min-width:100% !important;} " +
                    "" +
                    "table[id='emailHeader']," +
                    "table[id='emailBody']," +
                    "table[id='emailFooter']," +
                    "table[class='flexibleContainer']," +
                    "td[class='flexibleContainerCell'] {width:100% !important;}" +
                    "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                    "" +
                    "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                    "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                    "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                    "" +
                    "" +
                    "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                    "" +
                    "table[class='emailButton']{width:100% !important;}" +
                    "td[class='buttonContent']{padding:0 !important;}" +
                    "td[class='buttonContent'] a{padding:15px !important;}" +
                    "" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:1){" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                    "}" +
                    "" +
                    "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                    "}" +
                    "</style>" +
                    "" +
                    "</head>" +
                    "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                    "<center style='background-color:#E1E1E1;'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                    "<tr>" +
                    "<td align='center' valign='top' id='bodyCell'>" +
                    "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                    "<tr style='height:10px;'>" +
                    "<tr>" +
                    "</table>" +
                    "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                    "<tr>" +
                    "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                    "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td align='center' valign='top' class='textContent'>" +
                    "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Bill Information</h1>" +
                    "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>This is your shopping bill information.</h2>" +
                    "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>You can download data sets anytime which you have bought.</div>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>";
        }
        else{
            html += "</table>" +
                    "" +
                    "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                    "" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                    "<tr>" +
                    "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                    "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td valign='top' bgcolor='#E1E1E1'>" +
                    "" +
                    "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                    "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                    "" +
                    "</div>" +
                    "" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</center>" +
                    "</body>" +
                    "</html>";
        }
        return html;
    }


    public static String marketingHtmlString(int mode, String title , String content){
        String html = "";

        if(mode == 1) {
            html = "<html>" +
                    "<head>" +
                    "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                    "<meta name='format-detection' content='telephone=no' /> " +
                    "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                    "<style type='text/css'>" +
                    "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                    "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                    "table{border-collapse:collapse;}" +
                    "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                    "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                    "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                    "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                    "" +
                    ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                    ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                    "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                    "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                    "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                    "" +
                    "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                    "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                    "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                    "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                    ".flexibleImage{height:auto;}" +
                    ".linkRemoveBorder{border-bottom:0 !important;}" +
                    "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                    "" +
                    "body, #bodyTable{background-color:#E1E1E1;}" +
                    "#emailHeader{background-color:#E1E1E1;}" +
                    "#emailBody{background-color:#FFFFFF;}" +
                    "#emailFooter{background-color:#E1E1E1;}" +
                    ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                    ".emailButton{background-color:#205478; border-collapse:separate;}" +
                    ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                    ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                    ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                    ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                    ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                    ".imageContentText {margin-top: 10px;line-height:0;}" +
                    ".imageContentText a {line-height:0;}" +
                    "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                    "" +
                    "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                    "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                    "" +
                    ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                    ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                    "" +
                    "" +
                    "@media only screen and (max-width: 480px){" +
                    "body{width:100% !important; min-width:100% !important;} " +
                    "" +
                    "table[id='emailHeader']," +
                    "table[id='emailBody']," +
                    "table[id='emailFooter']," +
                    "table[class='flexibleContainer']," +
                    "td[class='flexibleContainerCell'] {width:100% !important;}" +
                    "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                    "" +
                    "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                    "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                    "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                    "" +
                    "" +
                    "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                    "" +
                    "table[class='emailButton']{width:100% !important;}" +
                    "td[class='buttonContent']{padding:0 !important;}" +
                    "td[class='buttonContent'] a{padding:15px !important;}" +
                    "" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:1){" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                    "}" +
                    "" +
                    "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                    "}" +
                    "</style>" +
                    "" +
                    "</head>" +
                    "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                    "<center style='background-color:#E1E1E1;'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                    "<tr>" +
                    "<td align='center' valign='top' id='bodyCell'>" +
                    "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                    "<tr style='height:10px;'>" +
                    "<tr>" +
                    "</table>" +
                    "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                    "<tr>" +
                    "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                    "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td align='center' valign='top' class='textContent'>" +
                    "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Invitation </h1>" +
                    "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>"+title+"</h2>" +
                    "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>"+content+"</div>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>";
        }
        else{
            html += "</table>" +
                    "" +
                    "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                    "" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                    "<tr>" +
                    "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                    "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td valign='top' bgcolor='#E1E1E1'>" +
                    "" +
                    "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                    "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                    "" +
                    "</div>" +
                    "" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</center>" +
                    "</body>" +
                    "</html>";
        }
        return html;
    }

    // contact us email template

    public static String ThanksReplyHtmlToUser (String name){
        String html = "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                "<meta name='format-detection' content='telephone=no' /> " +
                "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                "<style type='text/css'>" +
                "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                "table{border-collapse:collapse;}" +
                "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                "" +
                ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                "" +
                "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                ".flexibleImage{height:auto;}" +
                ".linkRemoveBorder{border-bottom:0 !important;}" +
                "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                "" +
                "body, #bodyTable{background-color:#E1E1E1;}" +
                "#emailHeader{background-color:#E1E1E1;}" +
                "#emailBody{background-color:#FFFFFF;}" +
                "#emailFooter{background-color:#E1E1E1;}" +
                ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                ".emailButton{background-color:#205478; border-collapse:separate;}" +
                ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                ".imageContentText {margin-top: 10px;line-height:0;}" +
                ".imageContentText a {line-height:0;}" +
                "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                "" +
                "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                "" +
                ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                "" +
                "" +
                "@media only screen and (max-width: 480px){" +
                "body{width:100% !important; min-width:100% !important;} " +
                "" +
                "table[id='emailHeader']," +
                "table[id='emailBody']," +
                "table[id='emailFooter']," +
                "table[class='flexibleContainer']," +
                "td[class='flexibleContainerCell'] {width:100% !important;}" +
                "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                "" +
                "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                "" +
                "" +
                "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                "" +
                "table[class='emailButton']{width:100% !important;}" +
                "td[class='buttonContent']{padding:0 !important;}" +
                "td[class='buttonContent'] a{padding:15px !important;}" +
                "" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                "}" +
                "" +
                "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                "" +
                "}" +
                "</style>" +
                "" +
                "</head>" +
                "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                "" +
                "<center style='background-color:#E1E1E1;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                "<tr>" +
                "<td align='center' valign='top' id='bodyCell'>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                "<tr style='height:50px;'>" +
                "<tr>" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top' class='textContent'>" +

                "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>You received email from dataexo member.</h2>" +

                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "<tr style='height:40px;'>" +
                "" +
                "</tr>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr style='padding-top:0;'>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: white;'>" +
                "<tr>" +
                "<h3> "+name+"</h3>" +
                "<h4>Thank you for contacting us, you will contact you soon.</h4>" +
                "</tr>" +
                "</table>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td valign='top' bgcolor='#E1E1E1'>" +
                "" +
                "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                "" +
                "</div>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</center>" +
                "</body>" +
                "</html>";
        return html;
    }


    public static String ContactAdminHtmlToUser (String name, String content){
        String html = "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                "<meta name='format-detection' content='telephone=no' /> " +
                "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                "<style type='text/css'>" +
                "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                "table{border-collapse:collapse;}" +
                "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                "" +
                ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                "" +
                "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                ".flexibleImage{height:auto;}" +
                ".linkRemoveBorder{border-bottom:0 !important;}" +
                "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                "" +
                "body, #bodyTable{background-color:#E1E1E1;}" +
                "#emailHeader{background-color:#E1E1E1;}" +
                "#emailBody{background-color:#FFFFFF;}" +
                "#emailFooter{background-color:#E1E1E1;}" +
                ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                ".emailButton{background-color:#205478; border-collapse:separate;}" +
                ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                ".imageContentText {margin-top: 10px;line-height:0;}" +
                ".imageContentText a {line-height:0;}" +
                "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                "" +
                "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                "" +
                ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                "" +
                "" +
                "@media only screen and (max-width: 480px){" +
                "body{width:100% !important; min-width:100% !important;} " +
                "" +
                "table[id='emailHeader']," +
                "table[id='emailBody']," +
                "table[id='emailFooter']," +
                "table[class='flexibleContainer']," +
                "td[class='flexibleContainerCell'] {width:100% !important;}" +
                "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                "" +
                "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                "" +
                "" +
                "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                "" +
                "table[class='emailButton']{width:100% !important;}" +
                "td[class='buttonContent']{padding:0 !important;}" +
                "td[class='buttonContent'] a{padding:15px !important;}" +
                "" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                "}" +
                "" +
                "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                "" +
                "}" +
                "</style>" +
                "" +
                "</head>" +
                "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                "" +
                "<center style='background-color:#E1E1E1;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                "<tr>" +
                "<td align='center' valign='top' id='bodyCell'>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                "<tr style='height:50px;'>" +
                "<tr>" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top' class='textContent'>" +

                "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>You received email from dataexo member.</h2>" +

                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "<tr style='height:40px;'>" +
                "" +
                "</tr>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr style='padding-top:0;'>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: white;'>" +
                "<tr>" +
                "<h3> "+name + " contact DataEXO"+"</h3>" +
                "<h4>" + content + "</h4>" +
                "</tr>" +
                "</table>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td valign='top' bgcolor='#E1E1E1'>" +
                "" +
                "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                "" +
                "</div>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</center>" +
                "</body>" +
                "</html>";
        return html;
    }

    public static String HtmlToUser (String content){
        String html = "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                "<meta name='format-detection' content='telephone=no' /> " +
                "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                "<style type='text/css'>" +
                "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                "table{border-collapse:collapse;}" +
                "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                "" +
                ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                "" +
                "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                ".flexibleImage{height:auto;}" +
                ".linkRemoveBorder{border-bottom:0 !important;}" +
                "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                "" +
                "body, #bodyTable{background-color:#E1E1E1;}" +
                "#emailHeader{background-color:#E1E1E1;}" +
                "#emailBody{background-color:#FFFFFF;}" +
                "#emailFooter{background-color:#E1E1E1;}" +
                ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                ".emailButton{background-color:#205478; border-collapse:separate;}" +
                ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                ".imageContentText {margin-top: 10px;line-height:0;}" +
                ".imageContentText a {line-height:0;}" +
                "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                "" +
                "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                "" +
                ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                "" +
                "" +
                "@media only screen and (max-width: 480px){" +
                "body{width:100% !important; min-width:100% !important;} " +
                "" +
                "table[id='emailHeader']," +
                "table[id='emailBody']," +
                "table[id='emailFooter']," +
                "table[class='flexibleContainer']," +
                "td[class='flexibleContainerCell'] {width:100% !important;}" +
                "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                "" +
                "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                "" +
                "" +
                "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                "" +
                "table[class='emailButton']{width:100% !important;}" +
                "td[class='buttonContent']{padding:0 !important;}" +
                "td[class='buttonContent'] a{padding:15px !important;}" +
                "" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                "}" +
                "" +
                "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                "" +
                "}" +
                "</style>" +
                "" +
                "</head>" +
                "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                "" +
                "<center style='background-color:#E1E1E1;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                "<tr>" +
                "<td align='center' valign='top' id='bodyCell'>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                "<tr style='height:50px;'>" +
                "<tr>" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top' class='textContent'>" +

                "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>You received email from dataexo administrator.</h2>" +

                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "<tr style='height:40px;'>" +
                "" +
                "</tr>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr style='padding-top:0;'>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: white;'>" +
                "<tr>" +
                "<h4>" + content + "</h4>" +
                "</tr>" +
                "</table>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td valign='top' bgcolor='#E1E1E1'>" +
                "" +
                "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                "" +
                "</div>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</center>" +
                "</body>" +
                "</html>";
        return html;
    }

    public static String activateHtmlTempl(String domain, User userInfo, String tokenVal){
        String html = "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                "<meta name='format-detection' content='telephone=no' /> " +
                "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                "<style type='text/css'>" +
                "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                "table{border-collapse:collapse;}" +
                "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                "" +
                ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                "" +
                "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                ".flexibleImage{height:auto;}" +
                ".linkRemoveBorder{border-bottom:0 !important;}" +
                "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                "" +
                "body, #bodyTable{background-color:#E1E1E1;}" +
                "#emailHeader{background-color:#E1E1E1;}" +
                "#emailBody{background-color:#FFFFFF;}" +
                "#emailFooter{background-color:#E1E1E1;}" +
                ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                ".emailButton{background-color:#205478; border-collapse:separate;}" +
                ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                ".imageContentText {margin-top: 10px;line-height:0;}" +
                ".imageContentText a {line-height:0;}" +
                "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                "" +
                "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                "" +
                ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                "" +
                "" +
                "@media only screen and (max-width: 480px){" +
                "body{width:100% !important; min-width:100% !important;} " +
                "" +
                "table[id='emailHeader']," +
                "table[id='emailBody']," +
                "table[id='emailFooter']," +
                "table[class='flexibleContainer']," +
                "td[class='flexibleContainerCell'] {width:100% !important;}" +
                "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                "" +
                "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                "" +
                "" +
                "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                "" +
                "table[class='emailButton']{width:100% !important;}" +
                "td[class='buttonContent']{padding:0 !important;}" +
                "td[class='buttonContent'] a{padding:15px !important;}" +
                "" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                "}" +
                "" +
                "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                "" +
                "}" +
                "</style>" +
                "" +
                "</head>" +
                "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                "" +
                "<center style='background-color:#E1E1E1;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                "<tr>" +
                "<td align='center' valign='top' id='bodyCell'>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                "<tr style='height:50px;'>" +
                "<tr>" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top' class='textContent'>" +
                "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Registration</h1>" +
                "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>Please Activate your account.</h2>" +
                "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>Please click 'Confirmation' button to activate your account.</div>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "<tr style='height:40px;'>" +
                "" +
                "</tr>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr style='padding-top:0;'>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: #3498DB;'>" +
                "<tr>" +
                "<td align='center' valign='middle' class='buttonContent' style='padding-top:15px;padding-bottom:15px;padding-right:15px;padding-left:15px;'>" +
                "<a style='color:#FFFFFF;text-decoration:none;font-family:Helvetica,Arial,sans-serif;font-size:20px;line-height:135%;' href='" + domain + "/account/activate/" + userInfo.getId() + "/" + tokenVal + "' target='_blank'>Confirmation</a>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td valign='top' bgcolor='#E1E1E1'>" +
                "" +
                "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                "" +
                "</div>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</center>" +
                "</body>" +
                "</html>";
        return html;
    }

    public static String getWebhookEmails(String domain){
        String mailTemplate =
                "<html>" +
                        "<head>" +
                        "    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                        "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "    <meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                        "    <meta name='format-detection' content='telephone=no' />" +
                        "    <title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                        "    <style type='text/css'>" +
                        "        html {" +
                        "            background-color: #E1E1E1;" +
                        "            margin: 0;" +
                        "            padding: 0;" +
                        "        }" +
                        "        body," +
                        "        #bodyTable," +
                        "        #bodyCell," +
                        "        #bodyCell {" +
                        "            height: 100% !important;" +
                        "            margin: 0;" +
                        "            padding: 0;" +
                        "            width: 100% !important;" +
                        "            font-family: Helvetica, Arial, 'Lucida Grande', sans-serif;" +
                        "        }" +
                        "        table {" +
                        "            border-collapse: collapse;" +
                        "        }" +
                        "        table[id=bodyTable] {" +
                        "            width: 100% !important;" +
                        "            margin: auto;" +
                        "            max-width: 500px !important;" +
                        "            color: #7A7A7A;" +
                        "            font-weight: normal;" +
                        "        }" +
                        "        img," +
                        "        a img {" +
                        "            border: 0;" +
                        "            outline: none;" +
                        "            text-decoration: none;" +
                        "            height: auto;" +
                        "            line-height: 100%;" +
                        "        }" +
                        "        a {" +
                        "            text-decoration: none !important;" +
                        "            border-bottom: 1px solid;" +
                        "        }" +
                        "        h1," +
                        "        h2," +
                        "        h3," +
                        "        h4," +
                        "        h5," +
                        "        h6 {" +
                        "            color: #5F5F5F;" +
                        "            font-weight: normal;" +
                        "            font-family: Helvetica;" +
                        "            font-size: 20px;" +
                        "            line-height: 125%;" +
                        "            text-align: Left;" +
                        "            letter-spacing: normal;" +
                        "            margin-top: 0;" +
                        "            margin-right: 0;" +
                        "            margin-bottom: 10px;" +
                        "            margin-left: 0;" +
                        "            padding-top: 0;" +
                        "            padding-bottom: 0;" +
                        "            padding-left: 0;" +
                        "            padding-right: 0;" +
                        "        }" +
                        "        .ReadMsgBody {" +
                        "            width: 100%;" +
                        "        }" +
                        "        .ExternalClass {" +
                        "            width: 100%;" +
                        "        }" +
                        "        .ExternalClass," +
                        "        .ExternalClass p," +
                        "        .ExternalClass span," +
                        "        .ExternalClass font," +
                        "        .ExternalClass td," +
                        "        .ExternalClass div {" +
                        "            line-height: 100%;" +
                        "        }" +
                        "        table," +
                        "        td {" +
                        "            mso-table-lspace: 0pt;" +
                        "            mso-table-rspace: 0pt;" +
                        "        }" +
                        "        img {" +
                        "            -ms-interpolation-mode: bicubic;" +
                        "            display: block;" +
                        "            outline: none;" +
                        "            text-decoration: none;" +
                        "        }" +
                        "        body," +
                        "        table," +
                        "        td," +
                        "        p," +
                        "        a," +
                        "        li," +
                        "        blockquote {" +
                        "            -ms-text-size-adjust: 100%;" +
                        "            -webkit-text-size-adjust: 100%;" +
                        "            font-weight: normal !important;" +
                        "        }" +
                        "        h1 {" +
                        "            display: block;" +
                        "            font-size: 26px;" +
                        "            font-style: normal;" +
                        "            font-weight: normal;" +
                        "            line-height: 100%;" +
                        "        }" +
                        "        h2 {" +
                        "            display: block;" +
                        "            font-size: 20px;" +
                        "            font-style: normal;" +
                        "            font-weight: normal;" +
                        "            line-height: 120%;" +
                        "        }" +
                        "        h3 {" +
                        "            display: block;" +
                        "            font-size: 17px;" +
                        "            font-style: normal;" +
                        "            font-weight: normal;" +
                        "            line-height: 110%;" +
                        "        }" +
                        "        h4 {" +
                        "            display: block;" +
                        "            font-size: 18px;" +
                        "            font-style: italic;" +
                        "            font-weight: normal;" +
                        "            line-height: 100%;" +
                        "        }" +
                        "        .flexibleImage {" +
                        "            height: auto;" +
                        "        }" +
                        "        .linkRemoveBorder {" +
                        "            border-bottom: 0 !important;" +
                        "        }" +
                        "        table[class=flexibleContainerCellDivider] {" +
                        "            padding-bottom: 0 !important;" +
                        "            padding-top: 0 !important;" +
                        "        }" +
                        "        body," +
                        "        #bodyTable {" +
                        "            background-color: #E1E1E1;" +
                        "        }" +
                        "        #emailHeader {" +
                        "            background-color: #E1E1E1;" +
                        "        }" +
                        "        #emailBody {" +
                        "            background-color: #FFFFFF;" +
                        "        }" +
                        "        #emailFooter {" +
                        "            background-color: #E1E1E1;" +
                        "        }" +
                        "        .nestedContainer {" +
                        "            background-color: #F8F8F8;" +
                        "            border: 1px solid #CCCCCC;" +
                        "        }" +
                        "        .emailButton {" +
                        "            background-color: #205478;" +
                        "            border-collapse: separate;" +
                        "        }" +
                        "        .buttonContent {" +
                        "            color: #FFFFFF;" +
                        "            font-family: Helvetica;" +
                        "            font-size: 18px;" +
                        "            font-weight: bold;" +
                        "            line-height: 100%;" +
                        "            padding: 15px;" +
                        "            text-align: center;" +
                        "        }" +
                        "        .buttonContent a {" +
                        "            color: #FFFFFF;" +
                        "            display: block;" +
                        "            text-decoration: none !important;" +
                        "            border: 0 !important;" +
                        "        }" +
                        "        .emailCalendar {" +
                        "            background-color: #FFFFFF;" +
                        "            border: 1px solid #CCCCCC;" +
                        "        }" +
                        "        .emailCalendarMonth {" +
                        "            background-color: #205478;" +
                        "            color: #FFFFFF;" +
                        "            font-family: Helvetica, Arial, sans-serif;" +
                        "            font-size: 16px;" +
                        "            font-weight: bold;" +
                        "            padding-top: 10px;" +
                        "            padding-bottom: 10px;" +
                        "            text-align: center;" +
                        "        }" +
                        "        .emailCalendarDay {" +
                        "            color: #205478;" +
                        "            font-family: Helvetica, Arial, sans-serif;" +
                        "            font-size: 60px;" +
                        "            font-weight: bold;" +
                        "            line-height: 100%;" +
                        "            padding-top: 20px;" +
                        "            padding-bottom: 20px;" +
                        "            text-align: center;" +
                        "        }" +
                        "        .imageContentText {" +
                        "            margin-top: 10px;" +
                        "            line-height: 0;" +
                        "        }" +
                        "        .imageContentText a {" +
                        "            line-height: 0;" +
                        "        }" +
                        "        #invisibleIntroduction {" +
                        "            display: none !important;" +
                        "        }" +
                        "        /* Removing the introduction text from the view */" +
                        "        span[class=ios-color-hack2] a {" +
                        "            color: #205478 !important;" +
                        "            text-decoration: none !important;" +
                        "        }" +
                        "        span[class=ios-color-hack3] a {" +
                        "            color: #8B8B8B !important;" +
                        "            text-decoration: none !important;" +
                        "        }" +
                        "        .a[href^='tel']," +
                        "        a[href^='sms'] {" +
                        "            text-decoration: none !important;" +
                        "            color: #606060 !important;" +
                        "            pointer-events: none !important;" +
                        "            cursor: default !important;" +
                        "        }" +
                        "        .mobile_link a[href^='tel']," +
                        "        .mobile_link a[href^='sms'] {" +
                        "            text-decoration: none !important;" +
                        "            color: #606060 !important;" +
                        "            pointer-events: auto !important;" +
                        "            cursor: default !important;" +
                        "        }" +
                        "        @media only screen and (max-width: 480px) {" +
                        "            body {" +
                        "                width: 100% !important;" +
                        "                min-width: 100% !important;" +
                        "            }" +
                        "            table[id='emailHeader']," +
                        "            table[id='emailBody']," +
                        "            table[id='emailFooter']," +
                        "            table[class='flexibleContainer']," +
                        "            td[class='flexibleContainerCell'] {" +
                        "                width: 100% !important;" +
                        "            }" +
                        "            td[class='flexibleContainerBox']," +
                        "            td[class='flexibleContainerBox'] table {" +
                        "                display: block;" +
                        "                width: 100%;" +
                        "                text-align: left;" +
                        "            }" +
                        "            td[class='imageContent'] img {" +
                        "                height: auto !important;" +
                        "                width: 100% !important;" +
                        "                max-width: 100% !important;" +
                        "            }" +
                        "            img[class='flexibleImage'] {" +
                        "                height: auto !important;" +
                        "                width: 100% !important;" +
                        "                max-width: 100% !important;" +
                        "            }" +
                        "            img[class='flexibleImageSmall'] {" +
                        "                height: auto !important;" +
                        "                width: auto !important;" +
                        "            }" +
                        "            table[class='flexibleContainerBoxNext'] {" +
                        "                padding-top: 10px !important;" +
                        "            }" +
                        "            table[class='emailButton'] {" +
                        "                width: 100% !important;" +
                        "            }" +
                        "            td[class='buttonContent'] {" +
                        "                padding: 0 !important;" +
                        "            }" +
                        "            td[class='buttonContent'] a {" +
                        "                padding: 15px !important;" +
                        "            }" +
                        "        }" +
                        "        @media only screen and (-webkit-device-pixel-ratio:.75) {}" +
                        "        @media only screen and (-webkit-device-pixel-ratio:1) {}" +
                        "        @media only screen and (-webkit-device-pixel-ratio:1.5) {}" +
                        "        @media only screen and (min-device-width: 320px) and (max-device-width:568px) {}" +
                        "    </style>" +
                        "</head>" +
                        "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                        "    <center style='background-color:#E1E1E1;'>" +
                        "        <table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                        "            <tr>" +
                        "                <td align='center' valign='top' id='bodyCell'>" +
                        "                    <table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                        "                        <tr style='height:50px;'>" +
                        "                            <tr>" +
                        "                    </table>" +
                        "                    <table bgcolor='#FFFFFF' border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                        "                        <tr>" +
                        "                            <td align='center' valign='top'>" +
                        "                                <table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                        "                                    <tr>" +
                        "                                        <td align='center' valign='top'>" +
                        "                                            <table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                        "                                                <tr>" +
                        "                                                    <td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                        "                                                        <table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                        "                                                            <tr>" +
                        "                                                                <td align='center' valign='top' class='textContent'>" +
                        "                                                                    <h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Membership Payment</h1>" +
                        "                                                                    <h2 style='margin-top: 20px; text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:white;line-height:135%;'>Dear user_name</h2>" +
                        "                                                                    <div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>$membership_fee paid for membership_plan. </div>" +
                        "                                                                    <div  style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>Your membership will be expired at membership_expiry</div>" +
                        "                                                                </td>" +
                        "                                                            </tr>" +
                        "                                                        </table>" +
                        "                                                    </td>" +
                        "                                                </tr>" +
                        "                                            </table>" +
                        "                                        </td>" +
                        "                                    </tr>" +
                        "                                </table>" +
                        "                            </td>" +
                        "                        </tr>" +
                        "                        <tr style='height:40px;'>" +
                        "                        </tr>" +
                        "                        <tr>" +
                        "                            <td align='center' valign='top'>" +
                        "                                <table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                        "                                    <tr style='padding-top:0;'>" +
                        "                                        <td align='center' valign='top'>" +
                        "                                            <table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                        "                                                <tr>" +
                        "                                                    <td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                        "                                                        <table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: #3498DB;'>" +
                        "                                                            <tr>" +
                        "                                                                <td align='center' valign='middle' class='buttonContent' style='padding-top:15px;padding-bottom:15px;padding-right:15px;padding-left:15px;'>" +
                        "                                                                    <a style='color:#FFFFFF;text-decoration:none;font-family:Helvetica,Arial,sans-serif;font-size:20px;line-height:135%;' href='" + domain + "' + ' target='_blank'>Confirm</a>" +
                        "                                                                </td>" +
                        "                                                            </tr>" +
                        "                                                        </table>" +
                        "                                                    </td>" +
                        "                                                </tr>" +
                        "                                            </table>" +
                        "                                        </td>" +
                        "                                    </tr>" +
                        "                                </table>" +
                        "                            </td>" +
                        "                        </tr>" +
                        "                    </table>" +
                        "                    <table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                        "                        <tr>" +
                        "                            <td align='center' valign='top'>" +
                        "                                <table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                        "                                    <tr>" +
                        "                                        <td align='center' valign='top'>" +
                        "                                            <table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                        "                                                <tr>" +
                        "                                                    <td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                        "                                                        <table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                        "                                                            <tr>" +
                        "                                                                <td valign='top' bgcolor='#E1E1E1'>" +
                        "                                                                    <div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                        "                                                                        <div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                        "                                                                    </div>" +
                        "                                                                </td>" +
                        "                                                            </tr>" +
                        "                                                        </table>" +
                        "                                                    </td>" +
                        "                                                </tr>" +
                        "                                            </table>" +
                        "                                        </td>" +
                        "                                    </tr>" +
                        "                                </table>" +
                        "                            </td>" +
                        "                        </tr>" +
                        "                    </table>" +
                        "                </td>" +
                        "                </tr>" +
                        "        </table>" +
                        "    </center>" +
                        "</body>" +
                        "</html>";

        return mailTemplate;
    }

    public static String activateHtmlTempl(String domain, boolean flag){

        String text = "approved";
        if(!flag){
            text = "disapproved";
        }
        String html = "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                "<meta name='format-detection' content='telephone=no' /> " +
                "<title>DataExo vendor activation email</title>" +
                "<style type='text/css'>" +
                "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                "table{border-collapse:collapse;}" +
                "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                "" +
                ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                "" +
                "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                ".flexibleImage{height:auto;}" +
                ".linkRemoveBorder{border-bottom:0 !important;}" +
                "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                "" +
                "body, #bodyTable{background-color:#E1E1E1;}" +
                "#emailHeader{background-color:#E1E1E1;}" +
                "#emailBody{background-color:#FFFFFF;}" +
                "#emailFooter{background-color:#E1E1E1;}" +
                ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                ".emailButton{background-color:#205478; border-collapse:separate;}" +
                ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                ".imageContentText {margin-top: 10px;line-height:0;}" +
                ".imageContentText a {line-height:0;}" +
                "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                "" +
                "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                "" +
                ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                "" +
                "" +
                "@media only screen and (max-width: 480px){" +
                "body{width:100% !important; min-width:100% !important;} " +
                "" +
                "table[id='emailHeader']," +
                "table[id='emailBody']," +
                "table[id='emailFooter']," +
                "table[class='flexibleContainer']," +
                "td[class='flexibleContainerCell'] {width:100% !important;}" +
                "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                "" +
                "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                "" +
                "" +
                "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                "" +
                "table[class='emailButton']{width:100% !important;}" +
                "td[class='buttonContent']{padding:0 !important;}" +
                "td[class='buttonContent'] a{padding:15px !important;}" +
                "" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                "}" +
                "" +
                "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                "" +
                "}" +
                "</style>" +
                "" +
                "</head>" +
                "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                "" +
                "<center style='background-color:#E1E1E1;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                "<tr>" +
                "<td align='center' valign='top' id='bodyCell'>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                "<tr style='height:50px;'>" +
                "<tr>" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top' class='textContent'>" +
                "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Vendor Registration</h1>" +
                "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>Your account " +text+ " by admin. .</h2>" ;

                if(flag) {
                    html += "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>Please click 'Login' button to login your account.</div>";
                }

                html += "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "<tr style='height:40px;'>" +
                "" +
                "</tr>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr style='padding-top:0;'>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" ;
                if(flag) {
                    html += "<table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: #3498DB;'>" +
                    "<tr>" +
                    "<td align='center' valign='middle' class='buttonContent' style='padding-top:15px;padding-bottom:15px;padding-right:15px;padding-left:15px;'>" +
                     "<a style='color:#FFFFFF;text-decoration:none;font-family:Helvetica,Arial,sans-serif;font-size:20px;line-height:135%;' href='" + domain + "/vendor/logout/" + "' target='_blank'>Login</a>" +
                    "</td>" +
                     "</tr>" +
                     "</table>" ;
                }


                 html +="" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td valign='top' bgcolor='#E1E1E1'>" +
                "" +
                "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                "" +
                "</div>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</center>" +
                "</body>" +
                "</html>";
        return html;
    }

    public static String getResetPasswordHTML(String url){
        String html = "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                "<meta name='format-detection' content='telephone=no' /> " +
                "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                "<style type='text/css'>" +
                "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                "table{border-collapse:collapse;}" +
                "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                "" +
                ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                "" +
                "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                ".flexibleImage{height:auto;}" +
                ".linkRemoveBorder{border-bottom:0 !important;}" +
                "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                "" +
                "body, #bodyTable{background-color:#E1E1E1;}" +
                "#emailHeader{background-color:#E1E1E1;}" +
                "#emailBody{background-color:#FFFFFF;}" +
                "#emailFooter{background-color:#E1E1E1;}" +
                ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                ".emailButton{background-color:#205478; border-collapse:separate;}" +
                ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                ".imageContentText {margin-top: 10px;line-height:0;}" +
                ".imageContentText a {line-height:0;}" +
                "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                "" +
                "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                "" +
                ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                "" +
                "" +
                "@media only screen and (max-width: 480px){" +
                "body{width:100% !important; min-width:100% !important;} " +
                "" +
                "table[id='emailHeader']," +
                "table[id='emailBody']," +
                "table[id='emailFooter']," +
                "table[class='flexibleContainer']," +
                "td[class='flexibleContainerCell'] {width:100% !important;}" +
                "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                "" +
                "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                "" +
                "" +
                "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                "" +
                "table[class='emailButton']{width:100% !important;}" +
                "td[class='buttonContent']{padding:0 !important;}" +
                "td[class='buttonContent'] a{padding:15px !important;}" +
                "" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1){" +
                "}" +
                "" +
                "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                "}" +
                "" +
                "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                "" +
                "}" +
                "</style>" +
                "" +
                "</head>" +
                "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                "" +
                "<center style='background-color:#E1E1E1;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                "<tr>" +
                "<td align='center' valign='top' id='bodyCell'>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                "<tr style='height:50px;'>" +
                "<tr>" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top' class='textContent'>" +
                "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo RESET PASSWORD</h1>" +
                "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>Please reset your password.</h2>" +
                "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>Please click 'Confirmation' button to reset your password.</div>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "<tr style='height:40px;'>" +
                "" +
                "</tr>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr style='padding-top:0;'>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td style='padding-top:0;' align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "" +
                "<table border='0' cellpadding='0' cellspacing='0' width='50%' class='emailButton' style='background-color: #3498DB;'>" +
                "<tr>" +
                "<td align='center' valign='middle' class='buttonContent' style='padding-top:15px;padding-bottom:15px;padding-right:15px;padding-left:15px;'>" +
                "<a style='color:#FFFFFF;text-decoration:none;font-family:Helvetica,Arial,sans-serif;font-size:20px;line-height:135%;' href='" + url + "' target='_blank'>Confirmation</a>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "" +
                "</table>" +
                "" +
                "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                "" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td align='center' valign='top'>" +
                "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                "<tr>" +
                "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                "<tr>" +
                "<td valign='top' bgcolor='#E1E1E1'>" +
                "" +
                "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                "<div>Copyright &#169; DataExo Inc.2017. All&nbsp;rights&nbsp;reserved.</div>" +
                "" +
                "</div>" +
                "" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</center>" +
                "</body>" +
                "</html>";
        return html;
    }
    public static String getCreditCardTypeByNumber(String creditCardNumber) {

        String regVisa = "^4[0-9]{12}(?:[0-9]{3})?$";
        String regMaster = "^5[1-5][0-9]{14}$";
        String regExpress = "^3[47][0-9]{13}$";
        String regDiners = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";
        String regDiscover = "^6(?:011|5[0-9]{2})[0-9]{12}$";
        String regJCB= "^(?:2131|1800|35\\d{3})\\d{11}$";

        if(creditCardNumber.matches(regVisa))
            return "VISA";
        if (creditCardNumber.matches(regMaster))
            return "MASTERCARD";
        if (creditCardNumber.matches(regExpress))
            return "AMEX";
        if (creditCardNumber.matches(regDiners))
            return "DINERS";
        if (creditCardNumber.matches(regDiscover))
            return "DISCOVER";
        if (creditCardNumber.matches(regJCB))
            return "JCB";
        return "UNKNOWN";
    }

    public static String getBasePath(){
        ClassLoader classLoader = UtilClass.class.getClassLoader();
        File file = new File(classLoader.getResource("application.yml").getFile());
        String filePath = file.getAbsolutePath();
        String basePath = filePath.substring(0 , filePath.length() - "application.yml".length());

        return basePath + "static/";
    }

    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}
