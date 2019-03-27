package com.cw.ssl_conn;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    TextView resultText;
    CharSequence cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLL_connection_task sll_connection_task = new SLL_connection_task();
        sll_connection_task.execute();

        resultText = findViewById(R.id.result);
    }


    class SLL_connection_task extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            String result = new String();
            InputStream in = null;
//            String urlStr =  "https://10.1.1.3:8443/ProjectWeb/viewNote/viewNote_json.jsp";
            String urlStr =  "https://project.ddns.net:8443/ProjectWeb/viewNote/viewNote_json.jsp";


            try {
                URL url = new URL(urlStr);
                trustEveryone();
                URLConnection urlConnection = url.openConnection();
                in = urlConnection.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(in == null)
                return null;

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String inputLine;

            try {
                while ((inputLine = br.readLine()) != null) {
                    System.out.println("MainActivity / inputLine = " + inputLine);
                    result += inputLine;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            System.out.println("MainActivity / result final = " + result);
            cs = result;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            resultText.setText(cs);
        }
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});

            SSLContext context = SSLContext.getInstance("TLS");

            context.init(null, new X509TrustManager[]
                {
                    new X509TrustManager()
                    {
                        public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException
                        {}

                        public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException
                        {}

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
                },
                    new SecureRandom()
            );

            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());

        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
