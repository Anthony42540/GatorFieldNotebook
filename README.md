# Gator Field Notebook
An app streamlining environmental data collection. This repository contains all source code and documentation.

## Table of Contents ## 
- [Project Overview](https://github.com/Anthony42540/GatorFieldNotebook/edit/master/README.md#project-overview)
- [Architecture](https://github.com/Anthony42540/GatorFieldNotebook/edit/master/README.md#architecture)
- [Features](https://github.com/Anthony42540/GatorFieldNotebook/edit/master/README.md#features)
- [Completed Work](https://github.com/Anthony42540/GatorFieldNotebook/edit/master/README.md#completed-work)
- [Known Bugs](https://github.com/Anthony42540/GatorFieldNotebook/edit/master/README.md#known-bugs)

### Project Overview
The Gator Field Notebook is a field notebook application that enhances field data collection by integrating GPS logging, customizable data fields, and Bluetooth printing for on-site label creation. The application also enables users to export collected data to Excel for analysis and reporting.

### Architecture
The Gator Field Notebook has distinct modules that work together to manage data collection, storage, processing, and output. The key architectural elements are as follows:

- **External Interface (Mobile Application)** \
The mobile application is the primary user interface, allowing users to input data, such as sample information, environmental conditions, and location coordinates. The interface transmits the collected data to the persistent database, allowing a seamless transfer from user interaction to storage.

- **Persistent State (MySQL Database)** \
The SQLDelight database acts as the app's persistant storage, where all field data are securely stored. It is the connection between the external interface, the connection responsible for data input and retrieval, and the internal systems, which process and utilize the stored data.

- **Internal Systems** \
Internal systems are responsible for processing the data from the database. They also retrieve relevant information that is not entered in by the user, such as GPS coordinates, and prepare the necessary information for the Bluetooth label for printing, such that the data flows efficiently to the label functionality.

- **Printer** \
The printer has a Bluetooth connection that recieves the label information and prints it.

### Features
Features that we would like to have fully implemented into the Gator Field Notebook:

- **GPS Integration** \
Automatically records location data for each entry.

- **Customizable Data Fields** \
Allows users to modify data fields and label format, allowing for adaptability of the labels for different projects.

- **Data Export** \
Provides an option to export collected data to Excel.

- **Bluetooth Printing** \
Connects via Bluetooth to a portable printer to print the generated labels in real time.

- **Device Compatibility** \
Works on iOS and Android devices.

### Completed Work
- Basic UI: different screen navigation via button interaction
- Navbar implementation
- SQL setup: hooks for connection to the database have been implemented

### Known Bugs
Known bugs will be added as we continue.

This is not a bug, but something to note for future work: Some styling (like importing icons) and logic (e.g. database connection) might have to be done twice for iOS and Android, respectively.

