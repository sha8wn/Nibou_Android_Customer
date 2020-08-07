package com.nibou.niboucustomer.utils;

import android.content.Context;
import android.os.Build;
import com.nibou.niboucustomer.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreditCardUtils {

    public static final String VISA_PREFIX = "4";
    public static final String MASTERCARD_PREFIX = "51,52,53,54,55,";
    public static final String DISCOVER_PREFIX = "6011";
    public static final String AMEX_PREFIX = "34,37,";

    public static int getCardType(String cardNumber) {
        if (cardNumber.length() > 0 && cardNumber.substring(0, 1).equals(VISA_PREFIX))
            return R.drawable.visa_card_icon;
        else if (cardNumber.length() > 1 && MASTERCARD_PREFIX.contains(cardNumber.substring(0, 2) + ","))
            return R.drawable.master_card_icon;
        else if (cardNumber.length() > 1 && AMEX_PREFIX.contains(cardNumber.substring(0, 2) + ","))
            return R.drawable.amex_icon;
        else if (cardNumber.length() > 3 && cardNumber.substring(0, 4).equals(DISCOVER_PREFIX))
            return R.drawable.discover_card_icon;
        return -1;
    }
}