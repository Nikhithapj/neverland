
package com.example.admin.controller;

import com.example.library.dto.DailyEarnings;
import com.example.library.dto.DailyEarningsMapping;
import com.example.library.dto.ProductDto;
import com.example.library.dto.TotalPriceByPayment;
import com.example.library.service.DashBoardService;
import com.example.library.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class DashboardController {

    private final DashBoardService dashBoardService;
    private final ProductService productService;

    public DashboardController(DashBoardService dashBoardService,ProductService productService  ) {
        this.dashBoardService = dashBoardService;
        this.productService=productService;
    }

    @RequestMapping(value={"/index","/"})
    public String home(Model model, Principal principal, HttpSession session,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                       @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws JsonProcessingException {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Home Page");
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication instanceof AnonymousAuthenticationToken){
            return "redirect:/login";
        }

        /* Earning card*/
        YearMonth currentYear=YearMonth.now();
        LocalDate localStartDate = LocalDate.of(currentYear.getYear(), currentYear.getMonthValue(), 1);
        LocalDate localEndDate = currentYear.atEndOfMonth();
         startDate = java.sql.Date.valueOf(localStartDate);
         endDate = java.sql.Date.valueOf(localEndDate);
        double currentMonthEarning=dashBoardService.findCurrentMonthOrder(startDate,endDate);
        Month currentMonth=currentYear.getMonth();
        model.addAttribute("currentMonth",currentMonth);
        model.addAttribute("currentMonthEarning",currentMonthEarning);

        LocalDate localStartDateYearly = LocalDate.of(currentYear.getYear(),Month.JANUARY,1);
        LocalDate localEndDateYearly = LocalDate.of(currentYear.getYear(),Month.DECEMBER,31);
        Date startDateYearly = java.sql.Date.valueOf(localStartDateYearly);
        Date endDateYearly = java.sql.Date.valueOf(localEndDateYearly);
        double currentYearlyEarning=dashBoardService.findCurrentMonthOrder(startDateYearly,endDateYearly);
        int year=currentYear.getYear();
        model.addAttribute("currentYear",year);
        model.addAttribute("currentYearlyEarning",currentYearlyEarning);

        int totalOrders= (int) dashBoardService.findOrdersTotal();
        model.addAttribute("totalOrders",totalOrders);
        int totalPendingOrders= (int) dashBoardService.findOrdersPending();
        model.addAttribute("totalPendingOrders",totalPendingOrders);
        int progress=0;
        if(totalOrders!=0) {
            progress = (totalPendingOrders * 100) / totalOrders;
        }else{
            progress=0;
        }
        model.addAttribute("progress",progress);

        /* Earning chart*/

        int currentYr=currentYear.getYear();
        int currentMnt=currentYear.getMonthValue();
        List<Object[]> dailyEarnings=dashBoardService.retrieveDailyEarnings(currentYr,currentMnt);
        List<DailyEarningsMapping> dailyEarningListForJson=new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Define your date format

        for(Object[] obj : dailyEarnings){

            Date date = (Date) obj[0];

            Double amount = (Double) obj[1];
            DailyEarnings dailyEarnings1 = new DailyEarnings(date,amount);
            String input=dailyEarnings1.toString();
            String datePart = input.substring(input.indexOf("date=")+"date=".length(),input.indexOf(" "));
            DailyEarningsMapping dailyEarningsMapping=new DailyEarningsMapping(datePart,amount);
            dailyEarningListForJson.add(dailyEarningsMapping);
        }

        ObjectMapper objectMapper=new ObjectMapper();
        String dailyEarningJson = objectMapper.writeValueAsString(dailyEarningListForJson);
        model.addAttribute("dailyEarnings",dailyEarningJson);

        /* Earning chart End */

        /* Pie chart Start*/
        List<Object[]> priceByPayMethod=dashBoardService.findTotalPricesByPayment();
        System.out.println(priceByPayMethod);
        List<TotalPriceByPayment> totalPriceByPaymentList=new ArrayList<>();
        for(Object[] obj: priceByPayMethod){
            String payMethod= (String) obj[0];
            Double amount= (Double) obj[1];
            TotalPriceByPayment totalPriceByPayment=new TotalPriceByPayment(payMethod,amount);
            totalPriceByPaymentList.add(totalPriceByPayment);
        }

        ObjectMapper objectMapper1=new ObjectMapper();
        String totalPriceByPayment = objectMapper1.writeValueAsString(totalPriceByPaymentList);
        model.addAttribute("totalPriceByPayment",totalPriceByPayment);

        List<Object[]> totalQuantityPerProduct=productService.getTotalQuantityPerProduct();
        model.addAttribute("totalQuantityPerProduct",totalQuantityPerProduct);




        String period;
        switch (filter) {
            case "week" -> {
                period = "week";
                // Get the starting date of the current week
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                startDate = calendar.getTime();
                // Get today's date
                endDate = new Date();
            }
            case "month" -> {
                period = "month";
                // Get the starting date of the current month
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTime();
                //Get today's Date
                endDate = new Date();
            }
            case "day" -> {
                period = "day";
                // Get today's date
                LocalDate today = LocalDate.now();
                // Set the start date to 12:00:00 AM
                LocalDateTime startDateTime = today.atStartOfDay();
                // Set the end date to 11:59:59 PM
                LocalDateTime endDateTime = today.atTime(23, 59, 59);
                // Convert to Date objects
                ZoneId zone = ZoneId.systemDefault();
                startDate = Date.from(startDateTime.atZone(zone).toInstant());
                endDate = Date.from(endDateTime.atZone(zone).toInstant());
            }
            case "year" -> {
                period= "year";
                // Get the starting date of the current year
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                startDate = calendar.getTime();
                // Get today's date
                endDate = new Date();
            }
            default -> {
                // Default case: filter
                period="";
                filter="";

            }
        }
        if (filter!="") {
            List<Object[]>productStats = productService.getProductsStatsBetweenDates(startDate,endDate);
            model.addAttribute("productStats",productStats);
        } else {
            List<Object[]>productStats = productService.getProductStats();
            model.addAttribute("productStats",productStats);
        }
//        Double totalAmount = orderService.getTotalOrderAmount();
//        model.addAttribute("period", period);
//        Long totalProducts = productService.countAllProducts();
//        Long totalCategory = categoryService.countAllCategories();
//        Long totallOrders = orderService.countTotalConfirmedOrders();
//        Double monthlyRevenue = orderService.getTotalAmountForMonth();
//        model.addAttribute("TotalAmount",totalAmount);
//        model.addAttribute("TotalProducts",totalProducts);
//        model.addAttribute("TotalCategory",totalCategory);
//        model.addAttribute("TotalOrders",totalOrders);
//        model.addAttribute("MonthlyRevenue",monthlyRevenue);
//        session.setAttribute("userLoggedIn", true);


        return "index";
    }
}
