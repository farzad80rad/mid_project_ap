package MainPart;
import Consol.Response;
import Controll.ResponseController;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;

/**
 * request at this step is just like a holder for info.
 */
public class Request implements Serializable {

    private String name;
    private ArrayList<String[]> headers;
    private ArrayList<String[]> formDataBody;
    private ArrayList<String[]> queries;
    private String[] proxy;
    private String bodyText;
    private transient Folder parentFol;
    private String urlQuery;
    private String url;
    private Kind kind;
    private long timeElapsed ;
    private String filePath;
    private int responseKind ;// 1 == textArea  2==JTextPan  3==Json
    private Response response ;
    private transient ResponseController responseController;
    private int massageBodyKind; // 0 == form date    1 == JSON    2 == binary


    public static final transient ImageIcon getI = new ImageIcon(new ImageIcon(".\\icons\\get2.png").getImage().getScaledInstance(80,17, Image.SCALE_DEFAULT));
    public static final transient ImageIcon putI = new ImageIcon(new ImageIcon(".\\icons\\put.png").getImage().getScaledInstance(80,17, Image.SCALE_DEFAULT));
    public static final transient ImageIcon patchI = new ImageIcon(new ImageIcon(".\\icons\\patch.png").getImage().getScaledInstance(80,17, Image.SCALE_DEFAULT));
    public static final transient ImageIcon postI =new ImageIcon(new ImageIcon(".\\icons\\post.png").getImage().getScaledInstance(80,17, Image.SCALE_DEFAULT));
    public static final transient ImageIcon deleteI = new ImageIcon(new ImageIcon(".\\icons\\delete.png").getImage().getScaledInstance(80,17, Image.SCALE_DEFAULT));

    public  enum Kind{
        GET, PUT, PATCH, POST, DELETE
    }






    /**
     * construct the request and by default put {"name","value","true"} for header and query info
     * and {"","","true"} for auth.
     * @param name name of the request.
     * @param parentFol folder that store this request.
     */
    public Request(String name,Folder parentFol){
        responseKind = 2;
        kind = Kind.GET;
        this.massageBodyKind = 0;
        this.parentFol = parentFol;
        headers = new ArrayList<>();
        queries = new ArrayList<>();
        formDataBody = new ArrayList<>();
        proxy = new String[]{"","","true"};
        urlQuery = "https://justForTest.com";
        headers.add( new String[]{"name","value","true"});
        queries.add( new String[]{"name","value","true"});
        formDataBody.add(new String[]{"name","value","true"});
        bodyText = "";
        this.name = name;
    }

    /**
     * get the name of this request.
     * @return name of this request.
     */
    public String getName() {
        return name;
    }

