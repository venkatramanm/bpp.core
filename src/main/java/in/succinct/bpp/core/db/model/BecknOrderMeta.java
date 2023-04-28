package in.succinct.bpp.core.db.model;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.Fulfillment.FulfillmentStatus;
import in.succinct.beckn.Order.Status;
import in.succinct.bpp.core.adaptor.FulfillmentStatusAdaptor.FulfillmentStatusAudit;

import java.util.Date;
import java.util.List;

public interface BecknOrderMeta extends Model {
    @UNIQUE_KEY("bt")
    @Index
    public String getBecknTransactionId();
    public void setBecknTransactionId(String becknTransactionId);

    @UNIQUE_KEY("bt")
    @Index
    public String getSubscriberId();
    public void setSubscriberId(String subscriberId);

    public String getNetworkId();
    public void setNetworkId(String networkId);




    @Index
    @UNIQUE_KEY("bap_order")
    public String getBapOrderId();
    public void setBapOrderId(String eCommerceOrderId);


    @Index
    @UNIQUE_KEY("do")
    @IS_NULLABLE
    public String getECommerceDraftOrderId();
    public void setECommerceDraftOrderId(String eCommerceDraftOrderId);

    @Index
    @UNIQUE_KEY("eo")
    public String getECommerceOrderId();
    public void setECommerceOrderId(String eCommerceOrderId);

    @COLUMN_SIZE(2048*16)
    @IS_NULLABLE
    public String getOrderJson();
    public void setOrderJson(String orderJson);


    @COLUMN_SIZE(2048*4)
    @IS_NULLABLE
    public String getContextJson();
    public void setContextJson(String contextJson);

    @COLUMN_DEF(StandardDefault.ZERO)
    public double getBuyerAppFinderFeeAmount();
    public void setBuyerAppFinderFeeAmount(double buyerAppFinderFeeAmount);

    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = "Percent")
    public String getBuyerAppFinderFeeType();
    public void setBuyerAppFinderFeeType(String buyerAppFinderFeeType);

    @COLUMN_SIZE(2048*4)
    @IS_NULLABLE
    public String getStatusUpdatedAtJson();
    public void setStatusUpdatedAtJson(String statusUpdatedAtJson);


    public Date getStatusReachedAt(Status status);
    public void setStatusReachedAt(Status status, Date at);

    public Date getFulfillmentStatusReachedAt(FulfillmentStatus status);
    public void setFulfillmentStatusReachedAt(FulfillmentStatus status, Date at);

    @IS_VIRTUAL
    public List<FulfillmentStatusAudit> getStatusAudits();

    public String getTrackingUrl();
    public void setTrackingUrl(String trackingUrl);

}
