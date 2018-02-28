package com.tld.company.upload;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tld.company.bean.PlantInfo;
import com.tld.company.bean.ResponseData;
import com.tld.company.http.ErrorType;
import com.tld.company.http.InvocationError;
import com.tld.company.http.ResponsePlant;
import com.tld.company.service.AliyunPlantApiService;

import java.lang.reflect.Type;

public class PlantInfoAsyncTask extends AsyncTask<Void, Void, ResponseData> {
    private String code;
    private OnRecognizeListener mRecognizeInterface;
    protected void setRecognizeInterface(OnRecognizeListener recognizeInterface) {
        mRecognizeInterface = recognizeInterface;
    }

    protected PlantInfoAsyncTask(String code) {
        this.code = code;
    }

    @Override
    protected ResponseData doInBackground(Void... params) {
        ResponseData responseData =AliyunPlantApiService.info(code);

        return responseData;
    }

    public void cancel() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
        AliyunPlantApiService.cancel();
    }

    @Override
    protected void onPostExecute(ResponseData responseData) {
        if (responseData != null && responseData.getHttpStatusCode() == 200) {
            responseData.print();
            Type type = new TypeToken<ResponsePlant<PlantInfo>>(){}.getType();
            ResponsePlant<PlantInfo> response = new Gson().fromJson(responseData.getBody(),type);
            if (response != null && response.getStatus()==0) {
                PlantInfo mPlantInfo = response.getResult();
                if (mPlantInfo != null ) {
                    mRecognizeInterface.onSuccess(mPlantInfo);
                    return;
                }
            }
            mRecognizeInterface.onFail(new InvocationError(ErrorType.BUSINESS_ERROR, response.getStatus(), response.getMessage()));
        } else {
            mRecognizeInterface.onFail(new InvocationError(ErrorType.HTTP_ERROR,
                    "http code : " + responseData.getHttpStatusCode() + ", message : " + responseData.getException().toString()));
        }
    }

}
