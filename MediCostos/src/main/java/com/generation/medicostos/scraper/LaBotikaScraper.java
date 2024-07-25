package com.generation.medicostos.scraper;

import com.generation.medicostos.dto.MedicamentoDTO;
import com.generation.medicostos.repository.MedicamentoRepository;
import com.generation.medicostos.service.MedicamentoService;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
public class LaBotikaScraper {

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
            driver.get("https://farmacialabotika.cl/collections/medicamentos");
            Thread.sleep(4000);

            WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("            //*[@id=\"omnisend-form-63b2014c3316ca7c173a8958-form-close-icon\"]\n")));
            noButton.click();




            int pg = 44;

            for (int page = 1; page <= pg; page++) {
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pagination__nav-item.is-active")));
                    WebElement currentPageElement = driver.findElement(By.cssSelector(".pagination__nav-item.is-active"));
                    String currentPage = currentPageElement.getText();
                    System.out.println("Página actual: " + currentPage);

                    // Scroll hasta el fondo de la página
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    Thread.sleep(2000);

                    js.executeScript("window.scrollBy(0, document.body.scrollHeight);");
                    Thread.sleep(2000);

                    js.executeScript("window.scrollBy(0, -700);");
                    Thread.sleep(1000);

                    List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".product-item.product-item--vertical")));
                    System.out.println("boxes: " + productList.size());

                    for (WebElement product : productList) {
                        String nombre = null;
                        String complemento = "";
                        String precioString = null;
                        System.out.println("entro al for");

                        try {
                            nombre = product.findElement(By.cssSelector(".product-item__title.text--strong.link")).getText();
                        } catch (Exception e) {
                            System.out.println("Nombre no encontrado");
                        }


                        try {
                            precioString = product.findElement(By.cssSelector(".price")).getText();
                        } catch (Exception e) {
                            System.out.println("Precio no encontrado");
                        }

                        if (precioString != null) {
                            String precioSinSimbolos = precioString.replace("$", "").replace(".", "").replace(",", "").replace("Precio de venta\n", "");

                            BigDecimal precio = new BigDecimal(precioSinSimbolos);

                            String urlImagen = null;
                            try {
                                WebElement imgElement = product.findElement(By.cssSelector("img.product-item__primary-image"));
                                urlImagen = imgElement.getAttribute("src");
                                System.out.println(urlImagen);
                            } catch (Exception e) {
                                System.out.println("Imagen no encontrada");
                            }

                            String urlMedicamento = null;
                            try {
                                urlMedicamento = product.findElement(By.cssSelector("a.product-item__title.text--strong.link")).getAttribute("href");
                            } catch (Exception e) {
                                System.out.println("URL de medicamento no encontrada");
                            }

                            // ID de la farmacia "Salcobrand"
                            Long farmaciaID = 6L;

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

                        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Siguiente')]")));
                        nextButton.click();
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
