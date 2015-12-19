package com.fstn.mobile.mymealcard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/*----------------------------------------------------
| Class : CardActivity
| Author: JPF, 2014
+----------------------------------------------------- */
public class CardActivity extends Activity implements View.OnClickListener{

    // Activity Controls
    TextView tvCard;
    TextView tvCVV;
    Button btCardSave;


    //globals
    SharedPreferences settings;


    /*----------------------------------------------------
    | Method: onCreate
    | Description: Button handler preparation
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // Get reference do layout controls
        tvCard = (TextView) findViewById(R.id.editTxtNumeroCartao);
        tvCVV = (TextView) findViewById(R.id.editTxtCVV);
        btCardSave = (Button) findViewById(R.id.btCardSave);

        settings = getSharedPreferences(DefaultSettings.PREFS_NAME, 0);
        tvCard.setText(settings.getString(DefaultSettings.CARDNUMBER, DefaultSettings.EMPTY));
        tvCVV.setText(settings.getString(DefaultSettings.CARDCVV, DefaultSettings.EMPTY));

        btCardSave.setEnabled((tvCard.getText().toString().trim().length() == DefaultSettings.CARD_LEN) && (tvCVV.getText().toString().trim().length() == DefaultSettings.CVV_LEN));

        /*----------------------------------------------------
        | addlistener for tvCard
        | Author: JPF, 2014
        +----------------------------------------------------- */
        tvCard.addTextChangedListener(new MyTextWatcher());
        tvCVV.addTextChangedListener(new MyTextWatcher());

        // prepare button click handler
        btCardSave.setOnClickListener(this);
        //btGetBalance_Onclick();
    }


    /*----------------------------------------------------
    | Method: onClick
    | Description: Button handler preparation
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    public void onClick(View view) {
        // Commit the edits!

        //store preferences
        SharedPreferences.Editor editor = settings.edit();

        //save values
        editor.putString(DefaultSettings.CARDNUMBER, tvCard.getText().toString().trim());
        editor.putString(DefaultSettings.CARDCVV, tvCVV.getText().toString().trim());
        editor.apply();

        Toast.makeText(getApplicationContext(),getResources().getString(R.string.STORED),Toast.LENGTH_LONG).show();
        this.finish();
    }

    /*----------------------------------------------------
    | Class: MyTextWatcher
    | Description: Button handler preparation
    | Author: JPF, 2014
    +----------------------------------------------------- */
    public class MyTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            btCardSave.setEnabled((tvCard.getText().toString().trim().length()==DefaultSettings.CARD_LEN) && (tvCVV.getText().toString().trim().length()==DefaultSettings.CVV_LEN));
        }
    }


    /*----------------------------------------------------
    | Method: onCreateOptionsMenu
    | Description: Button handler preparation
    | Author: JPF, 2014
    +----------------------------------------------------- */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return true;
    }

    /*----------------------------------------------------
    | Method: onOptionsItemSelected
    | Description: Button handler preparation
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
        }
        return super.onOptionsItemSelected(item);
    }

}
