# Smart-Car-parking-System-with-EV-Charging-System
•The rapid rate of urbanization, coupled with the increasing number of cars, provides major challenges for cities in terms of proper parking facility management. Traditional parking schemes are based on manual approaches, leading to time-wasting inefficiencies, unavailability of correct parking space information, and increased frequency of traffic jams. The increasing uptake of electric cars has also strengthened the demand for advanced infrastructure that supports electric vehicle charging and parking needs.

##Description
•This project is a Smart IoT-Based Car Parking System designed to optimize parking space usage and provide a seamless experience for both electric and non-electric vehicle owners. It addresses common urban parking challenges such as slot unavailability, inefficient manual systems, and lack of support for electric vehicle charging.

##What Problem It Solves
- Reduces parking time and traffic congestion by allowing real-time slot booking
- Prevents slot misuse with automatic vehicle detection and monitoring
- Supports electric vehicle infrastructure with wireless charging integration
- Enhances user experience with mobile-based booking, payment, and guidance
- Eliminates manual intervention with automatic gate access and cloud-based data handling

##How It Works
•Sensors (e.g., ultrasonic) detect vehicle presence in parking slots
•Wireless charging pads activate automatically for EVs based on paid duration
•License plate recognition enables gate automation for registered vehicles
•Real-time data (slot availability, user info, payment status) is synced to the cloud
•A mobile app allows users to register, book slots, make payments, and receive notifications
•Visual indicators (LEDs) display slot status and notify when booking time expires

##Core Functionalities
- Slot booking and cancellation via mobile app
- Online and manual payment options
- Wireless EV charging with auto on/off based on timing
- Automated gate control using license plate scanning
- Cloud-based user, vehicle, and booking data management
- Real-time monitoring and display of slot availability
- Overtime fee calculation and notification
- LED guidance for free, booked, and expired slots

##Features
- User Registration & Login
    •Secure user account creation with car and personal details.
- Mobile App Slot Booking
    •Book and reserve parking slots in advance through the mobile application.
- Online & Manual Payments
    •Make payments via the app or on-site for parking and EV charging.
- Wireless EV Charging
    •Auto-start and stop charging for electric vehicles based on paid time duration.
- Vehicle Detection via Sensors
    •Ultrasonic and IR sensors detect the presence and duration of vehicle parking.
- Real-Time Data Synchronization
    •Slot availability and vehicle data are updated live to the cloud database.
- Automatic Gate Access
    •License plate recognition allows only registered vehicles to enter/exit automatically.
- Visual Slot Indicators
    •LED indicators show real-time slot status (available, occupied, overtime).
- Overtime Fee Calculation
    •Detects extra time usage and prompts user to pay before exit.
- Admin Monitoring Dashboard
    •Admin panel to monitor parking usage, revenue, and system performance.

##Hardware Components
•Microcontroller → ESP 32 – for sensor control and internet communication
•Ultrasonic Sensors → HC-SR04 – to detect vehicle presence in parking slots
•IR Sensors → Generic IR module – for short-range vehicle detection
•Wireless Charging Pad → Qi Wireless Charger Module – for EV charging
•Relay Module → 5V Relay – to control wireless charger ON/OFF
•Camera Module → Raspberry Pi Camera / USB Webcam – for license plate recognition
•LED Indicators → Red/Green/Blue LEDs – to show slot availability and overtime status
•Servo Motor → SG90 Servo or DC Motor – for gate opening and closing
•Power Supply Unit → 5V/12V Power Adapter – to power components
•Breadboard & Wires → For circuit prototyping and testing
•Frame	Custom design → to simulate or implement real parking slots

##Software Requirements
Arduino IDE → Writing and uploading code to Arduino
C++ → Programming microcontroller and sensor logic
Android Studio → Developing the mobile application (for slot booking & payment)
Firebase → Cloud storage for user, vehicle, and booking data
OpenCV → License plate recognition and image processing
HTTP Protocol → Communication between devices and server
Git & GitHub → Version control and source code management
Figma → UI/UX designing for the mobile app
