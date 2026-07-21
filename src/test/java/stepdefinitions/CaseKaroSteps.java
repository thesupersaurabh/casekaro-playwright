package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import pages.HomePage;
import pages.MobileCoverPage;
import pages.ProductPage;
import pages.CartPage;
import utils.PlaywrightFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CaseKaroSteps {
    
    private HomePage homePage = new HomePage(PlaywrightFactory.getPage());
    private MobileCoverPage mobileCoverPage = new MobileCoverPage(PlaywrightFactory.getPage());
    private ProductPage productPage = new ProductPage(PlaywrightFactory.getPage());
    private CartPage cartPage = new CartPage(PlaywrightFactory.getPage());
    
    private int currentProductIndex = 0;
    private List<String> currentMaterials = Arrays.asList("Hard", "Soft", "Glass");

    @Given("the user is on the CaseKaro homepage")
    public void the_user_is_on_the_casekaro_homepage() {
        homePage.launchWebsite();
    }

    @When("the user navigates to {string} from the top menu")
    public void the_user_navigates_to_from_the_top_menu(String menuName) {
        if (menuName.equals("Mobile Covers")) {
            homePage.clickMobileCovers();
            mobileCoverPage.verifyMobileCoversPageOpens();
        }
    }

    @And("scrolls down to {string}")
    public void scrolls_down_to(String sectionName) {
        if (sectionName.equals("Phone cases by model")) {
            mobileCoverPage.scrollToPhoneCasesByModel();
        }
    }

    @And("searches for {string} in the phone model search box")
    public void searches_for_in_the_phone_model_search_box(String searchTerm) {
        mobileCoverPage.searchForPhoneModel(searchTerm);
    }

    @Then("only {word} devices should be visible")
    public void only_brand_devices_should_be_visible(String brand) {
        mobileCoverPage.verifyBrandVisible(brand);
    }

    @And("devices from brands like {string} should not be visible")
    public void devices_from_brands_like_should_not_be_visible(String brandsString) {
        String[] brands = brandsString.split(",\\s*");
        for (String brand : brands) {
            mobileCoverPage.verifyBrandNotVisible(brand);
        }
    }

    @When("the user searches for {string}")
    public void the_user_searches_for(String searchTerm) {
        // Clear and re-fill the search box (mirrors working JS: fill("") then fill("iPhone 16 Pro"))
        mobileCoverPage.searchForPhoneModel("");
        mobileCoverPage.searchForPhoneModel(searchTerm);
    }

    @And("clicks on the exact match {string} from autocomplete")
    public void clicks_on_the_exact_match_from_autocomplete(String matchText) {
        mobileCoverPage.clickExactMatchFromAutocomplete(matchText);
    }

    @And("clicks on {string} for the {word} product")
    public void clicks_on_for_the_product(String buttonName, String productIndexStr) {
        if (productIndexStr.equalsIgnoreCase("first") || productIndexStr.equals("1") || productIndexStr.equals("1st")) {
            currentProductIndex = 0;
        } else {
            try {
                currentProductIndex = Integer.parseInt(productIndexStr.replaceAll("[^0-9]", "")) - 1;
            } catch (NumberFormatException e) {
                currentProductIndex = 0;
            }
        }
        
        if (buttonName.equals("Choose Options")) {
            productPage.clickChooseOptions(currentProductIndex);
        }
    }

    @Then("the user should see materials {string} available")
    public void the_user_should_see_materials_available(String materialsString) {
        currentMaterials = Arrays.stream(materialsString.split(",\\s*"))
                .map(String::trim)
                .collect(Collectors.toList());
        productPage.verifyMaterialButtonsExist(currentMaterials);
    }

    @When("the user adds all available variants to the cart")
    public void the_user_adds_all_available_variants_to_the_cart() {
        productPage.addAllVariantsToCart(currentProductIndex, currentMaterials);
    }

    @And("opens the cart")
    public void opens_the_cart() {
        cartPage.openCart();
    }

    @Then("the cart should contain the added items")
    public void the_cart_should_contain_the_added_items() {
        int addedCount = productPage.getAddedCount();
        System.out.println("Verifying cart contains " + addedCount + " items (some variants may have been sold out)");
        cartPage.verifyCartCount(addedCount);
    }

    @And("the user prints the material, price, and product link for each item in the cart")
    public void the_user_prints_the_material_price_and_product_link_for_each_item_in_the_cart() {
        cartPage.printCartDetails(currentMaterials);
    }
}
