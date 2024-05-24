Feature: Check in on a trip

  Background: Already have a reservation
    Given I am at the url "http://localhost"

  Scenario: Check in on a trip
    When I navigate to "http://localhost/"
    Given I am signed in
    When I click on the "Reservations" button
    When I click on the "Check in" button
    Then I should see "Checked in"