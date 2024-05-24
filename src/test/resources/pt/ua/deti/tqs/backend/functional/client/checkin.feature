Feature: Check in on a trip

  Scenario: Check in on a trip
    Given I navigate to "http://localhost/"
    Given There is a user account with name "Jonhy", email "joao@ua.pt" and password "john1234"
    Given I am signed in
    Given I navigate to "http://localhost/trips?departure=1&arrival=3&departureTime=2024-05-19"
    When I click on the first trip that shows up
    Then I should be redirected to "Book reservation" page

    When I click on seat "1A"
    When I click on seat "1B"
    And I click on submit button
    Then I should be redirected to "Book confirmation" page
    And I should see the message "Your trip is booked! The reservation ID is 1 and you booked 2 seats:"
    And the seats "1A" and "1B" should appear

    When Im navigate to "http://localhost/"
    And Im click on the avatar
    When Im click on the reservations button
    When Im click on the Check in button
    When Im click on the confirm check in button
    Then Im should see Checked in