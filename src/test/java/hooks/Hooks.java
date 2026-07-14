package hooks;

import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import utils.PlaywrightFactory;

public class Hooks {
    
    private Page page;

    @Before
    public void setup() {
        String browserName = System.getProperty("browser", "chromium");
        page = PlaywrightFactory.initBrowser(browserName);
    }

    @After
    public void tearDown() {
        PlaywrightFactory.quitBrowser();
    }
}
