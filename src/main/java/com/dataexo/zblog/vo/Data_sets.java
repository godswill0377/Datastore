package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("data_sets")
public class Data_sets implements Serializable {


    private Integer id;
    private String name;

    private String asset_class_name;

    private Integer data_category_id;
    private String data_category_name;


    private Integer data_type_id;
    private String data_type_name;

    private Integer region_id;
    private String region_name;

    private Integer publisher_id;
    private String publisher_name;

    private String source_url;
    private String icon;
    private String description;
    private String code;
    private String api;

    private float onetime_price;
    private float download_price;

    private int onetime_expires;
    private int download_expires;
    private int purchase_id;
    private String token;

    private String order_date;
    private String download_url;

    private String download_url_update_at;

    private String bucket_name;
    private String s3_file_key;

    private String expries_on_date;
    private int has_series;
    private String table_name;
    private String embed_url;

    private String embed_url_chart;
    private String embed_url_datagrid;

    private Integer limitation;

    private long  vendor_id ;
    private String  vendor_name ;
    private String visiting_nums;
    private String download_nums;
    private int sub_available;

    public int getSub_available() {
        return sub_available;
    }

    public void setSub_available(int sub_available) {
        this.sub_available = sub_available;
    }


    public Integer getLimitation() {
        return limitation;
    }

    public void setLimitation(Integer limitation) {
        this.limitation = limitation;
    }

    public String getEmbed_url_chart() {
        return embed_url_chart;
    }

    public void setEmbed_url_chart(String embed_url_chart) {
        this.embed_url_chart = embed_url_chart;
    }

    public String getEmbed_url_datagrid() {
        return embed_url_datagrid;
    }

    public void setEmbed_url_datagrid(String embed_url_datagrid) {
        this.embed_url_datagrid = embed_url_datagrid;
    }



    public String getEmbed_url() {
        return embed_url;
    }

    public void setEmbed_url(String embed_url) {
        this.embed_url = embed_url;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getSchema_name() {
        return schema_name;
    }

    public void setSchema_name(String schema_name) {
        this.schema_name = schema_name;
    }

    private String schema_name;



    public int getHas_series() {
        return has_series;
    }

    public void setHas_series(int has_series) {
        this.has_series = has_series;
    }

    public String getDescription_data() {
        return description_data;
    }

    public void setDescription_data(String description_data) {
        this.description_data = description_data;
    }

    private String description_data;
    public Integer getPrice_model_id() {
        return price_model_id;
    }

    public void setPrice_model_id(Integer price_model_id) {
        this.price_model_id = price_model_id;
    }

    public Integer getAsset_class_id() {
        return asset_class_id;
    }

    public void setAsset_class_id(Integer asset_class_id) {
        this.asset_class_id = asset_class_id;
    }

    public Integer getData_type_id() {
        return data_type_id;
    }

    public void setData_type_id(Integer data_type_id) {
        this.data_type_id = data_type_id;
    }

    public Integer getRegion_id() {
        return region_id;
    }

    public void setRegion_id(Integer region_id) {
        this.region_id = region_id;
    }

    public Integer getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(Integer publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }


    private Integer price_model_id;

    public String getPrice_model_name() {
        return price_model_name;
    }

    public void setPrice_model_name(String price_model_name) {
        this.price_model_name = price_model_name;
    }

    private String price_model_name;

    private Integer asset_class_id;

    public String getAsset_class_name() {
        return asset_class_name;
    }

    public void setAsset_class_name(String asset_class_name) {
        this.asset_class_name = asset_class_name;
    }

    public String getData_type_name() {
        return data_type_name;
    }

    public void setData_type_name(String data_type_name) {
        this.data_type_name = data_type_name;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "data_sets{" +
                "id=" + id +
                ", price_model_id=" + price_model_id+
                ", name='" + name+ '\'' +
                ", price_model_id=" + price_model_id+
                ", asset_class_id=" + asset_class_id+
                ", data_type_id=" + data_type_id+
                ", region_id=" + region_id+
                ", publisher_id=" + publisher_id+

                ", source_url='" + source_url + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", api='" + api + '\'' +

                '}';
    }


    public String getData_category_name() {
        return data_category_name;
    }

    public void setData_category_name(String data_category_name) {
        this.data_category_name = data_category_name;
    }

    public Integer getData_category_id() {
        return data_category_id;
    }

    public void setData_category_id(Integer data_category_id) {
        this.data_category_id = data_category_id;
    }

    public float getOnetime_price() {
        return onetime_price;
    }

    public void setOnetime_price(float onetime_price) {
        this.onetime_price = onetime_price;
    }

    public float getDownload_price() {
        return download_price;
    }

    public void setDownload_price(float download_price) {
        this.download_price = download_price;
    }

    public int getOnetime_expires() {
        return onetime_expires;
    }

    public void setOnetime_expires(int onetime_expires) {
        this.onetime_expires = onetime_expires;
    }

    public int getDownload_expires() {
        return download_expires;
    }

    public void setDownload_expires(int download_expires) {
        this.download_expires = download_expires;
    }

    public int getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(int purchase_id) {
        this.purchase_id = purchase_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getExpries_on_date() {
        return expries_on_date;
    }

    public void setExpries_on_date(String expries_on_date) {
        this.expries_on_date = expries_on_date;
    }


    public String getDownload_url_update_at() {
        return download_url_update_at;
    }

    public void setDownload_url_update_at(String download_url_update_at) {
        this.download_url_update_at = download_url_update_at;
    }

    public String getBucket_name() {
        return bucket_name;
    }

    public void setBucket_name(String bucket_name) {
        this.bucket_name = bucket_name;
    }

    public String getS3_file_key() {
        return s3_file_key;
    }

    public void setS3_file_key(String s3_file_key) {
        this.s3_file_key = s3_file_key;
    }

    public long getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(long vendor_id) {
        this.vendor_id = vendor_id;
    }


    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVisiting_nums() {
        return visiting_nums;
    }

    public void setVisiting_nums(String visiting_nums) {
        this.visiting_nums = visiting_nums;
    }

    public String getDownload_nums() {
        return download_nums;
    }

    public void setDownload_nums(String download_nums) {
        this.download_nums = download_nums;
    }
}
