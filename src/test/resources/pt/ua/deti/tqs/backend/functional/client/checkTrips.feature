Feature: Check existing trips

  Scenario: Check existing trips
    When I navigate to "http://localhost/"
    And I fill in departure with 3
    And I fill in arrival with 1
    And I search for trips
    Then I should see a list of trips


  Scenario: Check existing trips on a specific date
    When I navigate to "http://localhost"
    And I fill in departure with 3
    And I fill in arrival with 1
    And I fill in date with "05/24/2024"
    And I search for trips
    Then I should see a list of trips on date "05/24/2024"


  Scenario: Check existing trips but there are none
    When I navigate to "http://localhost"
    And I fill in departure with 3
    And I fill in arrival with 1
    And I search for trips
    Then I should see a message saying there "No trips available for this date"