package com.example.vanessa.p_etika;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vanessa.p_etika.paypal.PayPalConfig;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;


public class Payment extends AppCompatActivity {

    Button mpesapaybutton, paypalbtn, exitbtn;

    /*********
    * PAYPAL DIALOG
     * * * ************/
    EditText paypalamount, paypalname, paypaldate, paypalcvc;
    Button paypalpay;
    String paymentAmount;
    String mCost;

//    intent request code to track onactivity result method
    public static final int PAYPAL_REQUEST_CODE = 123;

//    Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    /*********
     * MPESA DIALOG
     * ************/
    EditText mpesaeditnumber, mpesaeditamount;
    Button mpesabtn, mpesacancel;

    private String key;

    public Payment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment);

//        mpesabtn = (Button)findViewById(R.id.mpesabtn);
//        mpesabtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mpesaDialog();
//            }
//        });

        paypalbtn = (Button)findViewById(R.id.paypalbtn);
        paypalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paypalDialog();
            }
        });

        exitbtn = (Button)findViewById(R.id.cancelpay);
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Payment.this, HomeActivity.class));
            }
        });

//        return view;
    }

//    private void mpesaDialog() {
//
//        AlertDialog.Builder mpesadialog = new AlertDialog.Builder(this);
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View mpesalayout = inflater.inflate(R.layout.mpesadisplay, null);
//
//        mpesaeditnumber = mpesalayout.findViewById(R.id.number);
//        mpesaeditamount = mpesalayout.findViewById(R.id.amount);
//        mpesapaybutton = mpesalayout.findViewById(R.id.pay);
//        mpesacancel = mpesalayout.findViewById(R.id.cancel);
//
//        mpesadialog.setView(mpesalayout);
//
//        mpesapaybutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long testAmount = Long.parseLong(mpesaeditamount.getText().toString().trim());
//                String testNumber = mpesaeditnumber.getText().toString().trim();
//
//                try {
////                    Client client = new Client(this.getBaseContext());
//                    Client client = new Client(getBaseContext());
//                    client.stkPush(100, testNumber, "Hello", "World");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        mpesacancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Payment.this, HomeActivity.class));
//            }
//        });
//
//
//        mpesadialog.show();
//
//    }

    private void paypalDialog() {

        AlertDialog.Builder paypaldialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View paypallayout = inflater.inflate(R.layout.paypaldisplay, null);

        paypalamount = paypallayout.findViewById(R.id.paypalamount);
        paypalname = paypallayout.findViewById(R.id.cardholdername);
//        paypaldate = paypallayout.findViewById(R.id.date);
//        paypalcvc = paypallayout.findViewById(R.id.cvc);

        paypaldialog.setView(paypallayout);

        mCost = getIntent().getStringExtra(BottomSheetPatientRideFrag.EXTRA_COST);
        String j = mCost;
        paypalamount.setText(j);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        Button pay_bill = paypallayout.findViewById(R.id.paypalpay);
        pay_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayment();
            }
        });

        paypaldialog.show();

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void getPayment() {
        //Getting the amount from editText
        paymentAmount = paypalamount.getText().toString();

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Ambulance Service Trip",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, PayPalConfirmation.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
}
