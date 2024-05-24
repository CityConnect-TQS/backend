Feature: Sign-up and login

  Background: Logged out
    And I navigate to "http://localhost/staff/login"
    And I click on the avatar
    And I am logged out
    And I click on the avatar

  Scenario: Login with a staff account
    Given There is a staff account with name "Jane Doe", email "janedoe2@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "janedoe2@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Jane Doe"

  Scenario: Login with a normal user account
    Given There is a user account with name "John Doe", email "johndoe@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "johndoe@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    Then there is a message about unsufficient permissions
