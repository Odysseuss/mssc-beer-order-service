package guru.sfg.beer.order.service.testcomponents;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message message) {

        AllocateOrderRequest allocateOrderRequest = (AllocateOrderRequest) message.getPayload();

        final boolean isPendingInventory = "inventory-pending".equals(allocateOrderRequest.getBeerOrderDto().getCustomerRef());
        final boolean isAllocationError = "allocation-failed".equals(allocateOrderRequest.getBeerOrderDto().getCustomerRef());

        if ("hold-allocation".equals(allocateOrderRequest.getBeerOrderDto().getCustomerRef())) {
            return;
        }

        allocateOrderRequest.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            int allocatedQuantity = (!isPendingInventory) ? beerOrderLineDto.getOrderQuantity() : beerOrderLineDto.getOrderQuantity() - 1;
            beerOrderLineDto.setQuantityAllocated(allocatedQuantity);
        });

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                AllocateOrderResponse.builder()
                        .beerOrderDto(allocateOrderRequest.getBeerOrderDto())
                        .pendingInventory(isPendingInventory)
                        .allocationError(isAllocationError)
                        .build());
    }
}
