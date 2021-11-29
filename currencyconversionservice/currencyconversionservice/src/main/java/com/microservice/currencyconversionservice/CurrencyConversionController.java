package com.microservice.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.swing.plaf.basic.BasicIconFactory;
import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;


    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable BigDecimal quantity)
    {
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from",from);
        uriVariables.put("to",to);

        ResponseEntity<CurrencyConversion> entity =  new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                                             CurrencyConversion.class,
                                               uriVariables );

        CurrencyConversion currencyConversionResponse = entity.getBody();


        return new CurrencyConversion(currencyConversionResponse.getId(),
                                        from, to, quantity,
                                        currencyConversionResponse.getConversionMultiple(),
                                        quantity.multiply(currencyConversionResponse.getConversionMultiple()),
                                         currencyConversionResponse.getEnvironment());
    }



    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion_feign(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable BigDecimal quantity)
    {
        CurrencyConversion currencyConversionResponse = proxy.retrieveExchangeValue(from, to);


        return new CurrencyConversion(currencyConversionResponse.getId(),
                from, to, quantity,
                currencyConversionResponse.getConversionMultiple(),
                quantity.multiply(currencyConversionResponse.getConversionMultiple()),
                currencyConversionResponse.getEnvironment());
    }
}
