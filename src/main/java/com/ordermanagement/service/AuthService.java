package com.ordermanagement.service;

import com.ordermanagement.model.Customer;
import com.ordermanagement.model.Seller;
import com.ordermanagement.model.DeliveryPerson;
import com.ordermanagement.repository.CustomerRepository;
import com.ordermanagement.repository.SellerRepository;
import com.ordermanagement.repository.DeliveryPersonRepository;
import java.util.Optional;

public class AuthService {
    private CustomerRepository customerRepository;
    private SellerRepository sellerRepository;
    private DeliveryPersonRepository deliveryPersonRepository;
    
    public AuthService(CustomerRepository customerRepository, 
                      SellerRepository sellerRepository,
                      DeliveryPersonRepository deliveryPersonRepository) {
        this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository;
        this.deliveryPersonRepository = deliveryPersonRepository;
    }
    
    public Customer loginCustomer(String email, String password) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent() && customer.get().getPassword().equals(password)) {
            return customer.get();
        }
        throw new RuntimeException("Invalid credentials");
    }
    
    public Seller loginSeller(String email, String password) {
        Optional<Seller> seller = sellerRepository.findByEmail(email);
        if (seller.isPresent() && seller.get().getPassword().equals(password)) {
            return seller.get();
        }
        throw new RuntimeException("Invalid credentials");
    }
    
    public DeliveryPerson loginDeliveryPerson(String email, String password) {
        Optional<DeliveryPerson> deliveryPerson = deliveryPersonRepository.findByEmail(email);
        if (deliveryPerson.isPresent() && deliveryPerson.get().getPassword().equals(password)) {
            return deliveryPerson.get();
        }
        throw new RuntimeException("Invalid credentials");
    }
    
    public Customer registerCustomer(String email, String password, String name, String phone) {
        if (customerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        Customer customer = new Customer(email, password, name, phone);
        return customerRepository.save(customer);
    }
    
    public Seller registerSeller(String email, String password, String name, String phone, 
                                String businessName, String cnpj) {
        if (sellerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        Seller seller = new Seller(email, password, name, phone, businessName, cnpj);
        return sellerRepository.save(seller);
    }
    
    public DeliveryPerson registerDeliveryPerson(String email, String password, String name, 
                                                String phone, String vehicleType, String licensePlate) {
        if (deliveryPersonRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        DeliveryPerson deliveryPerson = new DeliveryPerson(email, password, name, phone, vehicleType, licensePlate);
        return deliveryPersonRepository.save(deliveryPerson);
    }
}