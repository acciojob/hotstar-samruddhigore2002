package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.exception.NoUserFoundException;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

        Optional<User> optionalUser = userRepository.findById(subscriptionEntryDto.getUserId());
        if(!optionalUser.isPresent()){
            throw new NoUserFoundException("User does not exists");
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

        return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        return null;
    }

}
