package com.example.wallethistory.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.wallethistory.utils.StringListConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

@Entity(tableName = "transactions_table")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    int id;

    @SerializedName("transaction_type")
    String type;

    @SerializedName("date")
    long date;

    @SerializedName("amount")
    double amount;

    @SerializedName("category")
    String category;

    @SerializedName("tags")
    @TypeConverters(StringListConverter.class)
    @ColumnInfo(name = "tags", defaultValue = "null")
    List<String> tags;

    @SerializedName("merchant_name")
    @ColumnInfo(name = "merchant_name", defaultValue = "null")
    String merchantName;

    @SerializedName("payment_method")
    @ColumnInfo(name = "payment_method", defaultValue = "null")
    String paymentMethod;

    @SerializedName("emoji")
    @ColumnInfo (name = "emoji")
    String emoji;

    @SerializedName("notes")
    @ColumnInfo(name = "note", defaultValue = "null")
    String note;

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id && date == that.date && Double.compare(amount, that.amount) == 0 && Objects.equals(type, that.type) && Objects.equals(category, that.category) && Objects.equals(tags, that.tags) && Objects.equals(merchantName, that.merchantName) && Objects.equals(paymentMethod, that.paymentMethod) && Objects.equals(emoji, that.emoji) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, date, amount, category, tags, merchantName, paymentMethod, emoji, note);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", tags=" + tags +
                ", merchantName='" + merchantName + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", emoji='" + emoji + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
