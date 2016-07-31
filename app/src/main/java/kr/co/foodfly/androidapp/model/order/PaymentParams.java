package kr.co.foodfly.androidapp.model.order;

import java.io.Serializable;

/**
 * Created by woozam on 2016-07-29.
 */
public class PaymentParams implements Serializable {

    public String P_MID;
    public String P_NOTI;
    public String P_RESERVED;
    public String P_HPP_METHOD;
    public String P_OID;
    public String P_GOODS;
    public int P_AMT;
    public String P_UNAME;
    public String P_MNAME;
    public String P_MOBILE;
    public String P_EMAIL;
    public String P_CHARSET;
    public String P_NEXT_URL;

    public PaymentParams() {
    }
}