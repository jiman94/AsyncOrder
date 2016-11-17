package ymyoo.order.domain.workflow.activity;

import ymyoo.order.domain.Order;
import ymyoo.payment.ApprovalOrderPayment;
import ymyoo.payment.PaymentGateway;

/**
 * 주문시 수행 해야할 결제 관련 작업 모음
 *
 * Created by 유영모 on 2016-10-10.
 */
public class PaymentGatewaySequenceActivity implements SequenceActivity<ApprovalOrderPayment> {
    private Order order;

    public PaymentGatewaySequenceActivity(Order order) {
        this.order = order;
    }

    @Override
    public ApprovalOrderPayment perform() {
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.authenticate(this.order.getOrderPayment());
        ApprovalOrderPayment approvalOrderPayment = paymentGateway.approve(this.order.getOrderPayment());

        return approvalOrderPayment;
    }
}