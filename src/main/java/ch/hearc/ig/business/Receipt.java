package ch.hearc.ig.business;

import ch.hearc.ig.apiService.annotation.QbField;

import java.util.Date;

public class Receipt {
    private Integer id;
    private Double amount;
    private String description;
    private Date dateReceipt;
    private String receiptType;
    private String receiptIssuer;
    private String requestCreator;
    private String requestDate;
    private String validator;

    public Receipt() {}

    public Receipt(Integer id, Double amount, String description, Date dateReceipt, String receiptType, String receiptIssuer, String requestCreator, String requestDate, String validator) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.dateReceipt = dateReceipt;
        this.receiptType = receiptType;
        this.receiptIssuer = receiptIssuer;
        this.requestCreator = requestCreator;
        this.requestDate = requestDate;
        this.validator = validator;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    @QbField("QB_Total")
    public void setAmount(Double amount) { this.amount = amount; }

    public Date getDateReceipt() {
        return dateReceipt;
    }

    @QbField("QB_DATE_DEP")
    public void setDateReceipt(Date dateReceipt) { this.dateReceipt = dateReceipt; }

    public String getDescription() {
        return description;
    }

    @QbField("QB_DESC")
    public void setDescription(String description) { this.description = description; }

    public String getReceiptType() {
        return receiptType;
    }

    @QbField("QB_TYPE")
    public void setReceiptType(String receiptType) { this.receiptType = receiptType; }

    public String getRequestCreator() {
        return requestCreator;
    }

    @QbField("QB_Créateur")
    public void setRequestCreator(String requestCreator) { this.requestCreator = requestCreator; }

    public String getRequestDate() {
        return requestDate;
    }

    @QbField("QB_DATE_DEMANDE")
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public String getValidator() {
        return validator;
    }

    @QbField("QB_Validateur")
    public void setValidator(String validator) { this.validator = validator; }

    public String getReceiptIssuer() {
        return receiptIssuer;
    }

    @QbField("QB_EME")
    public void setReceiptIssuer(String receiptIssuer) { this.receiptIssuer = receiptIssuer; }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", dateReceipt=" + dateReceipt +
                ", receiptType='" + receiptType + '\'' +
                ", receiptIssuer='" + receiptIssuer + '\'' +
                ", requestCreator='" + requestCreator + '\'' +
                ", requestDate='" + requestDate + '\'' +
                ", validator='" + validator + '\'' +
                '}';
    }
}
