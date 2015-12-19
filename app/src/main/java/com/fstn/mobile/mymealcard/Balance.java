package com.fstn.mobile.mymealcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/*----------------------------------------------------
| Class : Balance
| Author: JPF, 2014
+----------------------------------------------------- */
public class Balance extends Activity {

    //Const



    // new activity controls
    TextView txtBalance;
    TextView txtBalanceDate;
    TextView txtBalanceHour;
    TextView txtMessage;
    TextView txtMovs;
    ImageView imgBall;
    ImageView imgMovs;

    ProgressBar progBar;
    MenuItem itemRefresh;

    //globals
    SharedPreferences settings;
    // card info
    CardInfo card = new CardInfo();

    // self
    Balance me;


    /*----------------------------------------------------
    | Method: OnCreate
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // set visible activity
        super.onCreate(savedInstanceState);
        me = this;
        setContentView(R.layout.activity_summary);

        // Ensure cookie for http requests
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        // initialize settings
        settings = getSharedPreferences(DefaultSettings.PREFS_NAME, 0);

        // Get reference do layout controls
        // saldo
        txtBalance = (TextView) findViewById(R.id.txtBalance);
        imgBall = (ImageView) findViewById(R.id.imgBalance);

        imgMovs= (ImageView) findViewById(R.id.imageViewMovs);

        txtBalanceDate = (TextView) findViewById(R.id.txtBalanceDate);
        txtBalanceHour = (TextView) findViewById(R.id.txtBalanceHour);
        txtMovs = (TextView) findViewById(R.id.txtMovs);

        txtMessage = (TextView) findViewById(R.id.txtMessage);


        progBar = (ProgressBar) findViewById(R.id.progressBar);

        txtBalance.setText(settings.getString(DefaultSettings.STOREDBALANCE, DefaultSettings.EURONULL));
        txtBalanceDate.setText(settings.getString(DefaultSettings.STOREDDATE, DefaultSettings.NO_UPDATE_DATE));
        txtBalanceHour.setText(settings.getString(DefaultSettings.STOREDHOUR, DefaultSettings.NO_UPDATE_DATE));



        // check if we have card configured
        // if not launch definitions
        // get Stored Stuff
        card.cardNumber = settings.getString(DefaultSettings.CARDNUMBER, DefaultSettings.EMPTY);
        card.cardCVV = settings.getString(DefaultSettings.CARDCVV, DefaultSettings.EMPTY);

        if (card.cardNumber.length() != DefaultSettings.CARD_LEN || card.cardCVV.length() != DefaultSettings.CVV_LEN) {
            Intent intent = new Intent(this, CardActivity.class);
            startActivity(intent);
        }


        // add listener for transactions
        txtMovs.setOnClickListener(new View.OnClickListener() {
            //            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(me, TransactionsActivity.class);
                startActivity(intent);
            }
        });

        imgMovs.setOnClickListener(new View.OnClickListener() {
            //            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(me, TransactionsActivity.class);
                startActivity(intent);
            }
        });

        txtBalance.setOnClickListener(new View.OnClickListener() {
            //            @Override
            public void onClick(View arg0) {onClickAction(null);}
        });
        imgBall.setOnClickListener(new View.OnClickListener() {
            //            @Override
            public void onClick(View arg0) {onClickAction(null);}
        });



    }

    /*----------------------------------------------------
    | Method: onCreateOptionsMenu
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.balance, menu);
        return true;
    }

    /*----------------------------------------------------
    | Method: onOptionsItemSelected
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about : {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.ABOUT),Toast.LENGTH_LONG).show();
                return true;
            }

            case R.id.action_settings: {
                Intent intent = new Intent(this,CardActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_refresh: {
                // set global variable for further user
                itemRefresh = item;
                onClickAction(null);
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
}




    /*----------------------------------------------------
    | Method: btGetBalance_Onclick
    | Description: Button handler preparation
    | Author: JPF, 2014
    +----------------------------------------------------- */
    public void onClickAction(View arg0) {


        // clear balance
        txtBalance.setText(DefaultSettings.EURONULL);
        txtMessage.setText(DefaultSettings.EMPTY);

        // get Stored Stuff
        settings = getSharedPreferences(DefaultSettings.PREFS_NAME, 0);
        card.cardNumber = settings.getString(DefaultSettings.CARDNUMBER, DefaultSettings.EMPTY);
        card.cardCVV = settings.getString(DefaultSettings.CARDCVV, DefaultSettings.EMPTY);

        if (card.cardNumber.length() != DefaultSettings.CARD_LEN || card.cardCVV.length() != DefaultSettings.CVV_LEN) {
            // Display message
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.NOCARD),Toast.LENGTH_LONG).show();
            return;
        }

        // Display message
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.WAIT),Toast.LENGTH_LONG).show();

        // Check Network
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.NO_NET),Toast.LENGTH_SHORT).show();
            return;
        }

        // GET DEFAULT card info
        // PROGRESS BAR ZZ
        // Do not show progress bar in emulator - too slow
        if (!getResources().getString(R.string.HARDWARE_EMU).equals(Build.HARDWARE)) {
            progBar.setVisibility(View.VISIBLE);
        }
        if (itemRefresh!=null) itemRefresh.setEnabled(false);
        txtBalance.setEnabled(false);
        imgBall.setEnabled(false);


        try {
            new  balanceGetter().execute();
        }
        catch (Exception e){
            e.printStackTrace();
            progBar.setVisibility(View.INVISIBLE);
            if (itemRefresh!=null) itemRefresh.setEnabled(true);
            txtBalance.setEnabled(true);
            imgBall.setEnabled(true);

            Toast.makeText(getApplicationContext(),getResources().getString(R.string.CANT_GET_BALANCE),Toast.LENGTH_SHORT).show();
        }
    }


    public void enableButtons() {
    }

    public void disableButtons() {
    }



    /*----------------------------------------------------
    | Class : wwwHelper
    | Author: JPF, 2014
    +----------------------------------------------------- */
    /// INNER CLASS
    public class balanceGetter extends AsyncTask<String, Void, String> {

        private static final String DEBUG_TAG = "balanceGetter";

        private String lastResponse =DefaultSettings.EMPTY;
        private final int OK =0;
        private final int NOTOK =1;


        /*----------------------------------------------------
        | Method: getLastResponse
        | Description: Get last post response
        | Author: JPF, 2014
        +----------------------------------------------------- */
        public String getLastResponse() {
            return lastResponse;
        }
        /*----------------------------------------------------
        | Method: downloadUrl
        | Description: Post URL
        | Author: JPF, 2014
        +----------------------------------------------------- */
        private int downloadUrl(String myurl)  {
            InputStream is = null;

            // Only display the first x characters of the retrieved
            // web page content.



            HttpURLConnection conn= null;
            try {

                // parse URL
                URL url = new URL(myurl);

                // create a connection
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(DefaultSettings.readTimeout /* milliseconds */);
                conn.setConnectTimeout(DefaultSettings.connectTimeout /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type",getResources().getString(R.string.URL_CONTENT_TYPE));
                conn.setRequestProperty("User-Agent",getResources().getString(R.string.URL_USER_AGENT));

                // post data
                conn.setDoInput(true);

                // Starts the query
                conn.connect();

                // check response
                int response = conn.getResponseCode();

                Log.d(DEBUG_TAG, "The response is: " + response);
                if (DefaultSettings.HTTP_OK!=response) return NOTOK;

                is = conn.getInputStream();



                // Convert the InputStream into a string
                String contentAsString;
                contentAsString = readIt(is);
                //Log.d(DEBUG_TAG, "The return is: " + contentAsString);
                this.lastResponse = contentAsString;
                conn.disconnect();
                return OK;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                e.printStackTrace();
                return NOTOK;
            }

            finally {
                if (is != null) {
                    try {is.close();} catch (Exception ignored) {}
                }
                if (conn!=null) {
                    conn.disconnect();
                }

            }
        }



        /*----------------------------------------------------
        | Method: readIt
        | Description: Reads an InputStream and converts it to a String.
        | Author: JPF, 2014
        +----------------------------------------------------- */
        private String readIt(InputStream stream) throws IOException {

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();
            return out.toString();

        }


        /*----------------------------------------------------
        | Method: doInBackground
        | Description: Get url and parse data
        | Author: JPF, 2014
        +----------------------------------------------------- */
        @Override
        protected String doInBackground(String... strings) {
            String result =DefaultSettings.EMPTY;
            String resultLogin;// =EMPTY;
            String resultBalance ;//=EMPTY;

            card.status = CardInfo.BALANCE_OK;
            card.lastHttpResponseText =DefaultSettings.EMPTY;

            String urlLogin;
            urlLogin = getResources().getString(R.string.URL_LOGIN);
            urlLogin = urlLogin.replace(DefaultSettings.USER,card.cardNumber.trim());
            urlLogin = urlLogin.replace(DefaultSettings.PASS,card.cardCVV.trim() + card.cardCVV.trim());

            String urlBalance;
            urlBalance = getResources().getString(R.string.URL_BALANCE);

            try {
                if (downloadUrl(urlLogin)!=OK) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.LOGIN_NO_NET), Toast.LENGTH_SHORT).show();
                    card.status = CardInfo.BALANCE_ERROR;
                    card.lastMessage = getResources().getString(R.string.LOGIN_NO_NET);
                    return DefaultSettings.EMPTY;
            }

                // testar correcto login
                resultLogin = getLastResponse();
                String keyLogin1 = getResources().getString(R.string.KEY_LOGIN_TOKEN); //"var sessionId

                if  (resultLogin.indexOf(keyLogin1,0)<=0) {
                    // "Falha no Login";
                    card.status = CardInfo.BALANCE_ERROR;
                    card.lastMessage = getResources().getString(R.string.LOGIN_ERROR);
                    return result;
                }


                if (downloadUrl(urlBalance)!=OK) {
                    card.status = CardInfo.BALANCE_ERROR;
                    card.lastMessage = getResources().getString(R.string.BALANCE_NO_NET);
                    return DefaultSettings.EMPTY;
                }

                //testar correctaresposta de saldo
                //parse
                resultBalance = getLastResponse();
                card.lastHttpResponseText = resultBalance;

                //Log.d(DEBUG_TAG,"OUT:" + resultBalance.substring(1900,5500));

                String keyString1 = getResources().getString(R.string.KEY_BALANCE_TOKEN_START);//"<span class=\"money\">";
                String keyString2 = getResources().getString(R.string.KEY_BALANCE_TOKEN_END); // "</span>";



                if (resultBalance.equals(DefaultSettings.EMPTY)) {
                    //"Impossivel obter saldo";
                    card.status = CardInfo.BALANCE_ERROR;
                    card.lastMessage = getResources().getString(R.string.CANT_GET_BALANCE);
                    return result;
                }
                int index_start = resultBalance.indexOf(keyString1,0) + keyString1.length();
                int index_end = resultBalance.indexOf(keyString2,index_start );

                //Log.d(DEBUG_TAG, "index_start " + index_start);
                //Log.d(DEBUG_TAG, "index_end " + index_end);
                //Log.d(DEBUG_TAG, "Build.HARDWARE:" + Build.HARDWARE);



                if (index_start <= keyString1.length() || index_end<=0) {
                    card.status = CardInfo.BALANCE_ERROR;
                    card.lastMessage = getResources().getString(R.string.CANT_GET_BALANCE);
                    return result;
                }




                resultBalance = resultBalance.substring(index_start, index_end);
                // replace name for symbol
                resultBalance = resultBalance.replace(DefaultSettings.EURO,DefaultSettings.EURO_SYMBOL);

                card.status = CardInfo.BALANCE_OK;
                card.lastMessage = DefaultSettings.EMPTY;
                card.balance = resultBalance;

            }catch (Exception e){
                e.printStackTrace();
                card.status = CardInfo.BALANCE_ERROR;
                card.lastMessage = getResources().getString(R.string.CANT_GET_BALANCE);

            }

            return result;
        }


        /*----------------------------------------------------
        | Method: onPostExecute
        | Description: Updates Balance
        | Author: JPF, 2014
        +----------------------------------------------------- */
        @Override
        protected void onPostExecute(String result) {

            DateFormat df = new SimpleDateFormat(DefaultSettings.DATE_FORMAT);
            DateFormat hf = new SimpleDateFormat(DefaultSettings.HOUR_FORMAT);
            String mydate = df.format(Calendar.getInstance().getTime());
            String myhour = hf.format(Calendar.getInstance().getTime());

            if (card.status == CardInfo.BALANCE_OK) {
                txtBalance.setText(card.balance);
                txtBalanceDate.setText(mydate);
                txtBalanceHour.setText(myhour);

                // save balance and date
                //store preferences
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DefaultSettings.STOREDBALANCE, card.balance);
                editor.putString(DefaultSettings.STOREDDATE,mydate);
                editor.putString(DefaultSettings.STOREDHOUR,myhour);
                editor.putString(DefaultSettings.STOREDTRANSACTIONS,card.lastHttpResponseText);

                editor.apply();
            } else {
                txtMessage.setText(card.lastMessage);
            }

            progBar.setVisibility(View.INVISIBLE);
            if (itemRefresh!=null) itemRefresh.setEnabled(true);
            txtBalance.setEnabled(true);
            imgBall.setEnabled(true);





        }
    }


}
