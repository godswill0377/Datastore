package com.dataexo.zblog.service;


import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.Vendors;
import com.stripe.model.*;

public interface StripeService {

    Customer createCustomer(String email);

    String doPayment(String token, float amount, String email);

    Token createToken(String number, String expiry, String cvc
            , String address_city, String address_zip, String address_state, String address_line1);

    Subscription subscriptionPlan(String plan, String customer_id);

    Customer retriveCustomer(String customerId);

    String upgradeCustomer(String customerId, String plan, boolean upOrDown, boolean upFree);

    Subscription retrieveSubscription (String subscriptionId);

    String continueSubscription (String subscription_id);

    String upgradeSubscription(String subscriptionId, String plan, boolean upOrDown, boolean upFree);

    String cancelCustomer(String customerId);

    String continueMembership(String customerId);

    Card attachCard(String number, String expiry, String cvc, String customerId);

    Charge doPaymentByCustomer(String customerId, String currency, float amount);

    boolean setDefaultCard(String customer_id, String card_id);

    String deletePlan (String plan_id);

    Plan createPlan (String plan_id, String plan_name, String frequency, Double real_price);

    Plan retrivePlan (String plan_id);

    Account createStripeCustomAcc(User user, Vendors vendor , String Ip);
    Account getStripeCustomAccount(String id);

    Account attachBankAccount(String id, String routing_number, String account_number);
    Account attachCardAccount(String id, String number, String exp_year, String exp_month, String cvc);

    Payout transferToAccount(String acc_id , double amt);

}
