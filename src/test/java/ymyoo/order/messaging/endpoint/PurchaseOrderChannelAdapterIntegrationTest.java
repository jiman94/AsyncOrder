package ymyoo.order.messaging.endpoint;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Assert;
import org.junit.Test;
import ymyoo.infra.messaging.remote.channel.MessageChannel;
import ymyoo.order.domain.po.*;
import ymyoo.order.domain.so.OrderItemDeliveryType;
import ymyoo.order.domain.so.SalesOrderIdGenerator;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by 유영모 on 2016-12-16.
 */
public class PurchaseOrderChannelAdapterIntegrationTest {

    @Test
    public void onPurchaseOrderCreated() throws Exception {
        // given
        String orderId = SalesOrderIdGenerator.generate();
        PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem("prd-12345", 3, OrderItemDeliveryType.AGENCY);
        PurchaseOrderPayment purchaseOrderPayment = new PurchaseOrderPayment("t1234");
        Purchaser purchaser = new Purchaser("유영모", "010-0000-0000");

        PurchaseOrder purchaseOrder = PurchaseOrderFactory.create(orderId,purchaser, purchaseOrderItem, purchaseOrderPayment);

        String messageId = java.util.UUID.randomUUID().toString().toUpperCase();

        new Thread(() -> {
            // then
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("group.id", "test");
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(MessageChannel.PURCHASE_ORDER_CREATED));

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ConsumerRecords<String, String> records = consumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        Assert.assertEquals(messageId, record.key());
                        Assert.assertEquals(new Gson().toJson(purchaseOrder), record.value());
                        Thread.interrupted();
                    }
                }
            } finally {
                consumer.close();
            }
        }).start();

        // when
        PurchaseOrderChannelAdapter channelAdapter = new PurchaseOrderChannelAdapter();
        channelAdapter.onPurchaseOrderCreated(messageId, purchaseOrder);
    }
}