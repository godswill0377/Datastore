package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

@Alias("vendors")
public class Vendors implements Serializable {

        private long id;

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
        private String stripe_acc_id;
        private int stripe_verify;
        private String first_name;
        private String last_name;
        private String ssn_last_4;
        private String personal_id_number;
        private String document;
        private String dob_year;
        private String dob_month;
        private String dob_day;
        private String routing_last_4;
        private String card_last_4;
        private String reject_reason;


        private String source;
        private String routing_number;
        private String account_number;
        private String card_expiry;
        private String card_cvc;


        public long getId() {
                return id;
        }

        public void setId(long id) {
                this.id = id;
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

        public String getStripe_acc_id() {
                return stripe_acc_id;
        }

        public void setStripe_acc_id(String stripe_acc_id) {
                this.stripe_acc_id = stripe_acc_id;
        }

        public int getStripe_verify() {
                return stripe_verify;
        }

        public void setStripe_verify(int stripe_verify) {
                this.stripe_verify = stripe_verify;
        }

        public String getFirst_name() {
                return first_name;
        }

        public void setFirst_name(String first_name) {
                this.first_name = first_name;
        }

        public String getLast_name() {
                return last_name;
        }

        public void setLast_name(String last_name) {
                this.last_name = last_name;
        }

        public String getSsn_last_4() {
                return ssn_last_4;
        }

        public void setSsn_last_4(String ssn_last_4) {
                this.ssn_last_4 = ssn_last_4;
        }

        public String getPersonal_id_number() {
                return personal_id_number;
        }

        public void setPersonal_id_number(String personal_id_number) {
                this.personal_id_number = personal_id_number;
        }

        public String getDocument() {
                return document;
        }

        public void setDocument(String document) {
                this.document = document;
        }

        public String getDob_year() {
                return dob_year;
        }

        public void setDob_year(String dob_year) {
                this.dob_year = dob_year;
        }

        public String getDob_month() {
                return dob_month;
        }

        public void setDob_month(String dob_month) {
                this.dob_month = dob_month;
        }

        public String getDob_day() {
                return dob_day;
        }

        public void setDob_day(String dob_day) {
                this.dob_day = dob_day;
        }

        public String getRouting_last_4() {
                return routing_last_4;
        }

        public void setRouting_last_4(String routing_last_4) {
                this.routing_last_4 = routing_last_4;
        }

        public String getCard_last_4() {
                return card_last_4;
        }

        public void setCard_last_4(String card_last_4) {
                this.card_last_4 = card_last_4;
        }

        public String getReject_reason() {
                return reject_reason;
        }

        public void setReject_reason(String reject_reason) {
                this.reject_reason = reject_reason;
        }

        public String getSource() {
                return source;
        }

        public void setSource(String source) {
                this.source = source;
        }

        public String getRouting_number() {
                return routing_number;
        }

        public void setRouting_number(String routing_number) {
                this.routing_number = routing_number;
        }

        public String getAccount_number() {
                return account_number;
        }

        public void setAccount_number(String account_number) {
                this.account_number = account_number;
        }

        public String getCard_expiry() {
                return card_expiry;
        }

        public void setCard_expiry(String card_expiry) {
                this.card_expiry = card_expiry;
        }

        public String getCard_cvc() {
                return card_cvc;
        }

        public void setCard_cvc(String card_cvc) {
                this.card_cvc = card_cvc;
        }
}
