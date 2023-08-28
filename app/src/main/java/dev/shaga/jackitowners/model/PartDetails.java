package dev.shaga.jackitowners.model;

public class PartDetails {
    private String partType;
    private String amount;
    private String cost;

    public PartDetails(String partType, String amount, String cost) {
        this.partType = partType;
        this.amount = amount;
        this.cost = cost;
    }

    public String getPartType() {
        return partType;
    }

    public String getAmount() {
        return amount;
    }

    public String getCost() {
        return cost;
    }
}
