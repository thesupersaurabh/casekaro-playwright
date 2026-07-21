package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class ProductPage {
    private Page page;

    // Store cart item details as we add variants
    private List<String[]> cartItemDetails = new ArrayList<>();
    private int addedCount = 0;

    public ProductPage(Page page) {
        this.page = page;
    }

    /**
     * Returns the currently active (open) modal, waiting for it to appear.
     */
    private Locator getOpenModal() {
        Locator modal = page.locator("quick-add-modal[open]").first();
        modal.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        return modal;
    }

    public void clickChooseOptions(int index) {
        // Wait for the product grid to fully render before interacting
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(2000);

        Locator chooseOptionsBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("Choose options", Pattern.CASE_INSENSITIVE))).nth(index);
        chooseOptionsBtn.scrollIntoViewIfNeeded();
        assertThat(chooseOptionsBtn).isEnabled();
        chooseOptionsBtn.click();

        // Wait for the quick-add modal to open and have radio inputs
        Locator modal = getOpenModal();
        modal.locator("input[type='radio']").first()
                .waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(10000));
    }

    public void verifyMaterialButtonsExist(List<String> materials) {
        // Scope radio inputs to the active open modal
        Locator modal = getOpenModal();
        for (String material : materials) {
            Locator radio = modal.locator("input[type='radio'][value='" + material + "']").first();
            assertThat(radio).isAttached();
        }
    }

    public void addAllVariantsToCart(int index, List<String> materials) {
        cartItemDetails.clear();
        addedCount = 0;

        // The modal is already open from the clickChooseOptions() step for the 1st material.
        // After adding to cart + closing the cart drawer, the modal closes,
        // so we must re-click "Choose Options" to re-open it for each subsequent material.

        for (int i = 0; i < materials.size(); i++) {
            String material = materials.get(i);
            System.out.println("Selecting material: " + material);

            // Re-open the modal for the 2nd material onward
            if (i > 0) {
                // Ensure previous modal is fully closed
                closeModal();
                page.waitForTimeout(1000);

                Locator chooseOptionsBtn = page.getByRole(AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName(Pattern.compile("Choose options", Pattern.CASE_INSENSITIVE))).nth(index);
                chooseOptionsBtn.scrollIntoViewIfNeeded();
                chooseOptionsBtn.click();

                // Wait for modal to open and have radio inputs
                Locator modal = getOpenModal();
                modal.locator("input[type='radio']").first()
                        .waitFor(new Locator.WaitForOptions()
                                .setState(WaitForSelectorState.ATTACHED)
                                .setTimeout(10000));
            }

            // All interactions scoped to the active open modal
            Locator activeModal = getOpenModal();

            // Click the material variant using its radio input value
            Locator radio = activeModal.locator("input[type='radio'][value='" + material + "']").first();
            String radioId = radio.getAttribute("id");
            Locator label = activeModal.locator("label[for='" + radioId + "']");

            label.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(10000));
            label.click();

            // Wait for price/variant to settle
            page.waitForTimeout(1500);

            // Check if Add to Cart button is enabled (variant not sold out)
            Locator addBtn = activeModal.locator("button[name='add']").first();
            if (addBtn.isDisabled()) {
                System.out.println("⚠ " + material + " is sold out, skipping.");
                closeModal();
                continue;
            }

            // Capture price and link
            String price = activeModal.locator(".price").first().innerText().trim();
            String link = page.url();
            cartItemDetails.add(new String[]{material, price, link});

            // Click Add to Cart
            addBtn.click();
            addedCount++;

            // Allow network request to fire
            page.waitForTimeout(1000);

            // Close the cart drawer overlay (this also implicitly closes the modal)
            closeCartDrawer();

            System.out.println(material + " added ✓");
        }

        // Ensure any remaining modal is closed
        closeModal();
    }

    private void closeModal() {
        Locator modal = page.locator("quick-add-modal[open]").first();
        if (modal.isVisible()) {
            Locator closeBtn = modal.locator("button.quick-add-modal__toggle").first();
            if (closeBtn.isVisible()) {
                closeBtn.click(new Locator.ClickOptions().setForce(true));
            } else {
                page.keyboard().press("Escape");
            }
            try {
                modal.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(3000));
            } catch (Exception e) {
                System.out.println("Modal didn't close cleanly, continuing.");
            }
        }
    }

    private void closeCartDrawer() {
        Locator overlay = page.locator("#CartDrawer-Overlay");

        // Wait a brief moment to allow the drawer animation to trigger
        page.waitForTimeout(2000);

        // Deterministic check: Only close it if it actually appeared
        if (overlay.isVisible()) {
            overlay.click(new Locator.ClickOptions().setForce(true));
            overlay.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.HIDDEN)
                    .setTimeout(5000));
        } else {
            System.out.println("Cart drawer overlay did not appear. Continuing safely.");
        }
    }

    public int getAddedCount() {
        return addedCount;
    }

    public List<String[]> getCartItemDetails() {
        return cartItemDetails;
    }
}
