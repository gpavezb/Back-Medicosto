package com.generation.medicostos.scraper;

import com.generation.medicostos.dto.MedicamentoDTO;
import com.generation.medicostos.repository.MedicamentoRepository;
import com.generation.medicostos.service.MedicamentoService;
import org.openqa.selenium.*;
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
public class DrSimiScraper {

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
            driver.get("https://www.drsimi.cl/medicamento");
            Thread.sleep(7000);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            try{
            for (int i = 0; i < 65; i++) {
                Thread.sleep(2000);
                js.executeScript("window.scrollBy(0, 2500);");
                Thread.sleep(2000);
                WebElement npButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Mostrar más')]")));
                npButton.click();
            }}catch (Exception e){
                System.out.println("no encontro el boton");
            }

            List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".vtex-search-result-3-x-galleryItem.vtex-search-result-3-x-galleryItem--normal.vtex-search-result-3-x-galleryItem--grid.pa4")));
            System.out.println("boxes: " + productList.size());





            for (WebElement product : productList) {
                String nombre = null;
                String complemento = null;
                String precioString = null;

                try {
                    nombre = product.findElement(By.cssSelector("span.vtex-product-summary-2-x-productBrand.vtex-product-summary-2-x-brandName.t-body")).getText();
                } catch (Exception e) {
                    System.out.println("Nombre no encontrado");
                }

                try {
                    // Ajustar el selector CSS para el elemento que contiene "Prevención del embarazo"
                    complemento = product.findElement(By.cssSelector("span.vtex-product-summary-2-x-description.c-muted-2.t-small")).getText();
                }catch (Exception e) {
                    System.out.println("Complemento no encontrado");
                }

                try {
                    precioString = product.findElement(By.cssSelector(".justify-start.vtex-flex-layout-0-x-flexRowContent.vtex-flex-layout-0-x-flexRowContent--pricesQty.vtex-flex-layout-0-x-flexRowContent--pricesSummary.items-stretch.w-100")).getText();
                } catch (Exception e) {
                    System.out.println("Precio no encontrado");
                }

                if (precioString != null) {
                    String precioSinSimbolos = precioString.replace("$", "").replace(".", "");
                    BigDecimal precio = new BigDecimal(precioSinSimbolos);

                    String urlImagen = null;
                    try {
                        WebElement imgElement = product.findElement(By.cssSelector("img.vtex-product-summary-2-x-image"));
                        urlImagen = imgElement.getAttribute("src");
                    } catch (Exception e) {
                        System.out.println("Imagen no encontrada");
                    }

                    String urlMedicamento = null;
                    try {
                        urlMedicamento = product.findElement(By.cssSelector(".vtex-product-summary-2-x-clearLink")).getAttribute("href");
                    } catch (Exception e) {
                        System.out.println("URL de medicamento no encontrada");
                    }

                    // ID de la farmacia "Dr. Simi"
                    Long farmaciaID = 3L;

                    MedicamentoDTO medicamentoDTO = new MedicamentoDTO();
                    medicamentoDTO.setNombre(nombre);
                    medicamentoDTO.setComplemento(complemento);
                    medicamentoDTO.setPrecio(precio);
                    medicamentoDTO.setUrlImagen(urlImagen);
                    medicamentoDTO.setUrlMedicamento(urlMedicamento);
                    medicamentoDTO.setFarmaciaId(farmaciaID);


                    medicamentoService.saveMedicamento(medicamentoDTO);
                }
            }

            // Exportar la lista de medicamentos a un archivo Excel (opcional)
            // exportToExcel(medications);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
