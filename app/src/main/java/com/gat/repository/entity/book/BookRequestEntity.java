package com.gat.repository.entity.book;

import com.gat.common.util.Strings;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 14/04/2017.
 */

public class BookRequestEntity {

    @SerializedName("recordId")
    private int recordId;

    @SerializedName("bookId")
    private int bookId;

    @SerializedName("editionId")
    private int editionId;

    @SerializedName("editionTitle")
    private String editionTitle = Strings.EMPTY;

    @SerializedName("instanceId")
    private String instanceId = Strings.EMPTY;

    @SerializedName("borrowerId")
    private int borrowerId ;

    @SerializedName("borrowerName")
    private String borrowerName = Strings.EMPTY;

    @SerializedName("borrowerImageId")
    private String borrowerImageId = Strings.EMPTY;

    @SerializedName("ownerId")
    private int ownerId;

    @SerializedName("ownerName")
    private String ownerName  = Strings.EMPTY;

    @SerializedName("ownerImageId")
    private String ownerImageId = Strings.EMPTY;

    @SerializedName("messageId")
    private String messageId  = Strings.EMPTY;

    @SerializedName("recordStatus")
    private int recordStatus ;

    @SerializedName("recordType")
    private int recordType;

    @SerializedName("approveTime")
    private String approveTime = Strings.EMPTY;


    @SerializedName("requestTime")
    private String requestTime = Strings.EMPTY;

    @SerializedName("borrowTime")
    private String borrowTime = Strings.EMPTY;

    @SerializedName("completeTime")
    private String completeTime = Strings.EMPTY;

    @SerializedName("cancelTime")
    private String cancelTime = Strings.EMPTY;

    @SerializedName("rejectTime")
    private String rejectTime = Strings.EMPTY;

    private boolean isHeader;

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getRecordType() {
        return recordType;
    }

    public String getEditionTitle() {
        return editionTitle;
    }

    public int getEditionId() {
        return editionId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getBorrowerImageId() {
        return borrowerImageId;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public int getBookId() {
        return bookId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerImageId() {
        return ownerImageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public String getRejectTime() {
        return rejectTime;
    }

    @Override
    public String toString() {
        return "BookRequestEntity{" +
                "recordId=" + recordId +
                ", bookId=" + bookId +
                ", editionId=" + editionId +
                ", editionTitle='" + editionTitle + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", borrowerId=" + borrowerId +
                ", borrowerName='" + borrowerName + '\'' +
                ", borrowerImageId='" + borrowerImageId + '\'' +
                ", ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
                ", ownerImageId='" + ownerImageId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", recordStatus=" + recordStatus +
                ", recordType=" + recordType +
                ", approveTime='" + approveTime + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", borrowTime='" + borrowTime + '\'' +
                ", completeTime='" + completeTime + '\'' +
                ", cancelTime='" + cancelTime + '\'' +
                ", rejectTime='" + rejectTime + '\'' +
                ", isHeader=" + isHeader +
                '}';
    }
    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }
}
