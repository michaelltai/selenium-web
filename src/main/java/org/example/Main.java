package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class Main {

    static int countList (List<WebElement> x, String siteName){
        int size = 0;
        for(WebElement element: x){
            if(siteName.contains("Amazon")){
                if(element.findElement(By.cssSelector(".s-underline-text.s-underline-link-text")).getText().contains("iPhone")){
                    size = size + 1;
                }
            }else{
                if(element.findElement(By.className("s-item__title")).getText().contains("iPhone")){
                    size = size + 1;
                }
            }

        }
        return size;
    }
    static float formatPrice(String price){
        String removeCurrency =  price.replace("$","");
        String newFormat = removeCurrency.replace(",","");
        return Float.parseFloat(newFormat);
    }
    public static void main(String[] args) {

        PhoneInfo[] amazonPhone;
        PhoneInfo[] ebayPhone;
        PhoneInfo[] phoneList;

        int amazonplist = 0;
        int ebayplist = 0;
        int totalplist;

        WebElement searchbox;
        List<WebElement> elements;
        String websiteName;

        //1. OPEN YOUR PREFFERED BROWSER
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver","");
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        //2. NAVIGATE TO AMAZON
        driver.get("https://www.amazon.com");
        searchbox = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchbox.sendKeys("iPhone 13");
        driver.findElement(By.id("nav-search-submit-button")).click();

        //3. RETRIEVE ELEMENT LIST
        elements = driver.findElements(By.cssSelector("div[data-component-type=\"s-search-result\"]"));
        websiteName = driver.findElement(By.id("nav-logo-sprites")).getAttribute("aria-label");
        amazonPhone = new PhoneInfo[countList(elements, websiteName)];

        for(WebElement e : elements){
            if(e.findElement(By.cssSelector(".s-underline-text.s-underline-link-text")).getText().contains("iPhone")){
                amazonPhone[amazonplist] = new PhoneInfo();
                String price = "0";
                String name = e.findElement(By.cssSelector(".s-underline-text.s-underline-link-text")).getText();
                String link = e.findElement(By.cssSelector(".s-underline-text.s-underline-link-text")).getAttribute("href");
                try{
                    price = e.findElement(By.className("a-price-whole")).getText();
                } catch (NoSuchElementException ignore){
                }
                amazonPhone[amazonplist].setData(name,link,formatPrice(price),websiteName);
                amazonplist++;
            }
        }

        //4. NAVIGATE TO EBAY
        driver.get("https://www.ebay.com");
        searchbox = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("gh-ac")));
        searchbox.sendKeys("iPhone 13");
        driver.findElement(By.id("gh-btn")).click();

       //5. RETRIEVE ELEMENT LIST
        elements = driver.findElements(By.cssSelector("li[class=\"s-item s-item__pl-on-bottom\"]"));
        websiteName = driver.findElement(By.cssSelector("meta[content=\"eBay\"]")).getAttribute("content");
        ebayPhone = new PhoneInfo[countList(elements, websiteName)];

        for(WebElement e : elements){
            if(e.findElement(By.className("s-item__title")).getText().contains("iPhone")){
                ebayPhone[ebayplist] = new PhoneInfo();

                String name = e.findElement(By.cssSelector(".s-item__title")).getText();
                String link = e.findElement(By.className("s-item__link")).getAttribute("href");
                String priceRange = e.findElement(By.className("s-item__price")).getText();
                String[] substr = priceRange.split(" ");
                String price = substr[0];

                ebayPhone[ebayplist].setData(name,link,formatPrice(price),websiteName);
                ebayplist++;
            }
        }

        //6. MERGE 2 LIST INTO 1
        totalplist = amazonplist + ebayplist;
        phoneList = new PhoneInfo[totalplist];
        System.arraycopy(amazonPhone, 0, phoneList, 0,amazonplist);
        System.arraycopy(ebayPhone,0,phoneList,amazonplist,ebayplist);

        //7. SORT THE LIST ACCORDING TO PRICE
        Arrays.sort(phoneList, (a,b) -> Float.compare(a.phonePrice,b.phonePrice));

        //8. DISPLAY OUTPUT
        for (PhoneInfo phoneInfo : phoneList) {
            phoneInfo.displayInfo();
        }

        //3. CLOSE THE BROWSER
        driver.quit();
    }
}

class PhoneInfo {
    public String phoneName;
    public String phoneLink;
    public float phonePrice;
    public String phoneSite;

    public void setData(String phoneName, String phoneLink, float phonePrice, String phoneSite){
        this.phoneName = phoneName;
        this.phoneLink = phoneLink;
        this.phonePrice = phonePrice;
        this.phoneSite = phoneSite;
    }

    public String displayPrice(float price){
        float dummy = 0.0f;
        if(Float.compare(phonePrice,dummy) == 0) {
            return "N/A";
        }else{
            return String.valueOf(price);
        }
    }
    public void displayInfo(){
        System.out.println("Website Name: " + phoneSite + " || Product Name: " + phoneName + " || Product Price: " +   displayPrice(phonePrice) + " || Product Link: " + phoneLink);
    }
}