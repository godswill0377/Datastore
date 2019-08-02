package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;



@Alias("user")
public class User implements Serializable {

    private long id;
    private String username;    //用户名
    private String token;
    private String password;    //密码
    private String email;
    private String createTime;    //创建时间
    private String role;
    private int membership;
    private int up_membership;
    private double balance;
    private String expire_date;
    private int activate;
    private String apiKey;
    private String customer_id;
    private long vendor_id;

    private String legal_name;
    private String address;
    private String city;
    private String state_province;
    private String country;
    private String zip_postal;
    private String business_name;
    private String website_url;
    private String mobile_num;
    private String card_number;
    private String expiration;
    private String cvc_num;

    private String cur_password;
    private String new_password;
    private String new_confirm_password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public int getMembership() {
        return membership;
    }

    public void setMembership(int membership) {
        this.membership = membership;
    }

    public int getUp_membership() {
        return up_membership;
    }

    public void setUp_membership(int up_membership) {
        this.up_membership = up_membership;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(long vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public int getActivate() {
        return activate;
    }

    public void setActivate(int activate) {
        this.activate = activate;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_province() {
        return state_province;
    }

    public void setState_province(String state_province) {
        this.state_province = state_province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip_postal() {
        return zip_postal;
    }

    public void setZip_postal(String zip_postal) {
        this.zip_postal = zip_postal;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    public String getMobile_num() {
        return mobile_num;
    }

    public void setMobile_num(String mobile_num) {
        this.mobile_num = mobile_num;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getCvc_num() {
        return cvc_num;
    }

    public void setCvc_num(String cvc_num) {
        this.cvc_num = cvc_num;
    }

    public String getCur_password() {
        return cur_password;
    }

    public void setCur_password(String cur_password) {
        this.cur_password = cur_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getNew_confirm_password() {
        return new_confirm_password;
    }

    public void setNew_confirm_password(String new_confirm_password) {
        this.new_confirm_password = new_confirm_password;
    }
}