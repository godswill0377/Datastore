package com.dataexo.zblog.service.impl;


import com.dataexo.zblog.service.StripeService;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.Vendors;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    private static final Logger logger = Logger.getLogger(StripeServiceImpl.class);
    @Value("${strip.apikey}")
    private String apikey;

    @Override
    public Token createToken(String number, String expiry, String cvc
            , String address_city, String address_zip, String address_state,String address_line1){

        Token token = new Token();
        Stripe.apiKey = apikey;

        Map<String, Object> tokenParams = new HashMap<String, Object>();
        Map<String, Object> cardParams = new HashMap<String, Object>();
        cardParams.put("number", number);

        String[] ar = expiry.split("/");
        cardParams.put("exp_month", Integer.parseInt(ar[0]));
        cardParams.put("exp_year", Integer.parseInt("20" + ar[1]));

        cardParams.put("cvc", cvc);
        cardParams.put("address_city", address_city);
        cardParams.put("address_country", address_city);

        cardParams.put("address_zip", address_zip);
        cardParams.put("address_state", address_state);
        cardParams.put("address_line1", address_line1);

        tokenParams.put("card", cardParams);

        try {
            token = Token.create(tokenParams);

        } catch (AuthenticationException e) {
            logger.error( e);
            return  null;
        } catch (InvalidRequestException e) {
            logger.error( e);
            return  null;
        } catch (APIConnectionException e) {
            logger.error( e);
            return  null;

        } catch (CardException e) {
            logger.error( e);

            return  null;
        } catch (APIException e) {
            logger.error( e);
            return  null;
        }

        return  token;
    }

    @Override
    public String doPayment(String token , float amount, String email){

        Stripe.apiKey = apikey;

        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("source", token);
        customerParams.put("email", email);

        try {
            Customer customer = Customer.create(customerParams);

            Map<String, Object> chargeMap = new HashMap<String, Object>();

            int amt = (int)(amount * 100);
            chargeMap.put("amount", amt);
            chargeMap.put("currency", "usd");
          //  chargeMap.put("description", "Charge for matthew.thompson@example.com");

            String customerId = customer.getId();
            chargeMap.put("customer", customerId);

            RequestOptions requestOptions = (new RequestOptions.RequestOptionsBuilder()).setApiKey(apikey).build();

            Charge createdCharge = new Charge();
            try {
                createdCharge = Charge.create(chargeMap, requestOptions);
                if(createdCharge.getStatus().equals("succeeded")){
                    return "success";
                }
            } catch (StripeException e) {
                logger.error( e);
                return e.getMessage();
            }

        } catch (Exception e) {
            logger.error( e);
            return e.getMessage();
        }

        return  "error";
    }

    /*
    @Override
    public  Customer subscriptionPlan(String token , String email, String plan){
        Stripe.apiKey = apikey;

        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("source", token);
        customerParams.put("plan", plan);
        customerParams.put("email", email);

        try {
            Customer customer = Customer.create(customerParams);

            return customer;
        } catch (AuthenticationException e) {
            return null;

        } catch (InvalidRequestException e) {
            return null;

        } catch (APIConnectionException e) {
            return null;

        } catch (CardException e) {
            return null;

        } catch (APIException e) {
            return null;
        }
    }
    */

    @Override
    public  Subscription subscriptionPlan( String plan, String customer_id){
        try {
            Stripe.apiKey = apikey;

            Map<String, Object> item = new HashMap<String, Object>();
            item.put("plan", plan);

            Map<String, Object> items = new HashMap<String, Object>();
            items.put("0", item);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("customer", customer_id);
            params.put("items", items);

           Subscription subscription =  Subscription.create(params);

            return subscription;
        } catch (AuthenticationException e) {
            logger.error( e);
            return null;

        } catch (InvalidRequestException e) {
            logger.error( e);
            return null;

        } catch (APIConnectionException e) {
            logger.error( e);
            return null;

        } catch (CardException e) {
            logger.error( e);
            return null;

        } catch (APIException e) {
            logger.error( e);
            return null;
        }
    }

    @Override
    public Customer createCustomer(String email) {

        Stripe.apiKey = apikey;

        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", email);
        Customer customer = null;
        try {
            customer = Customer.create(customerParams);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.error("Signup/create customer error");
            logger.error(e.getMessage());
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            logger.error("Signup/create customer error");
            logger.error(e.getMessage());
        } catch (APIConnectionException e) {
            e.printStackTrace();
            logger.error("Signup/create customer error");
            logger.error(e.getMessage());
        } catch (CardException e) {
            e.printStackTrace();
            logger.error("Signup/create customer error");
            logger.error(e.getMessage());
        } catch (APIException e) {
            e.printStackTrace();
            logger.error("Signup/create customer error");
            logger.error(e.getMessage());
        }
        return customer;
    }


    @Override
    public Card attachCard(String number, String expiry, String cvc, String customerId) {

        Stripe.apiKey = apikey;

        Customer customer = retriveCustomer(customerId);

        Card card = null;
        try {
            Token token = createToken(number, expiry , cvc , "dataexo", "dataexo", "dataexo", "dataexo");
            if(token == null)
                return null;
            card = token.getCard();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("source", token.getId());
            customer.getSources().create(params);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.error("attach card to existing customer error");
            logger.error(e.getMessage());
            return card;
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            logger.error("attach card to existing customer error");
            logger.error(e.getMessage());
            return card;

        } catch (APIConnectionException e) {
            e.printStackTrace();
            logger.error("attach card to existing customer error");
            logger.error(e.getMessage());
            return card;

        } catch (CardException e) {
            e.printStackTrace();
            logger.error("attach card to existing customer error");
            logger.error(e.getMessage());
            return card;

        } catch (APIException e) {
            e.printStackTrace();
            logger.error("attach card to existing customer error");
            logger.error(e.getMessage());
            return card;

        }

        return  card;
    }


    @Override
    public Customer retriveCustomer(String customerId) {

        Stripe.apiKey = apikey;

        Customer customer = new Customer();
        try {
            customer = Customer.retrieve(customerId, apikey);

        } catch (AuthenticationException e) {
            logger.error( e);
            return null;

        } catch (InvalidRequestException e) {
            logger.error( e);
            return null;

        } catch (APIConnectionException e) {
            logger.error( e);
            return null;

        } catch (CardException e) {
            logger.error( e);
            return null;

        } catch (APIException e) {
            logger.error( e);
            return null;
        }

        return customer;
    }

    @Override
    public String cancelCustomer(String customerId ) {

        Stripe.apiKey = apikey;

        Customer customer =  retriveCustomer(customerId);

        try {
            customer.cancelSubscription();
            return "success";
        } catch (AuthenticationException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (APIConnectionException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (CardException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (APIException e) {
            logger.error( e);
            e.printStackTrace();
        }
        return "error";
    }

    @Override
    public String continueSubscription(String subscription_id) {
        Stripe.apiKey = apikey;
        Subscription subscription = retrieveSubscription(subscription_id);
        if(subscription == null){
            return "error";
        }
        Map<String, Object> item = new HashMap<>();
        item.put("id", subscription.getSubscriptionItems().getData().get(0).getId());
        item.put("plan", subscription.getPlan().getId());

        Map<String, Object> items = new HashMap<>();
        items.put("0", item);

        Map<String, Object> params = new HashMap<>();
        params.put("cancel_at_period_end", false);
        try {
            subscription.update(params);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (APIConnectionException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (CardException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (APIException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        }
        return subscription.getId();
    }



    //todo change this also and everything is completed from my side
    @Override
    public String continueMembership(String customerId) {
        Stripe.apiKey = apikey;
        Customer customer =  retriveCustomer(customerId);
        if(customer == null){
            return "error";
        }

        if (customer.getSubscriptions().getData().size() < 2) {
            Subscription currentSubscription = customer.getSubscriptions().getData().get(0);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("plan", currentSubscription.getPlan().getId());
            try {
                currentSubscription.update(params);
            } catch (AuthenticationException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (APIConnectionException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (CardException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (APIException e) {
                logger.error( e);
                e.printStackTrace();
            }

            return customerId;
        } else {
            try {
                Subscription currentSubscription = customer.getSubscriptions().getData().get(1);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("plan", currentSubscription.getPlan().getId());
                currentSubscription.update(params);

                Subscription upcomingSubscription = customer.getSubscriptions().getData().get(0);
                Map<String, Object> cancelParams = new HashMap<>();
                cancelParams.put("at_period_end", false);
                upcomingSubscription.cancel(cancelParams);

                return customerId;
            } catch (AuthenticationException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (APIConnectionException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (CardException e) {
                logger.error( e);
                e.printStackTrace();
            } catch (APIException e) {
                logger.error( e);
                e.printStackTrace();
            }
        }
        return "error";
    }


    @Override
    public Charge doPaymentByCustomer(String customerId, String currency, float amount) {
        Stripe.apiKey = apikey;


        Map<String, Object> customerParams = new HashMap<>();

        int amt = (int)(amount * 100);

        customerParams.put("amount", amt);
        customerParams.put("currency", currency);
        customerParams.put("customer", customerId);
        customerParams.put("description", "Dataset Purchase payment");
        try {
            Charge charge = Charge.create(customerParams);
            return charge;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.error("doPaymentByCustomer error");
            logger.error(e.getMessage());
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            logger.error("doPaymentByCustomer error");
            logger.error(e.getMessage());
        } catch (APIConnectionException e) {
            e.printStackTrace();
            logger.error("doPaymentByCustomer error");
            logger.error(e.getMessage());
        } catch (CardException e) {
            e.printStackTrace();
            logger.error("doPaymentByCustomer error");
            logger.error(e.getMessage());
        } catch (APIException e) {
            e.printStackTrace();
            logger.error("doPaymentByCustomer error");
            logger.error(e.getMessage());
        }
        return null;

    }
    @Override
    public String upgradeSubscription(String subscriptionId, String plan, boolean upDownOrCancel, boolean upFree) {
        Stripe.apiKey = apikey;
        Subscription subscription = retrieveSubscription(subscriptionId);
        try {
            long expiry = subscription.getCurrentPeriodEnd();
            Map<String, Object> cancelParams = new HashMap<>();
            if (upDownOrCancel) {
                cancelParams.put("at_period_end", false);

            }else {
                cancelParams.put("at_period_end", true);
            }
            subscription.cancel(cancelParams);
            if(upDownOrCancel) {
                Map<String, Object> item = new HashMap<>();
                item.put("plan", plan);

                Map<String, Object> items = new HashMap<>();
                items.put("0", item);

                Map<String, Object> params = new HashMap<>();
                params.put("customer", subscription.getCustomer());
                params.put("items", items);
                subscription = subscription.create(params);
            }
            return subscription.getId();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (APIConnectionException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (CardException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        } catch (APIException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return "Error: " + e.getMessage();
        }
//        return "error";
    }
        @Override
    public String upgradeCustomer(String customerId, String plan, boolean upOrDown, boolean upFree) {
        Stripe.apiKey = apikey;
        Customer customer =  retriveCustomer(customerId);

        try {
            if (customer == null || customer.getSubscriptions().getData().size() == 0) {
                Map<String, Object> updateParams = new HashMap<String, Object>();
                updateParams.put("plan", plan);
                Customer updatedCustomer = customer.update(updateParams);

                return updatedCustomer.getId();
            } else {
                int subCount = customer.getSubscriptions().getData().size();
                int index = upOrDown ? subCount : subCount - 1;
                for (int i = 0; i < index; i++) {
                    Subscription subscription = customer.getSubscriptions().getData().get(i);
                    Map<String, Object> cancelParams = new HashMap<>();
                    cancelParams.put("at_period_end", false);
                    subscription.cancel(cancelParams);
                }
                long expiry = 0;
                if (!upOrDown) {
                    Subscription curSubscription = customer.getSubscriptions().getData().get(subCount - 1);
                    Map<String, Object> cancelParams = new HashMap<>();
                    cancelParams.put("at_period_end", true);
                    curSubscription.cancel(cancelParams);

                    expiry = curSubscription.getCurrentPeriodEnd();
                }
                if (!upFree) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("plan", plan);

                    Map<String, Object> items = new HashMap<String, Object>();
                    items.put("0", item);

                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("customer", customerId);
                    params.put("items", items);

                    if (!upOrDown) { // Downgrade Case
                        // Set trial period until underlying expiry
                        params.put("trial_end", expiry);
                    }
                    Subscription.create(params);
                }

                return customerId;
            }
        } catch (AuthenticationException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (APIConnectionException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (CardException e) {
            logger.error( e);
            e.printStackTrace();
        } catch (APIException e) {
            logger.error( e);
            e.printStackTrace();
        }


        return "error";
    }

    @Override
    public Subscription retrieveSubscription(String subscriptionId) {
        Stripe.apiKey = apikey;
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            return subscription;
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;    }


    @Override
    public boolean setDefaultCard(String customer_id, String card_id){
        Stripe.apiKey = apikey;
        Customer customer =  retriveCustomer(customer_id);
        Map<String, Object> updateParams = new HashMap<String, Object>();
        updateParams.put("default_card", card_id);

        try {
            customer.update(updateParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.error("update default_card error:");
            logger.error(e.getMessage());
            return false;
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            logger.error("update default_card error:");
            logger.error(e.getMessage());
            return false;
        } catch (APIConnectionException e) {
            e.printStackTrace();
            logger.error("update default_card error:");
            logger.error(e.getMessage());
            return false;
        } catch (CardException e) {
            e.printStackTrace();
            logger.error("update default_card error:");
            logger.error(e.getMessage());
            return false;
        } catch (APIException e) {
            e.printStackTrace();
            logger.error("update default_card error:");
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public String deletePlan(String plan_id) {
        Stripe.apiKey = apikey;
        try {
            Plan.retrieve(plan_id).delete();
            return "success";
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Plan createPlan(String plan_id, String plan_name, String frequency, Double real_price) {
        Stripe.apiKey = apikey;
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("name", plan_name);

        Map<String, Object> planParams = new HashMap<String, Object>();

        //Params to creates a plan with new but existing plan_id
        planParams.put("id", plan_id);
        real_price *= 100;
        planParams.put("amount", real_price.intValue());
        planParams.put("interval", frequency);
        planParams.put("product", productParams);
        planParams.put("currency", "usd");

        try {
            //creates a new plan and returns it
            return Plan.create(planParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Plan retrivePlan(String plan_id) {
        Stripe.apiKey = apikey;
        try {
            if(Plan.retrieve(plan_id)!= null){
                return Plan.retrieve(plan_id);
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Account getStripeCustomAccount(String id){
        Stripe.apiKey = apikey;
        Account account = null;
        try {
            account = Account.retrieve(id, null);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            return null;
        } catch (APIConnectionException e) {
            e.printStackTrace();
            return null;
        } catch (CardException e) {
            e.printStackTrace();
            return null;
        } catch (APIException e) {
            e.printStackTrace();
            return null;
        }
        return account;
    }

    @Override
    public Account createStripeCustomAcc(User user, Vendors vendor, String ip_addr) {

        Stripe.apiKey = apikey;
        Account acct = null;
        try {

            Map<String, Object> accountParams = new HashMap<String, Object>();
            accountParams.put("type", "custom");
            accountParams.put("country", "US");

            if(vendor.getSource().equals("bank")) {
                Map<String, Object> externalAccountParams = new HashMap<String, Object>();
                externalAccountParams.put("object", "bank_account");
                externalAccountParams.put("country", "US");
                externalAccountParams.put("currency", "usd");
                externalAccountParams.put("routing_number", vendor.getRouting_number());
                externalAccountParams.put("account_number", vendor.getAccount_number());
                accountParams.put("external_account", externalAccountParams);
            }

            if(vendor.getSource().equals("card")) {

                String[] ar = vendor.getCard_expiry().split("/");

                Map<String, Object> externalAccountParams = new HashMap<String, Object>();
                externalAccountParams.put("object", "card");
                externalAccountParams.put("number", vendor.getCard_number());
                externalAccountParams.put("exp_year", Integer.parseInt("20" + ar[1]));
                externalAccountParams.put("exp_month", Integer.parseInt(ar[0]));
                externalAccountParams.put("cvc", vendor.getCard_cvc());
                externalAccountParams.put("currency", "usd");

                accountParams.put("external_account", externalAccountParams);
            }

            accountParams.put("business_name", vendor.getBusiness_name());
           // accountParams.put("business_url", vendor.getWebsite_url());
            accountParams.put("email", user.getEmail());

            Map<String, Object> legalEntityParams = new HashMap<String, Object>();
            Map<String, Object> dobParams = new HashMap<String, Object>();
            dobParams.put("day", vendor.getDob_day());
            dobParams.put("month", vendor.getDob_month());
            dobParams.put("year", vendor.getDob_year());
            legalEntityParams.put("dob", dobParams);
            legalEntityParams.put("first_name", vendor.getFirst_name());
            legalEntityParams.put("last_name", vendor.getLast_name());
            legalEntityParams.put("type", "individual");

            Map<String, Object> addressParams = new HashMap<String, Object>();
            addressParams.put("line1", vendor.getAddress());
            addressParams.put("postal_code", vendor.getZip_postal());
            addressParams.put("city", vendor.getCity());
            addressParams.put("state", vendor.getState_province());
            legalEntityParams.put("address", addressParams);

            legalEntityParams.put("ssn_last_4", vendor.getSsn_last_4());


            Map<String, Object> fileUploadParams = new HashMap<String, Object>();
            fileUploadParams.put("purpose", "identity_document");

            String filePath = UtilClass.getBasePath() + vendor.getDocument();

            fileUploadParams.put("file", new File(filePath));

            FileUpload fileObj = FileUpload.create(fileUploadParams);
            String file = fileObj.getId();


            Map<String, Object> verificationParams = new HashMap<String, Object>();
            verificationParams.put("document", file);
            legalEntityParams.put("verification", verificationParams);
            legalEntityParams.put("personal_id_number", vendor.getPersonal_id_number());

            accountParams.put("legal_entity", legalEntityParams);

            Map<String, Object> tosParams = new HashMap<String, Object>();
            tosParams.put("date", (long) System.currentTimeMillis() / 1000L);
            tosParams.put("ip", ip_addr);
            accountParams.put("tos_acceptance", tosParams);

            acct = Account.create(accountParams);

         /*   Map<String, Object> params = new HashMap<String, Object>();
            params.put("amount", 1000);
            params.put("currency", "usd");
            params.put("destination", "acct_1CWbhhG5ZhJPsDBV");
            Transfer transfer = Transfer.create(params);

            System.out.println(transfer.getId());*/


      /*      RequestOptions requestOptions = RequestOptions.builder().setStripeAccount("acct_1CWbhhG5ZhJPsDBV").build();

            Map<String, Object> payoutParams = new HashMap<String, Object>();
            payoutParams.put("amount", 400);
            payoutParams.put("currency", "usd");

            Payout.create(payoutParams, requestOptions);*/

        } catch (AuthenticationException e) {
            e.printStackTrace();
            acct = new Account();
            acct.setType("-1000");
            acct.setObject(e.getMessage());

        } catch (InvalidRequestException e) {
            e.printStackTrace();
            acct = new Account();
            acct.setType("-1000");
            acct.setObject(e.getMessage());

        } catch (APIConnectionException e) {
            e.printStackTrace();
            acct = new Account();
            acct.setType("-1000");
            acct.setObject(e.getMessage());

        } catch (CardException e) {
            e.printStackTrace();
            acct = new Account();
            acct.setType("-1000");
            acct.setObject(e.getMessage());

        } catch (APIException e) {
            e.printStackTrace();
            acct = new Account();
            acct.setType("-1000");
            acct.setObject(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            acct = new Account();
            acct.setType("-1000");
            acct.setObject(e.getMessage());

        }

        return acct;
    }

    @Override
    public Account attachBankAccount(String id, String routing_number, String account_number) {
        Stripe.apiKey = apikey;
        Account account = null;
        try {
            account = Account.retrieve(id, null);
            Map<String, Object> externalAccountParams = new HashMap<String, Object>();
            externalAccountParams.put("object", "bank_account");
            externalAccountParams.put("country", "US");
            externalAccountParams.put("currency", "usd");
            externalAccountParams.put("routing_number", routing_number);
            externalAccountParams.put("account_number", account_number);

            Map<String, Object> accountParams = new HashMap<String, Object>();
            accountParams.put("external_account", externalAccountParams);
            account = account.update(accountParams);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());

        } catch (APIConnectionException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        } catch (CardException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());

        } catch (APIException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        }

        return account;
    }

    @Override
    public  Account attachCardAccount(String id, String number, String exp_year, String exp_month, String cvc) {
        Stripe.apiKey = apikey;
        Account account = null;
        try {
            account = Account.retrieve(id, null);
            Map<String, Object> externalAccountParams = new HashMap<String, Object>();
            externalAccountParams.put("object", "card");
            externalAccountParams.put("number", number);
            externalAccountParams.put("exp_year", Integer.parseInt(exp_year));
            externalAccountParams.put("exp_month", Integer.parseInt(exp_month));
            externalAccountParams.put("cvc", cvc);
            externalAccountParams.put("currency", "usd");

            Map<String, Object> accountParams = new HashMap<String, Object>();
            accountParams.put("external_account", externalAccountParams);
            account = account.update(accountParams);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        } catch (APIConnectionException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        } catch (CardException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        } catch (APIException e) {
            e.printStackTrace();
            account = new Account();
            account.setType("-1000");
            account.setObject(e.getMessage());
        }

        return account;
    }

    @Override
    public Payout transferToAccount(String acc_id , double amt)
    {
        Stripe.apiKey = apikey;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", (int) (amt * 100));
        params.put("currency", "usd");
        params.put("destination", acc_id);
        Transfer transfer = null;
        Payout payout = null;
        try {
            transfer = Transfer.create(params);

            RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(acc_id).build();

            Map<String, Object> payoutParams = new HashMap<String, Object>();
            payoutParams.put("amount", (int) (amt * 100));
            payoutParams.put("currency", "usd");

            payout = Payout.create(payoutParams, requestOptions);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            payout = new Payout();
            payout.setCurrency("error");
            payout.setObject(e.getMessage());

        } catch (InvalidRequestException e) {
            e.printStackTrace();
            payout = new Payout();
            payout.setCurrency("error");
            payout.setObject(e.getMessage());

        } catch (APIConnectionException e) {
            e.printStackTrace();
            payout = new Payout();
            payout.setCurrency("error");
            payout.setObject(e.getMessage());

        } catch (CardException e) {
            e.printStackTrace();
            payout = new Payout();
            payout.setCurrency("error");
            payout.setObject(e.getMessage());

        } catch (APIException e) {
            e.printStackTrace();
            payout = new Payout();
            payout.setCurrency("error");
            payout.setObject(e.getMessage());

        }
        return payout;

    }
}
