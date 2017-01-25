package ymyoo.app.order.domain;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ymyoo.app.inventory.adapter.messaging.InventoryReplier;
import ymyoo.messaging.processor.entitiy.IncompleteBusinessActivity;
import ymyoo.app.order.infrastructure.OrderEntityManagerFactory;
import ymyoo.app.payment.adapter.messaging.PaymentReplier;
import ymyoo.messaging.core.MessageChannels;
import ymyoo.messaging.core.PollingMessageConsumer;
import ymyoo.messaging.processor.repository.OrderStatusEntityRepository;
import ymyoo.messaging.processor.MessageStoreProcessor;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 유영모 on 2016-10-07.
 */
public class OrderIntegrationTest {
    Thread inventoryReplier = null;
    Thread paymentReplier = null;

    Thread inventoryReplyMessageConsumer = null;
    Thread paymentReplyMessageConsumer = null;

    Thread orderStatusMessageProcessor = null;

    @Before
    public void setUp() throws Exception {
        // setup Message Replier
        inventoryReplier = new Thread(new InventoryReplier());
        inventoryReplier.start();

        paymentReplier = new Thread(new PaymentReplier());
        paymentReplier.start();

        // setup ReplyMessage Consumer
        inventoryReplyMessageConsumer = new Thread(new PollingMessageConsumer(MessageChannels.INVENTORY_REPLY));
        inventoryReplyMessageConsumer.start();

        paymentReplyMessageConsumer = new Thread(new PollingMessageConsumer(MessageChannels.PAYMENT_AUTH_APP_REPLY));
        paymentReplyMessageConsumer.start();

        // setup MessageStoreProcessor
        OrderStatusEntityRepository orderStatusMessageProcessorRepositroy =
                new OrderStatusEntityRepository(OrderEntityManagerFactory.getEntityManagerFactory());
        orderStatusMessageProcessor = new Thread(new MessageStoreProcessor(MessageChannels.LOG_ORDER_STATUS, orderStatusMessageProcessorRepositroy));
        orderStatusMessageProcessor.start();
    }

    private void waitCurrentThread(int seconds) throws InterruptedException {
        Thread.currentThread().sleep(TimeUnit.SECONDS.toMillis(seconds));
    }

    @Test
    public void placeOrder() throws Exception {
        // Given
        Order order = OrderFactory.create(new Orderer("유영모", "010-0000-0000"),
                new OrderItem("P0003", 1, OrderItemDeliveryType.DIRECTING),
                new OrderPayment(2000, "123-456-0789"));

        // When
        String orderId = order.placeOrder();

        // Then
        // 주문 ID 반환 확인(Synchronized)
        Assert.assertTrue(StringUtils.isNotBlank(orderId));

        // 비동기 처리 대기
        waitCurrentThread(5);

        OrderStatus actual = order.getOrderStatus();
        Assert.assertEquals(orderId, actual.getOrderId());
        Assert.assertEquals(OrderStatus.Status.PURCHASE_ORDER_CREATED, actual.getStatus());
    }

    @Test
    public void placeOrder_주문_완료_알림_오류_휴대전화() throws InterruptedException {
        // given
        Order order = OrderFactory.create(new Orderer("유영모", "010-0000-0000"),
                new OrderItem("P0003", 1, OrderItemDeliveryType.DIRECTING),
                new OrderPayment(2000, "123-456-0789"));

        // when
        String orderId = order.placeOrder();

        // then
        // 주문 ID 반환 확인(Synchronized)
        Assert.assertTrue(StringUtils.isNotBlank(orderId));

        // 비동기 처리 대기
        waitCurrentThread(5);

        // 주문 상태 확인
        OrderStatus actual = order.getOrderStatus();
        Assert.assertEquals(orderId, actual.getOrderId());
        Assert.assertEquals(OrderStatus.Status.PURCHASE_ORDER_CREATED, actual.getStatus());

        // 주문 완료 알림 오류 확인
        EntityManager em = OrderEntityManagerFactory.getEntityManagerFactory().createEntityManager();

        TypedQuery<IncompleteBusinessActivity> query =
                em.createQuery("select iba from IncompleteBusinessActivity iba where iba.orderId = :orderId"
                        ,IncompleteBusinessActivity.class);
        query.setParameter("orderId", orderId);

        List<IncompleteBusinessActivity> incompleteBusinessActivities = query.getResultList();
        Assert.assertEquals(1, incompleteBusinessActivities.size());
        Assert.assertEquals("SENDING_CELL_PHONE", incompleteBusinessActivities.get(0).getActivity());
    }
}
