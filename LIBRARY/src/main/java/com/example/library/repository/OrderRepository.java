package com.example.library.repository;

import com.example.library.model.Customer;
import com.example.library.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface OrderRepository  extends JpaRepository<Order,Long> {

Order findById(long id);

List<Order>findByCustomer(Customer customer);

List<Order>findByOrderDateBetween(Date startDate,Date endDate);
int countByIsAcceptIsFalse();

//
//    @Query(value="SELECT DATE_TRUNC('day', o.order_date) AS date," +
//            " SUM(SUM(o.total_price))"+
//            " OVER (PARTITION BY DATE_TRUNC('day', o.order_date)) AS earnings FROM orders o " +
//            "WHERE EXTRACT(YEAR FROM o.order_date) = :year AND EXTRACT(MONTH FROM o.order_date) " +
//            "=:month GROUP BY DATE_TRUNC('day', o.order_date)",nativeQuery = true)
//    List<Object[]> dailyEarnings(@Param("year") int year, @Param("month") int month);
//now changing 8/11/ 3 56
//    @Query(value="SELECT DATE_FORMAT(o.order_date, '%Y-%m-%d') AS date, " +
//            "SUM(o.total_price) AS earnings " +
//            "FROM orders o " +
//            "WHERE YEAR(o.order_date) = :year AND MONTH(o.order_date) = :month " +
//            "GROUP BY DATE_FORMAT(o.order_date, '%Y-%m-%d')", nativeQuery = true)
//    List<Object[]> dailyEarnings(@Param("year") int year, @Param("month") int month);



@Query(value="SELECT DATE(o.order_date) AS date, " +
        "SUM(o.total_price) AS earnings " +
        "FROM orders o " +
        "WHERE YEAR(o.order_date) = :year AND MONTH(o.order_date) = :month " +
        "GROUP BY DATE(o.order_date)", nativeQuery = true)
List<Object[]> dailyEarnings(@Param("year") int year, @Param("month") int month);

//
//    @Query("SELECT o.paymentMethod, SUM(o.totalPrice) FROM Order o WHERE o.paymentMethod IN ('COD', 'RazorPay') GROUP BY o.paymentMethod")
//    List<Object[]> findTotalPricesByPaymentMethod();

    @Query("SELECT o.paymentMethod, SUM(o.totalPrice) FROM Order o WHERE o.paymentMethod IN ('COD', 'RazorPay') GROUP BY o.paymentMethod")
    List<Object[]> findTotalPricesByPaymentMethod();



}
