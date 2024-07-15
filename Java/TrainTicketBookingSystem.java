import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import validation.*;

class Train {
    private String trainName;
    private int trainNumber;
    private int totalSeats;
    Map<String, Map<String, Integer>> seatsAvailable;
    private List<ScheduleEntry> schedule;

    public Train(String trainName, int trainNumber, int totalSeats, List<ScheduleEntry> schedule) {
        this.trainName = trainName;
        this.trainNumber = trainNumber;
        this.totalSeats = totalSeats;
        this.seatsAvailable = new HashMap<>();
        this.schedule = schedule;
    }

    public String getTrainName() {
        return trainName;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
    public Map<String, Map<String, Integer>> getSeatsAvailable() {
        return seatsAvailable;
    }
    public List<ScheduleEntry> getSchedule() {
        return schedule;
    }

    public int getRemainingSeats(String date, String source) {
        return seatsAvailable.getOrDefault(date, new HashMap<>()).getOrDefault(source, totalSeats);
    }

    public void bookSeats(String date, String source, int numSeats) {
        seatsAvailable.putIfAbsent(date, new HashMap<>());
        int remainingSeats = seatsAvailable.get(date).getOrDefault(source, totalSeats);
        seatsAvailable.get(date).put(source, remainingSeats - numSeats);
    }

    public void cancelSeats(String date, String source, int numSeats) {
        if (seatsAvailable.containsKey(date) && seatsAvailable.get(date).containsKey(source)) {
            int remainingSeats = seatsAvailable.get(date).get(source);
            seatsAvailable.get(date).put(source, remainingSeats + numSeats);
        }
    }
}

class ScheduleEntry {
    private String station;
    private String arrivalTime;
    private String departureTime;

    public ScheduleEntry(String station, String arrivalTime, String departureTime) {
        this.station = station;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getStation() {
        return station;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }
}

class Ticket {
    private String passengerName;
    private int trainNumber;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int seatNumber;
    private double fare;
    private int numPassengers;
    private int ticketId;
    private String date;

