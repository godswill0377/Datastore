package com.dataexo.zblog.controller;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.*;
import com.google.gson.stream.JsonWriter;
import com.opencsv.CSVWriter;

import org.apache.log4j.Logger;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This controller is for download the json , xml , csv.
 * Create Time: 6/21
 * Createc By:
 */
@Controller
@RequestMapping("/api/v3")
public class ServiceControlller extends AbstractController {

    private static final Logger logger = Logger.getLogger(ServiceControlller.class);

    @Resource
    private Data_categoryService data_categoryService;

    @Resource
    private Data_setsService data_setsService;  //文章service
    @Resource
    private Data_sets_seriesService data_sets_seriesService;  //文章service

    @Resource
    private UserService userService;

    @Resource
    private TokenService tokenService;
    @Resource
    private PurchaseService purchaseService;

    @RequestMapping("/datasets_series/{series_id}")
    public void data_sets_series(@PathVariable String series_id, HttpServletResponse response) {

        Data_sets_series series = data_sets_seriesService.getData_sets_seriesById(Integer.parseInt(series_id));

        String url = series.getSource_url();
        JSONObject json = null, result = null;

        try {
            if(!"".equals(url)){
                json = UtilClass.readJsonFromUrl(url);
                result = UtilClass.build_dataseries_json(json, series);

                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS");
                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(result.toString());

                response.getWriter().flush();
                response.getWriter().close();
            }

        } catch (IOException e) {
            System.out.println("ServiceController.java data_sets_series " + e.getMessage());
            logger.error( "ServiceController.java data_sets_series", e);
           // e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);

        } catch (JSONException e) {
         //   e.printStackTrace();
            logger.error(  e);
            System.out.println("ServiceController.java data_sets_series " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    @RequestMapping("/update_chart")
    public void update_chart(Data_chart chart, HttpServletResponse response) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = dateFormat.format(date);
        chart.setUpdate_date(today);
        data_sets_seriesService.updateChart_Diagram(chart);
    }

    @RequestMapping("/datatables/{type}/{series_id}")
    public void download_datatables(@PathVariable String type, @PathVariable String series_id, HttpServletResponse response) {

        Data_sets_series series = data_sets_seriesService.getData_sets_seriesById(Integer.parseInt(series_id));

        String uploadPath = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext().getRealPath("") + File.separator;
        String path = uploadPath + "/" + "table_" + series.getCode().replace('/', '-') + "." + type;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                if (type.equals("json")) {
                    String url = series.getSource_url();
                    JSONObject json = null, result = null;

                    json = UtilClass.readJsonFromUrl(url);

                    FileWriter file1 = new FileWriter(path);
                    file1.write(json.toString());
                    file1.flush();
                    file1.close();
                }

                if (type.equals("xml")) {
                    String url = series.getSource_url();
                    JSONObject json = null, result = null;

                    json = UtilClass.readJsonFromUrl(url);

                    FileWriter file1 = new FileWriter(path);
                    file1.write(XML.toString(json));
                    file1.flush();
                    file1.close();
                }

                if (type.equals("csv")) {
                    List<String[]> data = new ArrayList<String[]>();

                    String url = series.getSource_url();
                    JSONObject json = null, result = null;

                    json = UtilClass.readJsonFromUrl(url);

                    result = json.getJSONObject("datatable");
                    JSONArray filed_ar = result.getJSONArray("columns");

                    String[] record = new String[filed_ar.length()];
                    String[] datatype = new String[filed_ar.length()];

                    for (int j = 0; j < filed_ar.length(); j++) {
                        JSONObject obj = filed_ar.getJSONObject(j);
                        record[j] = obj.getString("name");
                        datatype[j] = obj.getString("type");
                    }
                    data.add(record);

                    JSONArray dataset_ar = result.getJSONArray("data");

                    for (int i = 0; i < dataset_ar.length() - 1; i++) {
                        JSONArray ar = dataset_ar.getJSONArray(i);
                        record = new String[filed_ar.length()];
                        for (int j = 0; j < filed_ar.length(); j++) {
                            record[j] =UtilClass.getDataFromJson( datatype[j] , ar , j);
                        }
                        data.add(record);
                    }

                    FileWriter file1 = new FileWriter(path);
                    //using custom delimiter and quote character
                    CSVWriter csvWriter = new CSVWriter(file1, ',', '\'');

                    csvWriter.writeAll(data);
                    csvWriter.close();
                    file1.close();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            logger.error(  e);
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);

        } catch (JSONException e) {
            e.printStackTrace();
            logger.error(  e);
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        } finally {

        }

        ///// download file
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            response.setContentType("application/octet-stream");

            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + file.getName() + "\"");
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            logger.error(  e);
        }
    }

    @RequestMapping("/datasets_series/{type}/{series_id}")
    public void download_datasets_series(@PathVariable String type, @PathVariable String series_id, HttpServletResponse response) {
        Data_sets_series series = data_sets_seriesService.getData_sets_seriesById(Integer.parseInt(series_id));

        String uploadPath = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext().getRealPath("") + File.separator;
        String path = uploadPath + "/" + series.getCode().replace('/', '-') + "." + type;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                if (type.equals("json")) {
                    String url = series.getSource_url();
                    JSONObject json = null, result = null;

                    json = UtilClass.readJsonFromUrl(url);
                    result = UtilClass.build_dataseries_json(json, series);
                    FileWriter file1 = new FileWriter(path);
                    file1.write(result.toString());
                    file1.flush();
                    file1.close();
                }

                if (type.equals("xml")) {
                    String url = series.getSource_url();
                    JSONObject json = null, result = null;

                    json = UtilClass.readJsonFromUrl(url);
                    result = UtilClass.build_dataseries_json(json, series);

                    FileWriter file1 = new FileWriter(path);
                    file1.write(XML.toString(result));
                    file1.flush();
                    file1.close();
                }

                if (type.equals("csv")) {
                    String url = series.getSource_url();
                    JSONObject json = null, result = null;

                    json = UtilClass.readJsonFromUrl(url);
                    result = UtilClass.build_dataseries_json(json, series);

                    JSONObject dataset = result.getJSONObject("dataset");
                    JSONArray dataset_ar = dataset.getJSONArray("data");
                    JSONArray dataset_name = dataset.getJSONArray("column_names");

                    JSONArray docs = new JSONArray();
                    //  docs.put(dataset_name);
                    docs.put(dataset_ar);

                    FileWriter file1 = new FileWriter(path);

                    //using custom delimiter and quote character
                    CSVWriter csvWriter = new CSVWriter(file1, ',', '\'');

                    List<String[]> data = new ArrayList<String[]>();

                    String[] record = new String[dataset_name.length()];
                    for (int j = 0; j < dataset_name.length(); j++) {
                        record[j] = dataset_name.getString(j);
                    }
                    data.add(record);
                    for (int i = 0; i < dataset_ar.length() - 1; i++) {
                        JSONArray ar = dataset_ar.getJSONArray(i);

                        record = new String[dataset_name.length()];
                        for (int j = 0; j < dataset_name.length(); j++) {
                            if (j == 0) {
                                record[j] = ar.getString(j);
                            } else {
                                record[j] = Double.toString(ar.getDouble(j));
                            }
                        }
                        data.add(record);
                    }

                    csvWriter.writeAll(data);
                    csvWriter.close();
                    file1.close();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            logger.error(  e);
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);

        } catch (JSONException e) {
            e.printStackTrace();
            logger.error(  e);
        } finally {

        }

        ///// download file
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            response.setContentType("application/octet-stream");

            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + file.getName() + "\"");
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            logger.error(  e);
        }

    }

    @RequestMapping("/datasets/{type}/{code}")
    public void download_datasets(@PathVariable String type, @PathVariable String code, HttpServletResponse response) {

        Data_sets data_sets = data_setsService.getData_setsByCode(code);
        Pager<Data_sets_series> pager = new Pager<Data_sets_series>();
        pager.setStart(1);
        pager.setLimit(100);
        pager.setParent_code(code);
        pager.setSearch_str("");
        List<Data_sets_series> list = data_sets_seriesService.loadData_sets_series(pager, null);

        String uploadPath = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext().getRealPath("") + File.separator;


        String path = uploadPath + "/" + code + ".";

        if (type.equals("json")) {
            path += "json";
            /// generate json file
            JsonWriter jsonWriter = null;
            try {
                File file = (new File(path));
                if (!file.exists()) {
                    file.createNewFile();
                    jsonWriter = new JsonWriter(new FileWriter(path));
                    jsonWriter.beginObject();
                    jsonWriter.name("datasets");

                    jsonWriter.beginArray();

                    for (int i = 0; i < list.size(); i++) {
                        jsonWriter.beginObject();
                        Data_sets_series series = list.get(i);

                        jsonWriter.name("id");
                        jsonWriter.value(data_sets.getId());

                        jsonWriter.name("database_code");
                        jsonWriter.value(data_sets.getCode());

                        jsonWriter.name("dataset_code");
                        jsonWriter.value(series.getCode());

                        jsonWriter.name("type");
                        jsonWriter.value(data_sets.getApi());

                        jsonWriter.name("name");
                        jsonWriter.value(series.getName());

                        jsonWriter.name("description");
                        jsonWriter.value(series.getDescription());

                        jsonWriter.name("refreshed_at");
                        jsonWriter.value(series.getLatest_update_date());

                        jsonWriter.name("premium");
                        if (data_sets.getPrice_model_id()== 2)
                            jsonWriter.value(true);
                        else
                            jsonWriter.value(false);

                        jsonWriter.endObject();
                    }

                    jsonWriter.endArray();

                    jsonWriter.endObject();
                }
            } catch (IOException e) {

                logger.error(  e);
            } finally {
                try {
                    if (jsonWriter != null)
                        jsonWriter.close();
                } catch (IOException e) {
                    logger.error(  e);
                }
            }
        }

        if (type.equals("csv")) {
            path += "csv";
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new File(path));
                StringBuilder sb = new StringBuilder();

                //
                sb.append("id");
                sb.append(',');
                sb.append("database_code");
                sb.append(',');
                sb.append("dataset_code");
                sb.append(',');
                sb.append("type");
                sb.append(',');
                sb.append("name");
                sb.append(',');
                sb.append("description");
                sb.append(',');
                sb.append("refreshed_at");
                sb.append(',');
                sb.append("premium");
                sb.append('\n');

                for (int i = 0; i < list.size(); i++) {

                    Data_sets_series series = list.get(i);

                    sb.append(data_sets.getId());
                    sb.append(',');
                    sb.append(data_sets.getCode());
                    sb.append(',');
                    sb.append(series.getCode());
                    sb.append(',');
                    sb.append(data_sets.getApi());
                    sb.append(',');
                    sb.append(series.getName().replaceAll(","," "));
                    sb.append(',');
                    sb.append(series.getDescription().replaceAll(","," "));
                    sb.append(',');
                    sb.append(series.getLatest_update_date());
                    sb.append(',');
                    if (data_sets.getPrice_model_id()== 2)
                        sb.append("true");
                    else
                        sb.append("false");
                    sb.append('\n');
                }

                pw.write(sb.toString());
                pw.close();

            } catch (IOException e) {
                logger.error(  e);
            } finally {
                if (pw != null)
                    pw.close();
            }
        }
        ///// download file
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            response.setContentType("application/octet-stream");

            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + file.getName() + "\"");
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            logger.error(  e);
        }
    }

    @RequestMapping("/dataset_download/{id}/{token}")
    public void dataset_download_purchase(@PathVariable String id, @PathVariable String token, HttpServletResponse response) {

        Token tokenmodel = tokenService.getByToken(token);
        Purchase purchase = new Purchase();
        purchase.setId(Integer.parseInt(id));
        purchase = purchaseService.selectPurchase(purchase);

        if(tokenmodel != null && purchase != null){
            try {
                    long current = System.currentTimeMillis();
                    if (current > Long.parseLong(tokenmodel.getExpire())) {
                        response.sendRedirect("/error_download");
                    } else {
                        Data_sets data_sets = data_setsService.getData_setsById(purchase.getDataset_id());

                       /* URL oracle = new URL(data_sets.getDownload_url());
                        URLConnection yc = oracle.openConnection();
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                yc.getInputStream()));

                        char[] buffer = new char[1000];
                        in.read(buffer);
                        String url = new String (buffer);
                        in.close();*/
                       String url = data_sets.getDownload_url();

                        if (url == ""){
                            response.sendRedirect("/error");
                        }
                        else {
                            URL redshitUrl = new URL(url);
                            URLConnection redshitCon = redshitUrl.openConnection();

                            String contexttype = redshitCon.getContentType();

                            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
                            MimeType ext = allTypes.forName(contexttype);

                            String fileExt = ext.getExtension();

                            if(fileExt.indexOf(".txt") >= 0) {

                                InputStream stream = redshitCon.getInputStream();
                                response.setContentType("application/octet-stream");
                                response.setHeader("Content-Disposition", "attachment;filename=" + data_sets.getName() + fileExt);

                                ServletOutputStream out = response.getOutputStream();

                                byte[] outputByte = new byte[4096];

                                while (stream.read(outputByte, 0, 4096) != -1) {
                                    out.write(outputByte, 0, 4096);
                                }

                                stream.close();

                                out.flush();
                                out.close();
                            }
                            else{
                                response.sendRedirect(url);
                            }
                        }
                    }
            } catch (Exception e) {
                logger.error(  e);
            }
        }
        else{
            try {
                response.sendRedirect("/error_download");
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(  e);
            }
        }
    }



    @RequestMapping("/database/{code}")
    public void download_database(@PathVariable String code, HttpServletResponse response) {

        Data_sets data_sets = data_setsService.getData_setsByCode(code);
        Pager<Data_sets_series> pager = new Pager<Data_sets_series>();
        pager.setStart(1);
        pager.setLimit(100000);
        pager.setParent_code(code);
        pager.setSearch_str("");
        List<Data_sets_series> list = data_sets_seriesService.loadData_sets_series(pager, null);

        String uploadPath = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext().getRealPath("") + File.separator;

        String path = uploadPath + "/" + code + ".";

        path += "csv";
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(path));
            StringBuilder sb = new StringBuilder();

            //
            sb.append("dataset_code");
            sb.append(',');
            sb.append("name");
            sb.append('\n');

            for (int i = 0; i < list.size(); i++) {

                Data_sets_series series = list.get(i);
                sb.append(series.getCode());
                sb.append(',');
                sb.append(series.getName().replaceAll(","," "));
                sb.append('\n');
            }

            pw.write(sb.toString());
            pw.close();

        } catch (IOException e) {
            logger.error(  e);
        } finally {
            if (pw != null)
                pw.close();
        }

        ///// download file
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            response.setContentType("application/octet-stream");

            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + file.getName() + "\"");
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            logger.error(  e);

        }
    }

    @RequestMapping("/free_dataset_download/{id}")
    public void free_dataset_download(@PathVariable String id ,HttpServletResponse response) {
        try {

                Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(id));
                if(data_sets == null){
                    response.sendRedirect("/error");
                }
                else if(data_sets.getPrice_model_id() != 1){
                    response.sendRedirect("/error");
                }
                else {
                   /* URL oracle = new URL(data_sets.getDownload_url());
                    URLConnection yc = oracle.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            yc.getInputStream()));

                    char[] buffer = new char[1000];
                    in.read(buffer);
                    String url = new String(buffer);
                    in.close();
                    */
                   String url = data_sets.getDownload_url();

                   if (url == ""){
                       response.sendRedirect("/error");
                   }
                   else {
                       URL redshitUrl = new URL(url);
                       URLConnection redshitCon = redshitUrl.openConnection();

                       String contexttype = redshitCon.getContentType();

                       MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
                       MimeType ext = allTypes.forName(contexttype);

                       String fileExt = ext.getExtension();

                       if(fileExt.indexOf(".txt") >= 0) {

                           InputStream stream = redshitCon.getInputStream();
                           response.setContentType("application/octet-stream");
                           response.setHeader("Content-Disposition", "attachment;filename=" + data_sets.getName() + fileExt);

                           ServletOutputStream out = response.getOutputStream();

                           byte[] outputByte = new byte[4096];

                           while (stream.read(outputByte, 0, 4096) != -1) {
                               out.write(outputByte, 0, 4096);
                           }

                           stream.close();

                           out.flush();
                           out.close();
                       }
                       else{
                           response.sendRedirect(url);
                       }
                   }

                }
        } catch (Exception e) {
            logger.error(  e);
        }
    }

}