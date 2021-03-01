package com.gexton.pink_uos.api;

public interface ApiCallback {

    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce);

}