package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.List;

public class CartPage {
    private Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public void openCart() {
        // Working JS uses #cart-icon-bubble to open the cart drawer
        page.locator("#cart-icon-bubble").click();

        // Verify the cart drawer is visible
        Locator cartDrawer = page.locator("#CartDrawer");
        assertThat(cartDrawer).isVisible();
    }

    public void verifyCartCount(int count) {
        // Working JS uses "#CartDrawer .cart-item, #CartDrawer tbody tr"
        Locator cartItems = page.locator("#CartDrawer .cart-item, #CartDrawer tbody tr");
        assertThat(cartItems).hasCount(count);
    }

    public void printCartDetails(List<String> materials) {
        Locator cartItems = page.locator("#CartDrawer .cart-item, #CartDrawer tbody tr");
        int count = cartItems.count();

        System.out.println("\n========== CART DETAILS ==========\n");

        // Verify each material is present in cart
        List<String> rowTexts = cartItems.allInnerTexts();
        for (String material : materials) {
            boolean found = false;
            for (String text : rowTexts) {
                if (text.contains(material)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("WARNING: Material " + material + " not found in cart (may be sold out)");
            }
        }

        for (int i = 0; i < count; i++) {
            Locator item = cartItems.nth(i);
            String itemText = item.innerText();

            // Dynamically detect material from the cart item text
            String material = "Unknown";
            for (String mat : materials) {
                if (itemText.contains(mat)) {
                    material = mat;
                    break;
                }
            }

            // Get price from the cart item
            String price = "N/A";
            Locator priceLocator = item.locator(".price, .cart-item__price").first();
            if (priceLocator.count() > 0) {
                price = priceLocator.innerText().trim();
            }

            // Get product link
            String link = "N/A";
            Locator linkLocator = item.locator("a").first();
            if (linkLocator.count() > 0) {
                link = linkLocator.getAttribute("href");
                if (link != null && !link.startsWith("http")) {
                    link = "https://casekaro.com" + link;
                }
            }

            System.out.println("Item " + (i + 1));
            System.out.println("Material : " + material);
            System.out.println("Price    : " + price);
            System.out.println("Link     : " + link);
            System.out.println("--------------------------------");
        }

        System.out.println("\n===================================\n");
    }
}
