Feature: Check in on a trip

  Scenario: Check in on a trip
    Given I navigate to "http://localhost/"
    Given There is a user account with name "John", email "john@ua.pt" and password "john2024"
    Given I am signed in!
    Given I navigate to "http://localhost/trips?departure=1&arrival=3&departureTime=2024-05-19"
    When I click on the first trip that shows up
    Then I should be redirected to "Book reservation" page

    When I click on seat "2A"
    When I click on seat "2B"
    And I click on submit button
    Then I should be redirected to "Book confirmation" page
    And I should see the message "Your trip is booked! The reservation ID is 2 and you booked 2 seats:"
    And the seats "2A" and "2B" should appear

    When I navigate to "http://localhost/"
    And I click on the avatar
    When I click on the reservations button
    When I click on the Check in button
    When I click on the confirm check in button
    Then I should see Checked in