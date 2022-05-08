package com.example.music_store.model;

public class request_rent {

    private String insturmentId;
    private String clientId;
    private String contactNum;
    private String req_date;
    private String num_days;


    public request_rent() {
    }

    public String getInsturmentId() {

        return insturmentId;
    }

    public void setInsturmentId(String insturmentId) {

        this.insturmentId = insturmentId;
    }

    public String getClientId() {

        return clientId;
    }

    public void setClientId(String clientId) {

        this.clientId = clientId;
    }

    public String getContactNum() {

        return contactNum;
    }

    public void setContactNum(String contactNum) {

        this.contactNum = contactNum;
    }

    public String getReq_date() {

        return req_date;
    }

    public void setReq_date(String req_date) {

        this.req_date = req_date;
    }

    public String getNum_days() {

        return num_days;
    }

    public void setNum_days(String num_days) {

        this.num_days = num_days;
    }
}