    public Ticket(String passengerName, int trainNumber, String source, String destination, String departureTime, String arrivalTime, int seatNumber, double fare, int numPassengers, int ticketId, String date) {
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatNumber = seatNumber;
        this.fare = fare;
        this.numPassengers = numPassengers;
        this.ticketId = ticketId;
        this.date = date;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getFare() {
        return fare;
    }

    public int getNumPassengers() {
        return numPassengers;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getDate() {
        return date;
    }
}

public class TrainTicketBookingSystem {
    private List<Train> trains;
    private List<Ticket> tickets;
    private Map<String, Integer> nextSeatNumberByDate;
    private int nextTicketId = 1;

    public TrainTicketBookingSystem() {
        trains = new ArrayList<>();
        tickets = new ArrayList<>();
        nextSeatNumberByDate = new HashMap<>();
    }

    public void addTrain(Train train) {
        trains.add(train);
    }

    public List<Train> getTrainsWithAvailability(String source, String destination, String date) {
        List<Train> result = new ArrayList<>();
        for (Train train : trains) {
            boolean hasSource = false;
            boolean hasDestination = false;
    
            for (ScheduleEntry entry : train.getSchedule()) {
                if (entry.getStation().equals(source)) {
                    hasSource = true;
                    break;
                }
            }

            for (ScheduleEntry entry : train.getSchedule()) {
                if (entry.getStation().equals(destination)) {
                    hasDestination = true;
                    break;
                }
            }
    
            if (hasSource && hasDestination) {
                result.add(train);
            }
        }
        return result;
    }

    private boolean isDateValid(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        dateFormat.setLenient(false);
        try {
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date();
            currentDate = dateFormat.parse(dateFormat.format(currentDate));
            return !inputDate.before(currentDate);
        } catch (Exception e) {
            return false;
        }
    }

    public Ticket bookTicket(String passengerName, int trainNumber, String source, String destination, int numPassengers, String date) {
        if (!isDateValid(date)) {
            System.out.println("Invalid date or date is in the past.");
            return null;
        }

        Train train = getTrainByNumber(trainNumber);
        if (train == null) {
            System.out.println("Train with number " + trainNumber + " not found.");
            return null;
        }

        int remainingSeats = Integer.MAX_VALUE;
        boolean routeAvailable = true;
        int sourceIndex = train.getSchedule().stream().map(ScheduleEntry::getStation).toList().indexOf(source);
        int destIndex = train.getSchedule().stream().map(ScheduleEntry::getStation).toList().indexOf(destination);
        if (sourceIndex == -1 || destIndex == -1 || sourceIndex >= destIndex) {
            routeAvailable = false;
        } 
        else {
            for (int i = sourceIndex; i < destIndex; i++) {
                int availableSeats = train.getRemainingSeats(date, train.getSchedule().get(i).getStation());
                remainingSeats = Math.min(remainingSeats, availableSeats);
                if (availableSeats < numPassengers) {
                    routeAvailable = false;
                    break;
                }
            }
        }

        if (!routeAvailable) {
            System.out.println("No seats available for the selected route.");
            return null;
        }

        double fare = calculateFare(source, destination);
        if (fare == -1) {
            System.out.println("Invalid route or destination.");
            return null;
        }

        int totalFare = (int) (fare * numPassengers);
        if (totalFare <= 0) {
            System.out.println("Invalid number of passengers.");
            return null;
        }

        int ticketId = nextTicketId++;
        int seatNumber = nextSeatNumberByDate.getOrDefault(date, 1);
        nextSeatNumberByDate.put(date, seatNumber + numPassengers);

        for (int i = sourceIndex; i < destIndex; i++) {
            train.bookSeats(date, train.getSchedule().get(i).getStation(), numPassengers);
        }

        ScheduleEntry sourceEntry = train.getSchedule().get(sourceIndex);
        ScheduleEntry destEntry = train.getSchedule().get(destIndex);
        
        Ticket ticket = new Ticket(passengerName, trainNumber, source, destination,
            sourceEntry.getDepartureTime(), destEntry.getArrivalTime(), seatNumber, fare, numPassengers, ticketId, date);
        tickets.add(ticket);
        return ticket;
    }

    private Train getTrainByNumber(int trainNumber) {
        for (Train train : trains) {
            if (train.getTrainNumber() == trainNumber) {
                return train;
            }
        }
        return null;
    }

    private double calculateFare(String source, String destination) {
        return 100.0 * (Math.abs(source.hashCode() - destination.hashCode()) % 10 + 1);
    }

    public Ticket getTicket(int ticketId) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId() == ticketId) {
                return ticket;
            }
        }
        return null;
    }

    public boolean cancelTicket(int ticketId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return false;
        }

