package com.example.rent_apartment_product_module.service;

import com.example.rent_apartment_product_module.email_sender.MailSender;
import com.example.rent_apartment_product_module.entity.ApartmentEntity;
import com.example.rent_apartment_product_module.entity.BookingHistoryEntity;
import com.example.rent_apartment_product_module.entity.ClientApplicationEntity;
import com.example.rent_apartment_product_module.entity.ProductEntity;
import com.example.rent_apartment_product_module.repository.BookingHistoryRepository;
import com.example.rent_apartment_product_module.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProductModuleServiceImpl implements ProductModuleService {

    private final BookingHistoryRepository bookingHistoryRepository;

    private final ProductRepository productRepository;

    private final MailSender mailSender;

    public Double prepareProduct(Long id) {

        BookingHistoryEntity history = bookingHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException());

        ApartmentEntity apartmentEntity = history.getApartmentEntity();

        ClientApplicationEntity clientApplicationEntity = history.getClientApplicationEntity();

        List<ProductEntity> products = prepareDiscountCollection();

        if (!products.isEmpty()) {
            for (ProductEntity p : products) {
                if (nonNull(p.getDiscount())) {
                    /**Скидка 17% если пользователь пользовался сервисом десять или больше раз*/
                    if (p.getId() == 3l) {
                        if (clientApplicationEntity.getBookingCount() >= 10) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 15% если пользователь еще ниразу не арендовал квартиру*/
                    if (p.getId() == 1l) {
                        if (clientApplicationEntity.getBookingCount() == 1) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 12% если пользователь пользовался сервисом пять или больше раз*/
                    if (p.getId() == 2l) {
                        if (clientApplicationEntity.getBookingCount() >= 5) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 10% если город пользователя и город аренды совпадают*/
                    if (p.getId() == 4l) {
                        if (clientApplicationEntity.getParentCity().equals(apartmentEntity.getAddressEntity().getCity())) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 10% если пользователь арендовал квартиру на 10 или больше дней*/
                    if (p.getId() == 6l) {
                        if (history.getDaysCount() >= 10) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 10% если пользователь зарегистрировался по реферальной ссылке,хранится 3 месяца*/
                    if (p.getId() == 9l) {
                        if (clientApplicationEntity.getParentCity().equals(apartmentEntity.getAddressEntity().getCity())) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 7% если пользователь арендует квартиру в зимнее время года*/
                    if (p.getId() == 8l && p.getStatus().equals("true")) {
                        if (clientApplicationEntity.getParentCity().equals(apartmentEntity.getAddressEntity().getCity())) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 5% если пользователь арендовал квартиру на 5 или больше дней*/
                    if (history.getDaysCount() >= 5) {
                        if (clientApplicationEntity.getParentCity().equals(apartmentEntity.getAddressEntity().getCity())) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                    /**Скидка 5% если пользователь ввел промокод*/
                    if (p.getId() == 7l) {
                        if (clientApplicationEntity.getParentCity().equals(apartmentEntity.getAddressEntity().getCity())) {
                            saveProductToBookingHistory(p, history);
                            break;
                        }
                    }
                }
            }
        }

        long paymentWithoutDiscount = history.getDaysCount() * Long.parseLong(apartmentEntity.getPrice());

        if (nonNull(history.getProductEntity().getDiscount())) {
            Long discountNumber = history.getProductEntity().getDiscount();
            Double discountPercent = (double) discountNumber / 100.0;
            Double finalPayment = (double) paymentWithoutDiscount - (paymentWithoutDiscount * discountPercent);
            history.setFinalPayment(finalPayment);
            bookingHistoryRepository.save(history);
            mailSender.sendEmail("Бронирование квартиры" , "Квартира забронирована", history.getClientApplicationEntity().getLoginMail());
            return finalPayment;
        }
        Double finalPayment = (double) paymentWithoutDiscount;
        history.setFinalPayment(finalPayment);
        return finalPayment;
    }

    private void saveProductToBookingHistory(ProductEntity productEntity, BookingHistoryEntity history) {
        history.setProductEntity(productEntity);
        bookingHistoryRepository.save(history);
    }

    private List<ProductEntity> prepareDiscountCollection() {

        List<ProductEntity> collect = productRepository.findAll();
        Collections.sort(collect, Comparator.comparing((ProductEntity productEntity) -> productEntity.getDiscount()).reversed());

        return collect.stream().filter(p -> p.getStatus().equals("true")).collect(Collectors.toList());
    }
}
