package com.example.goods4u.goods4u;




import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hasee on 2017/2/5.
 */

public class HttpUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public static final OkHttpClient client = new OkHttpClient();

    public static JSONObject post(String url, String json) throws IOException {
        JSONObject jsonObjectResp = null;
        try {

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            okhttp3.RequestBody body = RequestBody.create(JSON, json.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            System.out.println(body.toString());
            okhttp3.Response response = client.newCall(request).execute();

            String networkResp = response.body().string();
            System.out.println(networkResp);
            if (!networkResp.isEmpty()) {
                jsonObjectResp = parseJSONStringToJSONObject(networkResp);
            }
        } catch (Exception ex) {
            String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
            jsonObjectResp = parseJSONStringToJSONObject(err);
        }
        return jsonObjectResp;
    }
    private static JSONObject parseJSONStringToJSONObject(final String strr) {

        JSONObject response = null;
        try {
            response = new JSONObject(strr);
        } catch (Exception ex) {
            //  Log.e("Could not parse malformed JSON: \"" + json + "\"");
            try {
                response = new JSONObject();
                response.put("result", "failed");
                response.put("data", strr);
                response.put("error", ex.getMessage());
            } catch (Exception exx) {
            }
        }
        return response;
    }
    public static String get(String url) throws IOException {
        String jsonData=null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;
            responses = client.newCall(request).execute();
            jsonData = responses.body().string();
            } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonData);
        return jsonData;
    }
}
