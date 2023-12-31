package dev.shaga.jackitowners;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import dev.shaga.jackitowners.model.CustomerServiceDetails;
import dev.shaga.jackitowners.model.PartDetails;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    String displayMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.customerNameField).requestFocus();

        Button addButton = findViewById(R.id.add);
        EditText partName  = findViewById(R.id.partName);
        EditText partAmount = findViewById(R.id.partAmount);
        EditText partCost = findViewById(R.id.partCost);
        LinearLayout itemsContainer = findViewById(R.id.containerForItems);
        TextView totalAmount = findViewById(R.id.totalAmountField);
        TextView totalCost = findViewById(R.id.totalCostValue);
        TextView revenue = findViewById(R.id.revenueValue);
        TextView profitMargin = findViewById(R.id.profitMarginValue);
        EditText discount = findViewById(R.id.discountValue);
        TextView finalPayment = findViewById(R.id.finalPaymentValue);
        Spinner paymentStatusSpinner = findViewById(R.id.spinner_payment_status);
        LinearLayout paymentModeLayout = findViewById(R.id.paymentModeLayout);
        EditText dateOfService = findViewById(R.id.dateOfServiceField);
        EditText dateOfDelivery = findViewById(R.id.dateOfDeliveryField);
        EditText insuranceExpiryDate = findViewById(R.id.insuranceExpiryDateField);
        EditText pincode = findViewById(R.id.pincodeField);
        Spinner paymentModeSpinner = findViewById(R.id.spinner_payment_mode);

        Button submitButton = findViewById(R.id.submitButton);

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

                String dateToSet = i+"-"+(i1+1)+"-"+i2;
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

                String dateToSet = i+"-"+(i1+1)+"-"+i2;
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

                String dateToSet = i+"-"+(i1+1)+"-"+i2;
                insuranceExpiryDate.setText(dateToSet);
            }, year,month,day);


            insuranceExpiryDatePicker.show();
        });

        paymentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(paymentStatusSpinner.getSelectedItem().toString().equalsIgnoreCase("received")){
                    paymentModeSpinner.setSelection(1);
                    paymentModeLayout.setVisibility(View.VISIBLE);
                }
                else{
                    // This is required so that empty string is passed to backend, when payment status is pending.
                    // Otherwise, it would send payment mode as Cash/Gpay even when payment is pending.
                    paymentModeSpinner.setSelection(0);
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
            partName.requestFocus();
        });

        submitButton.setOnClickListener(view -> {


            String visitTypeData = ((Spinner)findViewById(R.id.spinner_visit_type)).getSelectedItem().toString();
            String areaData = ((EditText)findViewById(R.id.customerAreaField)).getText().toString();
            String pincodeData = pincode.getText().toString();
            String customerNameData = ((EditText)findViewById(R.id.customerNameField)).getText().toString();
            String contactNumberData = ((EditText)findViewById(R.id.contactNumberField)).getText().toString();
            String emailIdData = ((EditText)findViewById(R.id.emailField)).getText().toString();
            String phoneData = ((EditText)findViewById(R.id.phoneNumberField)).getText().toString();
            String customerTypeData = ((EditText)findViewById(R.id.customerTypeField)).getText().toString();

            String amcYesOrNoData = getSelectedRadioButtonValue(R.id.amcGroup);
            String vehicleCompanyData = ((EditText)findViewById(R.id.companyField)).getText().toString();
            String vehicleModelData = ((EditText)findViewById(R.id.modelField)).getText().toString();
            String vehicleColorData = ((EditText)findViewById(R.id.colorField)).getText().toString();
            String bikeNumberData = ((EditText)findViewById(R.id.bikeNumberField)).getText().toString();
            String insuranceExpiryDateData = insuranceExpiryDate.getText().toString();

            String insuranceExpiredYesOrNoData = getSelectedRadioButtonValue(R.id.insuranceExpiredRadioGroup);
            String insuranceCompanyData = ((EditText)findViewById(R.id.insuranceCompanyField)).getText().toString();
            String serviceTypeData = ((Spinner)findViewById(R.id.spinner_service_type)).getSelectedItem().toString();
            List<PartDetails> itemListData = getItemListData(itemsContainer);
            String dateOfServiceData = dateOfService.getText().toString();
            String deliveryDateData = dateOfDelivery.getText().toString();
            String totalAmountData = totalAmount.getText().toString();
            String totalCostData = totalCost.getText().toString();
            String revenueData = revenue.getText().toString();
            String profitMarginData = profitMargin.getText().toString();
            String discountData = discount.getText().toString();
            String finalPaymentData = finalPayment.getText().toString();
            String paymentStatusData = paymentStatusSpinner.getSelectedItem().toString();
            String paymentModeData = paymentModeSpinner.getSelectedItem().toString();
            String mechanicData = ((Spinner)findViewById(R.id.spinner_mechanic_name)).getSelectedItem().toString();

            StringBuilder warningBuilder = new StringBuilder();
            if(visitTypeData.isBlank()) warningBuilder.append("Visit Type is empty\n");
            if(customerNameData.isBlank()) warningBuilder.append("Customer Name is empty\n");
            if(phoneData.isBlank() && contactNumberData.isBlank()) warningBuilder.append("Phone No or Contact No should be provided\n");
            if(bikeNumberData.isBlank()) warningBuilder.append("Bike Number is empty\n");
            if(itemListData.isEmpty()) warningBuilder.append("Part/Item details are empty\n");
            if(dateOfServiceData.isBlank()) warningBuilder.append("Service Date is empty\n");

            String warning = warningBuilder.toString();
            boolean isDataCorrect;

            if(warning.isBlank()) {
                isDataCorrect = true;
                CustomerServiceDetails customerServiceDetails = new CustomerServiceDetails();
                customerServiceDetails.setVisitType(visitTypeData);
                customerServiceDetails.setArea(areaData);
                customerServiceDetails.setPincode(pincodeData);
                customerServiceDetails.setName(customerNameData);
                customerServiceDetails.setContactNumber(contactNumberData);
                customerServiceDetails.setEmailId(emailIdData);
                customerServiceDetails.setPhoneNumber(phoneData);
                customerServiceDetails.setCustomerType(customerTypeData);
                customerServiceDetails.setAmc(amcYesOrNoData);
                customerServiceDetails.setVehicleCompany(vehicleCompanyData);
                customerServiceDetails.setVehicleModel(vehicleModelData);
                customerServiceDetails.setVehicleColor(vehicleColorData);
                customerServiceDetails.setBikeNumber(bikeNumberData);
                customerServiceDetails.setBikeInsuranceExpiredYesOrNo(insuranceExpiredYesOrNoData);
                customerServiceDetails.setInsuranceExpiryDate(insuranceExpiryDateData);
                customerServiceDetails.setInsuranceCompanyName(insuranceCompanyData);
                customerServiceDetails.setTypeOfService(serviceTypeData);
                customerServiceDetails.setPartDetails(itemListData);
                customerServiceDetails.setDateOfService(dateOfServiceData);
                customerServiceDetails.setDeliveryDate(deliveryDateData);
                customerServiceDetails.setTotalAmount(totalAmountData);
                customerServiceDetails.setTotalCost(totalCostData);
                customerServiceDetails.setRevenue(revenueData);
                customerServiceDetails.setProfitMargin(profitMarginData);
                customerServiceDetails.setDiscount(discountData);
                customerServiceDetails.setFinalPayment(finalPaymentData);
                customerServiceDetails.setFinalPayment(finalPaymentData);
                customerServiceDetails.setPaymentStatus(paymentStatusData);
                customerServiceDetails.setPaymentMode(paymentModeData);
                customerServiceDetails.setMechanic(mechanicData);


                int responseCode = persistServiceDetails(customerServiceDetails);


                if (responseCode == 0 || responseCode / 100 >= 4) // failed if response code is not initialized,or it is 4XX, or 5XX
                    displayMessage = "Message sending failed . .";
                else
                    displayMessage = "Message sent successfully !";
            }
            else{
                isDataCorrect = false;
                displayMessage = warning;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Status");
            builder.setMessage(displayMessage);

            // add a button
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                if(isDataCorrect) {
                    ((EditText) findViewById(R.id.customerNameField)).setText("");
                    ((Spinner) findViewById(R.id.spinner_visit_type)).setSelection(0);
                    ((EditText) findViewById(R.id.customerAreaField)).setText("");
                    ((EditText) findViewById(R.id.pincodeField)).setText("");
                    ((EditText) findViewById(R.id.contactNumberField)).setText("");
                    ((EditText) findViewById(R.id.emailField)).setText("");
                    ((EditText) findViewById(R.id.phoneNumberField)).setText("");
                    ((RadioGroup) findViewById(R.id.amcGroup)).clearCheck();
                    ((EditText) findViewById(R.id.customerTypeField)).setText("");
                    ((EditText) findViewById(R.id.companyField)).setText("");
                    ((EditText) findViewById(R.id.modelField)).setText("");
                    ((EditText) findViewById(R.id.colorField)).setText("");
                    ((EditText) findViewById(R.id.bikeNumberField)).setText("");
                    ((RadioGroup) findViewById(R.id.insuranceExpiredRadioGroup)).clearCheck();
                    ((EditText) findViewById(R.id.insuranceExpiryDateField)).setText("");
                    ((EditText) findViewById(R.id.insuranceCompanyField)).setText("");
                    ((Spinner) findViewById(R.id.spinner_service_type)).setSelection(0);
                    ((EditText) findViewById(R.id.dateOfServiceField)).setText("");
                    ((EditText) findViewById(R.id.dateOfDeliveryField)).setText("");
                    removeAllPartDetails(itemsContainer);
                    ((EditText) findViewById(R.id.discountValue)).setText("");
                    ((Spinner) findViewById(R.id.spinner_payment_status)).setSelection(0);
                    ((Spinner) findViewById(R.id.spinner_mechanic_name)).setSelection(0);
                }
                displayMessage = "";
                findViewById(R.id.customerNameField).requestFocus();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }

    private String getSelectedRadioButtonValue(int radioGroupId){
        RadioGroup radioGroup = findViewById(radioGroupId);
        int selectedButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedButtonId==-1)
            return "";
        RadioButton selectedButton = findViewById(selectedButtonId);
        CharSequence text = selectedButton.getText();
        if(text==null) return "";
        else return text.toString();
    }

    private List<PartDetails> getItemListData(LinearLayout layout){
        int childCount = layout.getChildCount();
        return IntStream
                .range(0,childCount)
                .mapToObj( idx -> Arrays.stream(((TextView)((RelativeLayout)layout
                        .getChildAt(idx))
                                .getChildAt(1)) // Index 0 is for Remove Button, 1 is for Text Field
                        .getText().toString() // Get each record
                        .split("/")) // Split every record, as it contains details in form of 'Part Name / Amount / Cost'
                        .map(String::trim)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList())
                .stream()
                .map( list -> new PartDetails(list.get(0),list.get(1),list.get(2)))
                .collect(Collectors.toList());
    }

    private void removeAllPartDetails(LinearLayout layout){
        int partDetailsCount = layout.getChildCount();
        for(int recordIndex=0; recordIndex < partDetailsCount; recordIndex++){
            RelativeLayout childLayout = (RelativeLayout) layout.getChildAt(recordIndex);
            Button removeButton = (Button)childLayout.getChildAt(0); // Index 0 is for Remove Button, 1 is for Text Field
            removeButton.callOnClick();
        }
    }

    private int persistServiceDetails(CustomerServiceDetails serviceDetails){
        Callable<Integer> task = () -> {
            HttpPost httpPost = new HttpPost("");
            String jsonString = new Gson().toJson(serviceDetails);
            CloseableHttpClient httpClient;
            int responseCode = 0;

            try{
                StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
                httpClient = HttpClients.createDefault();
                httpPost.setEntity(entity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                responseCode =  response.getCode();
            }
            catch(IOException exception){
                exception.printStackTrace();
            }
            return responseCode;
        };

        ExecutorService service = Executors.newFixedThreadPool(1);
        final Future<Integer> future = service.submit(task);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}