    /**
     * set the body text for this request.
     * @param bodyText text to be set .
     */
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }


    /**
     * get the body text.
     * @return the body text.
     */
    public String getBodyText() {
        return bodyText;
    }


    /**
     * get the headers.
     * @return the headers.
     */
    public ArrayList<String[]> getHeaders() {
        return headers;
    }

    /**
     * get the form data.
     * @return the form data.
     */
    public ArrayList<String[]> getFormDataBody() {
        return formDataBody;
    }

    /**
     * make a new header by default info of {"name","value","true"}.
     */
    public void makeNewHeader()
    {
        headers.add( new String[]{"name","value","true"});
    }

    /**
     * remove a header from headers.
     * @param index index of header to be removed.
     */
    public void removeHeader(int index)
    {
        headers.remove(index);
    }

    /**
     * remove a line of Form data.
     * @param index index of line to be removed.
     */
    public void removeLineFormData(int index)
    {
        formDataBody.remove(index);
    }

    /**
     * get all queries.
     * @return all queries.
     */
    public ArrayList<String[]> getQueries() {
        return queries;
    }

    /**
     * update header info .
     * @param indexInfo 0 == name    1 ==  value   2 == "true" or "false"
     * @param indexHeader index header among headers.
     * @param newInfo the info to be replaced.
     */
    public void updateHeader(int indexInfo, int indexHeader,String newInfo)
    {
        headers.get(indexHeader)[indexInfo] = newInfo;
    }


    /**
     * update line of form data info .
     * @param indexInfo 0 == name    1 ==  value   2 == "true" or "false"
     * @param indexLine index line in form data.
     * @param newInfo the info to be replaced.
     */
    public void updateLineFormData(int indexInfo, int indexLine,String newInfo)
    {
        formDataBody.get(indexLine)[indexInfo] = newInfo;
    }

    /**
     * make a new query by default of {"name","value","true"}
     */
    public void makeNewQuery()
    {
        queries.add( new String[]{"name","value","true"});
    }


    /**
     * make a new form data line by default of {"name","value","true"}
     */
    public void makeNewFormDataLine()
    {
        formDataBody.add( new String[]{"name","value","true"});
    }

    /**
     * update query info .
     * @param indexInfo 0 == name    1 ==  value   2 == "true" or "false"
     * @param indexQuery index query among queries.
     * @param newInfo the info to be replaced.
     */
    public void updateQuery(int indexInfo,int indexQuery, String newInfo)
    {
        queries.get(indexQuery)[indexInfo] = newInfo;
    }

    /**
     * remove a query from queries.
     * @param index index of query in list.
     */
    public void removeQuery(int index)
    {
        System.out.println(Arrays.toString(queries.get(index)));
        queries.remove(index);
    }

    /**
     * get parent folder _ folder that store this request.
     * @return parent folder
     */
    public Folder getParentFol() {
        return parentFol;
    }

    /**
     * get URL query.
     * @return   URL query
     */
    public String getUrlQuery() {
        return urlQuery;
    }

    /**
     * get auth info.
     * @return auth info.
     */
    public String[] getProxy() {
        return proxy;
    }

    /**
     * update auth info.
     * @param indexInfo 0 == server   1 == ip  2== true/false
     * @param newInfo new info to be replace.
     */
    public void updateProxy(int indexInfo, String newInfo){
        proxy[indexInfo] = newInfo;
    }


    /**
     * set kind of this request.
     * @param kind kind to be set. its from Kind enum in this class.
     */
    public void setKind(Kind kind) {
        this.kind = kind;
    }

    /**
     * get kind of this req.
     * @return kind of this req.
     */
    public Kind getKind() {
        return kind;
    }


    /**
     * get message body kind.
     * @return kind of message body.  0 == form date    1 == JSON
     */
    public int getMassageBodyKind() {
        return massageBodyKind;
    }


    /**
     * set kind of message body.
     * @param kind kind of message body.  0 == form date    1 == JSON
     */
    public void setMassageBodyKind(int kind)
    {
        this.massageBodyKind = kind;
    }

    /**
     * set headers of this req.
     * @param headers headers of this req.
     */
    public void setHeaders(ArrayList<String[]> headers) {
        this.headers = headers;
    }

    /**
     * set form data for this req.
     * @param formData form data for this req.
     */
    public void setFormDataBody(ArrayList<String[]> formData) {
        this.formDataBody = formData;
    }

    /**
     * set proxy for this req.
     * @param proxy proxy to be set, "server,ip,true/false".
     */
    public void setProxy(String[] proxy) {
        this.proxy = proxy;
    }

    /**
     * set queries for this req.
     * @param queries queries to be set.
     */
    public void setQueries(ArrayList<String[]> queries) {
        this.queries = queries;
    }

    /**
     * get uri of this request
     * @return uri
     */
    public String getUrl() {
        return url;
    }

    /**
     * set uri for this request
     * @param url uri to be set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * make valid headers/query/FormData from headers/query/FormData of this request. headers/query/FormData which have the "name=value" are filtered.
     * @param HQF for headers enter "header", for query enter "query"  and for Form Data use "Form"/
     * @return valid headers/query/FormData of this request
     */
    public String[] HQF_TOStringArr(String HQF)
    {
        ArrayList<String> arrayList = new ArrayList<>();
        int index = 0;
        for(String[] header : (HQF.equals("header")?headers:HQF.equals("query")?queries:formDataBody)) {
            if(!((header[0].equals("name") && header[1].equals("value"))) && header[2].equals("true")) {
                arrayList.add(header[0]);
                arrayList.add(header[1]);
                index += 2;
            }
        }
        String[] result = new String[index];
        return arrayList.toArray(result);
    }

    /**
     * convert queryArray to queryString . query string is in format of "?name=value&name%202=value%202".
     * @param queryArray query array to be converted.
     * @return string that could be appended to url
     */
    public String getQueryString (String[] queryArray )
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        for(int i=0 ;i<queryArray.length ; i+=2)
        {
            stringBuilder.append(queryArray[i]).append("=").append(queryArray[i+1]).append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString().replaceAll(" ","%20");
    }

    /**
     * convert header array to header string . header string is in format of "name:value;name2:value2".
     * @param header query array to be converted.
     * @return string that shows headers in a line.
     */
    public String getHeaderString (String[] header )
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0 ;i<header.length ; i+=2)
        {
            stringBuilder.append(header[i]).append(":").append(header[i+1]).append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }


    /**
     * convert form data to string form . form data string is in format of "name=value&name2=value2".
     * @param formDataArray form data array to be converted.
     * @return string that shows form data info in a line.
     */
    public String convertFormDataBodyToString(String[] formDataArray)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0 ; i<formDataArray.length ; i+=2)
        {
            stringBuilder.append(formDataArray[i]).append("=").append(formDataArray[i+1]).append("&");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }



    public Response getResponse() {
        return response;
    }

    /**
     * set response ond notify response Controller.
     * @param response response to be set.
     */
    public void setResponse(Response response) {
        this.response = response;
        if(responseController != null)
        responseController.notifyChangedResponse();
    }

    public void notifyResponseController ()
    {
        responseController.notifyChangedResponse();
    }


    /**
     * set response controller .
     * @param responseController response controller to be set.
     */
    public void setResponseController(ResponseController responseController) {
        this.responseController = responseController;
    }


    /**
     * set response kind .
     * @param responseKind  0 == form date    1 == JSON    2 == binary
     */
    public void setResponseKind(int responseKind) {
        this.responseKind = responseKind;
    }

    /**
     * get kind of response panel.
     * @return  0 == form date    1 == JSON    2 == binary
     */
    public int getResponseKind() {
        return responseKind;
    }

    /**
     * get time elapsed between sending and showing this request response.
     * @return time elapsed.
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }

    /**
     * set time elapsed.
     * @param timeElapsed time elapsed between sending and showing this request response.
     */
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }


    /**
     * set path of file to be uploaded
     * @param filePath path of this file in string form.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * get file path.
     * @return path of file to be uploaded.
     */
    public String getFilePath() {
        return filePath;
    }
}

