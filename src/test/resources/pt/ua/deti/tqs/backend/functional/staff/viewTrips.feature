Feature: View list of trips

  Background: Sign in as a staff member
    Given There is a staff account with name "Jane Doe", email "janedoe3@ua.pt" and password "aBeautifulPassword2024"
    And I navigate to "http://localhost/staff/login"
    And I click on the avatar
    And I am logged out
    And I fill in email with "janedoe3@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Jane Doe"

  Scenario: View list of trips
    Then I should see a list of trips
