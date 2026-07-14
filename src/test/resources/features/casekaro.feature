Feature: CaseKaro E2E Shopping Cart Flow

  Scenario: Search and add Apple iPhone 16 Pro cases to cart
    Given the user is on the CaseKaro homepage
    When the user navigates to "Mobile Covers" from the top menu
    And scrolls down to "Phone cases by model"
    And searches for "Apple" in the phone model search box
    Then only Apple devices should be visible
    And devices from brands like "Samsung, OnePlus, Vivo, Oppo, Realme, Xiaomi" should not be visible
    When the user searches for "iPhone 16 Pro"
    And clicks on the exact match "iPhone 16 Pro" from autocomplete
    And clicks on "Choose Options" for the first product
    Then the user should see materials "Hard, Soft, Glass" available
    When the user adds all three variants to the cart
    And opens the cart
    Then the cart should contain exactly 3 items
    And the user prints the material, price, and product link for each item in the cart
