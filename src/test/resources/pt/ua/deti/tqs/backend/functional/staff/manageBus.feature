Feature: Manage buses

  Background: Logged out
    And I navigate to "http://staff.localhost/login"
    And I click on the avatar
    And I am logged out
    And I click on the avatar


  Scenario: Create a bus with a new company
    Given There is a staff account with name "Catarina Almeida", email "cataAlmeida@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "cataAlmeida@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Catarina Almeida"
    And I click on the buses button
    And I click on the add bus button
    Then I should see a modal for create bus with the title "Create a new bus"
    And I fill in the company name with "Bus New Company"
    And I fill in the capacity with "50"
    And I click on the save bus button
    Then I should see a card with the company name "Bus New Company"

  Scenario: Create a bus with an existing company
    Given There is a staff account with name "Catarina Almeida", email "cataAlmeida2@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "cataAlmeida2@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Catarina Almeida"
    And I click on the buses button
    And I check how many buses are on "Renex" company
    And I click on the add bus button on "Renex" company
    Then I should see a modal for create bus with the title "Create a new bus"
    And I fill in the capacity with "50"
    And I click on the save bus button
    Then I should see one more bus on "Renex" company

  Scenario: Update a bus
    Given There is a staff account with name "Catarina Almeida", email "cataAlmeida3@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "cataAlmeida3@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Catarina Almeida"
    And I click on the buses button
    And I click on the edit bus button on trip with id 3
    Then I should see a modal for create bus with the title "Edit bus"
    And I fill in the capacity with "50"
    And I click on the save bus button
    Then I should see 50 as capacity for bus 3

  Scenario: Delete a bus
    Given There is a staff account with name "Catarina Almeida", email "cataAlmeida4@ua.pt" and password "aBeautifulPassword2024"
    And I fill in email with "cataAlmeida4@ua.pt"
    And I fill in password with "aBeautifulPassword2024"
    When I click on the login button
    And I click on the avatar
    Then I should be logged in as "Catarina Almeida"
    And I click on the buses button
    And I check how many buses are on "Flix Bus" company
    And I click on the on delete bus button on trip with id 5
    Then I should see a modal for delete bus with the title "Delete Bus 5 - Flix Bus"
    And I click on the delete bus button
    Then I should see one less bus on "Flix Bus" company
    And I should see a success message with "Bus deleted successfully"






