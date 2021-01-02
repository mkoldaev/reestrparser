package ru.mkoldaev;

import jdk.nashorn.internal.parser.JSONParser;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.IOUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.out;

public class Parser {

    private static Object Lists;
    private static String fsaurl = "https://pub.fsa.gov.ru/api/v1/ral/common/showcases/get";
    private static SSLSocketFactory sf;
    private static SSLContext sslContext;

    public static void main(String[] args) throws IOException {
        initcerturl();
        //getreeatr(0);
        doPost();
        //getjson();
    }

    private static void getjson() throws IOException {
        URL url = new URL (fsaurl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = "{\"columns\":[],\"sort\":[\"-id\"],\"limit\":10,\"offset\":0}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            out.println(response.toString());
        }
    }

    private static void initcerturl() throws IOException {
        URL url = new URL(fsaurl);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        if (connection instanceof HttpsURLConnection) {
            try {
                KeyManager[] km = null;
                TrustManager[] tm = {new RelaxedX509TrustManager()};
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, tm, new java.security.SecureRandom());
                sf = sslContext.getSocketFactory();
                //((HttpsURLConnection)connection).setSSLSocketFactory(sf);
                out.println("setSSLSocketFactory OK!");
            }catch (java.security.GeneralSecurityException e) {
                out.println("GeneralSecurityException: "+e.getMessage());
            }
        }
    }

    private static void doPost() throws IOException {
        for(int i=0;i<=308;i++) {
            getjson(i);
        }
    }

    private static void getjson(int ii) throws MalformedURLException {
        String body="{\"columns\":[],\"sort\":[\"-id\"],\"limit\":100,\"offset\":"+ii+"}";
        String str_response="";
        String line="";
        URL url = new URL(fsaurl);
        try{
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "ru,en-US;q=0.9,en;q=0.8");
            connection.setRequestProperty("AUTHORIZATION","Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiIwMDg2OTUxZS02ZGVkLTRjNzEtYjgwYy02ZGQ1NTFiNzA4MjQiLCJzdWIiOiJhbm9ueW1vdXMiLCJleHAiOjE2MTA0MDc0NzB9.RDOM3LQdYzyBCTOHiUhjfcv-weXIFg801pPnLhv4eMh9LWq8tZK6ZTnqIFZokh1u_3wNYjoBC7ApqAFRfyXj7w");
            connection.setRequestProperty("Origin","https://pub.fsa.gov.ru");
            connection.setRequestProperty("Cookie","JSESSIONID=node0wsntlslykfk11vgbxpcmz6olm7054.node0;Path=/");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setSSLSocketFactory(sf);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader rd = new BufferedReader(isr);
            while ((line = rd.readLine()) != null) str_response+= line + '\r';
            rd.close();
            JSONObject jsonObject = new JSONObject(str_response.trim());
            JSONArray values = jsonObject.getJSONArray("items");
            for (int i = 0; i < values.length(); i++) {
                JSONObject it = values.getJSONObject(i);
                int id = it.getInt("id");
                String name = it.getString("fullName");
                out.println(id + ", " + name);
                PrintWriter writer = new PrintWriter("reestr/"+id+"_.json", "UTF-8");
                writer.println(it);
                writer.close();
            }
        } catch(Exception e){
            e.printStackTrace(System.out);
            //throw new RuntimeException(e);
        }
    }

    private static void getreeatr(int i) throws IOException {
        String postURL = fsaurl;
        CloseableHttpClient httpClient = HttpClients
                .custom().setSSLContext(sslContext)
                .setHostnameVerifier(AllowAllHostnameVerifier.INSTANCE)
                .build();
        HttpUriRequest request = RequestBuilder.post()
                .setUri(postURL)
                .setEntity(new StringEntity("{\"columns\":[],\"sort\":[\"-id\"],\"limit\":10,\"offset\":0}", ContentType.APPLICATION_JSON))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru,en-US;q=0.9,en;q=0.8")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJiOTk1ZTUwMC0zMjU5LTQ2NTgtOTg1ZS02MDhmM2I5OWFiNDUiLCJzdWIiOiJhbm9ueW1vdXMiLCJleHAiOjE2MTAyNjAzOTZ9.UDjxSCp6WYbLB9FfHmHCUeDjoy8uHjKY1-kd2vudazhzbzJImW2pufcERRGTg2xy5DTRcEXedCHvVNN_E4XUVQ")
                .build();
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        out.println(entity.getContentType());
        InputStream contentStream = entity.getContent();
        int data = contentStream.read();
        while(data != -1) {
            // do something with data variable
            data = contentStream.read(); // read next byte
            out.println(data);
        }
    }

}