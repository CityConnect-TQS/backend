Feature: Delete trip

  Background: Sign in as a staff member
    Given There is a staff account with name "Joana Filipa", email "joana123@ua.pt" and password "aBeautifulPassword2024"
    And I navigate to "http://staff.localhost/login"
    And I click on the avatar
    And I am logged out
    And I fill in email with "joana123@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Joana Filipa"

  Scenario: Delete a trip
    And I should see a list of trips
    And I click on the delete button of the trip 20 witch is from "Aveiro" to "Ovar"
    And I should see a modal with the title "Delete Trip Aveiro - Ovar"
    And I click on the delete button
    Then I should see a success message with the text "Trip deleted successfully"

