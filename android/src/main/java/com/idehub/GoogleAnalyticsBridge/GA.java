package com.idehub.GoogleAnalyticsBridge;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains logic for calling Google Analytics library methods
 */
public class GA {

    private static final int LOCAL_DISPATCH_PERIOD = 20;

    private Context _context;
    private Map<String, Tracker> _trackers;

    public GA(Context context) {
        _context = context;
        _trackers = new HashMap<String, Tracker>();
    }

    private synchronized Tracker getTracker(String trackerId) {
        if (!_trackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = getAnalyticsInstance();
            analytics.setLocalDispatchPeriod(LOCAL_DISPATCH_PERIOD);

            Tracker tracker = analytics.newTracker(trackerId);
            tracker.enableExceptionReporting(true);

            _trackers.put(trackerId, tracker);
        }

        return _trackers.get(trackerId);
    }

    private GoogleAnalytics getAnalyticsInstance() {
        return GoogleAnalytics.getInstance(_context);
    }

    public void trackScreenView(String trackerId, String screenName) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void trackEvent(String trackerId, String category,
            String action, Optional<String> label, Optional<Integer> value) {

        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            HitBuilders.EventBuilder hit = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action);

            if (label.isPresent()) {
                hit.setLabel(label.get());
            }

            if (value.isPresent()) {
                hit.setValue(value.get());
            }

            tracker.send(hit.build());
        }
    }

    public void trackTiming(String trackerId, String category,
            Double value, Optional<String> name, Optional<String> label) {

        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            HitBuilders.TimingBuilder hit = new HitBuilders.TimingBuilder()
                .setCategory(category)
                .setValue(value.longValue());

            if (name.isPresent()) {
                hit.setVariable(name.get());
            }

            if (label.isPresent()) {
                hit.setLabel(label.get());
            }

            tracker.send(hit.build());
        }
    }

    public void trackPurchaseEvent(String trackerId, Product product,
            ProductAction transaction, String eventCategory, String eventAction) {

        // This is the same as multi purchase events, only with one product in the
        // list
        List<Product> products = new ArrayList<Product>(1);
        products.add(product);

        trackMultiProductsPurchaseEvent(trackerId, products, transaction, eventCategory, eventAction);
    }

    public void trackMultiProductsPurchaseEvent(String trackerId,
            List<Product> products, ProductAction transaction,
            String eventCategory, String eventAction) {

        trackMultiProductsPurchaseEventWithCustomDimensionValues(
                trackerId, products, transaction, eventCategory, eventAction,
                new HashMap());
    }

    public void trackMultiProductsPurchaseEventWithCustomDimensionValues(
            String trackerId, List<Product> productList, ProductAction transaction,
            String eventCategory, String eventAction, Map<Integer, String> dimensionIndexValues) {

        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            HitBuilders.EventBuilder hit = new HitBuilders.EventBuilder()
                .setProductAction(transaction)
                .setCategory(eventCategory)
                .setAction(eventAction);

            for (Product product : productList) {
                hit.addProduct(product);
            }

            for (Integer index : dimensionIndexValues.keySet()) {
                String value = dimensionIndexValues.get(index);
                hit.setCustomDimension(index, value);
            }

            tracker.send(hit.build());
        }
    }

    public void trackException(String trackerId, String error, Boolean fatal) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            HitBuilders.ExceptionBuilder hit = new HitBuilders.ExceptionBuilder()
                .setDescription(error)
                .setFatal(fatal);

            tracker.send(hit.build());
        }
    }

    public void setUser(String trackerId, String userId) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.set("&uid", userId);
        }
    }

    public void allowAdvertisingIdCollection(String trackerId, Boolean enabled) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.enableAdvertisingIdCollection(enabled);
        }
    }

    public void trackSocialInteraction(String trackerId, String network, String action, String targetUrl) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            HitBuilders.SocialBuilder hit = new HitBuilders.SocialBuilder()
                .setNetwork(network)
                .setAction(action)
                .setTarget(targetUrl);

                tracker.send(hit.build());
        }
    }

    public void trackScreenViewWithCustomDimensionValues(String trackerId,
            String screenName, Map<Integer, String> dimensionIndexValues) {

        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.setScreenName(screenName);

            HitBuilders.ScreenViewBuilder hit = new HitBuilders.ScreenViewBuilder();

            for (Integer index : dimensionIndexValues.keySet()) {
                String value = dimensionIndexValues.get(index);
                hit.setCustomDimension(index, value);
            }

            tracker.send(hit.build());
        }
    }

    public void trackEventWithCustomDimensionValues(String trackerId,
            String category, String action, Optional<String> label,
            Optional<Integer> value,    Map<Integer, String> dimensionIndexValues) {

        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            HitBuilders.EventBuilder hit = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action);

            if (label.isPresent()) {
                hit.setLabel(label.get());
            }

            if (value.isPresent()) {
                hit.setValue(value.get());
            }

            for (Integer index : dimensionIndexValues.keySet()) {
                String dimValue = dimensionIndexValues.get(index);
                hit.setCustomDimension(index, dimValue);
            }

            tracker.send(hit.build());
        }
    }

    public void setSampleRate(String trackerId, Double sampleRate) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.setSampleRate(sampleRate);
        }
    }

    public void setDryRun(Boolean enabled) {
        GoogleAnalytics analytics = getAnalyticsInstance();

        if (analytics != null) {
            analytics.setDryRun(enabled);
        }
    }

    public void setDispatchInterval(Integer intervalInSeconds) {
        GoogleAnalytics analytics = getAnalyticsInstance();

        if (analytics != null) {
            analytics.setLocalDispatchPeriod(intervalInSeconds);
        }
    }

    public void setTrackExceptions(String trackerId, Boolean enabled) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.enableExceptionReporting(enabled);
        }
    }

    public void setAnonymizeIp(String trackerId, Boolean enabled) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.setAnonymizeIp(enabled);
        }
    }

    public void setOptOut(Boolean enabled) {
        GoogleAnalytics analytics = getAnalyticsInstance();

        if (analytics != null) {
            analytics.setAppOptOut(enabled);
        }
    }

    public void setAppName(String trackerId, String appName) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.setAppName(appName);
        }
    }

    public void setAppVersion(String trackerId, String appVersion) {
        Tracker tracker = getTracker(trackerId);

        if (tracker != null) {
            tracker.setAppVersion(appVersion);
        }
    }


    public ProductAction createPurchaseTransaction(String id, Double tax,
            Double revenue, Double shipping, String couponCode, String affiliation) {

        return new ProductAction(ProductAction.ACTION_PURCHASE)
            .setTransactionId(id)
            .setTransactionTax(tax)
            .setTransactionRevenue(revenue)
            .setTransactionShipping(shipping)
            .setTransactionCouponCode(couponCode)
            .setTransactionAffiliation(affiliation);
    }

    public Product createPurchaseProduct(String id, String name, String brand,
            Double price, Integer quantity, String variant,
            String category, Optional<String> couponCode) {

        Product product = new Product()
            .setId(id)
            .setName(name)
            .setBrand(brand)
            .setPrice(price)
            .setQuantity(quantity)
            .setVariant(variant)
            .setCategory(category);

        if (couponCode.isPresent()) {
            product.setCouponCode(couponCode.get());
        }

        return product;
    }
}
