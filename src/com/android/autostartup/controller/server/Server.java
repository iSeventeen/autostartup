package com.android.autostartup.controller.server;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.android.autostartup.app.Application;
import com.android.autostartup.model.CommonResult;
import com.android.autostartup.model.Parent;
import com.android.autostartup.model.Student;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class Server {

    public static final String TAG = Server.class.getSimpleName();

    public static final String BASE_URL = "http://192.168.1.248:9000/";
    public static final String API_BASE_URL = BASE_URL + "api/";
    public static final String FILE_BASE_URL = BASE_URL + "assets/files/";
    public static final String PICTURE_BASE_URL = FILE_BASE_URL + "picture/";
    public static final String AUDIO_BASE_URL = FILE_BASE_URL + "audio/";
    public static final String VEDIO_BASE_URL = FILE_BASE_URL + "vedio/";

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

    public static void requestAllStudent(final GetStudentsCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("students");
        GsonRequest<Student[]> gsonRequest = new GsonRequest<Student[]>(url.toString(),
                Student[].class, null, new Response.Listener<Student[]>() {
                    @Override
                    public void onResponse(Student[] students) {
                        callback.onSuccess(students);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);
    }

    public interface GetStudentsCallback {
        public void onSuccess(Student[] students);
    }

    public static void requestAllStudentIds(final GetStudentsCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("students/ids");
        GsonRequest<Student[]> gsonRequest = new GsonRequest<Student[]>(url.toString(),
                Student[].class, null, new Response.Listener<Student[]>() {
                    @Override
                    public void onResponse(Student[] students) {
                        callback.onSuccess(students);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);

    }

    public static void requestStudentsByIds(String ids, final GetStudentsCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("students/" + ids);
        GsonRequest<Student[]> gsonRequest = new GsonRequest<Student[]>(url.toString(),
                Student[].class, null, new Response.Listener<Student[]>() {
                    @Override
                    public void onResponse(Student[] students) {
                        callback.onSuccess(students);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);
    }

    public static void requestStudent(final String cardId, final GetStudentCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("students/" + cardId
                + "/student");

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

    public static void saveStudent(final Student student, final CommonCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("students");

        GsonRequest<CommonResult> gsonRequest = new GsonRequest<CommonResult>(Request.Method.POST,
                url.toString(), CommonResult.class, null, getParams(student),
                new Response.Listener<CommonResult>() {
                    @Override
                    public void onResponse(CommonResult result) {
                        if (result.status.equals("OK")) {
                            callback.onSuccess(result.status);
                        } else {
                            errorCallback.onFail("save student error");
                        }
                    }
                }, new SimpleErrorListener(errorCallback));
        executeRequest(gsonRequest);
    }

    public static void updateStudent(final Student student, final CommonCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("students/update");

        GsonRequest<CommonResult> gsonRequest = new GsonRequest<CommonResult>(Request.Method.PUT,
                url.toString(), CommonResult.class, null, getParams(student),
                new Response.Listener<CommonResult>() {
                    @Override
                    public void onResponse(CommonResult result) {
                        if (result.status.equals("OK")) {
                            callback.onSuccess(result.status);
                        } else {
                            errorCallback.onFail("update student error");
                        }

                    }
                }, new SimpleErrorListener(errorCallback));
        executeRequest(gsonRequest);
    }

    private static Map<String, String> getParams(Student student) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("cardId", student.cardId);
        params.put("name", student.name);
        params.put("grade", String.valueOf(student.grade));
        params.put("gender", String.valueOf(student.gender));
        params.put("address", student.address);
        params.put("avatar", student.avatar);
        params.put("notes", student.notes);
        params.put("createdAt", String.valueOf(student.createdAt));
        params.put("updatedAt", String.valueOf(student.updatedAt));

        return params;
    }

    public interface CommonCallback {
        public void onSuccess(String status);
    }

    // /////////////////////////////////////////////////////////////////////

    public static void requestAllParent(final GetParentsCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("parents");
        GsonRequest<Parent[]> gsonRequest = new GsonRequest<Parent[]>(url.toString(),
                Parent[].class, null, new Response.Listener<Parent[]>() {
                    @Override
                    public void onResponse(Parent[] parents) {
                        callback.onSuccess(parents);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);
    }

    public interface GetParentsCallback {
        public void onSuccess(Parent[] parents);
    }

    public static void requestAllParentIds(final GetParentsCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("parents/ids");
        GsonRequest<Parent[]> gsonRequest = new GsonRequest<Parent[]>(url.toString(),
                Parent[].class, null, new Response.Listener<Parent[]>() {
                    @Override
                    public void onResponse(Parent[] parents) {
                        callback.onSuccess(parents);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);

    }

    public static void requestParentsByIds(String ids, final GetParentsCallback callback,
            final ErrorCallback errorCallback) {
        StringBuilder url = new StringBuilder(API_BASE_URL).append("parents/" + ids);
        GsonRequest<Parent[]> gsonRequest = new GsonRequest<Parent[]>(url.toString(),
                Parent[].class, null, new Response.Listener<Parent[]>() {
                    @Override
                    public void onResponse(Parent[] parents) {
                        callback.onSuccess(parents);
                    }
                }, new SimpleErrorListener(errorCallback));
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1));
        executeRequest(gsonRequest, TAG);
    }

}
