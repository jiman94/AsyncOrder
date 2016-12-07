package ymyoo.order.domain.workflow.activity.impl;

import ymyoo.infra.messaging.remote.channel.Callback;
import ymyoo.order.domain.ApprovalOrderPayment;
import ymyoo.order.domain.Order;
import ymyoo.order.domain.workflow.activity.AsyncBusinessActivity;
import ymyoo.order.messaging.endpoint.PaymentGatewayChannelAdapter;

/**
 * 주문시 수행 해야할 결제 관련 작업 모음
 *
 * Created by 유영모 on 2016-10-10.
 */
public class PaymentGatewayBusinessActivity implements AsyncBusinessActivity<Order, ApprovalOrderPayment> {
    private final String id = java.util.UUID.randomUUID().toString().toUpperCase();

    @Override
    public void perform(Order order, Callback<ApprovalOrderPayment> callback) {
        PaymentGatewayChannelAdapter channelAdapter = new PaymentGatewayChannelAdapter();
        channelAdapter.authenticateAndApproval(id, order.getOrderPayment(), callback);
    }

    @Override
    public String getId() {
        return id;
    }
}
