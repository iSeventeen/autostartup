package com.android.autostartup.controller.server;

import java.util.Map;

import android.util.Log;

import com.android.autostartup.app.Application;
import com.android.autostartup.model.Student;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class Server {

    public static final String TAG = Server.class.getSimpleName();

    private static final String BASE_URL = "http://192.168.1.133:9000/api/";

    public static enum API {

    }

    private static void executeRequest(Request<?> request) {
        Log.d(TAG, toCurl(request));
        Application.getRequestQueue().add(request);
    }

    private static void executeRequest(Request<?> request, String tag) {
        request.setTag(tag);
        executeRequest(request);
    }

    private static String toCurl(Request<?> request) {
        StringBuffer sBuffer = new StringBuffer();
        switch (request.getMethod()) {
        case Request.Method.GET:
            sBuffer.append("curl -X GET");
            break;
        case Request.Method.PUT:
            sBuffer.append("curl -X PUT");
            break;
        case Request.Method.POST:
            sBuffer.append("curl -X POST");
            break;
        case Request.Method.DELETE:
            sBuffer.append("curl -X DELETE");
            break;
        }
        try {
            Map<String, String> headers = request.getHeaders();
            for (Object key : headers.keySet()) {
                sBuffer.append(" -H ").append('\'').append((String) key).append(": ")
                        .append(headers.get(key)).append('\'');
            }
        } catch (AuthFailureError authFailureError) {
        }
        sBuffer.append(" '").append(request.getUrl()).append("'");
        return sBuffer.toString();
    }

    public interface ErrorCallback {
        public void onFail(String reason);
    }

    private static class SimpleErrorListener implements Response.ErrorListener {

        private ErrorCallback errorCallback;

        public SimpleErrorListener(ErrorCallback errorCallback) {
            this.errorCallback = errorCallback;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(TAG, error.getMessage());
            errorCallback.onFail(error.getMessage());
        }
    }

    public static void requestStudent(final String cardId, final GetStudentCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(BASE_URL).append("students/" + cardId);

        GsonRequest<Student> gsonRequest = new GsonRequest<Student>(url.toString(), Student.class,
                null, new Response.Listener<Student>() {
                    @Override
                    public void onResponse(Student student) {
                        Log.i(TAG, student.toString());
                        callback.onSuccess(student);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);
    }

    public interface GetStudentCallback {
        public void onSuccess(Student student);
    }

}
