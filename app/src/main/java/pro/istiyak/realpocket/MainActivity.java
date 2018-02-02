package pro.istiyak.realpocket;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Locale;

import pro.istiyak.realpocket.MathPack.DecimalDigitsInputFilter;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Spinner spinner_firstChose, spinner_secondChose;
    HashMap<String, String> hm;
    EditText edt_firstCountry, edt_secondCountry;
    TextView txtview_result, TextView_date;
    String date;
    private TextToSpeech tts;
    ImageButton tts_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hm = new HashMap<String, String>();
        initilize();
        getOnlineMoneyData();
        addTextWatcher();
        spinnerListener();
        buttonListener();

    }

    private void initilize() {

//        getActionBar().setIcon(R.drawable.money);

        spinner_firstChose = (Spinner) findViewById(R.id.spinner_firstChose);
        spinner_secondChose = (Spinner) findViewById(R.id.spinner_secondChose);
        edt_firstCountry = (EditText) findViewById(R.id.edt_firstCountry);
        edt_secondCountry = (EditText) findViewById(R.id.edt_secondCountry);
        txtview_result = (TextView) findViewById(R.id.txtview_result);
        TextView_date = (TextView) findViewById(R.id.TextView_date);
        tts_button = (ImageButton) findViewById(R.id.tts_button);
        tts = new TextToSpeech(this, this);

        edt_secondCountry.setEnabled(false);
        edt_firstCountry.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        spinner_secondChose.setSelection(0);
        spinner_firstChose.setSelection(1);

    }

    private void addTextWatcher() {

        edt_firstCountry.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                calculateAndSetResult();
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });


    }

    private void spinnerListener() {

        spinner_firstChose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAndSetResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_secondChose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAndSetResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void buttonListener() {

        tts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtview_result.getText().toString().equals("")) {
                    tts.speak(txtview_result.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    RunAnimation(R.anim.clockwise,txtview_result);
                }

            }
        });
    }

    private void calculateAndSetResult() {


        if (edt_firstCountry.getText().toString().equals("")) {
            edt_firstCountry.setText("0");
        }

        if (!edt_firstCountry.getText().toString().equals("") || !edt_secondCountry.getText().toString().equals("")) {

            String inititCurrency = spinner_firstChose.getSelectedItem().toString();
            String targetCurrency = spinner_secondChose.getSelectedItem().toString();


            try {
                double baseRate = Double.valueOf(hm.get("USD"));
                double initRate = Double.valueOf(hm.get(inititCurrency));
                double targetRate = Double.valueOf(hm.get(targetCurrency));
                double first_input = Double.valueOf(edt_firstCountry.getText().toString());
                String resultFinal = String.valueOf(String.format("%.2f", ((targetRate * first_input) / initRate)));
                edt_secondCountry.setText(resultFinal);
                txtview_result.setText(edt_firstCountry.getText().toString() + " "
                        + inititCurrency + " = "+ resultFinal + " " + targetCurrency);
                RunAnimation(R.anim.blink,txtview_result);

            } catch (Exception e) {


            }

            //edt_secondCountry.setText(String.valueOf(((targetRate*first_input)/initRate)));

            // Toast.makeText(this, currency1+"---"+currency2, Toast.LENGTH_SHORT).show();
            // txtview_result.setText(currency1+"="+currency2);

        }
    }

    private void RunAnimation(int rID,TextView textView)
    {
        Animation a = AnimationUtils.loadAnimation(this,rID);
        a.reset();
        textView.clearAnimation();
        textView.startAnimation(a);
    }


    private void getOnlineMoneyData() {

        hm.clear();
        hm.put("USD", "1");
        hm.put("BDT", "85.45");

        String url = "https://api.fixer.io/latest?base=USD";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Tag", response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object

                    date = response.getString("date");
                    JSONObject phone = response.getJSONObject("rates");
                    String BGN = phone.getString("BGN");
                    String BRL = phone.getString("BRL");
                    String CAD = phone.getString("CAD");
                    String CHF = phone.getString("CHF");
                    String INR = phone.getString("INR");

                    hm.put("BGN", BGN);
                    hm.put("BRL", BRL);
                    hm.put("CAD", CAD);
                    hm.put("CHF", CHF);
                    hm.put("INR", INR);


                    TextView_date.setText("Last updated on " + date);
                    RunAnimation(R.anim.fade,TextView_date);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    @Override
    public void onInit(int status) {
        //check for successful instantiation
        if (status == TextToSpeech.SUCCESS) {
            if (tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.US);
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }
}

