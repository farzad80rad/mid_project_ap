package Consol;

import GUI.MainDisplayGui;

import java.io.Serializable;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class is for holding response info like body, headers and status cod.
 */
public class Response implements Serializable {
   private byte[] bodyRes ;
   private int statusCode;
   private Map<String, List<String>> headerRes;


    /**
     * create an empty response.
     */
    public Response ()
   {
       bodyRes = new byte[0];
       statusCode = 1000;
       headerRes = new HashMap<>();
   }

    /**
     * simple constructor.
     * @param httpResponse httpResponse to be saved.
     */
   public Response(HttpResponse<byte[]> httpResponse)
   {
       bodyRes = httpResponse.body();
       statusCode = httpResponse.statusCode();
       headerRes = httpResponse.headers().map();
   }

    /**
     * get body of response.
     * @return body of response.
     */
    public byte[] getBodyRes() {
        return bodyRes;
    }

    /**
     * get status code of response.
     * @return status code of response.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * get headers of response.
     * @return headers  of response.
     */
    public Map<String, List<String>> getHeaderRes() {
        return headerRes;
    }

    /**
     * set status code.
     * @param statusCode status code to be set.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * set body.
     * @param bodyRes body to be set.
     */
    public void setBodyRes(byte[] bodyRes) {
        this.bodyRes = bodyRes;
    }

    /**
     * set headers of response.
     * @param headerRes headers to be set.
     */
    public void setHeaderRes(Map<String, List<String>> headerRes) {
        this.headerRes = headerRes;
    }
}
