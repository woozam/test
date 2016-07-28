package kr.co.foodfly.androidapp.model.user;

import kr.co.foodfly.androidapp.model.BaseResponse;

public class VerificationCodeResponse extends BaseResponse {
    public String token;
    public long tokenValidUntil;
    public String status;
    public String phoneNumber;
    public boolean verified = false;
    public String verification_code;
}