        String ticketDate = ticket.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date ticketDateObj = dateFormat.parse(ticketDate);
            Date currentDate = new Date();
            if (ticketDateObj.before(currentDate)) {
                System.out.println("Cannot cancel ticket for a past date.");
                return false;
            }
        } 
        catch (Exception e) {
            System.out.println("Invalid date format in the ticket.");
            return false;
        }
        Train train = getTrainByNumber(ticket.getTrainNumber());
        if (train != null) {
            int sourceIndex = train.getSchedule().stream().map(ScheduleEntry::getStation).toList().indexOf(ticket.getSource());
            int destIndex = train.getSchedule().stream().map(ScheduleEntry::getStation).toList().indexOf(ticket.getDestination());
            for (int i = sourceIndex; i < destIndex; i++) {
                train.cancelSeats(ticket.getDate(), train.getSchedule().get(i).getStation(), ticket.getNumPassengers());
            }
        }
        tickets.remove(ticket);
        System.out.println("Ticket cancelled successfully.");
        return true;
    }

    public void saveDataToFile(String trainsFilePath, String ticketsFilePath) {
        try (PrintWriter trainWriter = new PrintWriter(new FileWriter(trainsFilePath));
             PrintWriter ticketWriter = new PrintWriter(new FileWriter(ticketsFilePath))) {

            // Save train data
            for (Train train : trains) {
                trainWriter.print(train.getTrainName() + "," + train.getTrainNumber() + "," + train.getTotalSeats()+ ",");
                for (ScheduleEntry entry : train.getSchedule()) {
                    trainWriter.print(entry.getStation() + "," + entry.getArrivalTime() + "," + entry.getDepartureTime()+ ",");
                }
                trainWriter.println();
            }

            // Save ticket data
            for (Ticket ticket : tickets) {
                ticketWriter.println(ticket.getPassengerName() + "," + ticket.getTrainNumber() + "," +
                                     ticket.getSource() + "," + ticket.getDestination() + "," +
                                     ticket.getDepartureTime() + "," + ticket.getArrivalTime() + "," +
                                     ticket.getSeatNumber() + "," + ticket.getFare() + "," +
                                     ticket.getNumPassengers() + "," + ticket.getTicketId() + "," +
                                     ticket.getDate());
            }

        } catch (IOException e) {
            System.err.println("Error saving data to files: " + e.getMessage());
        }
    }

    // Method to load train and ticket data from files
    public TrainTicketBookingSystem loadDataFromFile(String trainsFilePath, String ticketsFilePath) {
        try (BufferedReader trainReader = new BufferedReader(new FileReader(trainsFilePath));
             BufferedReader ticketReader = new BufferedReader(new FileReader(ticketsFilePath))) {
            
            TrainTicketBookingSystem bookingSystem = new TrainTicketBookingSystem();
            // Load train data
            String line;
            while ((line = trainReader.readLine()) != null) {
                int i = 0;
                String[] parts = line.split(",");
                String trainName = parts[i];
                i++;
                int trainNumber = Integer.parseInt(parts[i]);
                i++;
                int totalSeats = Integer.parseInt(parts[i]);
                i++;
                List<ScheduleEntry> schedule = new ArrayList<>();
                while (i < parts.length ) {
                    // String[] entryParts = line.split(",");
                    schedule.add(new ScheduleEntry(parts[i], parts[i+1], parts[i+2]));
                    i=i+3;
                }
                Train train = new Train(trainName, trainNumber, totalSeats, schedule);
                bookingSystem.addTrain(train);
            }

            // Load ticket data
            while ((line = ticketReader.readLine()) != null) {
                String[] parts = line.split(",");
                String passengerName = parts[0];
                int trainNumber = Integer.parseInt(parts[1]);
                String source = parts[2];
                String destination = parts[3];
                // String departureTime = parts[4];
                // String arrivalTime = parts[5];
                // int seatNumber = Integer.parseInt(parts[6]);
                // double fare = Double.parseDouble(parts[7]);
                int numPassengers = Integer.parseInt(parts[8]);
                // int ticketId = Integer.parseInt(parts[9]);
                String date = parts[10];
                bookingSystem.bookTicket(passengerName, trainNumber, source, destination, numPassengers, date);
            }
            return bookingSystem;
        } catch (IOException e) {
            System.err.println("Error loading data from files: " + e.getMessage());
            return null;
        }
    }

    
    

    public static void main(String[] args) {
        TrainTicketBookingSystem bookingSystem = new TrainTicketBookingSystem();
                
        bookingSystem = bookingSystem.loadDataFromFile("trains.txt", "tickets.txt");

        // List<ScheduleEntry> train1Schedule = Arrays.asList(
        //     new ScheduleEntry("City A", "08:00", "08:30"),
        //     new ScheduleEntry("City B", "10:00", "10:30"),
        //     new ScheduleEntry("City C", "12:00", "12:30")
        // );
        // Train train1 = new Train("Express1", 101, 100, train1Schedule);
                
        // List<ScheduleEntry> train2Schedule = Arrays.asList(
        //     new ScheduleEntry("City B", "09:00", "09:30"),
        //     new ScheduleEntry("City C", "11:00", "11:30"),
        //     new ScheduleEntry("City D", "13:00", "13:30")
        // );
        // Train train2 = new Train("Express2", 102, 100, train2Schedule);
                
        // bookingSystem.addTrain(train1);
        // bookingSystem.addTrain(train2);
                
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Search Trains");
            System.out.println("2. Book Ticket");
            System.out.println("3. View Ticket");
            System.out.println("4. Cancel Ticket");
            System.out.println("5. View All Trains and Schedules");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            try {
                String choiceInput = scanner.nextLine();
                InputValidator.validateNumberOnly(choiceInput);
                choice = Integer.parseInt(choiceInput);
            } 
            catch (NumberOnlyException e) {
                System.out.println(e.getMessage());
                continue;
            }
            
            switch (choice) {
                case 1:
                    System.out.print("Enter source: ");
                    String source = scanner.nextLine();
                    try {
                        InputValidator.validateAlphabetOnly(source);
                        source = source.toUpperCase();
                    }
                    catch (AlphabetOnlyException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.print("Enter destination: ");
                    String destination = scanner.nextLine();
                    try {
                        InputValidator.validateAlphabetOnly(destination);
                        destination = destination.toUpperCase();

                    } 
                    catch (AlphabetOnlyException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.print("Enter date (e.g., '17 May 2023'): ");
                    String date = scanner.nextLine();
                    List<Train> availableTrains = bookingSystem.getTrainsWithAvailability(source, destination, date);
                    if (availableTrains.isEmpty()) {
                        System.out.println("No trains available for the selected route or source/destination.");
                    } 
                    else {
                        System.out.println("Available Trains:");
                        for (Train train : availableTrains) {
                            System.out.println("Train Name: " + train.getTrainName() + ", Train Number: " + train.getTrainNumber());
                            System.out.println("Remaining Seats: " + train.getRemainingSeats(date, source));
                            System.out.println("Schedule:");
                            for (ScheduleEntry entry : train.getSchedule()) {
                                System.out.println("Station: " + entry.getStation() + ", Arrival Time: " + entry.getArrivalTime() + ", Departure Time: " + entry.getDepartureTime());
                            }
                        }
                    }
                    break;
                case 2:
                    System.out.print("Enter passenger name: ");
                    String passengerName = scanner.nextLine();
                    try {
                        InputValidator.validateAlphabetOnly(passengerName);
                    } catch (AlphabetOnlyException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.print("Enter source: ");
                    String bookSource = scanner.nextLine();
                    try {
                        InputValidator.validateAlphabetOnly(bookSource);
                        bookSource = bookSource.toUpperCase();

                    } 
                    catch (AlphabetOnlyException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.print("Enter destination: ");
                    String bookDestination = scanner.nextLine();
                    try {
                        InputValidator.validateAlphabetOnly(bookDestination);
                        bookDestination = bookDestination.toUpperCase();

                    } 
                    catch (AlphabetOnlyException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.print("Enter date (e.g., '17 May 2023'): ");
                    String bookDate = scanner.nextLine();
                    if (!bookingSystem.isDateValid(bookDate)) {
                        System.out.println("Invalid date or date is in the past.");
                        break;
                    }
                    System.out.print("Enter number of passengers: ");
                    try {
                        String numPassengersInput = scanner.nextLine();
                        InputValidator.validateNumberOnly(numPassengersInput);
                        int numPassengers = Integer.parseInt(numPassengersInput);
                        availableTrains = bookingSystem.getTrainsWithAvailability(bookSource, bookDestination, bookDate);
                        if (availableTrains.isEmpty()) {
                            System.out.println("No trains available for the selected route or source/destination.");
                        } 
                        else {
                            System.out.println("Available Trains:");
                            for (Train train : availableTrains) {
                                System.out.println("Train Name: " + train.getTrainName() + ", Train Number: " + train.getTrainNumber());
                                System.out.println("Remaining Seats: " + train.getRemainingSeats(bookDate, bookSource));
                                System.out.println("Schedule:");
                                for (ScheduleEntry entry : train.getSchedule()) {
                                    System.out.println("Station: " + entry.getStation() + ", Arrival Time: " + entry.getArrivalTime() + ", Departure Time: " + entry.getDepartureTime());
                                }
                            }
                        }
                        System.out.print("Enter train number: ");
                        try {
                            String trainNumberInput = scanner.nextLine();
                            InputValidator.validateAlphanumeric(trainNumberInput);
                            int trainNumber = Integer.parseInt(trainNumberInput);
                            Ticket bookedTicket = bookingSystem.bookTicket(passengerName, trainNumber, bookSource, bookDestination, numPassengers, bookDate);
                            if (bookedTicket != null) {
                                System.out.println("Ticket booked successfully:");
                                System.out.println("Passenger Name: " + bookedTicket.getPassengerName());
                                System.out.println("Train Number: " + bookedTicket.getTrainNumber());
                                System.out.println("Source: " + bookedTicket.getSource());
                                System.out.println("Destination: " + bookedTicket.getDestination());
                                System.out.println("Departure Time: " + bookedTicket.getDepartureTime());
                                System.out.println("Arrival Time: " + bookedTicket.getArrivalTime());
                                System.out.println("Seat Number(s): " + bookedTicket.getSeatNumber() + " - " + (bookedTicket.getSeatNumber() + numPassengers - 1));
                                System.out.println("Total Fare: " + bookedTicket.getFare());
                                System.out.println("Ticket ID: " + bookedTicket.getTicketId());
                                System.out.println("Date: " + bookedTicket.getDate());
                            } 
                            else {
                                System.out.println("Failed to book ticket.");
                            }
                        } 
                        catch (AlphanumericException e) {
                            System.out.println(e.getMessage());
                        }
                    } 
                    catch (NumberOnlyException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter ticket ID: ");
                    try {
                        String ticketIdInput = scanner.nextLine();
                        InputValidator.validateNumberOnly(ticketIdInput);
                        int ticketId = Integer.parseInt(ticketIdInput);
                        Ticket ticket = bookingSystem.getTicket(ticketId);
                        if (ticket != null) {
                            System.out.println("Ticket Details:");
                            System.out.println("Passenger Name: " + ticket.getPassengerName());
                            System.out.println("Train Number: " + ticket.getTrainNumber());
                            System.out.println("Source: " + ticket.getSource());
                            System.out.println("Destination: " + ticket.getDestination());
                            System.out.println("Departure Time: " + ticket.getDepartureTime());
                            System.out.println("Arrival Time: " + ticket.getArrivalTime());
                            System.out.println("Seat Number(s): " + ticket.getSeatNumber() + " - " + (ticket.getSeatNumber() + ticket.getNumPassengers() - 1));
                            System.out.println("Total Fare: " + ticket.getFare());
                            System.out.println("Date: " + ticket.getDate());
                        } 
                        else {
                            System.out.println("Ticket not found.");
                        }
                    } 
                    catch (NumberOnlyException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Enter ticket ID: ");
                    try {
                        String cancelTicketIdInput = scanner.nextLine();
                        InputValidator.validateNumberOnly(cancelTicketIdInput);
                        int cancelTicketId = Integer.parseInt(cancelTicketIdInput);
                        boolean cancelled = bookingSystem.cancelTicket(cancelTicketId);
                        if (!cancelled) {
                            System.out.println("Failed to cancel ticket.");
                        }
                    } 
                    catch (NumberOnlyException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("All Trains and Schedules:");
                    for (Train train : bookingSystem.trains) {
                        System.out.println("Train Name: " + train.getTrainName() + ", Train Number: " + train.getTrainNumber());
                        System.out.println("Schedule:");
                        for (ScheduleEntry entry : train.getSchedule()) {
                            System.out.println("Station: " + entry.getStation() + ", Arrival Time: " + entry.getArrivalTime() + ", Departure Time: " + entry.getDepartureTime());
                        }
                        System.out.println();
                    }
                    break;
                
                
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        } while (choice != 6);
        bookingSystem.saveDataToFile("trains.txt", "tickets.txt");

        scanner.close();
    }
}