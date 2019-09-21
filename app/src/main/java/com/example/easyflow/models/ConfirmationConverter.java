package com.example.easyflow.models;

import java.util.HashMap;
import java.util.Map;

public class ConfirmationConverter {

        private String mEmailreceiver;
        private double mValue;

        private ConfirmationConverter() {}

        public ConfirmationConverter(String email, double value) {
            this.mEmailreceiver = email;
            this.mValue = value;
        }

        public double getmValue() {
            return mValue;
        }

        public void setmValue(double mValue) {
            this.mValue = mValue;
        }

        public String getmEmailReceiver() {
            return mEmailreceiver;
        }

        public void setmEmailReceiver(String mEmailReceiver) {
            this.mEmailreceiver = mEmailReceiver;
        }
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("emailReceiving", getmEmailReceiver());
        result.put("value", getmValue());

        return result;

    }
}
