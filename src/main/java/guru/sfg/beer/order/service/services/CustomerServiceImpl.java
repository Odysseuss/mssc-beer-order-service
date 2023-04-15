package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.web.mappers.CustomerMapper;
import guru.sfg.brewery.model.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {
        Page<Customer> allCustomersPage = customerRepository.findAll(pageable);

        return new CustomerPagedList(allCustomersPage
                .stream()
                .map(customerMapper::customerToDto)
                .collect(Collectors.toList()), PageRequest.of(allCustomersPage.getPageable().getPageNumber(),
                allCustomersPage.getPageable().getPageSize()),
                allCustomersPage.getTotalElements());
    }
}
