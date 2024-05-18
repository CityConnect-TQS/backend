Feature: View list of trips

  Background: Sign in as a staff member
    Given There is a staff account with name "Jane Doe", email "janedoe2@ua.pt" and password "aBeautifulPassword2024"
    And I navigate to "http://staff.localhost/login"
    And I fill in email with "janedoe2@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Jane Doe"

  Scenario: View list of trips
    And I should see a list of trips
    Then I should see a trip with departure "Ovar" and destination "Aveiro"
