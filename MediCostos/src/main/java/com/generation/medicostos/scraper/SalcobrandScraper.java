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
public class SalcobrandScraper {

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
            driver.get("https://salcobrand.cl/t/medicamentos");

            Thread.sleep(2000);
            WebElement npButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"search-result\"]/div[1]/div[2]/div/div[2]/div[2]/div/div/select")));
            npButton.click();
            Thread.sleep(2000);
            WebElement np96Button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"search-result\"]/div[1]/div[2]/div/div[2]/div[2]/div/div/select/option[4]")));
            np96Button.click();
            Thread.sleep(5000);

            int pg = 33;

            for (int page = 1; page <= pg; page++) {
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pagination-sm .active a")));
                    WebElement currentPageElement = driver.findElement(By.cssSelector(".pagination-sm .active a"));
                    String currentPage = currentPageElement.getText();
                    System.out.println("Página actual: " + currentPage);

                    // Scroll hasta el fondo de la página
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    Thread.sleep(2000);

                    js.executeScript("window.scrollBy(0, document.body.scrollHeight);");
                    Thread.sleep(2000);

                    js.executeScript("window.scrollBy(0, -1500);");
                    Thread.sleep(1000);

                    List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".inner-product-box")));
                    System.out.println("boxes: " + productList.size());

                    for (WebElement product : productList) {
                        String nombre = null;
                        String complemento = null;
                        String precioString = null;
                        System.out.println("entro al for");

                        try {
                            nombre = product.findElement(By.cssSelector(".product-name.truncate")).getText();
                        } catch (Exception e) {
                            System.out.println("Nombre no encontrado");
                        }
                        System.out.println("hay nombre");

                        try {
                            complemento = product.findElement(By.cssSelector(".product-info.truncate")).getText();
                        } catch (Exception e) {
                            System.out.println("Compuesto activo no encontrado");
                        }

                        try {
                            precioString = product.findElement(By.cssSelector(".price.selling")).getText();
                        } catch (Exception e) {
                            System.out.println("Precio no encontrado");
                        }

                        if (precioString != null) {
                            String precioSinSimbolos = precioString.replace("Precio farmacia: $", "").replace(".", "");
                            BigDecimal precio = new BigDecimal(precioSinSimbolos);

                            String urlImagen = null;
                            try {
                                WebElement imgElement = product.findElement(By.cssSelector(".inner-product-box img"));
                                urlImagen = imgElement.getAttribute("src");
                            } catch (Exception e) {
                                System.out.println("Imagen no encontrada");
                            }

                            String urlMedicamento = null;
                            try {
                                urlMedicamento = product.findElement(By.cssSelector(".info a")).getAttribute("href");
                            } catch (Exception e) {
                                System.out.println("URL de medicamento no encontrada");
                            }

                            // ID de la farmacia "Salcobrand"
                            Long farmaciaID = 2L;

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

                    // Esperar a que el botón de siguiente página sea clickable y luego hacer clic
                    if (page < pg) {
                        Thread.sleep(100);

                        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'»')]")));
                        nextButton.click();
                        js.executeScript("window.scrollBy(0, -38500)");
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
