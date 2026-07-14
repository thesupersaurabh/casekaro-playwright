package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightFactory {

    private static ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    public static Page initBrowser(String browserName) {
        System.out.println("Initializing browser: " + browserName);
        playwrightThreadLocal.set(Playwright.create());

        BrowserType browserType;
        switch (browserName.toLowerCase()) {
            case "chromium":
            case "chrome":
                browserType = getPlaywright().chromium();
                break;
            case "firefox":
                browserType = getPlaywright().firefox();
                break;
            case "webkit":
            case "safari":
                browserType = getPlaywright().webkit();
                break;
            default:
                System.out.println("Please pass the right browser name: " + browserName);
                browserType = getPlaywright().chromium();
                break;
        }

        browserThreadLocal.set(browserType.launch(new BrowserType.LaunchOptions().setHeadless(false)));
        
        // --- ENTERPRISE QA BEST PRACTICE ---
        // Video recording is amazing for debugging, but in real enterprise projects, 
        // we usually keep this commented out locally and only enable it in our CI/CD 
        // pipeline (like Jenkins/GitHub Actions) on test failure to save disk space.
        // To enable locally, uncomment the contextOptions and use newContext(contextOptions).
        // 
        // Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
        //         .setRecordVideoDir(java.nio.file.Paths.get("videos/"));
        // com.microsoft.playwright.BrowserContext context = getBrowser().newContext(contextOptions);
        // pageThreadLocal.set(context.newPage());
        // ------------------------------------
        
        pageThreadLocal.set(getBrowser().newPage());
        return getPage();
    }

    public static Playwright getPlaywright() {
        return playwrightThreadLocal.get();
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static void quitBrowser() {
        if (getPage() != null) {
            getPage().close();
        }
        if (getBrowser() != null) {
            getBrowser().close();
        }
        if (getPlaywright() != null) {
            getPlaywright().close();
        }
    }
}
