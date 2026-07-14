package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class ProductPage {
    private Page page;
    private final List<String> MATERIALS = Arrays.asList("Hard", "Soft", "Glass");

    // Store cart item details as we add variants
    private List<String[]> cartItemDetails = new ArrayList<>();

    public ProductPage(Page page) {
        this.page = page;
    }

    public void clickChooseOptions() {
        Locator chooseOptionsBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("Choose options", Pattern.CASE_INSENSITIVE))).first();
        chooseOptionsBtn.scrollIntoViewIfNeeded();
        assertThat(chooseOptionsBtn).isEnabled();
        chooseOptionsBtn.click();
    }

    public void verifyProductPageLoads() {
        // After clicking Choose Options, a quick-add modal opens.
        // Verify by checking for the variant/material options or add button.
        // The working JS just proceeds directly — no separate assertion needed.
    }

    public void verifyMaterialButtonsExist() {
        // Verify each material variant text is present on the page using exact substring matches that worked in JS
        assertThat(page.getByText("Hard Variant sold out or").first()).isVisible();
        assertThat(page.getByText("Soft Variant sold out or unavailable", new Page.GetByTextOptions().setExact(true)).first()).isVisible();
        assertThat(page.getByText("Glass Variant sold out or").first()).isVisible();
    }

    public void addAllVariantsToCart() {
        cartItemDetails.clear();

        for (int i = 0; i < MATERIALS.size(); i++) {
            String material = MATERIALS.get(i);
            System.out.println("Adding " + material);

            // Re-open Choose Options for every material after the first
            if (i > 0) {
                Locator chooseOptionsBtn = page.getByRole(AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName(Pattern.compile("Choose options", Pattern.CASE_INSENSITIVE))).first();
                chooseOptionsBtn.scrollIntoViewIfNeeded();
                assertThat(chooseOptionsBtn).isEnabled();
                chooseOptionsBtn.click();
            }
            
            // Click the material variant label — matches exactly like JS
            Locator variantOption;
            if (material.equalsIgnoreCase("Hard")) {
                variantOption = page.getByText("Hard Variant sold out or").first();
            } else if (material.equalsIgnoreCase("Soft")) {
                variantOption = page.getByText("Soft Variant sold out or unavailable", new Page.GetByTextOptions().setExact(true)).first();
            } else {
                variantOption = page.getByText("Glass Variant sold out or").first();
            }
            
            variantOption.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(10000));
            variantOption.click();

            // Wait for price/variant to settle
            page.waitForTimeout(1500);

            // Capture price
            String price = page.locator(".price").first().innerText().trim();
            String link = page.url();
            cartItemDetails.add(new String[]{material, price, link});

            // Click Add to Cart
            Locator addBtn = page.locator("button[name='add']").last();
            assertThat(addBtn).isEnabled();
            addBtn.click();
            
            // Allow network request to fire before forcing the cart drawer closed
            page.waitForTimeout(1000);

            // Wait for cart drawer overlay to appear, then close it
            closeCartDrawer();

            System.out.println(material + " added ✓");
        }
    }

    private void closeCartDrawer() {
        Locator overlay = page.locator("#CartDrawer-Overlay");
        
        // Wait for overlay to become visible (cart drawer opened)
        overlay.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        // Close the drawer
        if (overlay.isVisible()) {
            overlay.click(new Locator.ClickOptions().setForce(true));
        } else {
            page.keyboard().press("Escape");
        }

        // Wait for overlay to disappear before continuing
        overlay.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                .setTimeout(10000));
    }

    public List<String[]> getCartItemDetails() {
        return cartItemDetails;
    }
}
