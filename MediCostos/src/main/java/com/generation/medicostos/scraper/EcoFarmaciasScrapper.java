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
public class EcoFarmaciasScrapper {

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
            driver.get("https://www.ecofarmacias.cl/categoria-producto/medicamentos/");

            Thread.sleep(3000);

            int pg = 59;

            for (int page = 1; page <= pg; page++) {
                try {
                    System.out.println("entre al try");
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.current")));
                    WebElement currentPageElement = driver.findElement(By.cssSelector("span.current"));
                    String currentPage = currentPageElement.getText();
                    System.out.println("Página actual: " + currentPage);

                    // Scroll hasta el fondo de la página
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    Thread.sleep(2000);

                    js.executeScript("window.scrollBy(0, document.body.scrollHeight);");
                    Thread.sleep(2000);


                    List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".product")));
                    System.out.println("boxes: " + productList.size());

                    for (WebElement product : productList) {
                        String nombre = null;
                        String complemento = "";
                        String precioString = null;
                        System.out.println("entro al for");

                        try {
                            nombre = product.findElement(By.cssSelector(".woocommerce-loop-product__title")).getText();
                            System.out.println("nombre: " + nombre);
                        } catch (Exception e) {
                            System.out.println("Nombre no encontrado");
                        }



                        try {
                            System.out.println("entra a precio");
                            precioString = product.findElement(By.cssSelector(".amount")).getText();
                        } catch (Exception e) {
                            System.out.println("Precio no encontrado");
                        }

                        if (precioString != null) {
                            String precioSinSimbolos = precioString.replace("$", "").replace(".", "");
                            System.out.println(precioSinSimbolos);
                            BigDecimal precio = new BigDecimal(precioSinSimbolos);
                            System.out.println("precio: " + precio);

                            String urlImagen = null;
                            try {
                                WebElement imgElement = product.findElement(By.cssSelector(".et_shop_image img"));
                                urlImagen = imgElement.getAttribute("src");
                            } catch (Exception e) {
                                System.out.println("Imagen no encontrada");
                            }

                            String urlMedicamento = null;
                            try {
                                urlMedicamento = product.findElement(By.cssSelector("a.woocommerce-LoopProduct-link.woocommerce-loop-product__link")).getAttribute("href");
                            } catch (Exception e) {
                                System.out.println("URL de medicamento no encontrada");
                            }

                            // ID de la farmacia "Eco Farmacias"
                            Long farmaciaID = 4L;

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

                        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'→')]")));
                        nextButton.click();
                    }
                } catch (Exception e) {
                    System.out.println("no avanza la pagina");
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
