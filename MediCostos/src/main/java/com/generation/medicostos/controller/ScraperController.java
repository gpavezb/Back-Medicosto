package com.generation.medicostos.controller;

import com.generation.medicostos.scraper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {

    @Autowired
    private FarmaciasAhumadaScraper farmaciasAhumadaScraper;

    @Autowired
    private SalcobrandScraper salcobrandScraper;

    @Autowired
    private DrSimiScraper drSimiScraper;

    @Autowired
    private EcoFarmaciasScrapper ecoFarmaciasScrapper;

    @Autowired
    private FarmaciasChileScraper farmaciasChileScraper;

    @Autowired
    private LaBotikaScraper laBotikaScraper;

    @GetMapping("/run")
    public String runScraper() {
    //    drSimiScraper.scrapeAndSaveMedications();
    //   farmaciasChileScraper.scrapeAndSaveMedications();
    //    farmaciasAhumadaScraper.scrapeAndSaveMedications();
      laBotikaScraper.scrapeAndSaveMedications();
    //  salcobrandScraper.scrapeAndSaveMedications();
     //  ecoFarmaciasScrapper.scrapeAndSaveMedications();
        return "Scraper ejecutado con éxito";
    }
}