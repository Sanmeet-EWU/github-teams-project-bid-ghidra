# Team Ghidra
Lab Section: 1

Team Number: 4

Team Members:
- Allen Chang     achang3@ewu.edu
- Carrie Sargent  csargent3@ewu.edu
- Earl Quinto     equinto@ewu.edu
- Ethan Crawford  ecrawford4@ewu.edu
- William Kern    wkern1@ewu.edu

# Developer Documentation for CalendarFX

Welcome to the CalendarFX developer documentation. This guide is intended for developers who want to contribute to the CalendarFX project.

#  Overview

CalendarFX is a Java-based library for creating calendar applications with a rich set of features and customizable components. It leverages the JavaFX framework to provide a visually appealing and interactive user interface. This documentation covers the installation, basic usage, and customization of CalendarFX.

# Prerequisites
JDK 11 or later
JavaFX SDK

# Core Components

### CalendarView

`CalendarView` is the main container for displaying calendar data. It provides different views such as day, week, month, and year views.

### Entry

An `Entry` represents an event or appointment in the calendar. It includes properties such as title, date, time, and description.

### Calendar

A `Calendar` holds multiple `Entry` objects and can be customized with different colors and styles.

### View Types

- **DayView**: Displays events for a single day.
- **WeekView**: Displays events for a week.
- **MonthView**: Displays events for a month.
- **YearView**: Displays events for a year.


## 1. How to Obtain the Source Code
To obtain the source code, follow these steps:

1. Clone the main repository:
   ```bash
   git clone https://github.com/Sanmeet-EWU/github-teams-project-bid-ghidra.git
   ```
2. Navigate to the java file:
   ```bash
   ./TimeTracker/src/main/java/com/ghidra/TimeTracker.java
   ```
For detailed instructions on setting up a project in Eclipse or IntelliJ, see the User Manual:
   ```bash
   ./UserManual/TimeTrackerUserManual.pdf
   ```
## Future Enhancements

### 1. Enhanced Alert System
- Implement alert notifications using native device notification systems.
- Introduce sound playback for notifications.
- Add snooze and dismiss functionality for alerts.

### 2. Expanded Personalization Options
- Enable customization of calendar background colors.
- Provide options to change font styles and sizes.
- Allow users to select event colors and adjust transparency levels.
- Allow users to create custom fields in the event details

### 3. Improved Security Measures
- Implement encryption for saved file data to enhance security.

### 4. Database System and User Authentication
- Implement a database system that can handle the data of many users
- Implement a user authentication system so that different users can access their data

### 5. Installation process and Executible
- Build the project so that it can be shipped as an executible
- Implement an installation process to install the executible to the machine and make a shortcut to it

# References
- https://dlsc-software-consulting-gmbh.github.io/CalendarFX/
- https://docs.oracle.com/javase/8/javafx/api/
- https://github.com/dlsc-software-consulting-gmbh/CalendarFX/tree/master-11/CalendarFXView/src/main/java/com/calendarfx
- https://github.com/openjdk/jfx/tree/master/modules


