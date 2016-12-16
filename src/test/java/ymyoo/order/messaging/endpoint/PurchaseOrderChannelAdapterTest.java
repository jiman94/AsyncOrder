package ymyoo.order.messaging.endpoint;

import com.google.gson.Gson;
import org.junit.Test;
import ymyoo.infra.messaging.remote.channel.MessageProducer;
import ymyoo.order.domain.po.*;
import ymyoo.order.domain.so.OrderItemDeliveryType;
import ymyoo.order.domain.so.SalesOrderIdGenerator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by 유영모 on 2016-12-16.
 */
public class PurchaseOrderChannelAdapterTest {
    @Test
    public void onPurchaseOrderCreated() throws Exception {
        // given
        String orderId = SalesOrderIdGenerator.generate();
        PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem("prd-12345", 3, OrderItemDeliveryType.AGENCY);
        PurchaseOrderPayment purchaseOrderPayment = new PurchaseOrderPayment("t1234");
        Purchaser purchaser = new Purchaser("유영모", "010-0000-0000");
        PurchaseOrder purchaseOrder = PurchaseOrderFactory.create(orderId,purchaser, purchaseOrderItem, purchaseOrderPayment);

        MessageProducer mockMessageProducer = mock(MessageProducer.class);
        String messageId = java.util.UUID.randomUUID().toString().toUpperCase();

        PurchaseOrderChannelAdapter channelAdapter = new PurchaseOrderChannelAdapter();
        channelAdapter.setMessageProducer(mockMessageProducer);

        // when
        channelAdapter.onPurchaseOrderCreated(messageId, purchaseOrder);

        // then
        verify(mockMessageProducer).send(messageId, new Gson().toJson(purchaseOrder));
    }

}