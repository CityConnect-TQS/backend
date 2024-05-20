Feature: Book a trip reservation

  Background: Already searched for trips
    Given I am at the url "http://localhost/trips?departure=1&arrival=3&departureTime=2024-05-19"

  Scenario: Book a trip reservation
    When I click on the first trip that shows up
    Then I should be redirected to "Book reservation" page
    #And the submit button should be disabled

    #When I click on seat "1A" and seat "1B"
    #And I click on submit button
    #Then I should be redirected to "Book confirmation" page
    #And I should see the message "Your trip is booked! The reservation ID is 1 and you booked 2 seats: 1A, 1B"

  #Scenario: Book a trip reservation but choose unavailable seats
    #When I click on the first trip that shows up
    #Then I should be redirected to "Book reservation" page
    #And the submit button should be disabled

    #When I click on seat "1A"
    #And the submit button should be disabled
