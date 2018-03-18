package com.github.lit.commons.http;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User : liulu
 * Date : 2018/3/16 19:40
 * version $Id: HttpUtils.java, v 0.1 Exp $
 */
public class HttpUtils {

    private static String ua_pc = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.162 Safari/537.36";

    private static String ua_phone = "";


    public static void main(String[] args) throws Exception {

        URL url = new URL("www.baidu.com");

        HttpURLConnection httpURLConnection = new HttpURLConnection(url);


        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        OutputStream outputStream1 = urlConnection.getOutputStream();


        urlConnection.connect();

        OutputStream outputStream = outputStream1;





    }


}
