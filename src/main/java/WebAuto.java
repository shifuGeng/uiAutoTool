import com.google.gson.JsonObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.FileWriter;
import java.lang.Thread;
import java.util.List;

public class WebAuto {
    private  WebDriver webDriver;
    private String url;
    private int waitTime = 3;
    private String browserType = "chrome";
    private boolean isAlertAutoAccept = false;
    private String successFile = "success_result.txt";
    private String failedFile = "failed_result.txt";
    boolean ternary_result = false;

    private WebElement findElement(String via, String value) {
        if(via.toLowerCase().equals("id")){
            return this.webDriver.findElement(By.id(value));
        }
        if(via.toLowerCase().equals("xpath")){
            return this.webDriver.findElement(By.xpath(value));
        }
        if(via.toLowerCase().equals("name")){
            return this.webDriver.findElement(By.name(value));
        }
        if(via.toLowerCase().equals("linkText")) {
            return this.webDriver.findElement(By.linkText(value));
        }
        return this.webDriver.findElement(By.id(value));
    }

    private List<WebElement> findElements(String via, String value) {
        if(via.toLowerCase().equals("id")){
            return this.webDriver.findElements(By.id(value));
        }
        if(via.toLowerCase().equals("xpath")){
            return this.webDriver.findElements(By.xpath(value));
        }
        if(via.toLowerCase().equals("name")){
            return this.webDriver.findElements(By.name(value));
        }
        return this.webDriver.findElements(By.id(value));
    }

    public void login(String bank, String user, String pwd, String bankElement, String userElement, String pwdElement,
                      String buttonElement, String via, String url) {
        try {
            this.webDriver.get(url);
            Thread.sleep(this.waitTime*1000);
            this.findElement(via, bankElement).sendKeys(bank);
            Thread.sleep(2*1000);
            this.findElement(via, userElement).sendKeys(user);
            Thread.sleep(2*1000);
            this.findElement(via, pwdElement).sendKeys(pwd);
            Thread.sleep(2*1000);
            this.findElement(via, buttonElement).click();
            Thread.sleep(this.waitTime*1000);
            this.dealPotentialAlert(true);
            Thread.sleep(this.waitTime*1000);
        } catch (Exception e) {
            System.out.println("登陆失败");
        }
    }

    public WebAuto setWebDriverPath(String webDriverPath, String browserType) {
        this.browserType = browserType.toLowerCase();
        if (this.browserType.equals("chrome")){
            System.setProperty("webdriver.chrome.driver", webDriverPath);
        } else if (this.browserType.equals("firefox")) {
            System.setProperty("webdriver.firefox.marionette", webDriverPath);
        }
        return this;
    }

    public WebAuto setAlertAutoAccept(boolean isOpen) {
        this.isAlertAutoAccept = isOpen;
        return this;
    }

    public WebAuto setURL(String url) {
        if(url != null){
            this.url = url;
        }
        return this;
    }

    public WebAuto setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public void initWebDriver() {
        switch (this.browserType) {
            case "chrome" :
                this.webDriver = new ChromeDriver();
                break;
            case "firefox":
                this.webDriver = new FirefoxDriver();
                break;
        }
    }

    private void saveResult(JsonObject jsonObject, boolean result) {
        String fileName = result ? successFile : failedFile;
        File FilePath = new File(System.getProperty("user.dir") + "\\" + fileName);
        try {
            if (!FilePath.exists()) {
                FilePath.createNewFile();
            }
            FileWriter fw = new FileWriter(FilePath, true);
            fw.write(jsonObject.toString());
            fw.write("\r\n");
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.out.println("执行保存结果操作出错");
            e.printStackTrace();
        }
    }

    public void quitWebDriver() {
        this.webDriver.quit();
    }

    private String getValue(JsonObject jsonObject, String key){
        if(jsonObject.has(key)) {
            return jsonObject.get(key).getAsString();
        } else {
            return key;
        }
    }

    /**
     *
     * @param actList 模拟用户行为的操作列表
     * @param viaList 获取页面元素所使用的方法（id， name， xpath）
     * @param valueList viaList所对应的值
     * @param keyList 对应的数据json的key
     * @param jsonObject 结论文件解析出来的k-v键值对
     */
    public void executeFlow(List<String> actList, List<String> viaList, List<String> valueList,
                            List<String> keyList, JsonObject jsonObject) {
        boolean result = true;
        this.webDriver.get(this.url);
        try {
            for (int i = 0; i < actList.size(); i++) {

                switch(actList.get(i).toLowerCase()){
                    case "input": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        element.clear();
                        element.sendKeys(this.getValue(jsonObject, keyList.get(i)));
                        break;
                    }
                    case "submit": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        element.submit();
                        Thread.sleep(this.waitTime*1000);
                        break;
                    }
                    case "click": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        element.click();
                        Thread.sleep(this.waitTime*1000);
                        break;
                    }
                    case "select": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        Select optionsElement = new Select(element);
                        optionsElement.selectByValue(this.getValue(jsonObject, keyList.get(i)));
                        Thread.sleep(this.waitTime*1000);
                        break;
                    }
                    case "liselect": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        List<WebElement> elementList = element.findElements(By.tagName("li"));
                        int j = 0;
                        boolean flag = true;
                        System.out.println(jsonObject.get(keyList.get(i)).getAsString());
                        while (j < elementList.size() && flag ) {
                            WebElement webElement = elementList.get(j);
                            if(webElement.getText().contains(this.getValue(jsonObject, keyList.get(i)))){
                                webElement.click();
                                flag = false;
                            }
                            j++;
                        }
                        Thread.sleep(this.waitTime * 1000);
                        break;
                    }
