package dev.shaga.jackitowners.model;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomerServiceDetails {
    private String visitType;
    private String area;
    private String pincode;
    private String name;
    private String contactNumber;
    private String emailId;
    private String phoneNumber;
    private String customerType;
    private String amc;
    private String vehicleCompany;
    private String vehicleModel;
    private String vehicleColor;
    private String bikeNumber;
    private String bikeInsuranceExpiredYesOrNo;
    private String insuranceExpiryDate;
    private String insuranceCompanyName;
    private String typeOfService;
    private List<PartDetails> partDetails;
    private String totalAmount;
    private String totalCost;
    private String revenue;
    private String profitMargin;
    private String discount;
    private String finalPayment;

    private String dateOfService;
    private String deliveryDate;

    private String paymentStatus;
    private String paymentMode;
    private String mechanic;

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setAmc(String amc) {
        this.amc = amc;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public void setBikeInsuranceExpiredYesOrNo(String bikeInsuranceExpiredYesOrNo) {
        this.bikeInsuranceExpiredYesOrNo = bikeInsuranceExpiredYesOrNo;
    }

    public void setInsuranceExpiryDate(String insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }

    public void setPartDetails(List<PartDetails> partDetails) {
        this.partDetails = partDetails;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public void setProfitMargin(String profitMargin) {
        this.profitMargin = profitMargin;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setFinalPayment(String finalPayment) {
        this.finalPayment = finalPayment;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setMechanic(String mechanic) {
        this.mechanic = mechanic;
    }

    public List<List<Object>> getDetailsAsListOfObjects() {
        List<List<Object>> result = new LinkedList<>();
        List<Object> headerRecord = new LinkedList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        final int fieldCount = fields.length;
        for (Field field : fields) {
            try {
                headerRecord.add(field.get(this).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        result.add(headerRecord);
        for(int i=1;i<partDetails.size();i++){
            List<Object> temporaryRecord = new ArrayList<>(fieldCount);
            temporaryRecord.set(17,this.partDetails.get(i).getPartType());
            temporaryRecord.set(18,this.partDetails.get(i).getAmount());
            temporaryRecord.set(19,this.partDetails.get(i).getCost());
            result.add(temporaryRecord);
        }
        return result;
    }
}
