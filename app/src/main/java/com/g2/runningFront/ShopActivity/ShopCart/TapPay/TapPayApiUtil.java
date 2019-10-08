package com.g2.runningFront.ShopActivity.ShopCart.TapPay;

import org.json.JSONException;
import org.json.JSONObject;

public class TapPayApiUtil {
    private static final String TAG ="TAG_AptiUtil";

    int amount , order_number;
    String order_details, Receiver_name,Receiver_Phone;

    public TapPayApiUtil(int amount, int order_number, String order_details, String receiver_name, String receiver_Phone) {
        this.amount = amount;
        this.order_number = order_number;
        this.order_details = order_details;
        Receiver_name = receiver_name;
        Receiver_Phone = receiver_Phone;
    }

    public  String generatePayByPrimeCURLForSanBox
            (String prime,String partnerKey,String merchanID){

        JSONObject paymentJO = new JSONObject();

        try{
            paymentJO.put("prime", prime);
            paymentJO.put("partner_key",partnerKey);
            paymentJO.put("merchant_id",merchanID);
            paymentJO.put("amount",amount);
            paymentJO.put("currency","TWD");
            paymentJO.put("order_number",String.valueOf(order_number));
            paymentJO.put("details",order_details);
            JSONObject cardholder = new JSONObject();
            cardholder.put("name",Receiver_name);
            cardholder.put("phone_number",Receiver_Phone);
            cardholder.put("email", "a10033135@gmail.com");

            paymentJO.put("cardholder",cardholder);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
        TapPayTask myTask = new TapPayTask(url,paymentJO.toString(),partnerKey);
        String result = "";
        try {
            result = myTask.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
