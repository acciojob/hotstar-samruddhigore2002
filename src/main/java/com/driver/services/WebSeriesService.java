package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.exception.NoProductionHouseFoundException;
import com.driver.exception.WebSeriesAlreadyExistsException;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

//        WebSeries foundWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
//        if(foundWebSeries.getSeriesName().equals(webSeriesEntryDto.getSeriesName())){
//            throw new WebSeriesAlreadyExistsException("Series already present");
//        }

        Optional<ProductionHouse> optionalProductionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
        if(!optionalProductionHouse.isPresent()){
            throw new NoProductionHouseFoundException("No production house found by this name");
        }

        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(), webSeriesEntryDto.getAgeLimit(), webSeriesEntryDto.getRating(), webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(optionalProductionHouse.get());
        webSeriesRepository.save(webSeries);

        ProductionHouse productionHouse = optionalProductionHouse.get();
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        webSeriesList.add(webSeries);
        productionHouse.setWebSeriesList(webSeriesList);
        productionHouseRepository.save(productionHouse);

        return webSeries.getId();
    }

}
