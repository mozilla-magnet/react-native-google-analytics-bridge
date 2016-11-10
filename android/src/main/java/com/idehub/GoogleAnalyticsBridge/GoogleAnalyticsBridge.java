package com.idehub.GoogleAnalyticsBridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleAnalyticsBridge extends ReactContextBaseJavaModule {
        private final String _trackingId;
        private final HashMap<String, Tracker> mTrackers
            = new HashMap<String, Tracker>();
        private final GA _ga;

        public GoogleAnalyticsBridge(ReactApplicationContext reactContext,
                String trackingId) {

            super(reactContext);
            _ga = new GA(reactContext);
            _trackingId = trackingId;
        }

        @Override
        public String getName() {
            return "GoogleAnalyticsBridge";
        }


        synchronized Tracker getTracker(String trackerId) {
             if (!mTrackers.containsKey(trackerId)) {
                     GoogleAnalytics analytics = GoogleAnalytics.getInstance(getReactApplicationContext());
                     analytics.setLocalDispatchPeriod(20);
                     Tracker t = analytics.newTracker(trackerId);
                     t.enableExceptionReporting(true);
                     mTrackers.put(trackerId, t);
             }
             return mTrackers.get(trackerId);
        }

        synchronized GoogleAnalytics getAnalyticsInstance() {
            return GoogleAnalytics.getInstance(getReactApplicationContext());
        }

        @Override
        public Map<String, Object> getConstants() {
                final Map<String, Object> constants = new HashMap<>();
                constants.put("nativeTrackerId", _trackingId);
                return constants;
        }

        @ReactMethod
        public void trackScreenView(String trackerId, String screenName){
            _ga.trackScreenView(trackerId, screenName);
        }

        @ReactMethod
        public void trackEvent(String trackerId, String category, String action, ReadableMap optionalValues) {

            Optional<String> label = optionalValues.hasKey("label") ?
                Optional.ofNullable(optionalValues.getString("label")) : Optional.emptyString();

            Optional<Integer> value = optionalValues.hasKey("value") ?
                Optional.ofNullable(optionalValues.getInt("value")) : Optional.emptyInteger();

            _ga.trackEvent(trackerId, category, action, label, value);
        }

        @ReactMethod
        public void trackTiming(String trackerId, String category, Double value, ReadableMap optionalValues){

            Optional<String> name = optionalValues.hasKey("name") ?
                Optional.ofNullable(optionalValues.getString("name")) : Optional.emptyString();

            Optional<String> label = optionalValues.hasKey("label") ?
                Optional.ofNullable(optionalValues.getString("label")) : Optional.emptyString();

            _ga.trackTiming(trackerId, category, value, name, label);
        }

        @ReactMethod
        public void trackPurchaseEvent(String trackerId, ReadableMap productMap, ReadableMap transactionMap, String eventCategory, String eventAction){

            Product product = getPurchaseProduct(productMap);
            ProductAction transaction = getPurchaseTransaction(transactionMap);

            _ga.trackPurchaseEvent(trackerId, product,
                    transaction, eventCategory, eventAction);
        }

        @ReactMethod
        public void trackMultiProductsPurchaseEvent(String trackerId, ReadableArray productArray, ReadableMap transactionMap, String eventCategory, String eventAction) {

            ProductAction transaction = getPurchaseTransaction(transactionMap);
            List<Product> productList = getPurchaseProducts(productArray);

            _ga.trackMultiProductsPurchaseEvent(trackerId, productList, transaction,
                    eventCategory, eventAction);
        }

        @ReactMethod
        public void trackMultiProductsPurchaseEventWithCustomDimensionValues(String trackerId, ReadableArray productArray, ReadableMap transactionMap, String eventCategory, String eventAction, ReadableMap dimensionIndexValues) {

            ProductAction transaction = getPurchaseTransaction(transactionMap);
            List<Product> productList = getPurchaseProducts(productArray);
            Map<Integer, String> dimensions = getDimensionIndices(dimensionIndexValues);

            _ga.trackMultiProductsPurchaseEventWithCustomDimensionValues(
                    trackerId, productList, transaction, eventCategory,
                    eventAction, dimensions);
        }

        @ReactMethod
        public void trackException(String trackerId, String error, Boolean fatal) {
            _ga.trackException(trackerId, error, fatal);
        }

        @ReactMethod
        public void setUser(String trackerId, String userId) {
            _ga.setUser(trackerId, userId);
        }

        @ReactMethod
        public void allowIDFA(String trackerId, Boolean enabled) {
            _ga.allowAdvertisingIdCollection(trackerId, enabled);
        }

        @ReactMethod
        public void trackSocialInteraction(String trackerId, String network,
                String action, String targetUrl) {

            _ga.trackSocialInteraction(trackerId, network, action, targetUrl);
        }

        @ReactMethod
        public void trackScreenViewWithCustomDimensionValues(String trackerId, String screenName, ReadableMap dimensionIndexValues) {

            _ga.trackScreenViewWithCustomDimensionValues(trackerId, screenName,
                    getDimensionIndices(dimensionIndexValues));
        }

        @ReactMethod
        public void trackEventWithCustomDimensionValues(String trackerId,
                String category, String action, ReadableMap optionalValues,
                ReadableMap dimensionIndexValues) {

            Optional<String> label = optionalValues.hasKey("label") ?
                Optional.ofNullable(optionalValues.getString("label")) : Optional.emptyString();

            Optional<Integer> value = optionalValues.hasKey("value") ?
                Optional.ofNullable(optionalValues.getInt("value")) : Optional.emptyInteger();

            _ga.trackEventWithCustomDimensionValues(trackerId, category, action,
                    label, value, getDimensionIndices(dimensionIndexValues));
        }

        @ReactMethod
        public void setSamplingRate(String trackerId, Double sampleRate){
            _ga.setSampleRate(trackerId, sampleRate);
        }

        @ReactMethod
        public void setDryRun(Boolean enabled){
            _ga.setDryRun(enabled);
        }

        @ReactMethod
        public void setDispatchInterval(Integer intervalInSeconds){
            _ga.setDispatchInterval(intervalInSeconds);
        }

        @ReactMethod
        public void setTrackUncaughtExceptions(String trackerId, Boolean enabled){
            _ga.setTrackExceptions(trackerId, enabled);
        }


        @ReactMethod
        public void setAnonymizeIp(String trackerId, Boolean enabled){
            _ga.setAnonymizeIp(trackerId, enabled);
        }

        @ReactMethod
        public void setOptOut(Boolean enabled){
            _ga.setOptOut(enabled);
        }

        @ReactMethod
        public void setAppName(String trackerId, String appName){
            _ga.setAppName(trackerId, appName);
        }

        @ReactMethod
        public void setAppVersion(String trackerId, String appVersion){
            _ga.setAppVersion(trackerId, appVersion);
        }

        private ProductAction getPurchaseTransaction(ReadableMap transaction) {
                ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                     .setTransactionId(transaction.getString("id"))
                     .setTransactionTax(transaction.getDouble("tax"))
                     .setTransactionRevenue(transaction.getDouble("revenue"))
                     .setTransactionShipping(transaction.getDouble("shipping"))
                     .setTransactionCouponCode(transaction.getString("couponCode"))
                     .setTransactionAffiliation(transaction.getString("affiliation"));

                return productAction;
        }

        private List<Product> getPurchaseProducts(ReadableArray products) {
            List<Product> productList = new ArrayList<Product>(products.size());

            for (int index = 0; index < products.size(); index++) {
                    ReadableMap productMap = products.getMap(index);
                    productList.add(this.getPurchaseProduct(productMap));
            }

            return productList;
        }

        private Product getPurchaseProduct(ReadableMap product) {
                Product ecommerceProduct = new Product()
                     .setId(product.getString("id"))
                     .setName(product.getString("name"))
                     .setBrand(product.getString("brand"))
                     .setPrice(product.getDouble("price"))
                     .setQuantity(product.getInt("quantity"))
                     .setVariant(product.getString("variant"))
                     .setCategory(product.getString("category"));

                if(product.hasKey("couponCode")) {
                     ecommerceProduct.setCouponCode(product.getString("couponCode"));
                }

                return ecommerceProduct;
        }

        private Map<Integer, String> getDimensionIndices(ReadableMap dimensionIndices) {
            Map<Integer, String> dimensions = new HashMap<Integer, String>();

            ReadableMapKeySetIterator iterator = dimensionIndices.keySetIterator();
            while (iterator.hasNextKey()) {
                String dimensionIndex = iterator.nextKey();
                String dimensionValue = dimensionIndices.getString(dimensionIndex);
                dimensions.put(Integer.parseInt(dimensionIndex), dimensionValue);
            }

            return dimensions;
        }
}
