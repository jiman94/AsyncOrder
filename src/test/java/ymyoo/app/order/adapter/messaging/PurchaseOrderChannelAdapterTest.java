package ymyoo.app.order.adapter.messaging;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ymyoo.messaging.MessageProducer;
import ymyoo.app.order.domain.po.*;
import ymyoo.app.order.domain.so.OrderItemDeliveryType;
import ymyoo.app.order.domain.so.SalesOrderIdGenerator;
import ymyoo.messaging.Requester;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by 유영모 on 2016-12-16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PurchaseOrderMessagingAdapter.class)
public class PurchaseOrderChannelAdapterTest {
    @Test
    public void onPurchaseOrderCreated() throws Exception {
        // given
        Requester requester = PowerMockito.mock(Requester.class);













        String orderId = SalesOrderIdGenerator.generate();
        PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem("prd-12345", 3, OrderItemDeliveryType.AGENCY);
        PurchaseOrderPayment purchaseOrderPayment = new PurchaseOrderPayment("t1234");
        Purchaser purchaser = new Purchaser("유영모", "010-0000-0000");
        PurchaseOrder purchaseOrder = PurchaseOrderFactory.create(orderId,purchaser, purchaseOrderItem, purchaseOrderPayment);

        MessageProducer mockMessageProducer = mock(MessageProducer.class);
        String messageId = java.util.UUID.randomUUID().toString().toUpperCase();

        PurchaseOrderMessagingAdapter channelAdapter = new PurchaseOrderMessagingAdapter();
  //      channelAdapter.setMessageProducer(mockMessageProducer);

        // when
   //     channelAdapter.onPurchaseOrderCreated(messageId, purchaseOrder);

        // then
        verify(mockMessageProducer).send(messageId, new Gson().toJson(purchaseOrder));
    }

}