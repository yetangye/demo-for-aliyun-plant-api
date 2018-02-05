package tld.your.company.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

public class Utils {
    public static String loadFileAsBase64(String filePath) {
        File file = new File(filePath);
        String encodedBase64 = null;
        try {
            FileInputStream fileReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileReader.read(bytes);
            encodedBase64 = new String(com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(bytes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedBase64;
    }
}