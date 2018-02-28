package com.tld.company.upload;

public class RecognizeManager {

    public static RecognizeAsyncTask recognize(String image_base64,final OnRecognizeListener recognizeInterface) {
        RecognizeAsyncTask recognizeAsyncTask=new RecognizeAsyncTask(image_base64);
        recognizeAsyncTask.setRecognizeInterface(recognizeInterface);
        return recognizeAsyncTask;
    }
    public static Recognize2AsyncTask recognize2(String image_base64,final OnRecognizeListener recognizeInterface) {
        Recognize2AsyncTask recognizeAsyncTask=new Recognize2AsyncTask(image_base64);
        recognizeAsyncTask.setRecognizeInterface(recognizeInterface);
        return recognizeAsyncTask;
    }
    public static WeedRecognizeAsyncTask weedRecognize(String image_base64,final OnRecognizeListener recognizeInterface) {
        WeedRecognizeAsyncTask weedRecognizeAsyncTask=new WeedRecognizeAsyncTask(image_base64);
        weedRecognizeAsyncTask.setRecognizeInterface(recognizeInterface);
        return weedRecognizeAsyncTask;
    }
    public static void getPlantInfo(String code,final OnRecognizeListener recognizeInterface) {
        PlantInfoAsyncTask plantInfoAsyncTask=new PlantInfoAsyncTask(code);
        plantInfoAsyncTask.setRecognizeInterface(recognizeInterface);
        plantInfoAsyncTask.execute();
    }
}
