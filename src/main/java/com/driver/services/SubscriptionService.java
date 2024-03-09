package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        // check if user is present or not
        Optional<User> optionalUser = userRepository.findById(subscriptionEntryDto.getUserId());
        if(!optionalUser.isPresent()){
            throw new RuntimeException("User does not exist");
        }


        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(optionalUser.get());

        int screens = subscriptionEntryDto.getNoOfScreensRequired();
        int amount;
        SubscriptionType type = subscriptionEntryDto.getSubscriptionType();

        if(String.valueOf(type).equals("BASIC")){
            amount = 500 + 200 * screens;
        }
        else if(String.valueOf(type).equals("PRO")){
            amount = 800 + 250 * screens;
        }
        else{
            amount = 1000 + 350 * screens;
        }

        subscription.setTotalAmountPaid(amount);
        subscriptionRepository.save(subscription);

        User user = optionalUser.get();
        user.setSubscription(subscription);
        userRepository.save(user);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()){
            throw  new RuntimeException("No user found");
        }

        User user = optionalUser.get();
        SubscriptionType type = user.getSubscription().getSubscriptionType();

        if(type == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }

        Subscription subscription = user.getSubscription();
        int screens = user.getSubscription().getNoOfScreensSubscribed();
        int alreadyPaidAmount = subscription.getTotalAmountPaid();;
        int amountToBePaid;
        int diffInAmount;
        if(type == SubscriptionType.BASIC){

            // amount to be paid for an updated(PRO) subscription
            amountToBePaid = 800 + 250 * screens;
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(amountToBePaid);
            diffInAmount = amountToBePaid - alreadyPaidAmount;


        }else{
            // amount to be paid for an updated(ELITE) subscription
            amountToBePaid = 1000 + 350 * screens;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(amountToBePaid);
            diffInAmount = amountToBePaid - alreadyPaidAmount;
        }

        subscriptionRepository.save(subscription);
        user.setSubscription(subscription);
        return diffInAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int totalRevenue = 0;
        for(Subscription sub: subscriptionList){
            totalRevenue += sub.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