//                    case "liscrollandselect": {
//                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
//                        List<WebElement> elementList = element.findElements(By.tagName("li"));
//                        int j = 0;
//                        boolean flag = true;
//                        System.out.println(jsonObject.get(keyList.get(i)).getAsString());
//                        while (j < elementList.size() && flag ) {
//                            WebElement webElement = elementList.get(j);
//                            if(webElement.getText().contains(jsonObject.get(keyList.get(i)).getAsString())){
//                                ((JavascriptExecutor) this.webDriver).executeScript("arguments[0].scrollIntoView;", element);
//                                webElement.click();
//                                flag = false;
//                            }
//                            j++;
//                        }
//                        Thread.sleep(this.waitTime * 1000);
//                        break;
//                    }
                    case "liselectclick" : {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        element.click();
                        break;
                    }
                    case "alert": {
                        boolean option = keyList.get(i).toLowerCase().equals("accept") ? true : false;
                        dealPotentialAlert(option);
                        Thread.sleep(this.waitTime*1000);
                        break;
                    }
                    case "condition": {
                        int jump = Integer.parseInt(keyList.get(i));
                        if (actList.get(i + 1).toLowerCase().equals("alert")){
                            if(dealPotentialAlert(true)) i = i + 1;
                            else i = i + jump;
                        } else {
                            if (isElementPresent(viaList.get(i+1).toLowerCase(), valueList.get(i + 1))) i = i+1;
                            else i = i + jump;
                        }
                        break;
                    }
                    case "jump": {
                        i = i+Integer.parseInt(keyList.get(i));
                        break;
                    }
                    case "ternary" : {
                        if(actList.get(i + 1).toLowerCase().equals("alert")) {
                            if(dealPotentialAlert(true)) ternary_result = true;
                        } else {
                            if (isElementPresent(viaList.get(i + 1).toLowerCase(), valueList.get(i + 1)))
                                ternary_result = true;
                        }
                        i = i + 1;
                        break;
                    }
                    case "operator": {
                        if (ternary_result) {
                            i = i + 1;
                            WebElement webElement = this.findElement(viaList.get(i).toLowerCase(), valueList.get(i));
                            webElement.click();
                        }
                        i = i + 1;
                        ternary_result = false;
                        break;
                    }
                    case "hover" : {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        Actions actions = new Actions(this.webDriver);
                        actions.clickAndHold(element).perform();
                        Thread.sleep(this.waitTime * 1000);
                    }
                    case "switch": {
                        this.webDriver.switchTo().defaultContent();
                        if(viaList.get(i) == null || "null".equalsIgnoreCase(viaList.get(i)) ||
                                viaList.get(i).isEmpty()) {
                            continue;
                        }
                        this.webDriver.switchTo().frame(Integer.parseInt(keyList.get(i)));
                        Thread.sleep(this.waitTime * 1000);
                        break;
                    }
                    case "scroll": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        ((JavascriptExecutor) this.webDriver).executeScript("arguments[0].scrollIntoView;", element);
                        break;
                    }
                    case "checkbox": {
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        if (this.getValue(jsonObject, keyList.get(i)).equals("1")) {
                            if(!element.isSelected()) element.click();
                        } else {
                            if (element.isSelected()) element.click();
                        }
                        break;
                    }
                    case "radio": {
                        String value = this.getValue(jsonObject, keyList.get(i));
                        WebElement radio_node = this.findElement(viaList.get(i).toLowerCase(),
                                valueList.get(i));
                        List<WebElement> radio_list = radio_node.findElements(By.xpath("//div[@option]"));
                        System.out.println(radio_list.size());
                        for (int j=0; j< radio_list.size(); j++) {
                            WebElement webElement = radio_list.get(j);
                            if(webElement.getAttribute("option").equals(value)) {
                                WebElement input = webElement.findElement(By.tagName("input"));
                                input.click();
                            }
                        }
                        break;
                    }
                    case "wait": {
                        Thread.sleep(Integer.parseInt(keyList.get(i))*1000);
                        break;
                    }
                    case "get": {
                        System.out.println(viaList.get(i) + " " + valueList.get(i));
                        WebElement element = this.findElement(viaList.get(i), valueList.get(i));
                        JavascriptExecutor js = (JavascriptExecutor) this.webDriver;
                        String value = (String) js.executeScript("return arguments[0].value;",element);
                        jsonObject.addProperty("addition_apply_no", value);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            saveResult(jsonObject, result);
        }
    }

    private boolean dealPotentialAlert(boolean option) {
        boolean flag = false;
        try {
            Alert alert = this.webDriver.switchTo().alert();
            if (null == alert) {
                throw new NoAlertPresentException();
            }
            try {
                if (option) {
                    alert.accept();
                } else {
                    alert.dismiss();
                }
                flag = true;
            } catch (WebDriverException ex) {
                if (ex.getMessage().startsWith("Could not find"))
                    System.out.println("There is no alert appear");
                else
                    throw ex;
            }
        } catch (NoAlertPresentException e) {
            System.out.println("There is no alert appear!");
        }
        return flag;
    }

    private boolean isElementPresent(String via, String value){
        try {
            this.findElement(via,value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
