Feature: Sign-up and login

  Background: Logged out & staff account created
    Given There is a staff account with name "Jane Doe", email "janedoe2@ua.pt" and password "aBeautifulPassword2024"
    And I navigate to "http://staff.localhost/login"
    And I click on the avatar
    And I am logged out
    And I click on the avatar

  Scenario: Login with an existing account
    Given I click on the avatar
    And I click on the login button
    And I fill in email with "janedoe2@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Jane Doe"
