package in.succinct.bpp.core.adaptor;

import com.venky.swf.db.Database;
import com.venky.swf.db.model.CryptoKey;
import com.venky.swf.db.model.application.Application;
import com.venky.swf.db.model.application.ApplicationPublicKey;
import com.venky.swf.db.model.application.ApplicationUtil;
import com.venky.swf.db.model.application.api.EndPoint;
import com.venky.swf.plugins.beckn.messaging.Subscriber;
import in.succinct.beckn.CancellationReasons;
import in.succinct.beckn.FeedbackCategories;
import in.succinct.beckn.Message;
import in.succinct.beckn.RatingCategories;
import in.succinct.beckn.Request;
import in.succinct.beckn.ReturnReasons;
import in.succinct.bpp.core.db.model.ProviderConfig;

import java.sql.Timestamp;
import java.util.Map;

public abstract class CommerceAdaptor{
    private final Subscriber subscriber;
    private final Map<String,String> configuration;
    private final Application application ;
    private final ProviderConfig providerConfig;
    private final FulfillmentStatusAdaptor fulfillmentStatusAdaptor ;
    private final IssueTracker issueTracker;

    public CommerceAdaptor(Map<String,String> configuration, Subscriber subscriber) {
        this.configuration = configuration;
        this.subscriber = subscriber;
        String key = configuration.keySet().stream().filter(k->k.endsWith(".provider.config")).findAny().get();
        providerConfig = new ProviderConfig(this.configuration.get(key));
        this.subscriber.setOrganization(providerConfig.getOrganization());
        this.fulfillmentStatusAdaptor = FulfillmentStatusAdaptorFactory.getInstance().createAdaptor(this);
        this.issueTracker = providerConfig.getIssueTrackerConfig() == null ? null : IssueTrackerFactory.getInstance().createIssueTracker(providerConfig.getIssueTrackerConfig().getName(),providerConfig.getIssueTrackerConfig());
        this.application = getApplication(getSubscriber().getAppId());
    }
    public Application getApplication(String appId){
        Application app = ApplicationUtil.find(appId);
        Application application  = app;
        if (application == null) {
            application = Database.getTable(Application.class).newRecord();
            application.setAppId(subscriber.getAppId());
            application.setHeaders("(created) (expires) digest");
            application.setSignatureLifeMillis(5000);
            application.setSigningAlgorithm(Request.SIGNATURE_ALGO);
            application.setHashingAlgorithm("BLAKE2B-512");
            application.setSigningAlgorithmCommonName(application.getSigningAlgorithm().toLowerCase());
            application.setHashingAlgorithmCommonName(application.getHashingAlgorithm().toLowerCase());
            application = Database.getTable(Application.class).getRefreshed(application);
            application.save();
        }


        //CryptoKey cryptoKey = CryptoKey.find(subscriber.getCryptoKeyId(), CryptoKey.PURPOSE_SIGNING);

        ApplicationPublicKey publicKey = Database.getTable(ApplicationPublicKey.class).newRecord();
        publicKey.setApplicationId(application.getId());
        publicKey.setPurpose(CryptoKey.PURPOSE_SIGNING);
        publicKey.setAlgorithm(Request.SIGNATURE_ALGO);
        publicKey.setKeyId(subscriber.getUniqueKeyId());
        publicKey.setValidFrom(new Timestamp(subscriber.getValidFrom().getTime()));
        publicKey.setValidUntil(new Timestamp(subscriber.getValidTo().getTime()));
        publicKey.setPublicKey(Request.getPemSigningKey(subscriber.getSigningPublicKey()));
        publicKey = Database.getTable(ApplicationPublicKey.class).getRefreshed(publicKey);
        publicKey.save();

        EndPoint endPoint = Database.getTable(EndPoint.class).newRecord();
        endPoint.setApplicationId(application.getId());
        endPoint.setBaseUrl(subscriber.getSubscriberUrl());
        endPoint = Database.getTable(EndPoint.class).getRefreshed(endPoint);
        endPoint.save();

        return app;
    }

    public IssueTracker getIssueTracker() {
        return issueTracker;
    }

    public FulfillmentStatusAdaptor getFulfillmentStatusAdaptor() {
        return fulfillmentStatusAdaptor;
    }

    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }


    public Application getApplication(){
        return application;
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public abstract void search(Request request, Request response);
    public abstract void select(Request request, Request response);
    public abstract void init(Request request, Request response);
    public abstract void confirm(Request request, Request response);
    public abstract void track(Request request, Request response);
    public abstract void issue(Request request, Request response);
    public abstract void issue_status(Request request, Request response);
    public abstract void cancel(Request request, Request response);
    public abstract void update(Request request, Request response);
    public abstract void status(Request request, Request response);
    public abstract void rating(Request request, Request response);
    public void support(Request request, Request reply) {
        reply.setMessage(new Message());
        reply.getMessage().setEmail(getProviderConfig().getSupportContact().getEmail());
        reply.getMessage().setPhone(getProviderConfig().getSupportContact().getPhone());
    }


    public void get_cancellation_reasons(Request request, Request reply) {
        reply.setCancellationReasons(new CancellationReasons());
    }

    public void get_return_reasons(Request request, Request reply) {
        reply.setReturnReasons(new ReturnReasons());
    }

    public void get_rating_categories(Request request, Request reply) {
        reply.setRatingCategories(new RatingCategories());
    }

    public void get_feedback_categories(Request request, Request reply) {
        reply.setFeedbackCategories(new FeedbackCategories());
    }

    public void get_feedback_form(Request request, Request reply) {
    }


    public void _search(Request reply){

    }
    public void clearCache() {

    }

}
