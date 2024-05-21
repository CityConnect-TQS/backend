Feature: Update trip

  Background: Logged out
    And I navigate to "http://staff.localhost/login"
    And I click on the avatar
    And I am logged out
    And I click on the avatar

  Scenario: Update trip with valid data
    Given There is a staff account with name "João Miguel", email "jmiguel@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "jmiguel@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "João Miguel"
    And I should see a list of trips
    And I search for trips in "Aveiro"
    And I click on the edit button of the trip 51 witch is from "Murtosa" to "Aveiro"
    And I change the price from 10 to 15
    And I click on the save button
    And I should see a edit button
    Then I should see 15 as the price of the trip

  Scenario: Update trip with invalid data
    Given There is a staff account with name "João Miguel", email "jmiguel2@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "jmiguel2@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "João Miguel"
    And I should see a list of trips
    And I search for trips in "Aveiro"
    And I click on the edit button of the trip 2 witch is from "Aveiro" to "Ovar"
    And I change the arrival city to "Aveiro"
    And I click on the save button
    Then I should see a error message saying "The departure city must be different from the arrival city."





