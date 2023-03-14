package guru.sfg.beer.order.service.services.listeners;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationResponseListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResponse allocateOrderResponse) {
        if (!allocateOrderResponse.getAllocationError() && !allocateOrderResponse.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPassed(allocateOrderResponse.getBeerOrderDto());
        } else if (!allocateOrderResponse.getAllocationError() && allocateOrderResponse.getPendingInventory()) {
            beerOrderManager.beeOrderAllocationPendingInventory(allocateOrderResponse.getBeerOrderDto());
        } else if (allocateOrderResponse.getAllocationError()) {
            beerOrderManager.beerOrderAllocationFailed(allocateOrderResponse.getBeerOrderDto());
        }
    }
}
