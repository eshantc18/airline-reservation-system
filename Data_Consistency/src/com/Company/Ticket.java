package com.Company;
class Ticket {
    String source;
    String destination;
    String company;
    int num_of_available_seats;

    Ticket(String source, String destination, String company, int num_of_available_seats) {
        this.source = source;
        this.destination = destination;
        this.company = company;
        this.num_of_available_seats = num_of_available_seats;
    }

    public boolean IsConfirmed() {
        if (num_of_available_seats == 0)
            return false;

        num_of_available_seats -= 1;
        return true;
    }

    public boolean cancelTicket() {
        num_of_available_seats += 1;
        return true;
    }
}