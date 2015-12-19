package com.fstn.mobile.mymealcard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/*----------------------------------------------------
| Class : TransactionsActivity
| Author: JPF, 2014
+----------------------------------------------------- */
public class TransactionsActivity extends Activity {

    //globals
    SharedPreferences settings;

    // transaction List
    private List<CardTransaction> myTrxs = new ArrayList<CardTransaction>();


    /*----------------------------------------------------
    | Operation : onCreate
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        populateList();
        populateListView();
    }


    /*----------------------------------------------------
    | Operation : populateList
    | Author: JPF, 2014
    +----------------------------------------------------- */
    private void populateList() {

        // get the data
        String transactionData;

        // get data from storage
        settings = getSharedPreferences(DefaultSettings.PREFS_NAME, 0);
        transactionData = settings.getString(DefaultSettings.STOREDTRANSACTIONS, DefaultSettings.EMPTY);

        //Log.d("populateListView:",""+transactionData.length());
        //Log.d("populateListView:",transactionData);

        // empty returns
        myTrxs.clear();
        if (transactionData.equals(DefaultSettings.EMPTY)) return;

        //
        //parse
        //
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature(Xml.FEATURE_RELAXED, true);
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            // set content to parse
            xpp.setInput(new StringReader(transactionData));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                //search tables
                // <tr class="off> OR  <tr class="on">
                if ((eventType == XmlPullParser.START_TAG)
                        && xpp.getName().equalsIgnoreCase(DefaultSettings.PARSER_TR)
                        && xpp.getAttributeCount() == 1
                        && xpp.getAttributeName(0).equalsIgnoreCase(DefaultSettings.PARSER_CLASS)
                        && (xpp.getAttributeValue(0).equalsIgnoreCase(DefaultSettings.PARSER_OFF) || xpp.getAttributeValue(0).equalsIgnoreCase(DefaultSettings.PARSER_ON))
                        ) {

                    // this is a row
                    CardTransaction ct = new CardTransaction();

                    // get <TD> Data
                    for (eventType = xpp.next();eventType != XmlPullParser.END_DOCUMENT && eventType != XmlPullParser.START_TAG;eventType=xpp.next());
                    xpp.next(); //text
                    ct.setTrxDate(xpp.getText().trim());

                    // get <TD> Data
                    for (eventType = xpp.next();eventType != XmlPullParser.END_DOCUMENT && eventType != XmlPullParser.START_TAG;eventType=xpp.next());
                    xpp.next(); //text
                    ct.setTrxDate(xpp.getText().trim());

                    // get <TD> Descricao
                    for (eventType = xpp.next();eventType != XmlPullParser.END_DOCUMENT && eventType != XmlPullParser.START_TAG;eventType=xpp.next());
                    xpp.next(); //text
                    String desc = xpp.getText().trim();

                    // optimize descritptions
                    for (int i=0; i<DefaultSettings.PARSER_MOVS.length;i++) {
                        int index = desc.indexOf(DefaultSettings.PARSER_MOVS[i], 0);
                        if (index > 0) {
                            desc = desc.substring(index + DefaultSettings.PARSER_MOVS[i].length(), desc.length());
                        }
                    }
                    ct.setTrxDescription(desc);

                    // get <TD> Montante
                    for (eventType = xpp.next();eventType != XmlPullParser.END_DOCUMENT && eventType != XmlPullParser.START_TAG;eventType=xpp.next());
                    xpp.next(); //text
                    ct.setTrxAmount(xpp.getText().trim());

                    // icon based on signal
                    String signal = ct.getTrxAmount();
                    if (!signal.isEmpty() && !signal.equals(DefaultSettings.EMPTY) && signal.substring(0, 1).equals("-")) {
                        ct.setTrxIcon(R.drawable.euro_red);
                    } else {
                        ct.setTrxIcon(R.drawable.euro_green);
                    }

                    // add the current transaction
                    myTrxs.add(ct);
                }

                // parse next node
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*----------------------------------------------------
    | Operation : populateListView
    | Author: JPF, 2014
    +----------------------------------------------------- */
    private void populateListView() {

        ArrayAdapter<CardTransaction> adapter = new MyTransactionListAdapter();
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(adapter);

    }


    /*----------------------------------------------------
    | Operation : onCreateOptionsMenu
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transactions, menu);
        return true;
    }

    /*----------------------------------------------------
    | Operation : onOptionsItemSelected
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about: {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ABOUT), Toast.LENGTH_LONG).show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    /*----------------------------------------------------
    | Class : MyTransactionListAdapter
    | Author: JPF, 2014
    +----------------------------------------------------- */
    private class MyTransactionListAdapter extends ArrayAdapter<CardTransaction> {
        public MyTransactionListAdapter() {
            super(TransactionsActivity.this, R.layout.item_view, myTrxs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // make sure we have a view
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            // find transaction
            CardTransaction currentTRX = myTrxs.get(position);

            // fill the view
            ImageView icon = (ImageView) itemView.findViewById(R.id.imageIcon);
            icon.setImageResource(currentTRX.getTrxIcon());

            TextView txtMov = (TextView) itemView.findViewById(R.id.txtMovimento);
            txtMov.setText(currentTRX.getTrxDescription());

            TextView txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtDate.setText(currentTRX.getTrxDate());

            TextView txtAmount = (TextView) itemView.findViewById(R.id.txtAmount);
            txtAmount.setText(currentTRX.getTrxAmount());


            return itemView;
        }

    }

}