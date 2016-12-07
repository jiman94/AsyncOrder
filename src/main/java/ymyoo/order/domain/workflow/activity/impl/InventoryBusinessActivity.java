package ymyoo.order.domain.workflow.activity.impl;

import ymyoo.infra.messaging.remote.channel.Callback;
import ymyoo.order.domain.Order;
import ymyoo.order.domain.workflow.activity.AsyncBusinessActivity;
import ymyoo.order.messaging.endpoint.InventoryChannelAdapter;

/**
 * 주문시 수행 해야할 재고 관련 작업 모음
 *
 * Created by 유영모 on 2016-10-10.
 */
public class InventoryBusinessActivity implements AsyncBusinessActivity<Order, Boolean> {
    private final String id = java.util.UUID.randomUUID().toString().toUpperCase();

    @Override
    public void perform(Order order, Callback<Boolean> callback) {
        InventoryChannelAdapter channelAdapter = new InventoryChannelAdapter();
        channelAdapter.checkAndReserveOrderItem(this.id, order.getOrderItem(), callback);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
