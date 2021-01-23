package com.charannarukulla.upiintentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.charannarukulla.upiintentapp.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int PAYCODE = 200;
    ActivityMainBinding bind;
EditText upiid;
EditText name;
EditText message;
EditText amount;
Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    bind=ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(bind.rootv);
        pay=bind.pay;
        upiid=bind.upiid;
        name=bind.Name;
        message=bind.message;
        amount=bind.amount;



    }
    boolean checkFormat(String upiid ){

        int count=0;

        for(int i=0;i<upiid.length();i++){

            if(upiid.charAt(i) =='@'){

                count++;}



        }

        if (count!=1)
            return  false;

        return true;
    }


    public void pay(View view) {
        String id=upiid.getText().toString().trim();

        if(checkFormat(id )){
            //valid UPI ID
        if(    checkFornonemptyfields(upiid.getText().toString(),name.getText().toString(),message.getText().toString(),amount.getText().toString())){
            if(!amount.getText().toString().equals("0")){
          startIntent(upiid.getText().toString(),name.getText().toString(),message.getText().toString(),amount.getText().toString());

            }
            else{
                Toast.makeText(this, "Amount can't be zero", Toast.LENGTH_SHORT).show();
            }
        }

        }
        else{
            //Invalid UPIID
            Toast.makeText(this, "UPI INVALID", Toast.LENGTH_SHORT).show();
            Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content),"INVALID UPI ID PLEASE RECHECK",Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void startIntent(String upiid, String name, String message, String toString3) {

        int random=new Random().nextInt();
       Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiid)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", message)
                .appendQueryParameter("am", toString3)
                .appendQueryParameter("cu", "INR")
               .appendQueryParameter("tr",String.valueOf(random) )
                .build();
        Intent pamentintent=new Intent();

        pamentintent.setData(uri);
        try {
         startActivityForResult(   Intent.createChooser(pamentintent,"Pay using"),PAYCODE);

        }catch (Exception e){
            Toast.makeText(this, "No app found", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkFornonemptyfields(String upiid, String name, String message, String amount) {
        if(upiid.length()==0|| name.length()==0|| message.length()==0|| amount.length()==0){
            Toast.makeText(this, "Enter all filelds ", Toast.LENGTH_SHORT).show();
        return false;
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
 if(requestCode==200  ){

         String extra=data.getStringExtra("response");
        Log.d("responseafterpay",extra);
        String transaction_id=" ";
        String status=" ";
        try {
            transaction_id=extra.substring(extra.indexOf("txnId"),extra.indexOf("&")).toLowerCase();
              status = extra.substring(extra.indexOf("Status"),extra.indexOf("Status")+9   ).toLowerCase();


        }catch (Exception s){
Log.d("errortxnst",s.toString());

        }
        if(transaction_id.equals("txnid=null")){
            status="Status=Fail";
        }
          if(status.equals("status=su")){
            status="Status=Success";
        }
     if(status.equals("status=pe")){
         status="Status=Pending";
     }


           MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(this).setTitle("Status").setMessage(status+"\n"  +transaction_id).setPositiveButton("OK:)", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
               }
           });
           materialAlertDialogBuilder.show();



     }}
        catch (Exception e){
            Log.d("responseafterpay",e.toString());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

     
  
    }
}