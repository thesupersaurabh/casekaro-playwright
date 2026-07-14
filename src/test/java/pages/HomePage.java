package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomePage {
    private Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public void launchWebsite() {
        page.navigate("https://casekaro.com/");
        assertThat(page).hasTitle(java.util.regex.Pattern.compile(".*Casekaro.*", java.util.regex.Pattern.CASE_INSENSITIVE));
    }

    public void clickMobileCovers() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("mobile covers", Pattern.CASE_INSENSITIVE))).first().click();
        page.waitForURL("**/phone-cases-by-model");
    }
}
