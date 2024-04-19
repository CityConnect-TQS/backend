Feature: Book a trip

  Scenario: Sign-up
    Given I navigate to "http://localhost"
    When I click on the signup button
    And I fill in username with "RGarrido"
    And I fill in name with "Rúben Garrido"
    And I fill in email with "rubengarrido@ua.pt"
    And I fill in password with "Ruben#78236"
    And I click on the submit button
    And I fill in departure with 1
    And I fill in arrival with 2
    And I search for trips
    And I choose trip 6
    And I book the trip
    Then I should see a success message