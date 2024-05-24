Feature: Update trip

  Background: Logged out
    And I navigate to "http://localhost/staff/login"
    And I click on the avatar
    And I am logged out
    And I click on the avatar

  Scenario: Create trip with valid data
    Given There is a staff account with name "Ana Maria Miguel", email "amariamiguel@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "amariamiguel@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Ana Maria Miguel"
    And I click on the avatar
    And I check how many trips are in the database
    And I click on the add trip button
    And I fill in the departure city with "Aveiro"
    And I fill in the arrival city with "Ovar"
    And I fill in the departure date with "06/08/2025" and time with "04:00 PM"
    And I fill in the arrival date with "06/08/2025" and time with "05:00 PM"
    And I fill in the price with 10
    And I choose the bus 3
    And I click on the save button
    Then I should have one more trip in the database

  Scenario: Update trip with invalid data
    Given There is a staff account with name "Ana Maria Miguel", email "amariamiguel2@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "amariamiguel2@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Ana Maria Miguel"
    And I click on the avatar
    And I click on the add trip button
    And I fill in the departure city with "Aveiro"
    And I fill in the arrival city with "Aveiro"
    And I fill in the departure date with "06/08/2025" and time with "05:00 PM"
    And I fill in the arrival date with "06/08/2025" and time with "06:00 PM"
    And I fill in the price with 10
    And I choose the bus 4
    And I click on the save button
    Then I should see an error message saying "The departure city must be different from the arrival city."





