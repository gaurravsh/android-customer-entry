package dev.shaga.jackitowners;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button addButton = findViewById(R.id.add);

        EditText partName  = findViewById(R.id.partName);
        EditText partAmount = findViewById(R.id.partAmount);
        EditText partCost = findViewById(R.id.partCost);
        LinearLayout itemsContainer = findViewById(R.id.containerForItems);
        TextView totalAmount = findViewById(R.id.totalAmountValue);
        TextView totalCost = findViewById(R.id.totalCostValue);
        TextView revenue = findViewById(R.id.revenueValue);
        TextView profitMargin = findViewById(R.id.profitMarginValue);
        EditText discount = findViewById(R.id.discountValue);
        TextView finalPayment = findViewById(R.id.finalPaymentValue);
        Spinner paymentStatus = findViewById(R.id.spinner_payment_status);
        LinearLayout paymentModeLayout = findViewById(R.id.paymentModeLayout);
        EditText dateOfService = findViewById(R.id.dateOfServiceField);
        EditText dateOfDelivery = findViewById(R.id.dateOfDeliveryField);
        EditText insuranceExpiryDate = findViewById(R.id.insuranceExpiryDateField);
        EditText pincode = findViewById(R.id.pincodeField);

        pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==6){
                    findViewById(R.id.contactNumberField).requestFocus();
                }

            }
        });



        dateOfService.setOnClickListener(view -> {
            final Date currentTime = Calendar.getInstance().getTime();
            int year = currentTime.getYear()+1900;
            int month = currentTime.getMonth();
            int day = currentTime.getDate();
            DatePickerDialog dateOfServicePicker = new DatePickerDialog(MainActivity.this, (datePicker, i, i1, i2) -> {

                String dateToSet = i+"-"+i1+"-"+i2;
                dateOfService.setText(dateToSet);
            }, year,month,day);


            dateOfServicePicker.show();
        });


        dateOfDelivery.setOnClickListener(view -> {
            final Date currentTime = Calendar.getInstance().getTime();
            int year = currentTime.getYear()+1900;
            int month = currentTime.getMonth();
            int day = currentTime.getDate();
            DatePickerDialog dateOfDeliveryPicker = new DatePickerDialog(MainActivity.this, (datePicker, i, i1, i2) -> {

                String dateToSet = i+"-"+i1+"-"+i2;
                dateOfDelivery.setText(dateToSet);
            }, year,month,day);


            dateOfDeliveryPicker.show();
        });

        insuranceExpiryDate.setOnClickListener(view -> {
            final Date currentTime = Calendar.getInstance().getTime();
            int year = currentTime.getYear()+1900;
            int month = currentTime.getMonth();
            int day = currentTime.getDate();
            DatePickerDialog insuranceExpiryDatePicker = new DatePickerDialog(MainActivity.this, (datePicker, i, i1, i2) -> {

                String dateToSet = i+"-"+i1+"-"+i2;
                insuranceExpiryDate.setText(dateToSet);
            }, year,month,day);


            insuranceExpiryDatePicker.show();
        });



        paymentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(paymentStatus.getSelectedItem().toString().equalsIgnoreCase("received")){
                    paymentModeLayout.setVisibility(View.VISIBLE);
                }
                else{
                    paymentModeLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        totalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                discount.setText("0");
            }
        });

        discount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int discountValue ;
                try{
                    discountValue = Integer.parseInt(editable.toString());
                }
                catch (NumberFormatException e){
                    discountValue= 0;
                }

                int finalPaymentToBeDone = Integer.parseInt(totalAmount.getText().toString()) - discountValue;
                finalPayment.setText(String.valueOf(finalPaymentToBeDone));
            }
        });

        addButton.setOnClickListener(view -> {

            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.row, null);
            TextView itemToAdd = addView.findViewById(R.id.itemsSoFar);
            itemToAdd.setText(String.format("%s / %s / %s",partName.getText(),partAmount.getText(),partCost.getText()));
            Button buttonRemove = addView.findViewById(R.id.remove);

            buttonRemove.setOnClickListener(v -> {
                // revenue = amount - cost
                // profit margin = revenue / total amount

                ((LinearLayout)addView.getParent()).removeView(addView);
                final String[] recordContent = itemToAdd.getText().toString().split("/");
                int oldAmount = Integer.parseInt(totalAmount.getText().toString());
                int oldCost = Integer.parseInt(totalCost.getText().toString());
                final int amountToDeduct = Integer.parseInt(recordContent[1].trim());
                final int costToDeduct = Integer.parseInt(recordContent[2].trim());


                int newAmount = oldAmount - amountToDeduct;
                int newCost = oldCost - costToDeduct;
                totalAmount.setText(String.valueOf(newAmount));
                totalCost.setText(String.valueOf(newCost));

                int revenueGenerated = newAmount - newCost;
                revenue.setText(String.valueOf( revenueGenerated));
                double profitMarginNew = 0.0;
                if(newAmount!=0){
                    profitMarginNew = ( revenueGenerated*100.0 ) / newAmount;
                }
                profitMargin.setText(String.format("%.1f",profitMarginNew));
            });

            itemsContainer.addView(addView);



            int oldAmount = Integer.parseInt(totalAmount.getText().toString());
            int oldCost = Integer.parseInt(totalCost.getText().toString());

            int newAmount = oldAmount + Integer.parseInt(partAmount.getText().toString());
            int newCost = oldCost + Integer.parseInt(partCost.getText().toString());
            totalAmount.setText(String.valueOf(newAmount));
            totalCost.setText(String.valueOf(newCost));

            int revenueGenerated = newAmount - newCost;
            revenue.setText(String.valueOf( revenueGenerated));
            double profitMarginNew = 0.0;
            if(newAmount!=0){
                profitMarginNew = ( revenueGenerated*100.0 ) / newAmount;
            }
            profitMargin.setText(String.format("%.1f",profitMarginNew));
            partName.setText("");
            partAmount.setText("");
            partCost.setText("");
        });
    }
}