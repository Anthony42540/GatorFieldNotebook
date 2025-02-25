# Gator Field Notebook
An app streamlining environmental data collection. This repository contains all source code and documentation.

## Table of Contents ## 
- [Project Overview](https://github.com/Anthony42540/GatorFieldNotebook#project-overview)
- [Architecture](https://github.com/Anthony42540/GatorFieldNotebook#architecture)
- [Features](https://github.com/Anthony42540/GatorFieldNotebook?tab=readme-ov-file#features)
- [Completed Work](https://github.com/Anthony42540/GatorFieldNotebook?tab=readme-ov-file#completed-work)
- [Known Bugs](https://github.com/Anthony42540/GatorFieldNotebook?tab=readme-ov-file#known-bugs)

### Project Overview
The Gator Field Notebook is a field notebook application that enhances field data collection by integrating GPS logging, customizable data fields, and Bluetooth printing for on-site label creation. The application also enables users to export collected data to Excel for analysis and reporting.

## Getting Started

### Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 11 or newer
- Git
- For iOS development: macOS with Xcode 13 or newer

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Anthony42540/GatorFieldNotebook.git
   cd GatorFieldNotebook
   ```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository and select it

3. Install dependencies:
   - Android Studio will automatically sync the Gradle files
   - Wait for the sync to complete (this may take a few minutes)

4. Set up Firebase:
   - Create a new Firebase project at [firebase.google.com](https://firebase.google.com)
   - Add Android and iOS apps to your Firebase project
   - Download and add `google-services.json` to the `/android` directory
   - For iOS, add `GoogleService-Info.plist` to the `/ios` directory

### Running the Application

#### Android

1. Connect an Android device or start an emulator
2. Select the 'android' configuration from the run configurations dropdown
3. Click the Run button (or press Shift+F10)

#### iOS (requires macOS)

1. Open the iOS project in Xcode:
   ```bash
   cd iosApp
   open iosApp.xcworkspace
   ```
2. Select your target device
3. Click the Run button

## Project Structure

```
GatorFieldNotebook/
├── androidApp/          # Android-specific code
├── iosApp/              # iOS-specific code
├── shared/              # Shared code (KMP)
│   ├── src/
│   │   ├── commonMain/  # Common Kotlin code
│   │   ├── androidMain/ # Android-specific implementations
│   │   └── iosMain/     # iOS-specific implementations
├── build.gradle.kts     # Project build configuration
└── gradle/              # Gradle configuration
```

### Architecture
The Gator Field Notebook has distinct modules that work together to manage data collection, storage, processing, and output. The key architectural elements are as follows:

- **External Interface (Mobile Application)** \
The mobile application is the primary user interface, allowing users to input data, such as sample information, environmental conditions, and location coordinates. The interface transmits the collected data to the persistent database, allowing a seamless transfer from user interaction to storage.

- **Persistent State (MySQL Database)** \
The SQLDelight database acts as the app's persistent storage, where all field data are securely stored. It is the connection between the external interface, the connection responsible for data input and retrieval, and the internal systems, which process and utilize the stored data.

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
- SQL setup: hooks for connection to the database have been implemented, tables have been added, and a database class has been made for interaction
- Sample Saving: user can input sample information, save, and view all samples
- Bluetooth library: implemented a base for bluetooth library implementation
- Form Creation: user can create forms for new collections and select their forms when adding a new sample
- Sample deletion: user can delete ALL samples from database

### SQLLight tables
#### SampleForm
| Field Name  | Field Type | Description |
|-------------|------------|-------------|
| `form_id`   | INTEGER    | Primary key |
| `form_name` | TEXT       | Form name   |

**Description:**  
This stores the "head" of each form. For example, if there was a form for bug samples, this table would store the name "Bug Samples" and the ID. The form's fields are linked through the Field table.

#### Field
| Field Name  | Field Type | Description                                                                 |
|-------------|------------|-----------------------------------------------------------------------------|
| `field_id`  | INTEGER    | Primary Key                                                                 |
| `form_id`   | INTEGER    | Form ID links field to its form                                             |
| `field_name`| TEXT       | Field name                                                                  |
| `order_num` | INTEGER    | Order of the field within the form (i.e. appears first or last in the form) |
| `field_type`| TEXT       | Field type (i.e. short text, number, multi-select)                          |
| `is_required` | INTEGER  | Indicates if field is required                                              |
| `options`   | TEXT       | List of options if field is dropdown or multi-select (optional field)       |

**Description:**  
Stores the fields associated with each form. Each field is linked to a form by form id.

#### SampleData
| Field Name           | Field Type | Description                             |
|-----------------------|------------|-----------------------------------------|
| `sample_id`          | INTEGER    | Primary Key                             |
| `form_id`            | INTEGER    | Links sample data to the connected form |
| `date_collected_utc` | TEXT       | Date and time of sample                 |
| `location`           | TEXT       | Location of sample                      |

**Description:**  
This is the "head" of each sample collected. It is associated with a form and contains the date and location, since those will always be required fields. Each field submission for a sample is stored as a data entry in the next table.

#### DataEntry
| Field Name  | Field Type | Description                           |
|-------------|------------|---------------------------------------|
| `entry_id`  | INTEGER    | Primary key                           |
| `sample_id` | INTEGER    | Links data to the sample it is from   |
| `field_id`  | INTEGER    | Links data to the type of field it is |
| `user_input`| TEXT       | The actual data stored as a string    |

**Description:**  
Stores individual data entries for each sample. It is linked to the sample it is from and the type of field that it is. In the future, if fields are to be reused for multiple forms, the SampleData table will have to store a list linking each dataEntry to its fields. This will save space in the database.



### Known Bugs
- There is no form field validation so leaving some fields empty might break the app
- There is no scroll window implemented when fields overflow the screen, so for now sample fields are limited to one screen
- There is no safeguard for submitting text with curly braces, but this should be implemented because it could mess up lists stored as json strings in the database
- This is not a bug, but something to note for future work: Some logic (e.g. database connection) might have to be done twice for iOS and Android, respectively

