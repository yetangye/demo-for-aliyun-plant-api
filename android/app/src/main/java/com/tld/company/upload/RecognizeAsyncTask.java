package com.tld.company.upload;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tld.company.bean.HblFlowerInfo;
import com.tld.company.bean.ResponseData;
import com.tld.company.http.ErrorType;
import com.tld.company.http.InvocationError;
import com.tld.company.http.Response;
import com.tld.company.service.AliyunPlantApiService;

import java.lang.reflect.Type;
import java.util.List;


public class RecognizeAsyncTask extends AsyncTask<Void, Void, ResponseData> {
    private String imag_baseb64;
    private OnRecognizeListener mRecognizeInterface;
    protected void setRecognizeInterface(OnRecognizeListener recognizeInterface) {
        mRecognizeInterface = recognizeInterface;
    }

    protected RecognizeAsyncTask(String imag_baseb64) {
        this.imag_baseb64 = imag_baseb64;
    }


    @Override
    protected ResponseData doInBackground(Void... params) {
        ResponseData responseData =AliyunPlantApiService.recognize(imag_baseb64);

        return responseData;
    }

    public void cancel() {
        if (getStatus() != AsyncTask.Status.FINISHED) {
            cancel(true);
        }
        AliyunPlantApiService.cancel();
    }

    @Override
    protected void onPostExecute(ResponseData responseData) {
        if (responseData != null && responseData.getHttpStatusCode() == 200) {
            responseData.print();
            Type type = new TypeToken<Response<List<HblFlowerInfo>>>(){}.getType();
            Response<List<HblFlowerInfo>> response = new Gson().fromJson(responseData.getBody(),type);
            if (response != null && response.getStatus()==0) {
                List<HblFlowerInfo> mRecognizeResultList = response.getResult();
                if (mRecognizeResultList != null && mRecognizeResultList.size() > 0) {
                    mRecognizeInterface.onSuccess(mRecognizeResultList);
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
