# Gator Field Notebook
An app streamlining environmental data collection. This repository contains all source code and documentation.  
  
![GatorFieldIcon.png](composeApp%2Fsrc%2FcommonMain%2FcomposeResources%2Fdrawable%2FGatorFieldIcon.png)  

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
- UI: navigation menu, consistent pages and formatting
- SQL database
- Samples: user can input sample information, save, edit, delete, and view all samples
- Bluetooth: implemented bluetooth connection with TSPL
- Printing: implemented printing functionality via bluetooth
- Form Creation: user can create forms for new collections and select their forms when adding a new sample  

### SQLLight tables
#### SampleForm
| Field Name  | Field Type | Description |
|-------------|------------|-------------|
| `form_id`   | INTEGER    | Primary key |
| `form_name` | TEXT       | Form name   |
| `is_active` | BOOLEAN    | Form active |

**Description:**  
This stores the "head" of each form. For example, if there was a form for bug samples, this table would store the name "Bug Samples" and the ID. The form's fields are linked through the Field table. If form is inactive, it has been deleted. Deleted forms are preserved to preserve the sample associated with them.

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
- There is no safeguard for submitting text with curly braces, but this should be implemented because it could mess up lists stored as json strings in the database
- No validation for submitting images, but they currently cannot be above a certain size. Submitting an image too large results in corrupting the sample data

### Next Steps
- Multi-select samples to print at once or specify number of copies to print, currently can only print one at a time
- Add more options for exporting data to CSV, currently it is restricted to form groups. Users should be able to export single samples, all data, or filtered samples.
- Implement additional filter and sort options, for example sort by most recent, alphabetical, etc.
- Add more flexibility to form creation, implement form editing and deleting. Form deleting is implemented in the back end, but needs the UI added. For form editing, the user should be able to rearrange the fields on the form or delete fields when making the form (i.e. user notices an error on a field, currently they would need to redo the entire form)
- Add a settings page for user information (name. account info when accounts are made), dark/light mode, printer settings (font size for printing, selecting a printer possibly)
- Allow for larger photos to be added
- Implement on IOS, some logic (e.g. database connection) might have to be done twice for iOS and Android, respectively
- Add more export methods, i.e. export to pdf
- Number fields could have an added option for units maybe
- Currently, QR codes use a custom URI ("myapp://sample/{sampleId}"). Scanning this on the camera app does not correctly redirect to the application because browsers like Chrome do not handle custom schemes like myApp. In the future, this URI should instead be an https scheme, for example https://gatorfieldnotebook.com/<userID>/sample/{sampleID} or something similar. Then register that address to link to the custom URI, or to the app store if the app is not installed. The QR code still works as-is if used on a QR code app. Note that the current URI will link to any user's first sample, since sampleID is not unique among users.
- For this app to be usable at Universities, Dr. Blanchard mentioned that the remote DB using FireStore does not meet University security requirements. Other remote databases should be explored in the future.