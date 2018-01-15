package com.reactnativenavigation.parse;

import org.json.JSONObject;

public class FractionParser {
    public static Fraction parse(JSONObject json, String fraction) {
        return json.has(fraction) ? new Fraction(json.optInt(fraction)) : new NullFraction();
    }
}
