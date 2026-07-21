Feature: CaseKaro E2E Shopping Cart Flow

  Scenario Outline: Search and add <phone_model> cases to cart
    Given the user is on the CaseKaro homepage
    When the user navigates to "Mobile Covers" from the top menu
    And scrolls down to "Phone cases by model"
    And searches for "<phone_brand>" in the phone model search box
    Then only <phone_brand> devices should be visible
    And devices from brands like "<other_brands>" should not be visible
    When the user searches for "<phone_model>"
    And clicks on the exact match "<phone_model>" from autocomplete
    And clicks on "Choose Options" for the <nth_product> product
    Then the user should see materials "<materials>" available
    When the user adds all available variants to the cart
    And opens the cart
    Then the cart should contain the added items
    And the user prints the material, price, and product link for each item in the cart

    Examples:
      | phone_brand | other_brands                                 | phone_model   | nth_product | materials         |
      | Apple       | Samsung, OnePlus, Vivo, Oppo, Realme, Xiaomi | iPhone 16 Pro | 1           | Hard, Soft, Glass |
