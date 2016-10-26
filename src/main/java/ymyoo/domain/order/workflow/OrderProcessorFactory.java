package ymyoo.domain.order.workflow;

import ymyoo.domain.order.Order;
import ymyoo.domain.order.OrderItemDeliveryType;
import ymyoo.domain.order.exception.UnSupportedDeliveryTypeException;

/**
 * Created by 유영모 on 2016-10-26.
 */
public class OrderProcessorFactory {

    public static OrderProcessor create(Order order) {
        if(order.getOrderItem().getDeliveryType() == OrderItemDeliveryType.DIRECTING) {
            return new DirectingDeliveryProductProcessor();
        } else if(order.getOrderItem().getDeliveryType() == OrderItemDeliveryType.AGENCY) {
            return new AgencyDeliveryProductProcessor();
        } else {
            throw new UnSupportedDeliveryTypeException();
        }
    }
}
