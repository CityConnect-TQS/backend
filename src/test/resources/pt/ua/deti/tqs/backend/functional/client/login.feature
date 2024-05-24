#Feature: Sign-up and login
#
#  Background: Logged out
#    Given I navigate to "http://localhost"
#    And I click on the avatar
#    And I am logged out
#    And I click on the avatar
#
#  Scenario: Sign-up for the first time
#    Given I click on the avatar
#    And I click on the sign-up button
#    And I fill in name with "Jane Doe"
#    And I fill in email with "janedoe@ua.pt"
#    And I fill in password with "aBeautifulPassword2024"
#    When I click on the sign-up button
#    And I click on the avatar
#    Then I should be logged in as "Jane Doe"
#
#  Scenario: Login with an existing account
#    Given I click on the avatar
#    And I click on the login button
#    And I fill in email with "janedoe@ua.pt"
#    And I fill in password with "aBeautifulPassword2024"
#    When I click on the login button
#    And I click on the avatar
#    Then I should be logged in as "Jane Doe"
