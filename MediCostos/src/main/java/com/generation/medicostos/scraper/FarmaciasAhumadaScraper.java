package com.generation.medicostos.scraper;

import com.generation.medicostos.dto.MedicamentoDTO;
import com.generation.medicostos.repository.MedicamentoRepository;
import com.generation.medicostos.service.MedicamentoService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Component
public class FarmaciasAhumadaScraper {

    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private MedicamentoService medicamentoService;

    public void scrapeAndSaveMedications() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.logfile", System.getProperty("user.dir") + "/src/main/resources/chromedriver.log");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("https://www.farmaciasahumada.cl/medicamentos");
            Thread.sleep(6000);
            try {

                WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'No')]")));
                noButton.click();
            }catch (Exception e){
            System.out.println("no hay ventana extra");}

           try {


            for (int i = 0; i < 500; i++) {
                Thread.sleep(750);

                WebElement npButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Más Resultados')]")));
                npButton.click();
            }}catch (Exception e){
               System.out.println("ya no hay mas paginas");
           }

            List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".product-tile-wrapper")));
            System.out.println("boxes: " + productList.size());

            for (WebElement product : productList) {
                System.out.println("Entra al for");
                String nombre = null;

                try {
                    nombre = product.findElement(By.cssSelector(".pdp-link a.link")).getText();
                } catch (Exception e) {
                    System.out.println("Nombre no encontrado");
                }

                String complemento = null;
                try {
                    complemento = product.findElement(By.cssSelector(".product-tile-brand span.link")).getText();
                } catch (Exception e) {
                    System.out.println("Complemento no encontrado");
                }

                String precioString = null;
                try {
                    precioString = product.findElement(By.cssSelector(".price .value")).getText();
                } catch (Exception e) {
                    System.out.println("Precio no encontrado");
                }

                if (precioString != null) {
                    String precioSinSimbolos = precioString.replace("$", "").replace(".", "");
                    BigDecimal precio = new BigDecimal(precioSinSimbolos);

                    System.out.println("Antes de imagen");
                    String urlImagen = null;
                    try {
                        WebElement imgElement = product.findElement(By.cssSelector(".image-container img.tile-image"));
                        urlImagen = imgElement.getAttribute("src");
                    } catch (Exception e) {
                        System.out.println("Imagen no encontrada");
                    }

                    String urlMedicamento = null;
                    try {
                        urlMedicamento = "https://www.farmaciasahumada.cl" + product.findElement(By.cssSelector(".pdp-link a.link")).getAttribute("href");
                    } catch (Exception e) {
                        System.out.println("URL de medicamento no encontrada");
                    }

                    // ID de la farmacia "Farmacias Ahumada"
                    Long farmaciaID = 1L;

                    MedicamentoDTO medicamentoDTO = new MedicamentoDTO();
                    medicamentoDTO.setNombre(nombre);
                    medicamentoDTO.setComplemento(complemento);
                    medicamentoDTO.setPrecio(precio);
                    medicamentoDTO.setUrlImagen(urlImagen);
                    medicamentoDTO.setUrlMedicamento(urlMedicamento);
                    medicamentoDTO.setFarmaciaId(farmaciaID);

                    System.out.println(medicamentoDTO);

                    medicamentoService.saveMedicamento(medicamentoDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
