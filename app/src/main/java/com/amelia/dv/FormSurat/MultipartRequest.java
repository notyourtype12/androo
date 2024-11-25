package com.amelia.dv.FormSurat;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class MultipartRequest extends Request<String> {
    private Map<String, String> params;
    private Map<String, DataPart> byteData;
    private Response.Listener<String> listener;
    private Response.ErrorListener errorListener;

    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    protected Map<String, DataPart> getByteData() {
        return byteData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String charset = HttpHeaderParser.parseCharset(response.headers, "UTF-8");
            String responseBody = new String(response.data, charset);
            return Response.success(responseBody, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }


    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setByteData(Map<String, DataPart> byteData) {
        this.byteData = byteData;
    }

    // Helper class to handle byte data for images
    public static class DataPart {
        private String fileName;
        private byte[] content;

        public DataPart(String fileName, byte[] content) {
            this.fileName = fileName;
            this.content = content;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }
    }
}

