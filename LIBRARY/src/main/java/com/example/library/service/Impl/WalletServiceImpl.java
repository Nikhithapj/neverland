package com.example.library.service.Impl;

import com.example.library.dto.WalletHistoryDto;
import com.example.library.enums.WalletTransactionType;
import com.example.library.model.Customer;
import com.example.library.model.Order;
import com.example.library.model.Wallet;
import com.example.library.model.WalletHistory;
import com.example.library.repository.WalletHistoryRepository;
import com.example.library.repository.WalletRepository;
import com.example.library.service.WalletService;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service

public class WalletServiceImpl implements WalletService {
    private WalletHistoryRepository walletHistoryRepository;
    private final WalletRepository walletRepository;
    public WalletServiceImpl(WalletHistoryRepository walletHistoryRepository,WalletRepository walletRepository) {
        this.walletHistoryRepository = walletHistoryRepository;
        this.walletRepository=walletRepository;
    }

    @Override
    public List<WalletHistoryDto> findAllById(long id) {
        List<WalletHistory>walletHistory=walletHistoryRepository.findAllById(id);
        List<WalletHistoryDto>walletHistoryDtoList=transferData(walletHistory);
        return walletHistoryDtoList;
    }

    @Override
    public WalletHistory debit(Wallet wallet, double totalPrice) {
        double newBalance=wallet.getBalance()-totalPrice;
        DecimalFormat  decimalFormat=new DecimalFormat("#0.00");
        String formattedBalance=decimalFormat.format(newBalance);
        double formattedDoubleBalance=Double.parseDouble(formattedBalance);
        wallet.setBalance(formattedDoubleBalance);
        walletRepository.save(wallet);
        WalletHistory walletHistory= new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionType.DEBITED);
        walletHistory.setAmount(totalPrice);
        walletHistory.setTransactionStatus("Success");
        LocalDate currentDate= LocalDate.now();
        walletHistory.setTransactionDate(currentDate);

        WalletHistory walletHistory1=walletHistoryRepository.save(walletHistory);
        return walletHistory1;
    }

    @Override
    public void saveOrderId(Order order, WalletHistory walletHistory) {
        walletHistory.setOrder(order);
        walletHistoryRepository.save(walletHistory);
    }

    @Override
    public Wallet findByCustomer(Customer customer) {
        Wallet wallet;
        if(customer.getWallet()==null){
            wallet=new Wallet();
            wallet.setBalance(0.0);
            wallet.setCustomer(customer);
            walletRepository.save(wallet);
        }
        else{
            wallet=customer.getWallet();
        }
        return wallet;
    }

    @Override
    public WalletHistory save(double amount, Customer customer) {
        Wallet wallet=customer.getWallet();
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionType.TOPUP);
        walletHistory.setAmount(amount);
        LocalDate currentDate=LocalDate.now();
        walletHistory.setTransactionDate(currentDate);
        walletHistoryRepository.save(walletHistory);
        return walletHistory;
    }

    @Override
    public WalletHistory findById(long id) {
        WalletHistory walletHistory=walletHistoryRepository.findById(id);
        return walletHistory;
    }

    @Override
    public void updateWallet(WalletHistory walletHistory, Customer customer, boolean status) {
    Wallet wallet=customer.getWallet();
    if(status){
        walletHistory.setTransactionStatus("Success");
        walletHistoryRepository.save(walletHistory);
        double newBalance= wallet.getBalance()+walletHistory.getAmount();
        DecimalFormat decimalFormat=new DecimalFormat("#0.00");
        String formattedBalance=decimalFormat.format(newBalance);
        double formattedDoubleBalance=Double.parseDouble(formattedBalance);
        wallet.setBalance(formattedDoubleBalance);
        walletRepository.save(wallet);

    }else{
        walletHistory.setTransactionStatus("Failed");
        walletHistoryRepository.save(walletHistory);
    }
    }

    @Override
    public void returnCredit(Order order, Customer customer) {
        Wallet wallet=customer.getWallet();
        wallet.setBalance(wallet.getBalance()+order.getTotalPrice());
        walletRepository.save(wallet);
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionType.CREDITED);
        walletHistory.setTransactionStatus("Success");
        walletHistory.setAmount(order.getTotalPrice());
        walletHistory.setOrder(order);
        LocalDate currentDate=LocalDate.now();
        walletHistory.setTransactionDate(currentDate);
        walletHistoryRepository.save(walletHistory);
    }

    @Override
    public boolean saveReferralOffer(double amount, Customer customer) {
      Wallet wallet;
      if(customer.getWallet()==null){
          wallet=new Wallet();
          wallet.setBalance(0.0);
          wallet.setCustomer(customer);
          walletRepository.save(wallet);
      }else{
          wallet=customer.getWallet();
      }
      WalletHistory walletHistory=new WalletHistory();
      walletHistory.setTransactionStatus("Success");
      walletHistory.setWallet(wallet);

      walletHistory .setType(WalletTransactionType.REFERAL_CREDIT);
      walletHistory.setAmount(amount);
      LocalDate currentDate=LocalDate.now();
      walletHistory .setTransactionDate(currentDate);
      try{
          walletHistoryRepository.save(walletHistory);
          double newBalance=wallet.getBalance()+walletHistory.getAmount();
          DecimalFormat decimalFormat=new DecimalFormat("#0.00");
          String formattedBalance=decimalFormat.format(newBalance);
          double formattedDoubleBalance=Double.parseDouble(formattedBalance);
          wallet.setBalance(formattedDoubleBalance);
          walletRepository.save(wallet);
          return  true;
      }
      catch(Exception e){
          e.printStackTrace();
          return  false;
      }

    }


    private List<WalletHistoryDto>transferData(List<WalletHistory>walletHistoryList){
        List<WalletHistoryDto>walletHistoryDtoList=new ArrayList<>();
        for(WalletHistory walletHistory:walletHistoryList){
            WalletHistoryDto walletHistoryDto=new WalletHistoryDto();
            walletHistoryDto.setId(walletHistory.getId());
            walletHistoryDto.setType(walletHistory.getType());
            walletHistoryDto.setAmount(walletHistory.getAmount());
            walletHistoryDto.setWallet(walletHistory.getWallet());
            walletHistoryDto.setTransactionStatus(walletHistory.getTransactionStatus());
            walletHistoryDto.setTransactionDate(walletHistory.getTransactionDate());

            Order order=walletHistory.getOrder();
            if(order!=null){
                walletHistoryDto.setOrderId(order.getId());
            }
            walletHistoryDtoList.add(walletHistoryDto);

        }
        return walletHistoryDtoList;
    }



}
